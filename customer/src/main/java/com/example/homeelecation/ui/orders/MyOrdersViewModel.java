package com.example.homeelecation.ui.orders;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class MyOrdersViewModel extends AndroidViewModel {
    private static final String TAG = "MyOrdersViewModel";
    private final FirebaseFirestore db;
    private final FirebaseAuth auth;

    private final MutableLiveData<List<MyOrderItemModel>> ordersLiData = new MutableLiveData<>();
    private final MutableLiveData<List<QuickOrderModel>> quickOrdersLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoadingData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isEmptyLiveData = new MutableLiveData<>();
    
    public LiveData<List<MyOrderItemModel>> getMyOrderItemModelList() {
        return ordersLiData;
    }

    public LiveData<List<QuickOrderModel>> getQuickOrdersLiveData() {
        return quickOrdersLiveData;
    }


    @Inject
    public MyOrdersViewModel( Application application,FirebaseFirestore db,FirebaseAuth auth) {
        super(application);
        this.db = db;
        this.auth = auth;
    }

    public MutableLiveData<List<MyOrderItemModel>> getOrdersLiData() {
        return ordersLiData;
    }

    public MutableLiveData<Boolean> getIsLoadingData() {
        return isLoadingData;
    }

    public MutableLiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public MutableLiveData<Boolean> getIsEmptyLiveData() {
        return isEmptyLiveData;
    }

    public void loadQuickOrders() {
        FirebaseUser currentUser = auth.getCurrentUser();
        isLoadingData.setValue(true);
        isEmptyLiveData.setValue(false);

        if (currentUser == null) {
            errorLiveData.setValue("Please log in first");
            isLoadingData.setValue(false);
            return;
        }
        String userId = currentUser.getUid();

        db.collection("ORDERS_QUICK")
                .whereEqualTo("userId", userId)
                .orderBy("dateTime", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<QuickOrderModel> quickOrders = new ArrayList<>();
                        for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                            QuickOrderModel model = doc.toObject(QuickOrderModel.class);
                            if (model != null) {
                                model.setDateTime(doc.getDate("dateTime")); // Safe Date fetching
                                quickOrders.add(model);
                            }
                        }
                        quickOrdersLiveData.postValue(quickOrders);
                        isEmptyLiveData.postValue(quickOrders.isEmpty());
                    } else {
                        String errorMsg = task.getException() != null ? task.getException().getMessage() : "Unknown Error";
                        errorLiveData.postValue("Error loading quick orders: " + errorMsg);
                    }
                    isLoadingData.postValue(false);
                });
    }

    public void loadMyOrders(){
        FirebaseUser currentUser = auth.getCurrentUser();
        isLoadingData.setValue(true);
        isEmptyLiveData.setValue(false);

        if (currentUser == null){
            errorLiveData.setValue("Please log in first");
            isLoadingData.setValue(false);
            return;
        }
        String userId = currentUser.getUid();

        db.collection("USER").document(userId).collection("USER_ORDERS")
                .orderBy("dateTime", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful() && task.getResult() !=null){
                        List<DocumentSnapshot> userOrders = task.getResult().getDocuments();

                        if (userOrders.isEmpty()){
                            ordersLiData.postValue(new ArrayList<>());
                            isEmptyLiveData.postValue(true);
                            isLoadingData.postValue(false);
                            return;
                        }
                        fetchOrderItems(userOrders);

                    }else {
                        String errorMsg = task.getException() != null ? task.getException().getMessage() : "Unknown Error";
                        errorLiveData.postValue("Error loading orders: " + errorMsg);
                        isLoadingData.postValue(false);
                    }

                });
    }

    private void fetchOrderItems(List<DocumentSnapshot> userOrders) {
        List<MyOrderItemModel> allItems = new ArrayList<>();
        AtomicInteger loadedOrderCount = new AtomicInteger(0);
        int totalOrders = userOrders.size();

        for (DocumentSnapshot userOrderDoc : userOrders){
            String orderId = userOrderDoc.getString("orderID");
            if (orderId == null){
                if (loadedOrderCount.incrementAndGet() == totalOrders){
                    processFinalList(allItems);
                }
                continue;
            }
            db.collection("ORDERS").document(orderId).collection("orderItems")
                    .get()
                    .addOnCompleteListener(itemsTask ->{
                        if (itemsTask.isSuccessful() && itemsTask.getResult() != null){
                            for (DocumentSnapshot itemDoc : itemsTask.getResult().getDocuments()){
                                MyOrderItemModel model = itemDoc.toObject(MyOrderItemModel.class);
                                if (model == null) model = new MyOrderItemModel();

                                // Safe String fetching
                                model.setProductID(itemDoc.getString("productID"));
                                model.setOrderID(itemDoc.getString("orderID"));
                                model.setProductTitle(itemDoc.getString("productTitle"));
                                model.setProductImage(itemDoc.getString("productImage"));
                                model.setOrderStatus(itemDoc.getString("orderStatus"));
                                
                                // Safe Date fetching
                                model.setOrderedDate(itemDoc.getDate("orderedDate"));
                                model.setPackedDate(itemDoc.getDate("packedDate"));
                                model.setShippedDate(itemDoc.getDate("shippedDate"));
                                model.setDeliveredDate(itemDoc.getDate("deliveredDate"));
                                model.setCancelledDate(itemDoc.getDate("cancelledDate"));
                                
                                // Address details
                                model.setFullName(itemDoc.getString("fullName"));
                                model.setAddress(itemDoc.getString("address"));
                                model.setMobile(itemDoc.getString("mobile"));
                                model.setPinCode(itemDoc.getString("pinCode"));
                                
                                // Pricing
                                model.setProductPrice(itemDoc.getString("productPrice"));
                                model.setCutPrice(itemDoc.getString("cutPrice"));
                                model.setUserId(itemDoc.getString("userId"));
                                model.setPaymentMethod(itemDoc.getString("paymentMethod"));
                                
                                // Numeric & Boolean safety
                                Object qtyObj = itemDoc.get("productQuantity");
                                model.setQuantity(qtyObj instanceof Number ? ((Number) qtyObj).longValue() : 0L);
                                model.setDeliveryCharge(itemDoc.getString("deliveryCharge"));
                                Object cancelReqObj = itemDoc.get("cancellationRequested");
                                model.setCancellationRequested(cancelReqObj instanceof Boolean && (Boolean) cancelReqObj);

                                // --- New Dynamic Text Fields ---
                                model.setOrderedTitle(itemDoc.getString("orderedTitle"));
                                model.setOrderedBody(itemDoc.getString("orderedBody"));
                                model.setPackedTitle(itemDoc.getString("packedTitle"));
                                model.setPackedBody(itemDoc.getString("packedBody"));
                                model.setShippedTitle(itemDoc.getString("shippedTitle"));
                                model.setShippedBody(itemDoc.getString("shippedBody"));
                                model.setDeliveredTitle(itemDoc.getString("deliveredTitle"));
                                model.setDeliveredBody(itemDoc.getString("deliveredBody"));

                                model.setBillItems((List<Map<String, Object>>) itemDoc.get("billItems"));
                                model.setFinalTotal(itemDoc.getString("finalTotal"));

                                allItems.add(model);
                            }
                        }

                        if (loadedOrderCount.incrementAndGet() == totalOrders){
                            processFinalList(allItems);
                        }
                    });
        }
    }

    private void processFinalList(List<MyOrderItemModel> allItems) {
        allItems.sort((o1, o2) -> {
            if (o1.getOrderedDate() != null && o2.getOrderedDate() != null) {
                return o2.getOrderedDate().compareTo(o1.getOrderedDate());
            }
            return 0;
        });
        ordersLiData.postValue(allItems);
        isLoadingData.postValue(false);
    }


    private final MutableLiveData<MyOrderItemModel> singleProductLiveData = new MutableLiveData<>();

    public LiveData<MyOrderItemModel> getSingleProductLiveData() {
        return singleProductLiveData;
    }

    public void loadSingleProductDetails(String orderId, String productId) {
        isLoadingData.postValue(true);
        if (orderId == null || productId == null) {
            singleProductLiveData.postValue(null);
            isLoadingData.postValue(false);
            return;
        }

        db.collection("ORDERS").document(orderId)
                .collection("orderItems").document(productId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        MyOrderItemModel model = documentSnapshot.toObject(MyOrderItemModel.class);
                        if (model == null) model = new MyOrderItemModel();

                        model.setProductID(documentSnapshot.getString("productID"));
                        model.setOrderID(documentSnapshot.getString("orderID"));
                        model.setProductTitle(documentSnapshot.getString("productTitle"));
                        model.setProductImage(documentSnapshot.getString("productImage"));
                        model.setOrderStatus(documentSnapshot.getString("orderStatus"));
                        model.setOrderedDate(documentSnapshot.getDate("orderedDate"));
                        model.setPackedDate(documentSnapshot.getDate("packedDate"));
                        model.setShippedDate(documentSnapshot.getDate("shippedDate"));
                        model.setDeliveredDate(documentSnapshot.getDate("deliveredDate"));
                        model.setCancelledDate(documentSnapshot.getDate("cancelledDate"));
                        model.setFullName(documentSnapshot.getString("fullName"));
                        model.setAddress(documentSnapshot.getString("address"));
                        model.setMobile(documentSnapshot.getString("mobile"));
                        model.setPinCode(documentSnapshot.getString("pinCode"));
                        model.setProductPrice(documentSnapshot.getString("productPrice"));
                        model.setCutPrice(documentSnapshot.getString("cutPrice"));
                        model.setUserId(documentSnapshot.getString("userId"));
                        model.setPaymentMethod(documentSnapshot.getString("paymentMethod"));
                        
                        Object qtyObj = documentSnapshot.get("productQuantity");
                        model.setQuantity(qtyObj instanceof Number ? ((Number) qtyObj).longValue() : 0L);
                        model.setDeliveryCharge(documentSnapshot.getString("deliveryCharge"));
                        Object cancelReqObj = documentSnapshot.get("cancellationRequested");
                        model.setCancellationRequested(cancelReqObj instanceof Boolean && (Boolean) cancelReqObj);

                        // Dynamic text fields
                        model.setOrderedTitle(documentSnapshot.getString("orderedTitle"));
                        model.setOrderedBody(documentSnapshot.getString("orderedBody"));
                        model.setPackedTitle(documentSnapshot.getString("packedTitle"));
                        model.setPackedBody(documentSnapshot.getString("packedBody"));
                        model.setShippedTitle(documentSnapshot.getString("shippedTitle"));
                        model.setShippedBody(documentSnapshot.getString("shippedBody"));
                        model.setDeliveredTitle(documentSnapshot.getString("deliveredTitle"));
                        model.setDeliveredBody(documentSnapshot.getString("deliveredBody"));

                        model.setBillItems((List<Map<String, Object>>) documentSnapshot.get("billItems"));
                        model.setFinalTotal(documentSnapshot.getString("finalTotal"));

                        singleProductLiveData.postValue(model);
                        isLoadingData.postValue(false);
                    } else {
                        singleProductLiveData.postValue(null);
                        isLoadingData.postValue(false);
                    }
                })
                .addOnFailureListener(e -> {
                    singleProductLiveData.postValue(null);
                    isLoadingData.postValue(false);
                });
    }
}
