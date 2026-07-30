package com.example.homeelecation.ui.notification;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class NotificationViewModel extends ViewModel {

    private final FirebaseFirestore db;
    private final FirebaseAuth auth;
    private ListenerRegistration registration;

    private final MutableLiveData<List<NotificationModel>> _notifications = new MutableLiveData<>();
    public final LiveData<List<NotificationModel>> notifications = _notifications;

    private final MutableLiveData<Integer> _unreadCount = new MutableLiveData<>(0);
    public final LiveData<Integer> unreadCount = _unreadCount;

    @Inject
    public NotificationViewModel(FirebaseFirestore db, FirebaseAuth auth) {
        this.db = db;
        this.auth = auth;
    }

    public void startNotificationListener() {
        String uid = auth.getUid();
        if (uid == null) return;

        if (registration != null) registration.remove();

        registration = db.collection("USER").document(uid)
                .collection("MY_NOTIFICATIONS")
                .addSnapshotListener((value, error) -> {
                    if (value != null) {
                        List<NotificationModel> list = new ArrayList<>();
                        int unread = 0;
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            NotificationModel model = new NotificationModel(
                                    doc.getId(),
                                    doc.getString("Image"),
                                    doc.getString("Body"),
                                    doc.getBoolean("Read")
                            );
                            list.add(0, model);
                            if (Boolean.FALSE.equals(doc.getBoolean("Read"))) unread++;
                        }
                        _notifications.setValue(list);
                        _unreadCount.setValue(unread);
                    }
                });
    }

    public void stopNotificationListener() {
        if (registration != null) {
            registration.remove();
            registration = null;
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        stopNotificationListener();
    }
}
