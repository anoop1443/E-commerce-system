package com.example.homeadmin.ui.finance;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homeadmin.R;
import com.example.homeadmin.ui.orders.OrderModel;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RefundApprovalActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView noRefundsTv;
    private FirebaseFirestore db;
    private List<OrderModel> refundList;
    private RefundAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refund_approval);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.refund_recycler_view);
        progressBar = findViewById(R.id.progress_bar);
        noRefundsTv = findViewById(R.id.no_refunds_text);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        refundList = new ArrayList<>();
        adapter = new RefundAdapter(refundList);
        recyclerView.setAdapter(adapter);

        loadRefunds();
    }

    private void loadRefunds() {
        progressBar.setVisibility(View.VISIBLE);
        db.collectionGroup("orderItems")
                .whereEqualTo("orderStatus", "Cancelled")
                .orderBy("cancelledDate", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    refundList.clear();
                    if (queryDocumentSnapshots.isEmpty()) {
                        progressBar.setVisibility(View.GONE);
                        noRefundsTv.setVisibility(View.VISIBLE);
                        adapter.notifyDataSetChanged();
                        return;
                    }

                    for (DocumentSnapshot itemDoc : queryDocumentSnapshots) {
                        if (!"Refunded".equals(itemDoc.getString("refundStatus"))) {
                            OrderModel model = new OrderModel();
                            model.setOrderID(itemDoc.getString("orderID"));
                            model.setFullName(itemDoc.getString("productTitle"));
                            model.setAddress(itemDoc.getString("paymentMethod"));

                            Object priceObj = itemDoc.get("productPrice");
                            long qty = itemDoc.getLong("productQuantity") != null ? itemDoc.getLong("productQuantity") : 1;
                            double price = 0.0;
                            if (priceObj != null) {
                                try { price = Double.parseDouble(priceObj.toString()); } catch (Exception ignored) {}
                            }

                            model.setTotalAmount(price * qty);
                            model.setMobile(itemDoc.getId()); // Product ID
                            model.setUserID(itemDoc.getString("productImage")); // Using userID field to store Image URL for UI

                            // Extra data for UI
                            model.setOrderStatus(String.valueOf(qty)); // Qty
                            model.setDate(itemDoc.getTimestamp("cancelledDate"));

                            refundList.add(model);
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                    noRefundsTv.setVisibility(refundList.isEmpty() ? View.VISIBLE : View.GONE);
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void approveRefund(OrderModel model, int position) {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Refund")
                .setMessage("Have you initiated the refund of ₹" + model.getTotalAmount() + " to customer for " + model.getFullName() + "?\nThis will mark the item as Refunded.")
                .setPositiveButton("Yes, Refunded", (dialog, which) -> {
                    processRefund(model, position);
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void processRefund(OrderModel model, int position) {
        progressBar.setVisibility(View.VISIBLE);
        Map<String, Object> updates = new HashMap<>();
        updates.put("refundStatus", "Refunded");
        updates.put("refundedDate", FieldValue.serverTimestamp());

        db.collection("ORDERS").document(model.getOrderID())
                .collection("orderItems").document(model.getMobile())
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Refund Marked as Completed!", Toast.LENGTH_SHORT).show();
                    refundList.remove(position);
                    adapter.notifyItemRemoved(position);
                    noRefundsTv.setVisibility(refundList.isEmpty() ? View.VISIBLE : View.GONE);
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private class RefundAdapter extends RecyclerView.Adapter<RefundAdapter.ViewHolder> {
        private List<OrderModel> list;

        public RefundAdapter(List<OrderModel> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_refund_approval, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            OrderModel model = list.get(position);
            holder.customerName.setText(model.getFullName());
            holder.amount.setText("₹" + model.getTotalAmount());
            holder.orderId.setText("Order ID: #" + model.getOrderID());
            holder.paymentMethod.setText("Payment Method: " + model.getAddress());
            holder.qtyTv.setText("Quantity: " + model.getOrderStatus());

            // Load Product Image
            if (model.getUserID() != null) {
                Glide.with(holder.itemView.getContext()).load(model.getUserID())
                        .placeholder(R.drawable.tebal_fan)
                        .into(holder.productImage);
            }
            
            if (model.getDate() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                holder.cancelledDateTv.setText("Cancelled on: " + sdf.format(model.getDate().toDate()));
            } else {
                holder.cancelledDateTv.setText("Cancelled on: N/A");
            }

            // Handle Razorpay Payment ID
            db.collection("ORDERS").document(model.getOrderID()).get()
                    .addOnSuccessListener(doc -> {
                        if (doc.exists()) {
                            String payId = doc.getString("paymentID");
                            if (payId != null) {
                                holder.payIdTv.setText("Pay ID: " + payId);
                                holder.copyBtn.setOnClickListener(v -> {
                                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                    ClipData clip = ClipData.newPlainText("Payment ID", payId);
                                    clipboard.setPrimaryClip(clip);
                                    Toast.makeText(RefundApprovalActivity.this, "Payment ID Copied!", Toast.LENGTH_SHORT).show();
                                });
                            } else {
                                holder.payIdTv.setText("Pay ID: N/A (COD?)");
                            }
                        }
                    });

            holder.approveBtn.setOnClickListener(v -> approveRefund(model, position));
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView customerName, amount, orderId, paymentMethod, payIdTv, qtyTv, cancelledDateTv;
            ImageView copyBtn, productImage;
            Button approveBtn;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                customerName = itemView.findViewById(R.id.customer_name_text);
                amount = itemView.findViewById(R.id.refund_amount_text);
                orderId = itemView.findViewById(R.id.order_id_text);
                paymentMethod = itemView.findViewById(R.id.payment_method_text);
                payIdTv = itemView.findViewById(R.id.payment_id_text);
                qtyTv = itemView.findViewById(R.id.product_qty_text);
                cancelledDateTv = itemView.findViewById(R.id.cancelled_date_text);
                copyBtn = itemView.findViewById(R.id.copy_pay_id_btn);
                productImage = itemView.findViewById(R.id.product_image);
                approveBtn = itemView.findViewById(R.id.approve_refund_btn);
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
