package com.example.homeelecation.ui.place;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.firestore.DocumentReference;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PlaceRepository {

    private final FirebaseFirestore db;

    @Inject
    public PlaceRepository(FirebaseFirestore db) {
        this.db = db;
    }

    public interface OnOrderCompleteListener {
        void onSuccess();
        void onFailure(String error);
    }

    public void placeOrderBatch(String orderId, Map<String, Map<String, Object>> itemsMap, Map<String, Object> totalDetails, OnOrderCompleteListener listener) {
        WriteBatch batch = db.batch();

        // Save Main Order Document
        DocumentReference mainOrderRef = db.collection("ORDERS").document(orderId);
        batch.set(mainOrderRef, totalDetails);

        // Save Individual Order Items
        for (Map.Entry<String, Map<String, Object>> entry : itemsMap.entrySet()) {
            DocumentReference itemRef = db.collection("ORDERS").document(orderId)
                    .collection("orderItems").document(entry.getKey());
            batch.set(itemRef, entry.getValue());
        }

        batch.commit().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                listener.onSuccess();
            } else {
                listener.onFailure(task.getException() != null ? task.getException().getMessage() : "Unknown error");
            }
        });
    }

    public void updateOrderStatus(String orderId, Map<String, Object> updates, OnOrderCompleteListener listener) {
        db.collection("ORDERS").document(orderId).update(updates)
                .addOnSuccessListener(aVoid -> listener.onSuccess())

                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    public void saveUserOrderHistory(String userId, String orderId, Map<String, Object> historyData, OnOrderCompleteListener listener) {
        db.collection("USER").document(userId).collection("USER_ORDERS").document(orderId)
                .set(historyData)
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }
    public void updateSingleFieldInItems(String orderId, String value,String orderStatus) {
        db.collection("ORDERS").document(orderId).collection("orderItems")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    WriteBatch batch = db.batch();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        // यहाँ सिर्फ वो एक फील्ड अपडेट होगी
                        batch.update(doc.getReference(),"paymentMethod", value,"orderStatus",orderStatus);
                    }
                    batch.commit();
                });
    }
}
