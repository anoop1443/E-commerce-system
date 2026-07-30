package com.example.homeelecation.ui.place;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;

import com.example.homeelecation.HomeActivity2;
import com.example.homeelecation.R;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.functions.FirebaseFunctions;
import com.razorpay.Checkout;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PaymentActivity extends AppCompatActivity implements PaymentResultWithDataListener {

    private Toolbar toolbar;
    private RadioButton codRadio, onlineRadio;
    private Button payNowBtn, shopNowBtn;
    private TextView totalAmountText, confirmationOrderIdText;
    private Dialog loadingDialog;
    private ConstraintLayout selectionLayout, successLayout;

    private String orderId, amount, phone, email;
    private PlaceViewModel placeViewModel;
    private FirebaseFunctions mFunctions;
    public String razorpayKey = "rzp_test_u6RLB7UD85ABD8";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        // Get data from intent
        orderId = getIntent().getStringExtra("ORDER_ID");
        amount = getIntent().getStringExtra("AMOUNT");
        phone = getIntent().getStringExtra("PHONE");
        email = getIntent().getStringExtra("EMAIL");

        initViews();
        setupListeners();
        
        placeViewModel = new ViewModelProvider(this).get(PlaceViewModel.class);
        mFunctions = FirebaseFunctions.getInstance();
        
        setupObservers();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        selectionLayout = findViewById(R.id.payment_selection_layout);
        successLayout = findViewById(R.id.order_confirmed_layout_constraintlayout);
        
        codRadio = findViewById(R.id.cod_radio);
        onlineRadio = findViewById(R.id.online_radio);
        payNowBtn = findViewById(R.id.pay_now_btn);
        shopNowBtn = findViewById(R.id.order_confirmed_layout_shop_button);
        totalAmountText = findViewById(R.id.payment_total_amount);
        confirmationOrderIdText = findViewById(R.id.order_confirmed_layout_order_id_textview);

        totalAmountText.setText("Total: Rs. " + amount);

        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void setupListeners() {
        findViewById(R.id.cod_card).setOnClickListener(v -> {
            codRadio.setChecked(true);
            onlineRadio.setChecked(false);
        });

        findViewById(R.id.online_card).setOnClickListener(v -> {
            codRadio.setChecked(false);
            onlineRadio.setChecked(true);
        });

        payNowBtn.setOnClickListener(v -> {
            if (codRadio.isChecked()) {
                processCOD();
            } else if (onlineRadio.isChecked()) {
                createRazorpayOrder();
            } else {
                Toast.makeText(this, "Please select a payment method", Toast.LENGTH_SHORT).show();
            }
        });

        if (shopNowBtn != null) {
            shopNowBtn.setOnClickListener(v -> {
                Intent intent = new Intent(this, HomeActivity2.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        }
    }

    private void setupObservers() {
        placeViewModel.paymentUpdateSuccess.observe(this, success -> {
            if (success) {
                loadingDialog.dismiss();
                showSuccessLayout();
            }
        });
    }

    private void showSuccessLayout() {
        if (selectionLayout != null) selectionLayout.setVisibility(View.GONE);
        if (successLayout != null) successLayout.setVisibility(View.VISIBLE);
        if (confirmationOrderIdText != null) confirmationOrderIdText.setText("Order ID: " + orderId);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setTitle("Order Confirmed");
        }
    }

    private void processCOD() {
        loadingDialog.show();
        Map<String, Object> historyData = new HashMap<>();
        historyData.put("orderID", orderId);
        historyData.put("paymentMethod", "COD");
        historyData.put("dateTime", FieldValue.serverTimestamp());
        
        placeViewModel.updatePaymentSuccess(orderId, "COD_" + System.currentTimeMillis(), "Cash On Delivery", historyData);
    }

    private void createRazorpayOrder() {
        loadingDialog.show();
        long amountInPaisa = Long.parseLong(amount)*100;
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("amount", amountInPaisa);

            mFunctions.getHttpsCallable("createRazorpayOrder")
                    .call(data)
                    .addOnSuccessListener(result -> {
                        Map<String, Object> res = (Map<String, Object>) result.getData();
                        String rzpOrderId = (String) res.get("id");
                        startRazorpayPayment(rzpOrderId);
                    })
                    .addOnFailureListener(e -> {
                        loadingDialog.dismiss();
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } catch (NumberFormatException e) {
            loadingDialog.dismiss();
            Toast.makeText(this, "Invalid amount format", Toast.LENGTH_SHORT).show();
        }
    }

    private void startRazorpayPayment(String rzpOrderId) {
        Checkout checkout = new Checkout();
        checkout.setKeyID(razorpayKey);
        try {
            JSONObject options = new JSONObject();
            options.put("name", "Avatar Election");
            options.put("order_id", rzpOrderId);
            options.put("currency", "INR");
            options.put("amount", Long.parseLong(amount) * 100);
            options.put("prefill.email", email);
            options.put("prefill.contact", phone);
            checkout.open(this, options);
            loadingDialog.dismiss();
        } catch (Exception e) {
            loadingDialog.dismiss();
            Log.e("Razorpay", "Error", e);
        }
    }

    @Override
    public void onPaymentSuccess(String s, PaymentData paymentData) {
        loadingDialog.show();
        
        // verifySignature logic from original activity
        Map<String, Object> data = new HashMap<>();
        data.put("order_id", paymentData.getOrderId());
        data.put("payment_id", s);
        data.put("signature", paymentData.getSignature());

        mFunctions.getHttpsCallable("verifySignature")
                .call(data)
                .addOnSuccessListener(result -> {
                    Map<String, Object> res = (Map<String, Object>) result.getData();
                    String method = "Online";
                    if (res != null && res.get("method") != null) {
                        method = (String) res.get("method");
                    }

                    Map<String, Object> historyData = new HashMap<>();
                    historyData.put("orderID", orderId);
                    historyData.put("paymentMethod", method);
                    historyData.put("dateTime", FieldValue.serverTimestamp());
                    loadingDialog.dismiss();
                    
                    placeViewModel.updatePaymentSuccess(orderId, s, method, historyData);
                })
                .addOnFailureListener(e -> {
                    loadingDialog.dismiss();
                    Toast.makeText(this, "Verification Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {
        loadingDialog.dismiss();
        Toast.makeText(this, "Payment Failed: " + s, Toast.LENGTH_LONG).show();
        placeViewModel.updatePaymentFailure(orderId,s,FieldValue.serverTimestamp());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (successLayout != null && successLayout.getVisibility() == View.VISIBLE) {
            Intent intent = new Intent(this, HomeActivity2.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            super.onBackPressed();
        }
    }
}
