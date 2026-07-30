package com.example.homeelecation.ui.quickOrder;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.homeelecation.R;
import com.example.homeelecation.ui.DbLoadData;
import com.example.homeelecation.ui.address.AddressViewModel;
import com.example.homeelecation.ui.address.AddressesSelectModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class QuickOrderActivity extends AppCompatActivity {

    @Inject
    FirebaseAuth mAuth;
    @Inject
    FirebaseFirestore firestore;

    private TextView serviceNameText, servicePriceText, userNameText, userPhoneText, userAddressText, changeAddressText;
    private TextView rulesText, successOrderIdText;
    private CheckBox agreeCheckbox;
    private Button confirmBtn, doneBtn;
    private ConstraintLayout loadingOverlay, successOverlay;
    private ImageView successImage, quickServiceImage;

    private String  documentID,sName, sPrice, sRules, sImage;
    private String finalFullAddress = "No Address Selected";
    private AddressViewModel addressViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_order);

        // UI Initialization
        serviceNameText = findViewById(R.id.quick_service_name);
        servicePriceText = findViewById(R.id.quick_service_price);
        quickServiceImage = findViewById(R.id.quick_service_image);
        userNameText = findViewById(R.id.user_name_text);
        userPhoneText = findViewById(R.id.user_phone_text);
        userAddressText = findViewById(R.id.user_address_text);
        changeAddressText = findViewById(R.id.change_address_text);
        confirmBtn = findViewById(R.id.btn_confirm_quick_order);
        
        rulesText = findViewById(R.id.service_rules_text);
        agreeCheckbox = findViewById(R.id.check_agree_rules);
        loadingOverlay = findViewById(R.id.loading_overlay);
        successOverlay = findViewById(R.id.success_overlay);
        successImage = findViewById(R.id.success_image);
        successOrderIdText = findViewById(R.id.order_id_success);
        doneBtn = findViewById(R.id.btn_success_done);

        addressViewModel = new ViewModelProvider(this).get(AddressViewModel.class);
        observeAddress();

        // Get Data from Intent
        documentID = getIntent().getStringExtra("SERVICE_ID");
        sName = getIntent().getStringExtra("SERVICE_NAME");
        sPrice = getIntent().getStringExtra("SERVICE_PRICE");
        sRules = getIntent().getStringExtra("SERVICE_RULES");
        sImage = getIntent().getStringExtra("SERVICE_IMAGE");

        String rules = sRules != null ? sRules.replace("\\n","\n") : "";

        // Set Data to UI
        serviceNameText.setText(sName);
        servicePriceText.setText("₹" + sPrice);
        
        if (sImage != null && !sImage.isEmpty()) {
            Glide.with(this)
                    .load(sImage)
                    .placeholder(R.drawable.tebal_fan)
                    .into(quickServiceImage);
        }
        if (sRules != null && !sRules.isEmpty()) {
            rulesText.setText(rules);
        }

        userNameText.setText("Name: " + DbLoadData.fullName);
        userPhoneText.setText("Mobile: " + DbLoadData.mobile);

        // Agreement Logic
        confirmBtn.setEnabled(false);
        confirmBtn.setAlpha(0.5f);
        agreeCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            confirmBtn.setEnabled(isChecked);
            confirmBtn.setAlpha(isChecked ? 1.0f : 0.5f);
        });

        changeAddressText.setOnClickListener(v -> {
            Intent intent = new Intent(QuickOrderActivity.this, com.example.homeelecation.ui.address.Select_Address_Activity3.class);
            intent.putExtra("MODE", 0);
            startActivity(intent);
        });

        confirmBtn.setOnClickListener(v -> {
            if (finalFullAddress.equals("No Address Selected")) {
                Toast.makeText(this, "Please select an address first!", Toast.LENGTH_SHORT).show();
            } else {
                orderSave();
            }
        });

        doneBtn.setOnClickListener(v -> finish());
    }

    private void observeAddress() {
        addressViewModel.addresses.observe(this, list -> {
            if (list != null && !list.isEmpty()) {
                int index = DbLoadData.selectedAddresses;
                if (index < 0 || index >= list.size()) index = 0;

                AddressesSelectModel model = list.get(index);
                finalFullAddress = model.getHouse() + ", " +
                        model.getRoadAreaColony() + ", " +
                        model.getCity() + " - " +
                        model.getPinCode();

                userAddressText.setText("Address: " + finalFullAddress);
            } else {
                userAddressText.setText("Address: No address found!");
                finalFullAddress = "No Address Selected";
            }
        });
    }

    private void orderSave() {
        loadingOverlay.setVisibility(View.VISIBLE);
        confirmBtn.setEnabled(false);

        String currentUserID = mAuth.getUid();
        String orderID = java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        java.util.Map<String, Object> orderData = new java.util.HashMap<>();
        orderData.put("productId", documentID);
        orderData.put("orderId", orderID);
        orderData.put("serviceName", sName);
        orderData.put("serviceImage", sImage);
        orderData.put("price", sPrice);
        orderData.put("userId", currentUserID);
        orderData.put("userName", DbLoadData.fullName);
        orderData.put("userMobile", DbLoadData.mobile);
        orderData.put("userAddress", finalFullAddress);
        orderData.put("orderStatus", "Ordered");
        orderData.put("paymentStatus", "COD/Pending");
        orderData.put("dateTime", FieldValue.serverTimestamp());

        firestore.collection("ORDERS_QUICK")
                .document(orderID)
                .set(orderData)
                .addOnSuccessListener(aVoid -> {
                    loadingOverlay.setVisibility(View.GONE);
                    showSuccessDialog(orderID);
                })
                .addOnFailureListener(e -> {
                    loadingOverlay.setVisibility(View.GONE);
                    confirmBtn.setEnabled(true);
                    Toast.makeText(QuickOrderActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void showSuccessDialog(String orderId) {
        successOrderIdText.setText("Order ID: #" + orderId);
        successOverlay.setVisibility(View.VISIBLE);

        // Simple Animation
        ScaleAnimation animation = new ScaleAnimation(0f, 1f, 0f, 1f, 
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(500);
        successImage.startAnimation(animation);
    }

    @Override
    protected void onResume() {
        super.onResume();
        addressViewModel.loadAddresses(false, false);
    }
}
