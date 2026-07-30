package com.example.deliveryboy.order;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deliveryboy.R;
import com.example.deliveryboy.map.MapsActivity;
import com.example.deliveryboy.util.EdgeToEdgeUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class OrderDetailsActivity extends AppCompatActivity {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private Dialog otpDialog;

    private RecyclerView orderItemsRecyclerView;
    private OrderDetailAdapter adapter;
    private List<MyOrderItemModel> orderItemList;

    private TextView orderIdTextView, customerNameTextView, customerAddressTextView, orderStatusTextView, locationTextView;
    private Button updateStatusButton, addExtraItemButton, viewBillButton;
    private FirebaseFirestore db;
    private String currentOrderId, currentProductId;
    private FusedLocationProviderClient fusedLocationClient;
    private FirebaseAuth mAuth;
    private boolean isQuickOrderFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_details);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Task Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Apply EdgeToEdge Insets
        EdgeToEdgeUtils.applyTopInset(findViewById(R.id.app_bar));
        EdgeToEdgeUtils.applyBottomInset(findViewById(R.id.bottom_action_layout));

        // Layout Elements
        orderIdTextView = findViewById(R.id.order_id);
        customerNameTextView = findViewById(R.id.customer_name);
        customerAddressTextView = findViewById(R.id.customer_address);
        orderStatusTextView = findViewById(R.id.order_status);
        locationTextView = findViewById(R.id.location_text_view);
        updateStatusButton = findViewById(R.id.update_status_button);
        addExtraItemButton = findViewById(R.id.add_extra_item_button);
        viewBillButton = findViewById(R.id.view_bill_button);
        Button trackLocationButton = findViewById(R.id.track_location_button);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        orderItemsRecyclerView = findViewById(R.id.order_items_recycler_view);
        orderItemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        orderItemList = new ArrayList<>();
        adapter = new OrderDetailAdapter(orderItemList, this);
        orderItemsRecyclerView.setAdapter(adapter);

        // Intent logic - Updated for Item-Level focus
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("ORDER_ID")) {
            currentOrderId = intent.getStringExtra("ORDER_ID");
            currentProductId = intent.getStringExtra("PRODUCT_ID");
            isQuickOrderFlag = intent.getBooleanExtra("IS_QUICK_ORDER", false);
            
            orderIdTextView.setText("ID: #" + currentOrderId);
            
            if (isQuickOrderFlag) {
                loadQuickOrderDetails(currentOrderId);
            } else {
                if (currentProductId != null) {
                    loadSingleItemDetails(currentOrderId, currentProductId);
                } else {
                    loadOrderDetails(currentOrderId); // Fallback for old list logic
                }
            }
        }

        addExtraItemButton.setOnClickListener(v -> showAddExtraItemDialog());
        viewBillButton.setOnClickListener(v -> fetchAndShowBill());
        //updateStatusButton.setOnClickListener(v -> );
        
        // Use individual item button instead of main button for consistency
        updateStatusButton.setVisibility(View.GONE);

        trackLocationButton.setOnClickListener(v -> {
            Intent mapIntent = new Intent(OrderDetailsActivity.this, MapsActivity.class);
            mapIntent.putExtra("ORDER_ID", currentOrderId);
            startActivity(mapIntent);
        });

        // Location check
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            startLocationUpdates();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            } else {
                Toast.makeText(this, "Location permission is required to track the task.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadQuickOrderDetails(String orderId) {
        db.collection("ORDERS_QUICK").document(orderId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("userName");
                        String address = documentSnapshot.getString("userAddress");
                        String status = documentSnapshot.getString("orderStatus");
                        String mobile = documentSnapshot.getString("userMobile");
                        String serviceName = documentSnapshot.getString("serviceName");
                        String price = String.valueOf(documentSnapshot.get("price"));

                        customerNameTextView.setText(name);
                        customerAddressTextView.setText(address);
                        orderStatusTextView.setText("Contact: " + (mobile != null ? mobile : "N/A"));
                        
                        orderItemList.clear();
                        MyOrderItemModel model = new MyOrderItemModel();
                        model.setOrderID(orderId);
                        model.setProductTitle(serviceName + " (Quick Service)");
                        model.setOrderStatus(status);
                        model.setProductPrice(price != null ? price : "0");
                        model.setQuantity(1);
                        model.setProductImage(null); 
                        
                        orderItemList.add(model);
                        adapter.notifyDataSetChanged();

                        Double lat = documentSnapshot.getDouble("latitude");
                        Double lon = documentSnapshot.getDouble("longitude");
                        if (lat != null && lon != null) {
                            locationTextView.setText("Navigation available");
                        }

                        if ("Completed".equalsIgnoreCase(status) || "Delivered".equalsIgnoreCase(status) || "Cancelled".equalsIgnoreCase(status)) {
                            addExtraItemButton.setVisibility(View.GONE);
                        } else {
                            addExtraItemButton.setVisibility(View.VISIBLE);
                        }
                    } else {
                        Toast.makeText(this, "Service details not found", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

    private void loadSingleItemDetails(String orderId, String productId) {
        db.collection("ORDERS").document(orderId).collection("orderItems").document(productId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        MyOrderItemModel model = doc.toObject(MyOrderItemModel.class);
                        if (model != null) {
                            model.setOrderID(orderId);
                            model.setProductID(doc.getId());
                            
                            orderItemList.clear();
                            orderItemList.add(model);
                            adapter.notifyDataSetChanged();
                            
                            // Load customer details from the same document
                            customerNameTextView.setText(model.getFullName());
                            customerAddressTextView.setText(model.getAddress() + ", " + model.getPinCode());
                            orderStatusTextView.setText("Contact: " + model.getMobile());

                            if ("Completed".equalsIgnoreCase(model.getOrderStatus()) || "Delivered".equalsIgnoreCase(model.getOrderStatus()) || "Cancelled".equalsIgnoreCase(model.getOrderStatus())) {
                                addExtraItemButton.setVisibility(View.GONE);
                            } else {
                                addExtraItemButton.setVisibility(View.VISIBLE);
                            }
                        }
                    } else {
                        Toast.makeText(this, "Task item not found", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

    private void loadOrderDetails(String orderId) {
        // LEGACY: Used if PRODUCT_ID is missing (shows entire order)
        db.collection("ORDERS").document(orderId).collection("orderItems")
                .get()
                .addOnSuccessListener(value -> {
                    if (value == null || value.isEmpty()) return;

                    orderItemList.clear();
                    for (DocumentSnapshot doc : value.getDocuments()) {
                        MyOrderItemModel model = doc.toObject(MyOrderItemModel.class);
                        if (model != null) {
                            model.setOrderID(orderId);
                            model.setProductID(doc.getId());
                            orderItemList.add(model);
                            
                            // Use first item for customer info
                            customerNameTextView.setText(model.getFullName());
                            customerAddressTextView.setText(model.getAddress() + ", " + model.getPinCode());
                            orderStatusTextView.setText("Contact: " + model.getMobile());
                        }
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
                .setMinUpdateIntervalMillis(2000)
                .build();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult != null && locationResult.getLastLocation() != null) {
                    double latitude = locationResult.getLastLocation().getLatitude();
                    double longitude = locationResult.getLastLocation().getLongitude();
                    locationTextView.setText("Location: " + latitude + ", " + longitude);
                }
            }
        }, Looper.getMainLooper());
    }

    private void updateOrderStatus(String orderId, boolean toDelivered) {
        String collectionPath = isQuickOrderFlag ? "ORDERS_QUICK" : "ORDERS";
        String statusField = isQuickOrderFlag ? "orderStatus" : "globalStatus";
        String deliveredStatus = "Completed";

        DocumentReference orderRef = db.collection(collectionPath).document(orderId);

        Map<String, Object> updates = new HashMap<>();
        if (toDelivered) {
            updates.put(statusField, deliveredStatus);
        }

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    updates.put("latitude", location.getLatitude());
                    updates.put("longitude", location.getLongitude());
                    updates.put("deliveryBoyLocation", new GeoPoint(location.getLatitude(), location.getLongitude()));
                }
                orderRef.update(updates)
                        .addOnSuccessListener(aVoid -> {
                            if (toDelivered) {
                                finalizeEarnings(orderId);
                            } else {
                                Toast.makeText(OrderDetailsActivity.this, "Status updated.", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        })
                        .addOnFailureListener(e -> Toast.makeText(OrderDetailsActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            });
        } else {
            orderRef.update(updates)
                    .addOnSuccessListener(aVoid -> {
                        if (toDelivered) {
                            finalizeEarnings(orderId);
                        } else {
                            Toast.makeText(OrderDetailsActivity.this, "Status updated.", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(OrderDetailsActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private void finalizeEarnings(String orderId) {
        String uid = mAuth.getCurrentUser().getUid();
        DocumentReference boyRef = db.collection("delivery_boy").document(uid);
        
        Map<String, Object> earningData = new HashMap<>();
        earningData.put("orderId", orderId);
        earningData.put("amount", 50.0);
        earningData.put("timestamp", FieldValue.serverTimestamp());
        earningData.put("isQuickOrder", isQuickOrderFlag);

        db.runTransaction(transaction -> {
            transaction.update(boyRef, "main balance", FieldValue.increment(50));
            DocumentReference historyRef = boyRef.collection("earnings_history").document();
            transaction.set(historyRef, earningData);
            return null;
        }).addOnSuccessListener(aVoid -> {
            Toast.makeText(this, "Earnings Updated!", Toast.LENGTH_LONG).show();
            finish();
        }).addOnFailureListener(e -> {
            Log.e("OrderDetails", "Earning update failed", e);
            finish();
        });
    }

    public void handleItemDelivery(String orderId, String productId, Map<String, Object> updates, boolean isQuickOrder) {
        otpDialog = new Dialog(OrderDetailsActivity.this);
        otpDialog.setContentView(R.layout.otp_verification_dialog);
        otpDialog.setCancelable(true);

        EditText otpEditText = otpDialog.findViewById(R.id.otp_edit_text);
        Button verifyOtpButton = otpDialog.findViewById(R.id.verify_otp_button);

        verifyOtpButton.setOnClickListener(v -> {
            String enteredOtpString = otpEditText.getText().toString().trim();
            if (enteredOtpString.length() == 6) {
                long enteredOtp = Long.parseLong(enteredOtpString);
                verifyItemOtp(enteredOtp, orderId, productId, updates, isQuickOrder, otpEditText, verifyOtpButton);
            } else {
                otpEditText.setError("Enter 6-digit OTP");
            }
        });

        otpDialog.show();
    }

    private void verifyItemOtp(long enteredOtp, String orderId, String productId, Map<String, Object> updates, boolean isQuickOrder, EditText otpField, Button verifyBtn) {
        verifyBtn.setEnabled(false);
        verifyBtn.setText("VERIFYING...");

        if (isQuickOrder) {
            db.collection("ORDERS_QUICK").document(orderId).get().addOnSuccessListener(doc -> {
                verifyBtn.setEnabled(true);
                verifyBtn.setText("VERIFY");
                
                Long storedOtp = doc.getLong("otp");
                if (storedOtp != null && enteredOtp == storedOtp) {
                    otpDialog.dismiss();
                    adapter.updateQuickOrderStatus(orderId, "Completed");
                } else {
                    otpField.setError("Invalid OTP");
                }
            });
        } else {
            db.collection("ORDERS").document(orderId).collection("orderItems").document(productId).get().addOnSuccessListener(doc -> {
                verifyBtn.setEnabled(true);
                verifyBtn.setText("VERIFY");
                
                Long storedOtp = doc.getLong("otp");
                if (storedOtp != null && enteredOtp == storedOtp) {
                    otpDialog.dismiss();
                    adapter.updateFirebaseOrder(orderId, productId, updates);
                } else {
                    otpField.setError("Invalid OTP");
                }
            });
        }
    }

    private void showOtpDialog() {
        otpDialog = new Dialog(OrderDetailsActivity.this);
        otpDialog.setContentView(R.layout.otp_verification_dialog);
        otpDialog.setCancelable(true);

        EditText otpEditText = otpDialog.findViewById(R.id.otp_edit_text);
        Button verifyOtpButton = otpDialog.findViewById(R.id.verify_otp_button);

        verifyOtpButton.setOnClickListener(v -> {
            String enteredOtpString = otpEditText.getText().toString().trim();
            if (enteredOtpString.length() == 6) {
                long enteredOtp = Long.parseLong(enteredOtpString);
                verifyOtp(enteredOtp, otpEditText, verifyOtpButton);
            } else {
                otpEditText.setError("OTP must be 6 digits.");
            }
        });

        otpDialog.show();
    }

    private void verifyOtp(long enteredOtp, EditText otpEditText, Button verifyButton) {
        verifyButton.setEnabled(false);
        verifyButton.setText("VERIFYING...");

        String collectionPath = isQuickOrderFlag ? "ORDERS_QUICK" : "ORDERS";

        db.collection(collectionPath).document(currentOrderId).get()
                .addOnCompleteListener(task -> {
                    verifyButton.setEnabled(true);
                    verifyButton.setText("VERIFY");

                    if (task.isSuccessful() && task.getResult().exists()) {
                        Long storedOtpLong = task.getResult().getLong("otp"); 
                        if (storedOtpLong != null && enteredOtp == storedOtpLong) {
                            updateOrderStatus(currentOrderId, true);
                            otpDialog.dismiss();
                        } else {
                            otpEditText.setError("Incorrect OTP.");
                        }
                    } else {
                        Toast.makeText(OrderDetailsActivity.this, "Order not found.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showAddExtraItemDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_add_extra_item, null);
        EditText descEt = view.findViewById(R.id.extra_item_desc);
        EditText qtyEt = view.findViewById(R.id.extra_item_qty);
        EditText priceEt = view.findViewById(R.id.extra_item_price);

        new AlertDialog.Builder(this)
                .setView(view)
                .setPositiveButton("Add to Bill", (dialog, which) -> {
                    String desc = descEt.getText().toString().trim();
                    String qtyStr = qtyEt.getText().toString().trim();
                    String priceStr = priceEt.getText().toString().trim();

                    if (!desc.isEmpty() && !qtyStr.isEmpty() && !priceStr.isEmpty()) {
                        int qty = Integer.parseInt(qtyStr);
                        double price = Double.parseDouble(priceStr);
                        saveExtraItem(desc, qty, price);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void saveExtraItem(String desc, int qty, double price) {
        if (isQuickOrderFlag) {
            updateQuickOrderExtraItems(desc, qty, price);
        } else {
            if (!orderItemList.isEmpty()) {
                updateNormalOrderExtraItems(orderItemList.get(0).getProductID(), desc, qty, price);
            }
        }
    }

    private void updateQuickOrderExtraItems(String desc, int qty, double price) {
        DocumentReference docRef = db.collection("ORDERS_QUICK").document(currentOrderId);
        db.runTransaction(transaction -> {
            DocumentSnapshot snapshot = transaction.get(docRef);
            List<Map<String, Object>> billItems = (List<Map<String, Object>>) snapshot.get("billItems");
            if (billItems == null) billItems = new ArrayList<>();
            
            Map<String, Object> newItem = new HashMap<>();
            newItem.put("desc", desc);
            newItem.put("qty", qty);
            newItem.put("amount", price * qty);
            billItems.add(newItem);

            double originalPrice = getNumericValue(snapshot, "price");
            double totalExtra = 0;
            for (Map<String, Object> item : billItems) {
                Object amt = item.get("amount");
                if (amt instanceof Number) totalExtra += ((Number) amt).doubleValue();
            }
            
            String finalTotal = String.valueOf((int)(originalPrice + totalExtra));
            transaction.update(docRef, "billItems", billItems);
            transaction.update(docRef, "finalTotal", finalTotal);
            return null;
        }).addOnSuccessListener(aVoid -> Toast.makeText(this, "Quick order bill updated", Toast.LENGTH_SHORT).show());
    }

    private void updateNormalOrderExtraItems(String productId, String desc, int qty, double price) {
        DocumentReference itemRef = db.collection("ORDERS").document(currentOrderId)
                .collection("orderItems").document(productId);

        db.runTransaction(transaction -> {
            DocumentSnapshot snapshot = transaction.get(itemRef);
            List<Map<String, Object>> billItems = (List<Map<String, Object>>) snapshot.get("billItems");
            if (billItems == null) billItems = new ArrayList<>();

            Map<String, Object> newItem = new HashMap<>();
            newItem.put("desc", desc);
            newItem.put("qty", qty);
            newItem.put("amount", price * qty);
            billItems.add(newItem);

            double unitPrice = getNumericValue(snapshot, "productPrice");
            long productQty = snapshot.getLong("productQuantity") != null ? snapshot.getLong("productQuantity") : 1;
            double baseTotal = unitPrice * productQty;
            
            double totalExtra = 0;
            for (Map<String, Object> item : billItems) {
                Object amt = item.get("amount");
                if (amt instanceof Number) totalExtra += ((Number) amt).doubleValue();
            }
            
            String finalTotal = String.valueOf((int)(baseTotal + totalExtra));
            transaction.update(itemRef, "billItems", billItems);
            transaction.update(itemRef, "finalTotal", finalTotal);
            return null;
        }).addOnSuccessListener(aVoid -> Toast.makeText(this, "Item bill updated", Toast.LENGTH_SHORT).show());
    }

    /**
     * Helper to safely extract a double value from a Firestore field, 
     * whether it's stored as a String or a Number.
     */
    private double getNumericValue(DocumentSnapshot snapshot, String field) {
        Object val = snapshot.get(field);
        if (val instanceof Number) {
            return ((Number) val).doubleValue();
        } else if (val instanceof String) {
            try {
                return Double.parseDouble((String) val);
            } catch (Exception e) {
                return 0.0;
            }
        }
        return 0.0;
    }

    private void fetchAndShowBill() {
        String collectionPath = isQuickOrderFlag ? "ORDERS_QUICK" : "ORDERS";
        db.collection(collectionPath).document(currentOrderId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        showProfessionalBill(documentSnapshot);
                    }
                });
    }

    private void showProfessionalBill(DocumentSnapshot mainDoc) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.layout_professional_invoice, null);
        AlertDialog dialog = new AlertDialog.Builder(this, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen)
                .setView(dialogView).create();

        TextView billId = dialogView.findViewById(R.id.bill_order_id);
        TextView billTotal = dialogView.findViewById(R.id.bill_amount);
        TextView billService = dialogView.findViewById(R.id.bill_service_name);
        TextView billDate = dialogView.findViewById(R.id.bill_date);
        LinearLayout itemsContainer = dialogView.findViewById(R.id.invoice_items_container);

        billId.setText("Order #" + currentOrderId);
        
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MMM-yyyy hh:mm a", java.util.Locale.getDefault());
        if (mainDoc.contains("dateTime")) {
            billDate.setText(sdf.format(mainDoc.getDate("dateTime")));
        }

        if (isQuickOrderFlag) {
            billService.setText(mainDoc.getString("serviceName"));
            String total = mainDoc.getString("finalTotal") != null ? mainDoc.getString("finalTotal") : mainDoc.getString("price");
            billTotal.setText("₹" + total);
            addInvoiceRow(itemsContainer, mainDoc.getString("serviceName"), "1", mainDoc.getString("price"));
            
            List<Map<String, Object>> billItems = (List<Map<String, Object>>) mainDoc.get("billItems");
            if (billItems != null) {
                for (Map<String, Object> item : billItems) {
                    addInvoiceRow(itemsContainer, (String)item.get("desc"), String.valueOf(item.get("qty")), String.valueOf(item.get("amount")));
                }
            }
        } else {
            if (!orderItemList.isEmpty()) {
                MyOrderItemModel model = orderItemList.get(0);
                billService.setText(model.getProductTitle() + (orderItemList.size() > 1 ? " (+ others)" : ""));
                
                db.collection("ORDERS").document(currentOrderId).collection("orderItems").document(model.getProductID())
                        .get().addOnSuccessListener(doc -> {
                            String total = doc.getString("finalTotal") != null ? doc.getString("finalTotal") : String.valueOf(Long.parseLong(doc.getString("productPrice")) * (long)doc.get("productQuantity"));
                            billTotal.setText("₹" + total);
                            for (MyOrderItemModel item : orderItemList) {
                                addInvoiceRow(itemsContainer, item.getProductTitle(), String.valueOf(item.getQuantity()), item.getProductPrice());
                            }
                            List<Map<String, Object>> billItems = (List<Map<String, Object>>) doc.get("billItems");
                            if (billItems != null) {
                                for (Map<String, Object> item : billItems) {
                                    addInvoiceRow(itemsContainer, (String)item.get("desc"), String.valueOf(item.get("qty")), String.valueOf(item.get("amount")));
                                }
                            }
                        });
            }
        }

        dialogView.findViewById(R.id.close_bill_button).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void addInvoiceRow(LinearLayout container, String desc, String qty, String price) {
        View row = LayoutInflater.from(this).inflate(R.layout.invoice_item_row, null);
        ((TextView) row.findViewById(R.id.row_desc)).setText(desc);
        ((TextView) row.findViewById(R.id.row_qty)).setText(qty);
        ((TextView) row.findViewById(R.id.row_price)).setText("₹" + price);
        container.addView(row);
    }
}
