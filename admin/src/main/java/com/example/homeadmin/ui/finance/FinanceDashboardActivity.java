package com.example.homeadmin.ui.finance;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.homeadmin.R;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Calendar;
import java.util.Date;

public class FinanceDashboardActivity extends AppCompatActivity {

    private static final String TAG = "FinanceDashboard";
    private TextView todaySaleTv, todayRefundTv, todayWithdrawalTv, todayProfitTv, summaryTv;
    private Spinner timeSpinner;
    private FirebaseFirestore db;

    private double totalSales = 0;
    private double totalRefunds = 0;
    private double totalWithdrawals = 0;
    private Date currentStartDate, currentEndDate;
    private String selectedTimeLabel = "Today";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finance_dashboard);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        db = FirebaseFirestore.getInstance();

        todaySaleTv = findViewById(R.id.today_sale_text);
        todayRefundTv = findViewById(R.id.today_refund_text);
        todayWithdrawalTv = findViewById(R.id.today_withdrawal_text);
        todayProfitTv = findViewById(R.id.today_profit_text);
        summaryTv = findViewById(R.id.finance_summary_text);
        timeSpinner = findViewById(R.id.time_period_spinner);

        setupTimeSpinner();

        findViewById(R.id.card_today_sale).setOnClickListener(v -> openDetails(FinanceDetailsActivity.MODE_SALES));
        findViewById(R.id.card_today_refund).setOnClickListener(v -> openDetails(FinanceDetailsActivity.MODE_REFUNDS));
        findViewById(R.id.card_today_withdrawal).setOnClickListener(v -> openDetails(FinanceDetailsActivity.MODE_WITHDRAWALS));
        findViewById(R.id.card_admin_profit).setOnClickListener(v -> Toast.makeText(this, "Profit breakdown coming soon", Toast.LENGTH_SHORT).show());

        findViewById(R.id.manage_refunds_btn).setOnClickListener(v -> {
            startActivity(new Intent(this, RefundApprovalActivity.class));
        });

        findViewById(R.id.manage_withdrawals_btn).setOnClickListener(v -> {
            startActivity(new Intent(this, WithdrawalApprovalActivity.class));
        });
    }

    private void setupTimeSpinner() {
        String[] options = {"Today", "Yesterday", "Last 7 Days", "This Month", "All Time"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeSpinner.setAdapter(adapter);

        timeSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                selectedTimeLabel = options[position];
                calculateDateRange(position);
                loadFinanceData();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
    }

    private void calculateDateRange(int position) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        currentEndDate = null; // Default to latest

        switch (position) {
            case 0: // Today
                currentStartDate = cal.getTime();
                break;
            case 1: // Yesterday
                cal.add(Calendar.DAY_OF_YEAR, -1);
                currentStartDate = cal.getTime();
                Date yesterday = cal.getTime();
                cal.add(Calendar.DAY_OF_YEAR, 1);
                currentEndDate = cal.getTime(); // Up to start of today
                break;
            case 2: // Last 7 Days
                cal.add(Calendar.DAY_OF_YEAR, -7);
                currentStartDate = cal.getTime();
                break;
            case 3: // This Month
                cal.set(Calendar.DAY_OF_MONTH, 1);
                currentStartDate = cal.getTime();
                break;
            case 4: // All Time
                currentStartDate = null;
                break;
        }
    }

    private void openDetails(String mode) {
        Intent intent = new Intent(this, FinanceDetailsActivity.class);
        intent.putExtra(FinanceDetailsActivity.EXTRA_MODE, mode);
        if (currentStartDate != null) intent.putExtra("START_DATE", currentStartDate.getTime());
        if (currentEndDate != null) intent.putExtra("END_DATE", currentEndDate.getTime());
        intent.putExtra("LABEL", selectedTimeLabel);
        startActivity(intent);
    }

    private void loadFinanceData() {
        summaryTv.setText("Analyzing transactions for " + selectedTimeLabel + "...");

        // 1. Fetch Sales & Refunds from 'orderItems' sub-collections
        // Note: To query sub-collections in Firestore, we need to fetch all main orders first 
        // OR use a Collection Group query. But for accurate time filtering on sub-items,
        // we'll fetch orders from the main collection and then aggregate their items.
        
        db.collection("ORDERS")
                .get() // Fetching all (or filtered by date) to scan sub-collections
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    totalSales = 0;
                    totalRefunds = 0;
                    
                    final int[] ordersProcessed = {0};
                    final int totalOrders = queryDocumentSnapshots.size();
                    
                    if (totalOrders == 0) {
                        updateUI();
                        return;
                    }

                    for (DocumentSnapshot orderDoc : queryDocumentSnapshots) {
                        orderDoc.getReference().collection("orderItems")
                                .get()
                                .addOnSuccessListener(itemSnapshots -> {
                                    for (DocumentSnapshot item : itemSnapshots) {
                                        String status = item.getString("orderStatus");
                                        Object priceObj = item.get("productPrice");
                                        long qty = item.getLong("productQuantity") != null ? item.getLong("productQuantity") : 1;
                                        
                                        double price = 0;
                                        if (priceObj != null) price = Double.parseDouble(priceObj.toString());

                                        Timestamp itemDate = item.getTimestamp("orderedDate");
                                        Timestamp refundDate = item.getTimestamp("refundedDate");

                                        // Apply Time Filter manually for sub-items
                                        if (isWithinRange(itemDate)) {
                                            if ("Delivered".equalsIgnoreCase(status)) {
                                                totalSales += (price * qty);
                                            }
                                        }
                                        
                                        if (isWithinRange(refundDate)) {
                                            if ("Refunded".equalsIgnoreCase(item.getString("refundStatus"))) {
                                                totalRefunds += (price * qty);
                                            }
                                        }
                                    }
                                    
                                    ordersProcessed[0]++;
                                    if (ordersProcessed[0] == totalOrders) {
                                        updateUI();
                                    }
                                });
                    }
                });

        // 2. Fetch Withdrawals (Already efficient)
        Query withdrawalQuery = db.collection("withdrawal_requests").whereEqualTo("status", "approved");
        if (currentStartDate != null) withdrawalQuery = withdrawalQuery.whereGreaterThanOrEqualTo("approvedDate", new Timestamp(currentStartDate));
        if (currentEndDate != null) withdrawalQuery = withdrawalQuery.whereLessThan("approvedDate", new Timestamp(currentEndDate));

        withdrawalQuery.get().addOnSuccessListener(queryDocumentSnapshots -> {
            totalWithdrawals = 0;
            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                Double amount = doc.getDouble("amount");
                if (amount != null) totalWithdrawals += amount;
            }
            updateUI();
        });
    }

    private boolean isWithinRange(Timestamp timestamp) {
        if (timestamp == null) return false;
        if (currentStartDate == null) return true; // All Time
        
        Date date = timestamp.toDate();
        boolean afterStart = !date.before(currentStartDate);
        boolean beforeEnd = (currentEndDate == null) || date.before(currentEndDate);
        
        return afterStart && beforeEnd;
    }

    private void updateUI() {
        todaySaleTv.setText("₹" + String.format("%.2f", totalSales));
        todayRefundTv.setText("₹" + String.format("%.2f", totalRefunds));
        todayWithdrawalTv.setText("₹" + String.format("%.2f", totalWithdrawals));

        double profit = (totalSales * 0.10);
        todayProfitTv.setText("₹" + String.format("%.2f", profit));

        double net = totalSales - totalRefunds - totalWithdrawals;
        String summary = "Summary for " + selectedTimeLabel + ":\n" +
                "• Total Revenue: ₹" + String.format("%.2f", totalSales) + "\n" +
                "• Total Refunds: ₹" + String.format("%.2f", totalRefunds) + "\n" +
                "• Total Withdrawals: ₹" + String.format("%.2f", totalWithdrawals) + "\n" +
                "• Net Balance: ₹" + String.format("%.2f", net) + "\n\n" +
                "Your commission (10% profit): ₹" + String.format("%.2f", profit);
        
        summaryTv.setText(summary);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
