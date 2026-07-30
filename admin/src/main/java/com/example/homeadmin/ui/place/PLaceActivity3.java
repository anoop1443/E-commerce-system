package com.example.homeadmin.ui.place;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homeadmin.HomeActivity2;
import com.example.homeadmin.R;
import com.example.homeadmin.ui.Cart.CartAdapter;
import com.example.homeadmin.ui.Cart.CartItemModel;
import com.example.homeadmin.ui.DbLoadData;
import com.example.homeadmin.ui.address.Select_Address_Activity3;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;


public class PLaceActivity3 extends AppCompatActivity implements PaymentResultListener {

    private boolean onBackActivity = false;
    private Toolbar toolbar;
    private Dialog loadingDialog;
    private RecyclerView recyclerView;

    private ConstraintLayout constraintLayoutPlace, constraintLayoutOrder;
    private Button changeAddresses, continueBe;
    private TextView fullName, fullAddresses, pinCode, mobile, totalAmount;
    public static final int SELECT_ADDRESS = 0;

    private String paymentMethod = "online";
    public static List<CartItemModel> cartItemModelList = new ArrayList<>();

    private Dialog paymentDialog;

    private FirebaseFirestore firebaseFirestore;
    String order_ids;

    // order confirmation layout
    private TextView confirmationText, confirmationOrderIdText, expectedDeliveryText;
    private Button confirmationContinueShopingBtn;
    private ImageView imageChek;
    // order confirmation layout


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place3);
        // setContentView(R.layout.order_confirmed_layout);

        //networking
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        //networking


        firebaseFirestore = FirebaseFirestore.getInstance();


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Place order");


        //loading dialog
        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(true);

        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        // loadingDialog.show();
        //loading dialog


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

        // order confirmation layout
        confirmationText = findViewById(R.id.order_confirmed_layout_confirmed_textview);
        confirmationOrderIdText = findViewById(R.id.order_confirmed_layout_order_id_textview);
        expectedDeliveryText = findViewById(R.id.order_confirmed_layout_expected_delivery_textview);
        confirmationContinueShopingBtn = findViewById(R.id.order_confirmed_layout_shop_button);
        imageChek = findViewById(R.id.order_confirmed_layout_check_image);
        // order confirmation layout

        confirmationContinueShopingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentShop = new Intent(PLaceActivity3.this,HomeActivity2.class);
                intentShop.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intentShop);
                finish();
            }
        });


        //order uid

        Calendar calendar = Calendar.getInstance();
        // Get the display name for the month in the current locale.
        String monthName = calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault());
        String month = monthName.toUpperCase();
        // Print the month name.
        order_ids = month + 0 + randomCode();

        //order uid



        fullName.setText(DbLoadData.addressesSelectModelList.get(DbLoadData.selectedAddresses).getFullName());
        mobile.setText(DbLoadData.addressesSelectModelList.get(DbLoadData.selectedAddresses).getMobileNumber());

        String pinCode = DbLoadData.addressesSelectModelList.get(DbLoadData.selectedAddresses).getPinCode();
        String state = DbLoadData.addressesSelectModelList.get(DbLoadData.selectedAddresses).getState();
        String city = DbLoadData.addressesSelectModelList.get(DbLoadData.selectedAddresses).getCity();
        String house = DbLoadData.addressesSelectModelList.get(DbLoadData.selectedAddresses).getHouse();
        String roadAreaColony = DbLoadData.addressesSelectModelList.get(DbLoadData.selectedAddresses).getRoadAreaColony();
        fullAddresses.setText(house +" "+ roadAreaColony +" "+ city +" "+ state +" "+ pinCode);

        // payment dialog
        paymentDialog = new Dialog(PLaceActivity3.this);
        paymentDialog.setContentView(R.layout.paymentdailog);
        paymentDialog.setCancelable(true);

        paymentDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        ImageButton dialogCashBtn = paymentDialog.findViewById(R.id.cashPayment);
        ImageButton dialogCardBtn = paymentDialog.findViewById(R.id.cartPayment);

        dialogCashBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                starCOD();
            }
        });
        // payment dialog


        LinearLayoutManager layoutManager = new LinearLayoutManager(PLaceActivity3.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);


        changeAddresses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PLaceActivity3.this, Select_Address_Activity3.class);
                intent.putExtra("MODE", SELECT_ADDRESS);
                startActivity(intent);
            }
        });


        CartAdapter adapter = new CartAdapter(cartItemModelList,totalAmount, false);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

         long amount = Long.parseLong(totalAmount.getText().toString().substring(3));
       // totalAmount.getText().toString().substring(3)

        String phone = DbLoadData.addressesSelectModelList.get(DbLoadData.selectedAddresses).getMobileNumber();
        String email = "exmtf.hf1423@gmail.com";

        continueBe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String orderId = "";

//                try {
//
//                    RazorpayClient instance = new RazorpayClient("rzp_test_u6RLB7UD85ABD8","G8m4hCu0LecEf9ArlPLmmt4X");
//
//                    JSONObject orderRequest = new JSONObject();
//
//                    orderRequest.put("amount", totalAmount.getText().toString().substring(3)+"00");
//                    orderRequest.put("currency", "INR");
//                    orderRequest.put("receipt", "receipt#1");
//                    JSONObject notes = new JSONObject();
//                    notes.put("notes_key_1", "Tea, Earl Grey, Hot");
//                    notes.put("notes_key_1", "Tea, Earl Grey, Hot");
//                    orderRequest.put("notes", notes);
//
//                    Order order = instance.orders.create(orderRequest);
//                    orderId = order.get("id");
//
//                    if (!orderId.isEmpty()){
//                        placeOrderDetails(orderId,phone,email);
//                        //startPayment(orderId,phone,"anooo.hghfgg1322@gmail.com");
//                    }else {
//                        Toast.makeText(PLaceActivity3.this, " error ematy", Toast.LENGTH_LONG).show();
//                    }
//
//                } catch (JSONException ignored) {
//
//
//                } catch (RazorpayException e) {
//                    throw new RuntimeException(e);
//                }



                Toast.makeText(PLaceActivity3.this, phone, Toast.LENGTH_LONG).show();
            }
        });


        dialogCardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Toast.makeText(PLaceActivity3.this, "Please use Card", Toast.LENGTH_SHORT).show();


            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.men_search) {
            // search code w
            item.setVisible(false);

            Toast.makeText(this, "Ok", Toast.LENGTH_SHORT).show();

            return true;


        } else if (id == R.id.menu_add) {
            //cart code w
            Toast.makeText(this, "please wait ", Toast.LENGTH_SHORT).show();
            item.setVisible(false);

            return true;
        } else if (id == android.R.id.home) {
            finish();
            return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        fullName.setText(DbLoadData.addressesSelectModelList.get(DbLoadData.selectedAddresses).getFullName());
        mobile.setText(DbLoadData.addressesSelectModelList.get(DbLoadData.selectedAddresses).getMobileNumber());
        pinCode.setText(DbLoadData.addressesSelectModelList.get(DbLoadData.selectedAddresses).getPinCode());

        String pinCode = DbLoadData.addressesSelectModelList.get(DbLoadData.selectedAddresses).getPinCode();
        String state = DbLoadData.addressesSelectModelList.get(DbLoadData.selectedAddresses).getState();
        String city = DbLoadData.addressesSelectModelList.get(DbLoadData.selectedAddresses).getCity();
        String house = DbLoadData.addressesSelectModelList.get(DbLoadData.selectedAddresses).getHouse();
        String roadAreaColony = DbLoadData.addressesSelectModelList.get(DbLoadData.selectedAddresses).getRoadAreaColony();
        fullAddresses.setText(house +" "+roadAreaColony+" "+ city +" "+ state +" ");

    }



    // order uid
    public static String randomCode() {
        UUID uuid = UUID.randomUUID();
        long lo = uuid.getLeastSignificantBits();
        long hi = uuid.getMostSignificantBits();
        lo = (lo >> (64 - 31)) ^ lo;
        hi = (hi >> (64 - 31)) ^ hi;
        String s = String.format("%010d", Math.abs(hi) + Math.abs(lo));
        return s.substring(s.length() - 14);
    }

    // order uid 


//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        if (onBackActivity) {
//            Intent intent = new Intent(PLaceActivity3.this, HomeActivity2.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            startActivity(intent);
//            finish();
//        }
//    }


    public void placeOrderDetails(String id, String phone,String email) {
        loadingDialog.show();

// time attrs
        // --- Option 1: Using Calendar (Older but common) ---
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1); // Add one day
        Date nextDayDate = calendar.getTime();

        Calendar calendar2 = Calendar.getInstance();
        calendar2.add(Calendar.DAY_OF_YEAR,3);
        Date nextDayDate2 = calendar2.getTime();


// time attrs

        String userID = FirebaseAuth.getInstance().getUid();
        for (CartItemModel cartItemModel : cartItemModelList) {

            if (cartItemModel.getType() == cartItemModel.CART_ITEM_LAYOUT) {

                Map<String, Object> orderDetails = new HashMap<>();
                orderDetails.put("orderID", order_ids);
                orderDetails.put("productID", cartItemModel.getProductID());
                orderDetails.put("productImage", cartItemModel.getProductImage());
                orderDetails.put("productTitle",cartItemModel.getProduct_Title());
                orderDetails.put("userID", userID);
                orderDetails.put("productQuantity", cartItemModel.getProductQty());
                if (cartItemModel.getProduct_cut_Price() != null) {
                    orderDetails.put("cutPrice", cartItemModel.getProduct_cut_Price());
                }else {
                    orderDetails.put("cutPrice","null");

                }
                //String price = String.valueOf(Long.parseLong(cartItemModel.getProduct_Price())*cartItemModel.getProductQty());

                orderDetails.put("productPrice",cartItemModel.getProduct_Price());
                orderDetails.put("orderedDate", FieldValue.serverTimestamp());
                orderDetails.put("packedDate", FieldValue.serverTimestamp());
                orderDetails.put("shippedDate",nextDayDate);
                orderDetails.put("deliveredDate", nextDayDate2);
                orderDetails.put("cancelledDate", FieldValue.serverTimestamp());
                orderDetails.put("orderStatus", "Ordered");
                orderDetails.put("paymentMethod", paymentMethod);
                orderDetails.put("deliveryCharge",cartItemModelList.get(cartItemModelList.size() -1).getDeliveryCharges());
                orderDetails.put("fullName", fullName.getText());
                orderDetails.put("address", fullAddresses.getText());
                orderDetails.put("mobile",mobile.getText());
                orderDetails.put("pinCode",pinCode.getText());
                orderDetails.put("cancellationRequested",false);

                firebaseFirestore.collection("ORDERS").document(order_ids).collection("orderItems").document(cartItemModel.getProductID())
                        .set(orderDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (!task.isSuccessful()) {
                                    String error = task.getException().getMessage();
                                    Toast.makeText(PLaceActivity3.this, error, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            } else {

                Map<String, Object> orderDetails = new HashMap<>();
                orderDetails.put("totalItems", cartItemModel.getTotalItem());
                orderDetails.put("totalItemsPrice", cartItemModel.getTotalItemPrise());
                orderDetails.put("totalItemsDiscount", cartItemModel.getTotalItemDiscount());
                orderDetails.put("deliveryCharges", cartItemModel.getDeliveryCharges());
                orderDetails.put("totalAmount", cartItemModel.getTotalAmount());
                orderDetails.put("paymentStatus", "not paid");
                orderDetails.put("globalStatus", "Ordered");

                firebaseFirestore.collection("ORDERS").document(order_ids).set(orderDetails)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (!task.isSuccessful()) {
                                    String error = task.getException().getMessage();
                                    Toast.makeText(PLaceActivity3.this, error, Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
                loadingDialog.dismiss();
            }
        }
        //startPayment(id,phone,email);

    }

    public void startPayment(String id ,String phone,String email) {

       // placeOrderDetails();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_u6RLB7UD85ABD8");

        /**
         * Set your logo here
         */
        //checkout.setImage(R.drawable.rzp_logo);

        /**
         * Reference to current activity
         */
        final Activity activity = PLaceActivity3.this;

        /**
         * Pass your payment options to the Razorpay Checkout as a JSONObject
         */
        try {
            JSONObject options = new JSONObject();

            options.put("name", "Avatar Election");
            options.put("description", "Reference No. #123456");
            options.put("image", "http://example.com/image/rzp.jpg");
            options.put("order_id", id);//from response of step 3.
            options.put("theme.color", "#3399cc");
            options.put("currency", "INR");
            options.put("amount", totalAmount.getText().toString().substring(3)+"00");//pass amount in currency subunits
            options.put("prefill.email", email);
            options.put("prefill.contact",phone);
            JSONObject retryObj = new JSONObject();
            retryObj.put("enabled", true);
            retryObj.put("max_count", 4);
            options.put("retry", retryObj);

            checkout.open(activity, options);
           // activity.finish();
            loadingDialog.dismiss();

        } catch(Exception e) {
            Log.e(TAG, "Error in starting Razorpay Checkout", e);
        }


        // options.put("amount", totalAmount.getText().toString().substring(3)+"00");//pass amount in currency subunits

    }

    public void starCOD() {
        paymentMethod = "Cash On Delivery";
        //placeOrderDetails();

        Map<String, Object> updateStatus = new HashMap<>();
        updateStatus.put("paymentStatus", "COD");
        updateStatus.put("globalStatus", "Ordered");

        firebaseFirestore.collection("ORDERS").document(order_ids).update(updateStatus)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            Map<String,Object> userOrder = new HashMap<>();
                            userOrder.put("order id",order_ids);
                            userOrder.put("time",FieldValue.serverTimestamp());

                            firebaseFirestore.collection("USER").document(FirebaseAuth.getInstance().getUid()).collection("USER_ORDERS").document(order_ids).set(userOrder).
                                    addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                constraintLayoutOrder.setVisibility(View.VISIBLE);
                                                confirmationOrderIdText.setText(order_ids);
                                                constraintLayoutPlace.setVisibility(View.GONE);

                                            }else {
                                                String error = task.getException().getMessage();
                                                Toast.makeText(PLaceActivity3.this, "Order failed"+error, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                            Toast.makeText(PLaceActivity3.this, "Please use Cash on delivery" + order_ids, Toast.LENGTH_SHORT).show();

                        }else {
                            Toast.makeText(PLaceActivity3.this, "order Cancelled", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

        paymentDialog.dismiss();
//        Toast.makeText(this, " payment success ", Toast.LENGTH_SHORT).show();
//        constraintLayoutOrder.setVisibility(View.VISIBLE);
//        constraintLayoutPlace.setVisibility(View.GONE);


    }

    @Override
    public void onPaymentSuccess(String s) {
        paymentDialog.dismiss();
        Toast.makeText(this, " payment success ", Toast.LENGTH_SHORT).show();

        Map<String, Object> updateStatus = new HashMap<>();
        updateStatus.put("paymentStatus", "Paid");
        updateStatus.put("globalStatus", "Ordered");
        updateStatus.put("paymentID",s);
        updateStatus.put("paymentTime",FieldValue.serverTimestamp());


        firebaseFirestore.collection("ORDERS").document(order_ids).update(updateStatus)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {

                            Map<String,Object> userOrder = new HashMap<>();
                            userOrder.put("order id",order_ids);

                            userOrder.put("time",FieldValue.serverTimestamp());

                            firebaseFirestore.collection("USER").document(FirebaseAuth.getInstance().getUid()).collection("USER_ORDERS").document(order_ids).set(userOrder).
                                    addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                constraintLayoutOrder.setVisibility(View.VISIBLE);
                                                confirmationOrderIdText.setText(order_ids+","+s);
                                                constraintLayoutPlace.setVisibility(View.GONE);

                                            }else {
                                                String error = task.getException().getMessage();
                                                Toast.makeText(PLaceActivity3.this, error, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                        }else {
                            Toast.makeText(PLaceActivity3.this, "order Cancelled", Toast.LENGTH_SHORT).show();
                        }

                    }
                });


        // onBackActivity = true;


    }

    @Override
    public void onPaymentError(int i, String s) {

        Map<String, Object> updateStatus = new HashMap<>();
        updateStatus.put("paymentStatus", "failed");
        updateStatus.put("globalStatus", "Payment Failed");
        updateStatus.put("paymentID",s);
        updateStatus.put("paymentTime",FieldValue.serverTimestamp());


        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();

    }

}


