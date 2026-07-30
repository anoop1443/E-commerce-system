package com.example.homeelecation.ui.wishList;

import android.app.Application;
import android.app.Dialog;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class WishlistViewModel extends AndroidViewModel {

    private static final String TAG = "WishlistViewModel";

    private final MutableLiveData<ArrayList<WishlistModel>> wishlistItemsLiveData;
    private final MutableLiveData<Boolean> isLoadingLiveData;
    private final MutableLiveData<String> errorLiveData;
    private final MutableLiveData<List<String>> wishlistedProductIdsLiveData;

    private final FirebaseFirestore db;
    private final FirebaseAuth auth;

    public WishlistViewModel(@NonNull Application application) {
        super(application);
        wishlistItemsLiveData = new MutableLiveData<>();
        isLoadingLiveData = new MutableLiveData<>();
        errorLiveData = new MutableLiveData<>();
        wishlistedProductIdsLiveData = new MutableLiveData<>();

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    public LiveData<ArrayList<WishlistModel>> getWishlistItems() {
        return wishlistItemsLiveData;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoadingLiveData;
    }

    public LiveData<String> getError() {
        return errorLiveData;
    }

    public LiveData<List<String>> getWishlistedProductIds() {
        return wishlistedProductIdsLiveData;
    }

    public void loadWishlist(Dialog loadingDialog, FirebaseUser currentUser) {
        if (loadingDialog != null) loadingDialog.show();

        isLoadingLiveData.setValue(true);

        if (currentUser == null) {
            wishlistItemsLiveData.setValue(new ArrayList<>());
            wishlistedProductIdsLiveData.setValue(new ArrayList<>());
            isLoadingLiveData.setValue(false);
            if (loadingDialog != null) loadingDialog.dismiss();
            return;
        }

        String userId = currentUser.getUid();

        db.collection("USER").document(userId).collection("MY_WISHLIST")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<String> productIds = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String productId = document.getString("product_ID");
                            if (productId != null) {
                                productIds.add(productId);
                            }
                        }
                        wishlistedProductIdsLiveData.postValue(productIds);

                        if (productIds.isEmpty()) {
                            wishlistItemsLiveData.postValue(new ArrayList<>());
                            isLoadingLiveData.postValue(false);
                        } else {
                            fetchProductDetails(productIds);
                        }
                    } else {
                        String err = task.getException() != null ? task.getException().getMessage() : "Unknown Error";
                        errorLiveData.postValue("Wishlist loading error: " + err);
                        isLoadingLiveData.postValue(false);
                    }
                    if (loadingDialog != null) loadingDialog.dismiss();
                });
    }

    private void fetchProductDetails(List<String> productIds) {
        ArrayList<WishlistModel> itemsList = new ArrayList<>();
        AtomicInteger counter = new AtomicInteger(0);
        int total = productIds.size();

        for (String pID : productIds) {
            db.collection("Product_Details").document(pID)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            DocumentSnapshot doc = task.getResult();
                            if (doc.exists()) {
                                // Safe image fetch
                                String firstImage = "";
                                Object imagesObj = doc.get("imageUrls");
                                if (imagesObj instanceof List) {
                                    List<?> imagesList = (List<?>) imagesObj;
                                    if (!imagesList.isEmpty() && imagesList.get(0) instanceof String) {
                                        firstImage = (String) imagesList.get(0);
                                    }
                                }

                                // Safe numeric fetch
                                long freeCoupon = 0L;
                                Object couponObj = doc.get("freeCoupon");
                                if (couponObj instanceof Number) freeCoupon = ((Number) couponObj).longValue();

                                double starRating = 0.0;
                                Object ratingObj = doc.get("starRating");
                                if (ratingObj instanceof Number) starRating = ((Number) ratingObj).doubleValue();

                                long totalRatings = 0L;
                                Object tRatingsObj = doc.get("totalRatings");
                                if (tRatingsObj instanceof Number) totalRatings = ((Number) tRatingsObj).longValue();

                                long productPrise = 0L;
                                Object priceObj = doc.get("productPrise");
                                if (priceObj instanceof Number) productPrise = ((Number) priceObj).longValue();

                                long productCatPrise = 0L;
                                Object catPriceObj = doc.get("productCatPrise");
                                if (catPriceObj instanceof Number) productCatPrise = ((Number) catPriceObj).longValue();

                                String title = doc.getString("productTitle");
                                String method = doc.getString("paymentMethod");

                                itemsList.add(new WishlistModel(
                                        pID, firstImage, freeCoupon, starRating, totalRatings,
                                        title != null ? title : "",
                                        productPrise, productCatPrise,
                                        method != null ? method : ""
                                ));
                            }
                        }

                        // Spinner management - whether success or failure
                        if (counter.incrementAndGet() == total) {
                            wishlistItemsLiveData.postValue(itemsList);
                            isLoadingLiveData.postValue(false);
                        }
                    });
        }
    }

    public void removeFromWishlist(String productId) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            errorLiveData.setValue("Login required");
            return;
        }

        isLoadingLiveData.setValue(true);
        String userId = currentUser.getUid();

        db.collection("USER").document(userId).collection("MY_WISHLIST").document(productId)
                .delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> currentIds = wishlistedProductIdsLiveData.getValue();
                        if (currentIds != null) {
                            currentIds.remove(productId);
                            wishlistedProductIdsLiveData.postValue(currentIds);
                        }

                        ArrayList<WishlistModel> currentItems = wishlistItemsLiveData.getValue();
                        if (currentItems != null) {
                            currentItems.removeIf(item -> item.getProductID().equals(productId));
                            wishlistItemsLiveData.postValue(currentItems);
                        }
                    } else {
                        String err = task.getException() != null ? task.getException().getMessage() : "Failed to remove";
                        errorLiveData.postValue(err);
                    }
                    isLoadingLiveData.postValue(false);
                });
    }

    public void toggleWishlistStatus(String productId) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            errorLiveData.setValue("Login required");
            return;
        }

        isLoadingLiveData.setValue(true);
        String userId = currentUser.getUid();
        DocumentReference wishlistDocRef = db.collection("USER").document(userId).collection("MY_WISHLIST").document(productId);

        List<String> currentIds = wishlistedProductIdsLiveData.getValue();
        boolean alreadyInWishlist = (currentIds != null && currentIds.contains(productId));

        if (alreadyInWishlist) {
            wishlistDocRef.delete()
                    .addOnSuccessListener(aVoid -> {
                        if (currentIds != null) {
                            currentIds.remove(productId);
                            wishlistedProductIdsLiveData.postValue(currentIds);
                        }
                        ArrayList<WishlistModel> currentItems = wishlistItemsLiveData.getValue();
                        if (currentItems != null) {
                            currentItems.removeIf(item -> item.getProductID().equals(productId));
                            wishlistItemsLiveData.postValue(currentItems);
                        }
                        isLoadingLiveData.postValue(false);
                    })
                    .addOnFailureListener(e -> {
                        errorLiveData.postValue("Remove failed");
                        isLoadingLiveData.postValue(false);
                    });
        } else {
            Map<String, Object> productData = new HashMap<>();
            productData.put("product_ID", productId);

            wishlistDocRef.set(productData)
                    .addOnSuccessListener(aVoid -> {
                        if (currentIds != null) {
                            currentIds.add(productId);
                            wishlistedProductIdsLiveData.postValue(currentIds);
                        } else {
                            List<String> newList = new ArrayList<>();
                            newList.add(productId);
                            wishlistedProductIdsLiveData.postValue(newList);
                        }
                        isLoadingLiveData.postValue(false);
                    })
                    .addOnFailureListener(e -> {
                        errorLiveData.postValue("Add failed");
                        isLoadingLiveData.postValue(false);
                    });
        }
    }
}
