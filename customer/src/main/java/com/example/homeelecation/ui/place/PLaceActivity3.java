package com.example.homeelecation.ui.place;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homeelecation.HomeActivity2;
import com.example.homeelecation.R;
import com.example.homeelecation.ui.Cart.CartAdapter;
import com.example.homeelecation.ui.Cart.CartItemModel;
import com.example.homeelecation.ui.Cart.CartViewModel;
import com.example.homeelecation.ui.DbLoadData;
import com.example.homeelecation.ui.address.Select_Address_Activity3;
import com.example.homeelecation.util.EdgeToEdgeUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;
import com.razorpay.Checkout;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PLaceActivity3 extends AppCompatActivity implements PaymentResultWithDataListener {

    private FirebaseAuth firebaseUser;
    private PlaceViewModel placeViewModel;
    private CartViewModel cartViewModel;
    private FirebaseFunctions mFunctions;

    private Toolbar toolbar;
    private Dialog loadingDialog;
    private RecyclerView recyclerView;

    private ConstraintLayout constraintLayoutPlace, constraintLayoutOrder;
    private Button changeAddresses, continueBe;
    private TextView fullName, fullAddresses, pinCode, mobile, totalAmount;
    public static final int SELECT_ADDRESS = 0;

    public static List<CartItemModel> cartItemModelList = new ArrayList<>();
    private FirebaseFirestore firebaseFirestore;
    private String order_ids;

    public String key = ""; // Initially empty, fetched from Cloud Function
    private String razorpayOrderIdFromServer, userPhone, userEmail;
    private long serverCalculatedAmount;
    private TextView confirmationOrderIdText;
    private Button confirmationContinueShopBtn;


  

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place3);
        EdgeToEdge.enable(this);
        EdgeToEdgeUtils.applyTopInset(findViewById(R.id.appbar));
        EdgeToEdgeUtils.applyBottomInset(findViewById(R.id.place_order_constraintlayout));



        // Initialization
        placeViewModel = new ViewModelProvider(this).get(PlaceViewModel.class);
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);
        mFunctions = FirebaseFunctions.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        // यह लाइन जोड़ें (10.0.2.2 Android Emulator के लिए आपके कंप्यूटर का लोकल IP है)
        //mFunctions.useEmulator("10.218.24.190", 5001);
        // Unique Order ID generation
        firebaseUser = FirebaseAuth.getInstance();
        order_ids = generateOrderId();

        initViews();
        setupObservers();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        CartAdapter adapter = new CartAdapter(cartItemModelList, totalAmount, false, cartViewModel);
        recyclerView.setAdapter(adapter);

        // Continue Button: Cloud Function के जरिए Order ID बनाना
        continueBe.setOnClickListener(view -> {
            continueBe.setEnabled(false); // Disable to prevent duplicate clicks
            loadingDialog.show();

            // userPhone aur userEmail fetch karein


            if (firebaseUser.getCurrentUser() == null) {
                // अगर currentUser null है, तो इसका मतलब है कि कोई भी उपयोगकर्ता Firebase में लॉग इन नहीं है।
                loadingDialog.dismiss();
                Log.e("AuthError", "User is not logged in to Firebase."); // Logcat में देखें
                return; // Cloud Function को कॉल न करें
            } else {
                Log.d("AuthInfo", "User is logged in: " + firebaseUser.getUid()); // Logcat में देखें
            }

            userPhone = DbLoadData.addressesSelectModelList.get(DbLoadData.selectedAddresses).getMobileNumber();
            userEmail = (DbLoadData.email != null && !DbLoadData.email.isEmpty()) ? DbLoadData.email : (firebaseUser.getCurrentUser().getEmail() != null ? firebaseUser.getCurrentUser().getEmail() : "no-email@example.com");

            // Server-side calculation ke liye items list taiyar karein
            List<Map<String, Object>> itemsList = new ArrayList<>();
            for (CartItemModel model : cartItemModelList) {
                if (model.getType() == CartItemModel.CART_ITEM_LAYOUT) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("productId", model.getProductID());
                    item.put("qty", model.getProductQty());
                    itemsList.add(item);
                }
            }

            if (itemsList.isEmpty()) {
                loadingDialog.dismiss();
                Toast.makeText(this, "Cart is empty", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> data = new HashMap<>();
            data.put("items", itemsList);

            mFunctions.getHttpsCallable("createRazorpayOrder")
                    .call(data)
                    .addOnSuccessListener(result -> {
                        Map<String, Object> res = (Map<String, Object>) result.getData();
                        razorpayOrderIdFromServer = (String) res.get("id");
                        
                        // Server se key mil rahi hai toh update karein
                        if (res.containsKey("key")) {
                            key = (String) res.get("key");
                        } else if (res.containsKey("key_id")) {
                            key = (String) res.get("key_id");
                        }

                        if (res.containsKey("amount")) {
                            serverCalculatedAmount = ((Number) res.get("amount")).longValue();
                        }

                        if (razorpayOrderIdFromServer != null) {
                            prepareAndPlaceOrder();
                        } else {
                            loadingDialog.dismiss();
                            continueBe.setEnabled(true); // Re-enable if server returns null
                            Log.e("RazorpayApp", "Order ID creation failed: received null ID");
                            Toast.makeText(this, "Order ID creation failed", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        loadingDialog.dismiss();
                        continueBe.setEnabled(true); // Re-enable on failure
                        Log.e("FirebaseError", "Error calling Cloud Function: " + e.getMessage(), e);
                        //Log.d("RazorpayApp", "Calculated amountInPaisa: " + amountInPaisa);
                        Log.d("RazorpayApp", "Sending data to Cloud Function: " + data.toString());
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        });

    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Place order");

        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        constraintLayoutPlace = findViewById(R.id.place_order_constraintlayout);
        constraintLayoutOrder = findViewById(R.id.order_confirmed_layout_constraintlayout);
        recyclerView = findViewById(R.id.place_recyclerview);
        changeAddresses = findViewById(R.id.place_deliver_addres_btn);
        fullName = findViewById(R.id.place_deliver_addres_userName);
        fullAddresses = findViewById(R.id.place_deliver_addres_userAddresses);
        pinCode = findViewById(R.id.place_deliver_addres_picode);
        mobile = findViewById(R.id.place_deliver_addres_user_mobile);
        totalAmount = findViewById(R.id.total_place_Amount);
        continueBe = findViewById(R.id.place_order_bt);
        confirmationOrderIdText = findViewById(R.id.order_confirmed_layout_order_id_textview);
        confirmationContinueShopBtn = findViewById(R.id.order_confirmed_layout_shop_button);

        // Continue Shopping Button (Default setup)
        confirmationContinueShopBtn.setOnClickListener(v -> {
            Intent intentShop = new Intent(PLaceActivity3.this, HomeActivity2.class);
            intentShop.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intentShop);
            finish();
        });

        changeAddresses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PLaceActivity3.this, Select_Address_Activity3.class);
                intent.putExtra("MODE", SELECT_ADDRESS);
                startActivity(intent);
            }
        });

        fullName.setText(DbLoadData.addressesSelectModelList.get(DbLoadData.selectedAddresses).getFullName());
        mobile.setText(DbLoadData.addressesSelectModelList.get(DbLoadData.selectedAddresses).getMobileNumber());
        pinCode.setText(DbLoadData.addressesSelectModelList.get(DbLoadData.selectedAddresses).getPinCode());

        }

    private void setupObservers() {
        placeViewModel.isLoading.observe(this, isLoading -> {
            if (isLoading) loadingDialog.show();
            else loadingDialog.dismiss();
        });

        placeViewModel.orderPlaced.observe(this, orderPlaced -> {
            if (orderPlaced) {
                // Save history as soon as order is placed in Firestore
                saveUserOrderHistory("Pending");
                startPayment(razorpayOrderIdFromServer, userPhone, userEmail, serverCalculatedAmount);
            }
        });

        placeViewModel.paymentUpdateSuccess.observe(this, success -> {
            if (success) {

                constraintLayoutOrder.setVisibility(View.VISIBLE);
                confirmationOrderIdText.setText("Order ID: " + order_ids);
                constraintLayoutPlace.setVisibility(View.GONE);
            }
        });

    }

    private void prepareAndPlaceOrder() {
        Map<String, Object> shippingInfo = new HashMap<>();
        shippingInfo.put("fullName", fullName.getText().toString());
        shippingInfo.put("address", fullAddresses.getText().toString());
        shippingInfo.put("mobile", mobile.getText().toString());
        shippingInfo.put("pinCode", pinCode.getText().toString());

        Map<String, Object> timeInfo = new HashMap<>();
        Calendar cal = Calendar.getInstance();
        timeInfo.put("now", FieldValue.serverTimestamp());
        cal.add(Calendar.DAY_OF_YEAR, 1);
        timeInfo.put("next1", cal.getTime());
        cal.add(Calendar.DAY_OF_YEAR, 1);
        timeInfo.put("next2", cal.getTime());
        cal.add(Calendar.DAY_OF_YEAR, 2);
        timeInfo.put("next4", cal.getTime());

        placeViewModel.placeOrder(order_ids, cartItemModelList, shippingInfo, 
                razorpayOrderIdFromServer, String.valueOf(Calendar.getInstance().get(Calendar.YEAR)),
                String.valueOf(Calendar.getInstance().get(Calendar.MONTH) + 1), timeInfo);
    }

    public void startPayment(String orderId, String phone, String email, long amountInPaisa) {
        Checkout checkout = new Checkout();
        checkout.setKeyID(key);
        
        try {
            JSONObject options = new JSONObject();
            options.put("name", "Avatar Election");
            options.put("order_id", orderId);
            options.put("currency", "INR");
            options.put("amount", amountInPaisa); // Use server-calculated amount
            options.put("prefill.email", email);
            options.put("prefill.contact", phone);
            
            checkout.open(this, options);
            if (loadingDialog != null && loadingDialog.isShowing()) {
                loadingDialog.dismiss();
            }
        } catch (Exception e) {
            Log.e(TAG, "Razorpay Error", e);
            loadingDialog.dismiss();
            Toast.makeText(this, "Error starting payment", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPaymentSuccess(String paymentId, PaymentData paymentData) {
        loadingDialog.show();
        try {
            TextView tv = loadingDialog.findViewById(R.id.loading_text);
            if (tv != null) tv.setText("Verifying Payment...");
        } catch (Exception e) {}

        // Don't show the order layout yet, wait for verification

        Map<String, Object> data = new HashMap<>();
        data.put("order_id", paymentData.getOrderId());
        data.put("payment_id", paymentData.getPaymentId());
        data.put("signature", paymentData.getSignature());

        mFunctions.getHttpsCallable("verifySignature")
                .call(data)
                .addOnSuccessListener(result -> {
                    loadingDialog.dismiss();
                    
                    // Now show Success UI
                    constraintLayoutPlace.setVisibility(View.GONE);
                    constraintLayoutOrder.setVisibility(View.VISIBLE);

                    confirmationOrderIdText.setText("Order ID: " + order_ids);
                    ImageView imageView = findViewById(R.id.order_confirmed_layout_check_image);
                    imageView.setImageResource(R.drawable.baseline_check_circle_24);

                    TextView textView = findViewById(R.id.order_confirmed_layout_confirmed_textview);
                    textView.setText("Order Confirmed");

                    // Clear Cart after success
                    try {
                        if (cartViewModel != null) {
                            // Agar aapke ViewModel mein clearCart method hai
                            // cartViewModel.clearCart(); 
                        }
                    } catch (Exception e) {}

                    confirmationContinueShopBtn.setText("Continue Shopping");
                    confirmationContinueShopBtn.setOnClickListener(v -> {
                        Intent intentShop = new Intent(PLaceActivity3.this, HomeActivity2.class);
                        intentShop.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intentShop);
                        finish();
                    });
                })
                .addOnFailureListener(e -> {
                    loadingDialog.dismiss();
                    Log.e("Verification Failed:", "Error: " + e.getMessage(), e);
                    Toast.makeText(this, "Verification Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    showErrorUI("Verification Failed: " + e.getMessage());
                });
    }

    private void showErrorUI(String message) {
        constraintLayoutPlace.setVisibility(View.GONE);
        constraintLayoutOrder.setVisibility(View.VISIBLE);
        confirmationOrderIdText.setText("Order ID: " + order_ids);
        
        ImageView imageView = findViewById(R.id.order_confirmed_layout_check_image);
        imageView.setImageResource(R.drawable.baseline_error_24);
        
        TextView textView = findViewById(R.id.order_confirmed_layout_confirmed_textview);
        textView.setText(message);

        // Hide delivery text on failure
        TextView deliveryText = findViewById(R.id.order_confirmed_layout_expected_delivery_textview);
        if (deliveryText != null) deliveryText.setVisibility(View.GONE);
        
        // Update button
        confirmationContinueShopBtn.setText("Retry Payment");
        confirmationContinueShopBtn.setOnClickListener(v -> {
            loadingDialog.show();
            startPayment(razorpayOrderIdFromServer, userPhone, userEmail, serverCalculatedAmount);
        });
    }

    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
        
        showErrorUI("Payment Failed: " + s);

        placeViewModel.updatePaymentFailure(order_ids, s, FieldValue.serverTimestamp());
        Toast.makeText(this, "Payment Failed: " + s, Toast.LENGTH_LONG).show();
    }

    private String generateOrderId() {
        Calendar cal = Calendar.getInstance();
        String month = cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()).toUpperCase();
        //return month + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return month + randomCode();
    }

    @Override
    public void onBackPressed() {
        if (constraintLayoutOrder.getVisibility() == View.VISIBLE) {
            Intent intentShop = new Intent(PLaceActivity3.this, HomeActivity2.class);
            intentShop.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intentShop);
            finish();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // PLaceActivity3.java में ये बदलाव करें

    @Override
    protected void onStart() {
        super.onStart();
        // चेक करें कि एड्रेस लिस्ट खाली तो नहीं है
        if (DbLoadData.addressesSelectModelList.size() > 0) {
            int selected = DbLoadData.selectedAddresses;

            fullName.setText(DbLoadData.addressesSelectModelList.get(selected).getFullName());
            mobile.setText(DbLoadData.addressesSelectModelList.get(selected).getMobileNumber());
            pinCode.setText(DbLoadData.addressesSelectModelList.get(selected).getPinCode());

            String state = DbLoadData.addressesSelectModelList.get(selected).getState();
            String city = DbLoadData.addressesSelectModelList.get(selected).getCity();
            String house = DbLoadData.addressesSelectModelList.get(selected).getHouse();
            String roadAreaColony = DbLoadData.addressesSelectModelList.get(selected).getRoadAreaColony();

            fullAddresses.setText(house + ", " + roadAreaColony + ", " + city + ", " + state);
        }
    }


    // order uid
    public String randomCode() {
        UUID uuid = UUID.randomUUID();
        long lo = uuid.getLeastSignificantBits();
        long hi = uuid.getMostSignificantBits();
        long uniqueNum = Math.abs(hi ^ lo);

        String s = String.valueOf(uniqueNum);
        if (s.length() >= 12) {
            return s.substring(s.length() - 12);
        } else {
            return String.format("%012d", uniqueNum);
        }
    }
    // order uid

    private void saveUserOrderHistory(String paymentId) {
        Map<String, Object> userOrder = new HashMap<>();
        userOrder.put("orderID", order_ids);
        userOrder.put("dateTime", FieldValue.serverTimestamp());

        firebaseFirestore.collection("USER").document(FirebaseAuth.getInstance().getUid())
                .collection("USER_ORDERS").document(order_ids).set(userOrder);
        // Sirf data save karenge, UI yahan se change nahi karenge
    }


}


