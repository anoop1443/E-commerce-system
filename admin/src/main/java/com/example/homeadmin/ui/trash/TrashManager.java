package com.example.homeadmin.ui.trash;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class TrashManager {
    private static final String TAG = "TrashManager";
    private static final String TRASH_COLLECTION = "TRASH";
    private static final int EXPIRY_DAYS = 7;

    public interface OnTrashOperationListener {
        void onSuccess();
        void onFailure(Exception e);
    }

    public static void moveToTrash(String originalCollection, String documentId, String type, String label, String imageUrl, OnTrashOperationListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(originalCollection).document(documentId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Map<String, Object> data = documentSnapshot.getData();
                Date now = new Date();
                Calendar cal = Calendar.getInstance();
                cal.setTime(now);
                cal.add(Calendar.DAY_OF_YEAR, EXPIRY_DAYS);
                Date expiry = cal.getTime();

                String trashId = db.collection(TRASH_COLLECTION).document().getId();
                TrashModel trashItem = new TrashModel(
                        trashId, documentId, originalCollection, data, now, expiry, type, label, imageUrl
                );

                WriteBatch batch = db.batch();
                batch.set(db.collection(TRASH_COLLECTION).document(trashId), trashItem);
                batch.delete(db.collection(originalCollection).document(documentId));

                batch.commit().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (listener != null) listener.onSuccess();
                    } else {
                        if (listener != null) listener.onFailure(task.getException());
                    }
                });
            } else {
                if (listener != null) listener.onFailure(new Exception("Document does not exist"));
            }
        }).addOnFailureListener(e -> {
            if (listener != null) listener.onFailure(e);
        });
    }

    public static void restoreFromTrash(TrashModel trashItem, OnTrashOperationListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        WriteBatch batch = db.batch();

        batch.set(db.collection(trashItem.getOriginalCollection()).document(trashItem.getOriginalId()), trashItem.getData());
        batch.delete(db.collection(TRASH_COLLECTION).document(trashItem.getTrashId()));

        batch.commit().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (listener != null) listener.onSuccess();
            } else {
                if (listener != null) listener.onFailure(task.getException());
            }
        });
    }

    public static void permanentDelete(TrashModel trashItem, OnTrashOperationListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        
        // Cleanup storage if there are images
        cleanupStorage(trashItem);

        db.collection(TRASH_COLLECTION).document(trashItem.getTrashId()).delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (listener != null) listener.onSuccess();
                    } else {
                        if (listener != null) listener.onFailure(task.getException());
                    }
                });
    }

    private static void cleanupStorage(TrashModel trashItem) {
        // This is a basic implementation. Ideally, we should identify all image URLs in the data map.
        List<String> urlsToDelete = new ArrayList<>();
        if (trashItem.getImageUrl() != null && trashItem.getImageUrl().contains("firebase")) {
            urlsToDelete.add(trashItem.getImageUrl());
        }
        
        // Also check data for imageUrls list (common in products)
        if (trashItem.getData() != null && trashItem.getData().containsKey("imageUrls")) {
            Object imageUrls = trashItem.getData().get("imageUrls");
            if (imageUrls instanceof List) {
                for (Object url : (List<?>) imageUrls) {
                    if (url instanceof String && ((String) url).contains("firebase")) {
                        urlsToDelete.add((String) url);
                    }
                }
            }
        }

        FirebaseStorage storage = FirebaseStorage.getInstance();
        for (String url : urlsToDelete) {
            try {
                StorageReference ref = storage.getReferenceFromUrl(url);
                ref.delete().addOnFailureListener(e -> Log.e(TAG, "Failed to delete storage file: " + url, e));
            } catch (Exception e) {
                Log.e(TAG, "Error getting reference for URL: " + url, e);
            }
        }
    }

    public static void cleanupExpiredItems() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Date now = new Date();

        db.collection(TRASH_COLLECTION)
                .whereLessThan("expiryDate", now)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        TrashModel item = doc.toObject(TrashModel.class);
                        permanentDelete(item, null);
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Failed to cleanup expired items", e));
    }
}
