package com.example.homeelecation.ui.Cart;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class CartViewModel extends ViewModel {

    private static final String TAG = "CartViewModel";

    private final FirebaseFirestore db;
    private final FirebaseAuth auth;

    private final MutableLiveData<List<CartItemModel>> cartItemsLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<String>> cartProductIdsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Long> totalAmountLiveData = new MutableLiveData<>();
    private final MutableLiveData<Integer> badgeCountLiveData = new MutableLiveData<>();

    @Inject
    public CartViewModel(FirebaseFirestore db, FirebaseAuth auth) {
        this.db = db;
        this.auth = auth;
    }

    public LiveData<List<CartItemModel>> getCartItems() { return cartItemsLiveData; }
    public LiveData<List<String>> getCartProductIds() { return cartProductIdsLiveData; }
    public LiveData<Boolean> getIsLoading() { return isLoadingLiveData; }
    public LiveData<String> getError() { return errorLiveData; }
    public LiveData<Long> getTotalAmount() { return totalAmountLiveData; }
    public LiveData<Integer> getBadgeCount() { return badgeCountLiveData; }

    public void loadCart() {
        FirebaseUser currentUser = auth.getCurrentUser();
        isLoadingLiveData.setValue(true);

        if (currentUser == null) {
            cartItemsLiveData.setValue(new ArrayList<>());
            cartProductIdsLiveData.setValue(new ArrayList<>());
            badgeCountLiveData.setValue(0);
            totalAmountLiveData.setValue(0L);
            isLoadingLiveData.setValue(false);
            return;
        }

        String userId = currentUser.getUid();
        db.collection("USER").document(userId).collection("MY_CART")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> productIds = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            productIds.add(document.getId());
                        }
                        cartProductIdsLiveData.postValue(productIds);
                        badgeCountLiveData.postValue(productIds.size());

                        if (productIds.isEmpty()) {
                            cartItemsLiveData.postValue(new ArrayList<>());
                            totalAmountLiveData.postValue(0L);
                            isLoadingLiveData.postValue(false);
                        } else {
                            fetchProductDetailsForCart(productIds);
                        }
                    } else {
                        errorLiveData.postValue("Failed to load cart: " + task.getException().getMessage());
                        isLoadingLiveData.postValue(false);
                    }
                });
    }

    private void fetchProductDetailsForCart(List<String> productIds) {
        ArrayList<CartItemModel> finalItemsList = new ArrayList<>();
        AtomicLong totalAmount = new AtomicLong(0);
        AtomicInteger loadedCount = new AtomicInteger(0);

        for (String pID : productIds) {
            db.collection("Product_Details").document(pID).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            DocumentSnapshot doc = task.getResult();
                            if (doc.exists()) {
                                
                                // 1. Image URLs - Type Safe check
                                String firstImage = "";
                                Object imagesObj = doc.get("imageUrls");
                                if (imagesObj instanceof List) {
                                    List<?> imagesList = (List<?>) imagesObj;
                                    if (!imagesList.isEmpty() && imagesList.get(0) instanceof String) {
                                        firstImage = (String) imagesList.get(0);
                                    }
                                }

                                // 2. Service Info (Map) - Type Safe check
                                String servicePrice = "0";
                                Object serviceObj = doc.get("service_info");
                                if (serviceObj instanceof Map) {
                                    Map<?, ?> serviceInfo = (Map<?, ?>) serviceObj;
                                    Object price = serviceInfo.get("price");
                                    if (price != null) {
                                        servicePrice = String.valueOf(price);
                                    }
                                }

                                // 3. Numeric Values (Long/Double) - Hyper-Safe conversion
                                long productPrice = 0L;
                                Object pPriceObj = doc.get("productPrise");
                                if (pPriceObj instanceof Number) productPrice = ((Number) pPriceObj).longValue();

                                long productCatPrice = 0L;
                                Object cPriceObj = doc.get("productCatPrise");
                                if (cPriceObj instanceof Number) productCatPrice = ((Number) cPriceObj).longValue();

                                long freeCoupon = 0L;
                                Object couponObj = doc.get("freeCoupon");
                                if (couponObj instanceof Number) freeCoupon = ((Number) couponObj).longValue();

                                // 4. Boolean (inStock) - Type Safe check
                                boolean inStock = false;
                                Object stockObj = doc.get("inStock");
                                if (stockObj instanceof Boolean) inStock = (Boolean) stockObj;

                                // 5. String (productTitle) - Type Safe check
                                String productTitle = "Unknown Product";
                                Object titleObj = doc.get("productTitle");
                                if (titleObj instanceof String) productTitle = (String) titleObj;

                                finalItemsList.add(new CartItemModel(
                                        CartItemModel.CART_ITEM_LAYOUT, pID, firstImage,
                                        productTitle,
                                        String.valueOf(productPrice),
                                        String.valueOf(productCatPrice),
                                        String.valueOf(freeCoupon),
                                        "Order Place next 36 hours",
                                        servicePrice,
                                        1L,
                                        inStock
                                ));
                                totalAmount.addAndGet(productPrice);
                            }
                        }
                        if (loadedCount.incrementAndGet() == productIds.size()) {
                            totalAmountLiveData.postValue(totalAmount.get());
                            if (!finalItemsList.isEmpty()) {
                                finalItemsList.add(new CartItemModel(CartItemModel.CART_TOTAL_AMOUNT_LAYOUT));
                            }
                            cartItemsLiveData.postValue(finalItemsList);
                            isLoadingLiveData.postValue(false);
                        }
                    });
        }
    }

    public void removeFromCart(String productId) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            errorLiveData.setValue("You must login");
            return;
        }
        isLoadingLiveData.setValue(true);
        db.collection("USER").document(currentUser.getUid()).collection("MY_CART").document(productId)
                .delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        loadCart();
                    } else {
                        errorLiveData.postValue("Product remove error");
                        isLoadingLiveData.postValue(false);
                    }
                });
    }

    public void addProductToCartIfNotExists(String productId) {
        FirebaseUser currentUser = auth.getCurrentUser();
        isLoadingLiveData.setValue(true);

        if (currentUser == null) {
            errorLiveData.setValue("Please login");
            isLoadingLiveData.setValue(false);
            return;
        }

        List<String> currentIds = cartProductIdsLiveData.getValue();
        if (currentIds != null && currentIds.contains(productId)) {
            errorLiveData.postValue("Product already in cart");
            isLoadingLiveData.postValue(false);
            return;
        }

        DocumentReference cartDocRef = db.collection("USER").document(currentUser.getUid()).collection("MY_CART").document(productId);
        Map<String, Object> cartItem = new HashMap<>();
        cartItem.put("product_ID", productId);
        cartItem.put("dateTime", FieldValue.serverTimestamp());

        cartDocRef.set(cartItem).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                loadCart();
            } else {
                errorLiveData.postValue("Product add error");
                isLoadingLiveData.postValue(false);
            }
        });
    }
}
