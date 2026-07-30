package com.example.homeelecation.ui.details;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * यह रिपॉजिटरी ProductDetailsActivity के लिए सभी डेटा ऑपरेशन्स को संभालती है,
 * जैसे कि प्रोडक्ट डिटेल्स, रेटिंग्स, विशलिस्ट और कार्ट को मैनेज करना।
 */
@Singleton
public class ProductDetailsRepository {

    private final FirebaseFirestore db;

    @Inject
    public ProductDetailsRepository(FirebaseFirestore db) {
        this.db = db;
    }

    /**
     * एक जेनेरिक इंटरफ़ेस जो Firestore से डेटा लोड होने के बाद कॉलबैक प्रदान करता है।
     */
    public interface OnDataLoadedListener<T> {
        void onSuccess(T data);
        void onFailure(String error);
    }

    /**
     * Firestore से किसी प्रोडक्ट की पूरी जानकारी प्राप्त करता है।
     */
    public void getProductDetails(String productId, OnDataLoadedListener<DocumentSnapshot> listener) {
        db.collection("Product_Details").document(productId).get()
                .addOnSuccessListener(listener::onSuccess)
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    /**
     * किसी प्रोडक्ट की रेटिंग को अपडेट करता है (जैसे "5_star" का काउंट)।
     */
    public void updateProductRating(String productId, Map<String, Object> updateMap, OnDataLoadedListener<Void> listener) {
        db.collection("Product_Details").document(productId).update(updateMap)
                .addOnSuccessListener(listener::onSuccess)
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    /**
     * यूजर की रेटिंग को एक अलग सब-कलेक्शन में सेव करता है।
     */
    public void saveUserRating(String userId, String productId, Map<String, Object> ratingData, OnDataLoadedListener<Void> listener) {
        db.collection("USER").document(userId).collection("RATINGS").document(productId).set(ratingData)
                .addOnSuccessListener(listener::onSuccess)
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    /**
     * किसी प्रोडक्ट के लिए यूजर की पिछली रेटिंग प्राप्त करता है।
     */
    public void getUserRating(String userId, String productId, OnDataLoadedListener<DocumentSnapshot> listener) {
        db.collection("USER").document(userId).collection("RATINGS").document(productId).get()
                .addOnSuccessListener(listener::onSuccess)
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    /**
     * यूजर की विशलिस्ट में प्रोडक्ट को जोड़ता या अपडेट करता है।
     */
    public void updateWishlist(String userId, String productId, Map<String, Object> data, OnDataLoadedListener<Void> listener) {
        db.collection("USER").document(userId).collection("MY_WISHLIST").document(productId).set(data)
                .addOnSuccessListener(listener::onSuccess)
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    /**
     * विशलिस्ट से किसी प्रोडक्ट को हटाता है।
     */
    public void removeFromWishlist(String userId, String productId, OnDataLoadedListener<Void> listener) {
        db.collection("USER").document(userId).collection("MY_WISHLIST").document(productId).delete()
                .addOnSuccessListener(listener::onSuccess)
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    /**
     * यूजर के कार्ट में प्रोडक्ट को जोड़ता या अपडेट करता है।
     */
    public void updateCart(String userId, String productId, Map<String, Object> data, OnDataLoadedListener<Void> listener) {
        db.collection("USER").document(userId).collection("MY_CART").document(productId).set(data)
                .addOnSuccessListener(listener::onSuccess)
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }
}
