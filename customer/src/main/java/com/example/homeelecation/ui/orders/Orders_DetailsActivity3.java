package com.example.homeelecation.ui.orders;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.homeelecation.R;
import com.example.homeelecation.ui.details.ProductDetailsActivity;
import com.example.homeelecation.util.EdgeToEdgeUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class Orders_DetailsActivity3 extends AppCompatActivity {
    @Inject
    public FirebaseAuth auth;
    
    @Inject
    public FirebaseFirestore db;

    Toolbar toolbar;
    private int position;
    private int rating;
    private LinearLayout rateNowContainer, linearLayoutRating;
    private TextView orderId;
    private TextView productTitle, productPrice, productQty;
    ImageView proImage;
    private ImageView orderedIndicator, packedIndicator, shippedIndicator, deliveredIndicator;
    private ProgressBar o_p_progressBar, p_s_progressBar, s_d_progressBar;
    private TextView orderedTitle, packedTitle, shippedTitle, deliveredTitle;
    private TextView orderedDate, packedDate, shippedDate, deliveredDate;
    private TextView orderedBody, packedBody, shippedBody, deliveredBody;
    private TextView otpTextView;

    private TextView fullNamee, address, mobile;
    private TextView totalItems, totalItemsPrice, discountPrice, serviceCharge, totalAmount, saveAmount;

    private Dialog loadingDialog, cancelDialog;

    private Button cancelOrderBtn, needHelpBtn, viewBillBtn;
    private LinearLayout refundDetailsLayout;
    private TextView refundStatusText;
    private TextView refundAmountText;
    private TextView refundTimelineText;


    private final Executor executor = Executors.newSingleThreadExecutor();
    private boolean running_rating_query = false; 
    private int initialRating = -1; 
    private int newStarPosition = 0; 

    private SimpleDateFormat pdfDateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders_details3);

        EdgeToEdge.enable(this);
        EdgeToEdgeUtils.applyTopInset(findViewById(R.id.appbar));
        EdgeToEdgeUtils.applyBottomInset(findViewById(R.id.Orders_DetailsActivity_constrain));



        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle("Order Details");
        }

        MyOrdersViewModel myOrdersViewModelL = new ViewModelProvider(this).get(MyOrdersViewModel.class);

        position = getIntent().getIntExtra("POSITION", -1);
        String orderID = getIntent().getStringExtra("ORDER_ID");
        String productID = getIntent().getStringExtra("PRODUCT_ID");

        //loading dialog
        loadingDialog = new Dialog(Orders_DetailsActivity3.this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        if (loadingDialog.getWindow() != null) {
            loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        //cancel dialog
        cancelDialog = new Dialog(Orders_DetailsActivity3.this);
        cancelDialog.setCancelable(true);
        cancelDialog.setContentView(R.layout.order_cancel_dailog_layout);
        if (cancelDialog.getWindow() != null) {
            cancelDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        initViews();

        if (orderID != null && productID != null) {
            myOrdersViewModelL.loadSingleProductDetails(orderID, productID);
        }

        myOrdersViewModelL.getSingleProductLiveData().observe(this, model -> {
            if (model != null) {
                updateUIWithModelData(model); 
            } else {
                Toast.makeText(this, "Error loading item details.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        myOrdersViewModelL.getIsLoadingData().observe(this, isLoading -> {
            if (isLoading != null && isLoading) {
                loadingDialog.show();
            } else {
                loadingDialog.dismiss();
            }
        });
    }

    private void initViews() {
        rateNowContainer = findViewById(R.id.rate_now_contenr);
        linearLayoutRating = findViewById(R.id.orders_datails_linearLayout_rating);
        orderId = findViewById(R.id.Ord_detai_orderID);
        proImage = findViewById(R.id.Ord_detai_product_imageView);
        productTitle = findViewById(R.id.Ord_detai_product_title);
        productPrice = findViewById(R.id.Ord_detai_product_prise);
        productQty = findViewById(R.id.Ord_detai_Qty);


        orderedIndicator = findViewById(R.id.ordered_indicator);
        packedIndicator = findViewById(R.id.packed_indicator2);
        shippedIndicator = findViewById(R.id.shiping_indicator3);
        deliveredIndicator = findViewById(R.id.delivered_indicator4);

        o_p_progressBar = findViewById(R.id.ordered_packed_progressBar);
        p_s_progressBar = findViewById(R.id.packed_shipping_progressBar);
        s_d_progressBar = findViewById(R.id.shiping_delivered_progressBar);

        orderedTitle = findViewById(R.id.ordered_title);
        packedTitle = findViewById(R.id.packed_title);
        shippedTitle = findViewById(R.id.shiping_title);
        deliveredTitle = findViewById(R.id.delivered_title);
        
        packedTitle.setText("Processing");
        shippedTitle.setText("Out for Service");
        deliveredTitle.setText("Work Completed");

        orderedDate = findViewById(R.id.ordered_date);
        packedDate = findViewById(R.id.packed_date);
        shippedDate = findViewById(R.id.shiping_date);
        deliveredDate = findViewById(R.id.delivered_date);

        orderedBody = findViewById(R.id.ordered_body);
        packedBody = findViewById(R.id.packed_body);
        shippedBody = findViewById(R.id.shiping_body);
        deliveredBody = findViewById(R.id.delivered_body);
        otpTextView = findViewById(R.id.otp_text_view);

        fullNamee = findViewById(R.id.shippinhDetails_full_name);
        address = findViewById(R.id.shippinhDetails_addres);
        mobile = findViewById(R.id.shippinhDetails_pincode);

        totalItems = findViewById(R.id.total_items);
        totalItemsPrice = findViewById(R.id.total_items_price);
        discountPrice = findViewById(R.id.discount_price);
        serviceCharge = findViewById(R.id.service_amount);
        totalAmount = findViewById(R.id.totalAmnount);
        saveAmount = findViewById(R.id.save_amount);

        cancelOrderBtn = findViewById(R.id.order_detail_cancelBtn);
        needHelpBtn = findViewById(R.id.order_detail_helpBtn);
        viewBillBtn = findViewById(R.id.btn_view_product_bill);
        
        //refund
        refundDetailsLayout = findViewById(R.id.refund_details_layout);
        refundStatusText = findViewById(R.id.refund_status_text);
        refundAmountText = findViewById(R.id.refund_amount_text);
        refundTimelineText = findViewById(R.id.refund_timeline_text);
    }

    private void updateUIWithModelData(MyOrderItemModel model) {
        rating = model.getRating();
        initialRating = rating - 1; 
        SetRating(initialRating); 

        orderId.setText("order ID - " + (model.getOrderID() != null ? model.getOrderID() : "N/A"));
        productTitle.setText(model.getProductTitle() != null ? model.getProductTitle() : "Product");
        
        long pPrice = 0;
        try {
            pPrice = Long.parseLong(model.getProductPrice() != null ? model.getProductPrice() : "0");
        } catch (NumberFormatException ignored) {}
        
        long price = model.getQuantity() * pPrice;
        productPrice.setText("Rs." + price);
        productQty.setText("Qty: " + model.getQuantity());
        Glide.with(this).load(model.getProductImage()).placeholder(R.drawable.address_icon).into(proImage);


        // price Details
        long totalItemsPriceValue = model.getQuantity() * pPrice;
        totalItems.setText("Price(" + model.getQuantity() + ") items");
        
        long cPrice = 0;
        try {
            cPrice = Long.parseLong(model.getCutPrice() != null ? model.getCutPrice() : "0");
        } catch (NumberFormatException ignored) {}
        
        long discount = (cPrice - pPrice) * model.getQuantity();
        discountPrice.setText("-Rs." + (discount > 0 ? discount : 0));
        totalItemsPrice.setText("Rs." + totalItemsPriceValue);

        String deliveryC = model.getDeliveryCharge() != null ? model.getDeliveryCharge() : "0";
        if (deliveryC.equalsIgnoreCase("FREE") || deliveryC.isEmpty() || deliveryC.equals("0")) {
            serviceCharge.setText("FREE");
            totalAmount.setText("Rs." + totalItemsPriceValue);
        } else {
            serviceCharge.setText("Rs." + deliveryC);
            long dCharge = 0;
            try { dCharge = Long.parseLong(deliveryC); } catch (NumberFormatException ignored) {}
            totalAmount.setText("Rs." + (totalItemsPriceValue + dCharge));
        }

        saveAmount.setText("Payment Method: " + (model.getPaymentMethod() != null ? model.getPaymentMethod() : "N/A"));
        saveAmount.setTextColor(Color.BLACK);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EE, dd MMM yy, hh:mm aa", Locale.getDefault());

        if (model.getOrderedDate() != null) orderedDate.setText(simpleDateFormat.format(model.getOrderedDate()));
        if (model.getPackedDate() != null) packedDate.setText(simpleDateFormat.format(model.getPackedDate()));
        if (model.getShippedDate() != null) shippedDate.setText(simpleDateFormat.format(model.getShippedDate()));
        if (model.getDeliveredDate() != null) deliveredDate.setText(simpleDateFormat.format(model.getDeliveredDate()));
        
        if (model.getOrderedTitle() != null) orderedTitle.setText(model.getOrderedTitle());
        if (model.getOrderedBody() != null) orderedBody.setText(model.getOrderedBody());
        if (model.getPackedTitle() != null) packedTitle.setText(model.getPackedTitle());
        if (model.getPackedBody() != null) packedBody.setText(model.getPackedBody());
        if (model.getShippedTitle() != null) shippedTitle.setText(model.getShippedTitle());
        if (model.getShippedBody() != null) shippedBody.setText(model.getShippedBody());
        if (model.getDeliveredTitle() != null) deliveredTitle.setText(model.getDeliveredTitle());
        if (model.getDeliveredBody() != null) deliveredBody.setText(model.getDeliveredBody());

        String orderStatus = model.getOrderStatus() != null ? model.getOrderStatus() : "";

        switch (orderStatus) {
            case "Ordered":
                updateOrderStatusUI(1);
                o_p_progressBar.setProgress(50);
                break;
            case "Processing":
            case "Packed":
                updateOrderStatusUI(2);
                p_s_progressBar.setProgress(40);
                break;
            case "Out for Service":
            case "Shipped":
                updateOrderStatusUI(3);
                s_d_progressBar.setProgress(60);
                break;
            case "Completed":
            case "Delivered":
                updateOrderStatusUI(4);
                break;
            case "Cancelled":
                updateOrderStatusUI(2);
                o_p_progressBar.setProgress(100);
                p_s_progressBar.setProgress(100);
                p_s_progressBar.setProgressTintList(ColorStateList.valueOf(Color.RED));
                shippedIndicator.setImageTintList(ColorStateList.valueOf(Color.RED));
                s_d_progressBar.setProgress(0);

                if (model.getCancelledDate() != null) shippedDate.setText(simpleDateFormat.format(model.getCancelledDate()));
                shippedTitle.setText(model.getShippedTitle() != null ? model.getShippedTitle() : "Cancelled");
                shippedBody.setText(model.getShippedBody() != null ? model.getShippedBody() : "Your order has been cancelled.");

                deliveredIndicator.setVisibility(GONE);
                deliveredTitle.setVisibility(GONE);
                deliveredDate.setVisibility(GONE);
                deliveredBody.setVisibility(GONE);
                s_d_progressBar.setVisibility(GONE);

                refundDetailsLayout.setVisibility(VISIBLE);
                refundAmountText.setText(totalAmount.getText());
                refundTimelineText.setText("Amount will be credited in 5-7 business days.");

                if ("Initiated".equals(model.getRefundStatus())) {
                    refundStatusText.setText("Refund Initiated");
                    refundStatusText.setTextColor(Color.parseColor("#FFA500"));
                } else if ("Completed".equals(model.getRefundStatus())) {
                    refundStatusText.setText("Refund Completed");
                    refundStatusText.setTextColor(Color.GREEN);
                    refundTimelineText.setText("Amount credited to your original payment source.");
                }
                break;

            case "Payment Failed":
                o_p_progressBar.setVisibility(GONE);
                p_s_progressBar.setVisibility(GONE);
                s_d_progressBar.setVisibility(GONE);
                orderedIndicator.setImageTintList(ColorStateList.valueOf(Color.RED));
                orderedTitle.setText("Payment Failed");
                orderedTitle.setTextColor(Color.RED);
                if (model.getOrderedDate() != null) orderedDate.setText(simpleDateFormat.format(model.getOrderedDate()));
                orderedBody.setText("Your payment was unsuccessful. Please try again.");

                packedIndicator.setVisibility(GONE);
                packedTitle.setVisibility(GONE);
                packedDate.setVisibility(GONE);
                packedBody.setVisibility(GONE);
                shippedIndicator.setVisibility(GONE);
                shippedTitle.setVisibility(GONE);
                shippedDate.setVisibility(GONE);
                shippedBody.setVisibility(GONE);
                deliveredIndicator.setVisibility(GONE);
                deliveredTitle.setVisibility(GONE);
                deliveredDate.setVisibility(GONE);
                deliveredBody.setVisibility(GONE);

                cancelOrderBtn.setVisibility(VISIBLE);
                cancelOrderBtn.setText("Retry Payment");
                cancelOrderBtn.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                cancelOrderBtn.setOnClickListener(v -> Toast.makeText(Orders_DetailsActivity3.this, "Redirecting...", Toast.LENGTH_SHORT).show());
                break;
        }

        if (orderStatus.equals("Completed") || orderStatus.equals("Delivered")) {
            linearLayoutRating.setVisibility(VISIBLE);
            cancelOrderBtn.setVisibility(GONE);
            viewBillBtn.setVisibility(VISIBLE);
            viewBillBtn.setOnClickListener(v -> showProfessionalInvoice(model));
        } else if (orderStatus.equals("Ordered") || orderStatus.equals("Processing") || orderStatus.equals("Packed")) {
            linearLayoutRating.setVisibility(GONE);
            cancelOrderBtn.setVisibility(VISIBLE);
            viewBillBtn.setVisibility(GONE);
            cancelOrderBtn.setText("Cancel Order");
            cancelOrderBtn.setOnClickListener(v -> showCancelConfirmationDialog(model));
        } else if (!orderStatus.equals("Payment Failed")) {
            linearLayoutRating.setVisibility(GONE);
            cancelOrderBtn.setVisibility(GONE);
            viewBillBtn.setVisibility(GONE);
        }

        for (int x = 0; x < rateNowContainer.getChildCount(); x++) {
            final int starPosition = x;
            rateNowContainer.getChildAt(x).setOnClickListener(v -> {
                if (starPosition == initialRating || running_rating_query) return;

                running_rating_query = true;
                loadingDialog.show();
                newStarPosition = starPosition + 1;
                SetRating(starPosition);

                executor.execute(() -> {
                    String productId = model.getProductID();
                    if (productId == null) {
                        handleRatingFailure("Error: Product ID missing", starPosition);
                        return;
                    }
                    DocumentReference productRef = db.collection("Product_Details").document(productId);
                    Map<String, Object> update = new HashMap<>();
                    update.put(newStarPosition + "_star", FieldValue.increment(1));
                    if (initialRating != -1) update.put((initialRating + 1) + "_star", FieldValue.increment(-1));

                    productRef.update(update).addOnSuccessListener(aVoid -> {
                        String uid = auth.getUid();
                        if (uid == null) {
                            handleRatingFailure("User not logged in.", starPosition);
                            return;
                        }
                        db.collection("USER").document(uid).collection("USER_DATA").document("MY_RATINGS")
                                .update("ratings_map." + productId, (long) newStarPosition)
                                .addOnSuccessListener(bVoid -> runOnUiThread(() -> {
                                    running_rating_query = false;
                                    loadingDialog.dismiss();
                                    Toast.makeText(Orders_DetailsActivity3.this, "Thank you for rating!", Toast.LENGTH_SHORT).show();
                                    initialRating = starPosition;
                                    model.setRating(newStarPosition);
                                }))
                                .addOnFailureListener(e -> handleRatingFailure("Failed to save rating.", starPosition));
                    }).addOnFailureListener(e -> handleRatingFailure("Failed to update product rating.", starPosition));
                });
            });
        }

        fullNamee.setText(model.getFullName() != null ? model.getFullName() : "");
        address.setText((model.getAddress() != null ? model.getAddress() : "") + " pinCode " + (model.getPinCode() != null ? model.getPinCode() : ""));
        mobile.setText("Mobile " + (model.getMobile() != null ? model.getMobile() : ""));

        proImage.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProductDetailsActivity.class);
            intent.putExtra("PRODUCT_ID", model.getProductID());
            startActivity(intent);
        });

        needHelpBtn.setOnClickListener(v -> showNeedHelpDialog(model));

        if (orderStatus.equals("Out for Service") && model.getOtp() != 0) {
            otpTextView.setVisibility(VISIBLE);
            otpTextView.setText("Task OTP: " + model.getOtp());
        } else {
            otpTextView.setVisibility(GONE);
        }
    }

    private void showProfessionalInvoice(MyOrderItemModel model) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.layout_invoice_pdf, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        TextView invUserName = dialogView.findViewById(R.id.inv_user_name);
        TextView invUserPhone = dialogView.findViewById(R.id.inv_user_phone);
        TextView invUserAddress = dialogView.findViewById(R.id.inv_user_address);
        TextView invOrderId = dialogView.findViewById(R.id.inv_order_id);
        TextView invDate = dialogView.findViewById(R.id.inv_date);
        TextView invSubtotal = dialogView.findViewById(R.id.inv_subtotal);
        TextView invGrandTotal = dialogView.findViewById(R.id.inv_grand_total);
        TextView invPayment = dialogView.findViewById(R.id.inv_payment);
        LinearLayout itemsContainer = dialogView.findViewById(R.id.invoice_items_container);

        invUserName.setText(model.getFullName());
        invUserPhone.setText(model.getMobile());
        invUserAddress.setText(model.getAddress() + " - " + model.getPinCode());
        invOrderId.setText("Order ID: #" + model.getOrderID());
        invPayment.setText("Payment : "+model.getPaymentMethod());
        
        if (model.getOrderedDate() != null) {
            invDate.setText("Date: " + pdfDateFormat.format(model.getOrderedDate()));
        }

        long pPrice = Long.parseLong(model.getProductPrice() != null ? model.getProductPrice() : "0");
        long totalProductPrice = model.getQuantity() * pPrice;
        
        addInvoiceRow(itemsContainer, model.getProductTitle(), String.valueOf(model.getQuantity()), String.valueOf(totalProductPrice));
        
        // 2. Add Dynamic Items from Array (billItems) added by Delivery Boy
        java.util.List<java.util.Map<String, Object>> billItems = model.getBillItems();
        if (billItems != null) {
            for (java.util.Map<String, Object> item : billItems) {
                String desc = (String) item.get("desc");
                String qty = String.valueOf(item.get("qty"));
                String amount = String.valueOf(item.get("amount"));
                addInvoiceRow(itemsContainer, desc, qty, amount);
            }
        }

        String deliveryC = model.getDeliveryCharge() != null ? model.getDeliveryCharge() : "0";
        long dCharge = 0;
        if (!deliveryC.equalsIgnoreCase("FREE") && !deliveryC.isEmpty() && !deliveryC.equals("0")) {
            dCharge = Long.parseLong(deliveryC);
            addInvoiceRow(itemsContainer, "Delivery Charges", "1", deliveryC);
        }

        if (model.getFinalTotal() != null) {
            invSubtotal.setText("Rs." + model.getFinalTotal());
            invGrandTotal.setText("Rs." + model.getFinalTotal());
        } else {
            invSubtotal.setText("Rs." + (totalProductPrice + dCharge));
            invGrandTotal.setText("Rs." + (totalProductPrice + dCharge));
        }

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
        tPrice.setText("Rs." + price);
        container.addView(row);
    }

    private void updateOrderStatusUI(int stage) {
        int color = getColor(R.color.green);
        if (stage >= 1) orderedIndicator.setImageTintList(ColorStateList.valueOf(color));
        if (stage >= 2) {
            o_p_progressBar.setProgress(100);
            packedIndicator.setImageTintList(ColorStateList.valueOf(color));
        }
        if (stage >= 3) {
            p_s_progressBar.setProgress(100);
            shippedIndicator.setImageTintList(ColorStateList.valueOf(color));
        }
        if (stage >= 4) {
            s_d_progressBar.setProgress(100);
            deliveredIndicator.setImageTintList(ColorStateList.valueOf(color));
        }
    }

    private void showCancelConfirmationDialog(MyOrderItemModel model) {
        Spinner reasonSpinner = cancelDialog.findViewById(R.id.cancel_reason_spinner);
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
                processOrderCancellation(model, selectedReason);
            }
        });
    }

    private void processOrderCancellation(MyOrderItemModel model, String reason) {
        executor.execute(() -> {
            Map<String, Object> map = new HashMap<>();
            map.put("orderID", model.getOrderID());
            map.put("productID", model.getProductID());
            map.put("reason", reason);
            map.put("dateTime", FieldValue.serverTimestamp());

            db.collection("CANCELLED ORDERS").add(map).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Map<String, Object> update = new HashMap<>();
                    update.put("orderStatus", "Cancelled");
                    update.put("cancelReason", reason);
                    update.put("cancelledDate", FieldValue.serverTimestamp());
                    db.collection("ORDERS").document(model.getOrderID()).collection("orderItems").document(model.getProductID())
                            .update(update).addOnCompleteListener(uTask -> runOnUiThread(() -> {
                                loadingDialog.dismiss();
                                cancelDialog.dismiss();
                                if (uTask.isSuccessful()) {
                                    Toast.makeText(this, "Order Cancelled", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }));
                }
            });
        });
    }

    private void handleRatingFailure(String message, int previousRatingIndex) {
        runOnUiThread(() -> {
            running_rating_query = false;
            loadingDialog.dismiss();
            SetRating(previousRatingIndex);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        });
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.search_cart_icon, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void SetRating(int starPosition) {
        if (rateNowContainer == null) return;
        for (int x = 0; x < rateNowContainer.getChildCount(); x++) {
            ImageView starBtn = (ImageView) rateNowContainer.getChildAt(x);
            starBtn.setImageTintList(ColorStateList.valueOf(x <= starPosition ? Color.GREEN : Color.LTGRAY));
        }
    }

    private void showNeedHelpDialog(MyOrderItemModel model) {
        final Dialog d = new Dialog(this);
        d.setContentView(R.layout.need_help_dialog);
        d.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        TextView oid = d.findViewById(R.id.dialog_order_id);
        EditText desc = d.findViewById(R.id.dialog_issue_description);
        Button sub = d.findViewById(R.id.dialog_submit_button);

        oid.setText("Order ID: #" + model.getOrderID());
        sub.setOnClickListener(v -> submitSupportTicket(model.getOrderID(), desc.getText().toString(), d));
        d.show();
    }

    private void submitSupportTicket(String orderId, String issue, Dialog d) {
        if (issue.isEmpty()) {
            Toast.makeText(this, "Please describe your issue", Toast.LENGTH_SHORT).show();
            return;
        }
        
        loadingDialog.show();

        // Professional Numeric Ticket ID (6 Digits)
        int randomId = (int) (Math.random() * 900000) + 100000;
        String ticketId = String.valueOf(randomId);

        Map<String, Object> ticket = new HashMap<>();
        ticket.put("ticketId", ticketId);
        ticket.put("orderId", orderId);
        ticket.put("userId", auth.getUid());
        ticket.put("issue_description", issue);
        ticket.put("status", "OPEN");
        ticket.put("type", "CUSTOMER_ORDER_ISSUE");
        
        // Detailed Timestamps
        ticket.put("createdDate", FieldValue.serverTimestamp());
        ticket.put("lastUpdateDate", FieldValue.serverTimestamp());
        ticket.put("resolvedDate", null);
        ticket.put("closedDate", null);
        
        // Admin fields
        ticket.put("adminNote", "");

        db.collection("SUPPORT_TICKETS").document(ticketId)
                .set(ticket)
                .addOnCompleteListener(task -> {
                    loadingDialog.dismiss();
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Ticket #" + ticketId + " Raised Successfully!", Toast.LENGTH_LONG).show();
                        d.dismiss();
                    } else {
                        Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
