package com.example.homeadmin.ui.orders;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homeadmin.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdminOrdersActivity extends AppCompatActivity {

    private RecyclerView ordersRecyclerView;
    private AdminOrderAdapter adapter;
    private List<OrderModel> orderList;
    private TextView noOrdersText;
    private String orderStatus;
    private FirebaseFirestore db;
    private Dialog loadingDialog;

    private LinearLayout filterLayout;
    private Spinner yearSpinner, monthSpinner;
    private boolean showFilter = false;
    private int selectedYear, selectedMonth; // month is 0-indexed (0=Jan)
    private ListenerRegistration orderListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_orders);

        orderStatus = getIntent().getStringExtra("orderStatus");
        showFilter = getIntent().getBooleanExtra("showFilter", false);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            if (orderStatus != null) {
                getSupportActionBar().setTitle(orderStatus + " Orders");
            }
        }

        db = FirebaseFirestore.getInstance();
        ordersRecyclerView = findViewById(R.id.orders_recycler_view);
        noOrdersText = findViewById(R.id.no_orders_text);
        filterLayout = findViewById(R.id.filter_layout);
        yearSpinner = findViewById(R.id.year_spinner);
        monthSpinner = findViewById(R.id.month_spinner);

        // Loading Dialog
        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        if (loadingDialog.getWindow() != null) {
            loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        orderList = new ArrayList<>();
        adapter = new AdminOrderAdapter(orderList, this);
        ordersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        ordersRecyclerView.setAdapter(adapter);

        if (showFilter) {
            filterLayout.setVisibility(View.VISIBLE);
            setupFilters();
        } else {
            loadOrders();
        }
    }

    private void setupFilters() {
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);

        // Years setup (Current and last 5 years)
        List<String> years = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            years.add(String.valueOf(currentYear - i));
        }
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, years);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(yearAdapter);
        selectedYear = currentYear;

        // Months setup
        String[] monthNames = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, monthNames);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSpinner.setAdapter(monthAdapter);
        monthSpinner.setSelection(currentMonth);
        selectedMonth = currentMonth;

        AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getId() == R.id.year_spinner) {
                    selectedYear = Integer.parseInt(years.get(position));
                } else if (parent.getId() == R.id.month_spinner) {
                    selectedMonth = position;
                }
                loadOrders();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };

        yearSpinner.setOnItemSelectedListener(itemSelectedListener);
        monthSpinner.setOnItemSelectedListener(itemSelectedListener);
    }

    private void loadOrders() {
        if (orderStatus == null) return;

        if (loadingDialog != null && !loadingDialog.isShowing()) {
            loadingDialog.show();
        }

        if (orderListener != null) {
            orderListener.remove();
        }

        Query query = db.collection("ORDERS");

        if (!orderStatus.equals("Total")) {
            query = query.whereEqualTo("globalStatus", orderStatus);
        }

        if (showFilter) {
            Calendar startCal = Calendar.getInstance();
            startCal.set(selectedYear, selectedMonth, 1, 0, 0, 0);
            startCal.set(Calendar.MILLISECOND, 0);
            Date startDate = startCal.getTime();

            Calendar endCal = Calendar.getInstance();
            endCal.set(selectedYear, selectedMonth, startCal.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59);
            endCal.set(Calendar.MILLISECOND, 999);
            Date endDate = endCal.getTime();

            query = query.whereGreaterThanOrEqualTo("dateTime", startDate)
                         .whereLessThanOrEqualTo("dateTime", endDate)
                         .orderBy("dateTime", Query.Direction.DESCENDING);
        } else {
            query = query.orderBy("dateTime", Query.Direction.DESCENDING);
        }

        orderListener = query.addSnapshotListener((value, error) -> {
            if (loadingDialog != null) loadingDialog.dismiss();
            if (error != null) {
                Log.e("AdminOrdersActivity", "Listen failed.", error);
                return;
            }

            if (value != null) {
                orderList.clear();
                for (DocumentSnapshot doc : value.getDocuments()) {
                    OrderModel model = doc.toObject(OrderModel.class);
                    if (model != null) {
                        model.setOrderID(doc.getId());

                        orderList.add(model);
                    }
                }
                adapter.notifyDataSetChanged();

                if (orderList.isEmpty()) {
                    ordersRecyclerView.setVisibility(View.GONE);
                    noOrdersText.setVisibility(View.VISIBLE);
                } else {
                    ordersRecyclerView.setVisibility(View.VISIBLE);
                    noOrdersText.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (orderListener != null) {
            orderListener.remove();
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
}
