package com.example.homeelecation.ui.orders;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.example.homeelecation.R;
import com.example.homeelecation.ui.support.MyTicketsActivity;
import com.example.homeelecation.util.EdgeToEdgeUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class QuickOrderDetailsActivity extends AppCompatActivity {

    private TextView orderIdTv, orderDate, serviceName, servicePrice;
    private TextView userName, userPhone, userAddress;
    private ImageView indicatorOrdered, indicatorAssigned, indicatorCompleted, serviceImage;
    private View connector1, connector2;
    private TextView textOrdered, textAssigned, textCompleted;

    // Refund Views
    private CardView refundCard;
    private TextView refundId, refundStatusText, refundEstimate;
    private ProgressBar refundProgress;

    // Bill Details
    private CardView billCard;
    private TextView billTitle;
    private TextView billVisiting, billTotal, quickOtpTextView;
    private Button btnViewBill, btnNeedHelp, btnCancelOrder, btnViewTickets;
    
    private Dialog cancelDialog, loadingDialog;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm a", Locale.getDefault());

    @Inject
    FirebaseFirestore db;

    @Inject
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_order_details);

        EdgeToEdge.enable(this);
        EdgeToEdgeUtils.applyTopInset(findViewById(R.id.app_bar_quick_details));


        Toolbar toolbar = findViewById(R.id.toolbar_quick_details);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        // Initialize Views
        orderIdTv = findViewById(R.id.detail_order_id);
        orderDate = findViewById(R.id.detail_order_date);
        serviceName = findViewById(R.id.detail_service_name);
        servicePrice = findViewById(R.id.detail_service_price);
        userName = findViewById(R.id.detail_user_name);
        userPhone = findViewById(R.id.detail_user_phone);
        userAddress = findViewById(R.id.detail_user_address);
        serviceImage = findViewById(R.id.detail_service_image);

        indicatorOrdered = findViewById(R.id.indicator_ordered);
        indicatorAssigned = findViewById(R.id.indicator_assigned);
        indicatorCompleted = findViewById(R.id.indicator_completed);
        
        connector1 = findViewById(R.id.connector_1);
        connector2 = findViewById(R.id.connector_2);
        
        textOrdered = findViewById(R.id.text_ordered);
        textAssigned = findViewById(R.id.text_assigned);
        textCompleted = findViewById(R.id.text_completed);
        
        textAssigned.setText("Technician Assigned");
        textCompleted.setText("Work Completed");

        // Refund Views
        refundCard = findViewById(R.id.refund_card);
        refundId = findViewById(R.id.refund_id);
        refundStatusText = findViewById(R.id.refund_status_text);
        refundEstimate = findViewById(R.id.refund_estimate);
        refundProgress = findViewById(R.id.refund_progress);

        // Bill Views
        billCard = findViewById(R.id.bill_details_card);
        billTitle = findViewById(R.id.bill_details_title);
        billVisiting = findViewById(R.id.bill_visiting_charges);
        billTotal = findViewById(R.id.bill_total_amount);
        quickOtpTextView = findViewById(R.id.quick_otp_text_view);
        
        btnViewBill = findViewById(R.id.btn_view_bill);
        btnNeedHelp = findViewById(R.id.btn_quick_help);
        btnCancelOrder = findViewById(R.id.btn_quick_cancel);
        btnViewTickets = findViewById(R.id.btn_view_tickets);

        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        if (loadingDialog.getWindow() != null) {
            loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        cancelDialog = new Dialog(this);
        cancelDialog.setContentView(R.layout.order_cancel_dailog_layout);

        String oId = getIntent().getStringExtra("ORDER_ID");
        if (oId != null) {
            loadOrderDetails(oId);
        }

        btnNeedHelp.setOnClickListener(v -> showSupportDialog(oId));
        btnCancelOrder.setOnClickListener(v -> showCancelConfirmationDialog(oId));
        btnViewTickets.setOnClickListener(v -> startActivity(new Intent(this, MyTicketsActivity.class)));
    }

    private void showCancelConfirmationDialog(String orderId) {
        android.widget.Spinner reasonSpinner = cancelDialog.findViewById(R.id.cancel_reason_spinner);
        String[] reasons = {
                "Select Reason",
                "Ordered by mistake",
                "Found a better price",
                "Delivery time is too long",
                "Changed my mind",
                "Technician issue",
                "Other"
        };
        android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<>(this, android.R.layout.simple_spinner_item, reasons);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        reasonSpinner.setAdapter(adapter);

        cancelDialog.show();
        cancelDialog.findViewById(R.id.order_cancel_dailog_noBtn).setOnClickListener(v -> cancelDialog.dismiss());
        cancelDialog.findViewById(R.id.order_cancel_dailog_yesBtn).setOnClickListener(v -> {
            String selectedReason = reasonSpinner.getSelectedItem().toString();
            if (selectedReason.equals("Select Reason")) {
                Toast.makeText(this, "Please select a reason for cancellation", Toast.LENGTH_SHORT).show();
            } else {
                loadingDialog.show();
                processQuickOrderCancellation(orderId, selectedReason);
            }
        });
    }

    private void processQuickOrderCancellation(String orderId, String reason) {
        Map<String, Object> map = new HashMap<>();
        map.put("orderID", orderId);
        map.put("reason", reason);
        map.put("type", "QUICK_ORDER");
        map.put("cancelledDate", FieldValue.serverTimestamp());

        db.collection("CANCELLED ORDERS").add(map).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Map<String, Object> update = new HashMap<>();
                update.put("orderStatus", "Cancelled");
                update.put("cancelReason", reason);
                db.collection("ORDERS_QUICK").document(orderId)
                        .update(update).addOnCompleteListener(uTask -> {
                            loadingDialog.dismiss();
                            cancelDialog.dismiss();
                            if (uTask.isSuccessful()) {
                                Toast.makeText(this, "Order Cancelled Successfully", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                loadingDialog.dismiss();
                Toast.makeText(this, "Error: " + (task.getException() != null ? task.getException().getMessage() : "Unknown"), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showSupportDialog(String orderId) {
        if (orderId == null) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("How can we help you?");
        
        final android.widget.EditText input = new android.widget.EditText(this);
        input.setHint("Describe your issue here...");
        android.widget.LinearLayout.LayoutParams lp = new android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(40, 0, 40, 0);
        input.setLayoutParams(lp);
        builder.setView(input);

        builder.setPositiveButton("Submit Ticket", (dialog, which) -> {
            String issue = input.getText().toString().trim();
            if (!issue.isEmpty()) {
                submitSupportTicket(orderId, issue);
            } else {
                Toast.makeText(this, "Please describe the issue", Toast.LENGTH_SHORT).show();
            }
        });
        
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void submitSupportTicket(String orderId, String issue) {
        String ticketId = "TKT" + (int) (Math.random() * 900000 + 100000);
        
        java.util.Map<String, Object> ticket = new java.util.HashMap<>();
        ticket.put("ticketId", ticketId);
        ticket.put("orderId", orderId);
        ticket.put("issue_description", issue);
        ticket.put("status", "OPEN");
        ticket.put("userId", auth.getUid());
        ticket.put("userName", userName.getText().toString());
        ticket.put("createdDate", com.google.firebase.firestore.FieldValue.serverTimestamp());

        db.collection("SUPPORT_TICKETS").document(ticketId)
                .set(ticket)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Ticket #" + ticketId + " submitted! You can check status in 'My Tickets'.", Toast.LENGTH_LONG).show();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void loadOrderDetails(String oId) {
        db.collection("ORDERS_QUICK").document(oId)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (value != null && value.exists()) {
                        updateUI(value);
                    }
                });
    }

    private void updateUI(DocumentSnapshot doc) {
        orderIdTv.setText("Order ID: #" + doc.getString("orderId"));
        serviceName.setText(doc.getString("serviceName"));
        servicePrice.setText("Total Amount: ₹" + doc.getString("price"));
        userName.setText(doc.getString("userName"));
        userPhone.setText(doc.getString("userMobile"));
        userAddress.setText(doc.getString("userAddress"));

        String sImage = doc.getString("serviceImage");
        if (sImage != null && !sImage.isEmpty()) {
            Glide.with(this)
                    .load(sImage)
                    .placeholder(R.drawable.tebal_fan)
                    .into(serviceImage);
        }

        if (doc.getDate("dateTime") != null) {
            orderDate.setText("Date: " + dateFormat.format(doc.getDate("dateTime")));
        }

        String status = doc.getString("orderStatus");
        setStatusTimeline(status);

        if (doc.contains("otp") && ("Assigned".equalsIgnoreCase(status) || "Out for Service".equalsIgnoreCase(status))) {
            quickOtpTextView.setVisibility(View.VISIBLE);
            quickOtpTextView.setText("Service OTP: " + doc.getLong("otp"));
        } else {
            quickOtpTextView.setVisibility(View.GONE);
        }

        if ("Ordered".equalsIgnoreCase(status)) {
            btnCancelOrder.setVisibility(View.VISIBLE);
        } else {
            btnCancelOrder.setVisibility(View.GONE);
        }

        // Handle Refund & Canceled UI
        handleCancelledAndRefundUI(doc);

        // Handle Bill Details
        handleBillUI(doc);
    }

    private void handleCancelledAndRefundUI(DocumentSnapshot doc) {
        String status = doc.getString("orderStatus");
        String pStatus = doc.getString("paymentStatus");

        if ("Cancelled".equalsIgnoreCase(status)) {
            if (pStatus != null && !pStatus.equalsIgnoreCase("COD")) {
                refundCard.setVisibility(View.VISIBLE);
                
                String rId = doc.getString("refundId");
                String rStatus = doc.getString("refundStatus");
                
                if (rId != null) refundId.setText("Refund ID: #" + rId);
                
                if (rStatus != null) {
                    refundStatusText.setText("Refund " + rStatus);
                    if ("Initiated".equalsIgnoreCase(rStatus)) {
                        refundProgress.setProgress(30);
                        refundEstimate.setText("Estimated: 5-7 Working Days");
                    } else if ("Processed".equalsIgnoreCase(rStatus)) {
                        refundProgress.setProgress(60);
                        refundEstimate.setText("Estimated: 2-3 Working Days");
                    } else if ("Completed".equalsIgnoreCase(rStatus)) {
                        refundProgress.setProgress(100);
                        refundProgress.setProgressTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));
                        refundStatusText.setTextColor(Color.parseColor("#4CAF50"));
                        refundEstimate.setText("Refund credited to your original source.");
                    }
                } else {
                    refundStatusText.setText("Refund Initiated");
                    refundProgress.setProgress(20);
                }
            } else {
                refundCard.setVisibility(View.GONE);
            }
        } else {
            refundCard.setVisibility(View.GONE);
        }
    }

    private void handleBillUI(DocumentSnapshot doc) {
        if (doc.contains("finalTotal")) {
            billCard.setVisibility(View.VISIBLE);
            billTitle.setVisibility(View.VISIBLE);

            String vCharge = String.valueOf(doc.get("price"));
            String fTotal = String.valueOf(doc.get("finalTotal"));

            billVisiting.setText("₹" + vCharge);
            billTotal.setText("₹" + fTotal);

            btnViewBill.setOnClickListener(v -> showProfessionalInvoice(doc));
        } else {
            billCard.setVisibility(View.GONE);
            billTitle.setVisibility(View.GONE);
        }
    }

    private void showProfessionalInvoice(DocumentSnapshot doc) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.layout_invoice_pdf, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        // Initialize Invoice Views
        TextView invUserName = dialogView.findViewById(R.id.inv_user_name);
        TextView invUserPhone = dialogView.findViewById(R.id.inv_user_phone);
        TextView invUserAddress = dialogView.findViewById(R.id.inv_user_address);
        TextView invOrderId = dialogView.findViewById(R.id.inv_order_id);
        TextView invDate = dialogView.findViewById(R.id.inv_date);
        TextView invSubtotal = dialogView.findViewById(R.id.inv_subtotal);
        TextView invGrandTotal = dialogView.findViewById(R.id.inv_grand_total);
        TextView invPayment = dialogView.findViewById(R.id.inv_payment);
        LinearLayout itemsContainer = dialogView.findViewById(R.id.invoice_items_container);

        // Set Header Data
        invUserName.setText(doc.getString("userName"));
        invUserPhone.setText(doc.getString("userMobile"));
        invUserAddress.setText(doc.getString("userAddress"));
        invOrderId.setText("Order ID: #" + doc.getString("orderId"));
        if (doc.getDate("dateTime") != null) {
            invDate.setText("Date: " + dateFormat.format(doc.getDate("dateTime")));
        }
        invPayment.setText("Payment: " + (doc.getString("paymentStatus") != null ? doc.getString("paymentStatus") : "N/A"));

        // 1. Add Main Service Row
        addInvoiceRow(itemsContainer, doc.getString("serviceName"), "1", String.valueOf(doc.get("price")));
        
        // 2. Add Dynamic Items from Array (billItems)
        List<Map<String, Object>> billItems = (List<Map<String, Object>>) doc.get("billItems");
        if (billItems != null) {
            for (Map<String, Object> item : billItems) {
                String desc = String.valueOf(item.get("desc"));
                String qty = String.valueOf(item.get("qty"));
                String amount = String.valueOf(item.get("amount"));
                addInvoiceRow(itemsContainer, desc, qty, amount);
            }
        }

        // Totals
        String finalTotalStr = "₹" + doc.get("finalTotal");
        invSubtotal.setText(finalTotalStr);
        invGrandTotal.setText(finalTotalStr);

        dialogView.findViewById(R.id.invoice_container).setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void addInvoiceRow(LinearLayout container, String desc, String qty, String price) {
        View row = LayoutInflater.from(this).inflate(R.layout.invoice_item_row, null);
        TextView tDesc = row.findViewById(R.id.row_desc);
        TextView tQty = row.findViewById(R.id.row_qty);
        TextView tPrice = row.findViewById(R.id.row_price);

        tDesc.setText(desc);
        tQty.setText(qty);
        tPrice.setText("₹" + price);

        container.addView(row);
    }

    private void setStatusTimeline(String status) {
        int gray = Color.parseColor("#DDDDDD");
        int green = Color.parseColor("#4CAF50");
        int red = Color.parseColor("#F44336");
        int black = Color.parseColor("#000000");
        int textGray = Color.parseColor("#888888");

        indicatorOrdered.setImageTintList(ColorStateList.valueOf(gray));
        indicatorAssigned.setImageTintList(ColorStateList.valueOf(gray));
        indicatorCompleted.setImageTintList(ColorStateList.valueOf(gray));
        connector1.setBackgroundColor(gray);
        connector2.setBackgroundColor(gray);
        textOrdered.setTextColor(textGray);
        textAssigned.setTextColor(textGray);
        textCompleted.setTextColor(textGray);

        if (status == null) return;
        
        if ("Cancelled".equalsIgnoreCase(status)) {
            indicatorOrdered.setImageTintList(ColorStateList.valueOf(red));
            textOrdered.setText("Order Cancelled");
            textOrdered.setTextColor(red);
            indicatorAssigned.setVisibility(View.GONE);
            indicatorCompleted.setVisibility(View.GONE);
            connector1.setVisibility(View.GONE);
            connector2.setVisibility(View.GONE);
            textAssigned.setVisibility(View.GONE);
            textCompleted.setVisibility(View.GONE);
        } else if ("Ordered".equalsIgnoreCase(status)) {
            indicatorOrdered.setImageTintList(ColorStateList.valueOf(green));
            textOrdered.setTextColor(black);
        } else if ("Assigned".equalsIgnoreCase(status)) {
            indicatorOrdered.setImageTintList(ColorStateList.valueOf(green));
            indicatorAssigned.setImageTintList(ColorStateList.valueOf(green));
            connector1.setBackgroundColor(green);
            textOrdered.setTextColor(green);
            textAssigned.setTextColor(black);
        } else if ("Completed".equalsIgnoreCase(status) || "Delivered".equalsIgnoreCase(status) ) {
            indicatorOrdered.setImageTintList(ColorStateList.valueOf(green));
            indicatorAssigned.setImageTintList(ColorStateList.valueOf(green));
            indicatorCompleted.setImageTintList(ColorStateList.valueOf(green));
            connector1.setBackgroundColor(green);
            connector2.setBackgroundColor(green);
            textOrdered.setTextColor(green);
            textAssigned.setTextColor(green);
            textCompleted.setTextColor(green);
        }
    }
}
