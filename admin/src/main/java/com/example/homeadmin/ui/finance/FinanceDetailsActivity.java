package com.example.homeadmin.ui.finance;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homeadmin.R;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FinanceDetailsActivity extends AppCompatActivity {

    public static final String EXTRA_MODE = "EXTRA_MODE";
    public static final String MODE_SALES = "SALES";
    public static final String MODE_REFUNDS = "REFUNDS";
    public static final String MODE_WITHDRAWALS = "WITHDRAWALS";

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView noDataTv;
    private FirebaseFirestore db;
    private List<DetailModel> list;
    private DetailsAdapter adapter;
    private String mode, label;
    private Date startDate, endDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finance_details);

        mode = getIntent().getStringExtra(EXTRA_MODE);
        if (mode == null) mode = MODE_SALES;
        
        label = getIntent().getStringExtra("LABEL");
        long startMillis = getIntent().getLongExtra("START_DATE", -1);
        long endMillis = getIntent().getLongExtra("END_DATE", -1);
        
        if (startMillis != -1) startDate = new Date(startMillis);
        if (endMillis != -1) endDate = new Date(endMillis);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setToolbarTitle();
        }

        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.details_recycler_view);
        progressBar = findViewById(R.id.progress_bar);
        noDataTv = findViewById(R.id.no_data_text);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();
        adapter = new DetailsAdapter(list);
        recyclerView.setAdapter(adapter);

        loadData();
    }

    private void setToolbarTitle() {
        String titleLabel = (label != null) ? label : "Today";
        switch (mode) {
            case MODE_SALES: getSupportActionBar().setTitle(titleLabel + " Sales"); break;
            case MODE_REFUNDS: getSupportActionBar().setTitle(titleLabel + " Refunds"); break;
            case MODE_WITHDRAWALS: getSupportActionBar().setTitle(titleLabel + " Withdrawals"); break;
        }
    }

    private void loadData() {
        progressBar.setVisibility(View.VISIBLE);

        if (MODE_WITHDRAWALS.equals(mode)) {
            loadWithdrawalDetails();
        } else {
            loadItemLevelDetails();
        }
    }

    private void loadWithdrawalDetails() {
        Query query = db.collection("withdrawal_requests").whereEqualTo("status", "approved");

        if (startDate != null) query = query.whereGreaterThanOrEqualTo("approvedDate", new Timestamp(startDate));
        if (endDate != null) query = query.whereLessThan("approvedDate", new Timestamp(endDate));

        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            progressBar.setVisibility(View.GONE);
            list.clear();
            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                DetailModel model = new DetailModel();
                model.title = "Delivery Boy ID: " + doc.getString("deliveryBoyId");
                model.amount = doc.getDouble("amount") != null ? doc.getDouble("amount") : 0.0;
                model.subtitle = "Request ID: " + doc.getId();
                model.timestamp = doc.getTimestamp("approvedDate");
                list.add(model);
            }
            adapter.notifyDataSetChanged();
            noDataTv.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
        }).addOnFailureListener(this::handleError);
    }

    private void loadItemLevelDetails() {
        // Sales aur Refunds ke liye humen Sub-collection scan karni hogi
        db.collection("ORDERS").get().addOnSuccessListener(orderSnapshots -> {
            list.clear();
            final int[] processedOrders = {0};
            final int totalOrders = orderSnapshots.size();

            if (totalOrders == 0) {
                progressBar.setVisibility(View.GONE);
                noDataTv.setVisibility(View.VISIBLE);
                return;
            }

            for (DocumentSnapshot orderDoc : orderSnapshots) {
                orderDoc.getReference().collection("orderItems").get().addOnSuccessListener(itemSnapshots -> {
                    for (DocumentSnapshot item : itemSnapshots) {
                        processItemForDetails(item);
                    }
                    processedOrders[0]++;
                    if (processedOrders[0] == totalOrders) {
                        progressBar.setVisibility(View.GONE);
                        adapter.notifyDataSetChanged();
                        noDataTv.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
                    }
                });
            }
        }).addOnFailureListener(this::handleError);
    }

    private void processItemForDetails(DocumentSnapshot item) {
        String status = item.getString("orderStatus");
        Timestamp itemDate = item.getTimestamp("orderedDate");
        String refundStatus = item.getString("refundStatus");
        Timestamp refundDate = item.getTimestamp("refundedDate");

        if (MODE_SALES.equals(mode)) {
            if ("Delivered".equalsIgnoreCase(status) && isWithinRange(itemDate)) {
                addDetailModel(item, itemDate);
            }
        } else if (MODE_REFUNDS.equals(mode)) {
            if ("Refunded".equalsIgnoreCase(refundStatus) && isWithinRange(refundDate)) {
                addDetailModel(item, refundDate);
            }
        }
    }

    private void addDetailModel(DocumentSnapshot item, Timestamp ts) {
        DetailModel model = new DetailModel();
        model.title = item.getString("productTitle");
        Object amt = item.get("productPrice");
        long qty = item.getLong("productQuantity") != null ? item.getLong("productQuantity") : 1;
        double price = (amt != null) ? Double.parseDouble(amt.toString()) : 0.0;
        
        model.amount = price * qty;
        model.subtitle = "Order ID: #" + item.getString("orderID");
        model.timestamp = ts;
        list.add(model);
    }

    private boolean isWithinRange(Timestamp timestamp) {
        if (timestamp == null) return false;
        if (startDate == null) return true;
        Date date = timestamp.toDate();
        boolean afterStart = !date.before(startDate);
        boolean beforeEnd = (endDate == null) || date.before(endDate);
        return afterStart && beforeEnd;
    }

    private void handleError(Exception e) {
        progressBar.setVisibility(View.GONE);
        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
    }

    private static class DetailModel {
        String title, subtitle;
        double amount;
        Timestamp timestamp;
    }

    private class DetailsAdapter extends RecyclerView.Adapter<DetailsAdapter.ViewHolder> {
        private List<DetailModel> list;

        public DetailsAdapter(List<DetailModel> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_finance_detail, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            DetailModel model = list.get(position);
            holder.title.setText(model.title);
            holder.amount.setText("₹" + String.format("%.2f", model.amount));
            holder.subtitle.setText(model.subtitle);
            
            if (model.timestamp != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                holder.date.setText("Time: " + sdf.format(model.timestamp.toDate()));
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView title, amount, subtitle, date;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.detail_title);
                amount = itemView.findViewById(R.id.detail_amount);
                subtitle = itemView.findViewById(R.id.detail_subtitle);
                date = itemView.findViewById(R.id.detail_date);
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
