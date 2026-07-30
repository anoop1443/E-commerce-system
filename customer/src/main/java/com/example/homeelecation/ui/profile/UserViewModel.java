package com.example.homeelecation.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class UserViewModel extends ViewModel {

    private final FirebaseFirestore db;
    private final FirebaseAuth auth;

    private final MutableLiveData<UserModel> _userData = new MutableLiveData<>();
    public final LiveData<UserModel> userData = _userData;

    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>();
    public final LiveData<Boolean> isLoading = _isLoading;

    private final MutableLiveData<String> _error = new MutableLiveData<>();
    public final LiveData<String> error = _error;

    @Inject
    public UserViewModel(FirebaseFirestore db, FirebaseAuth auth) {
        this.db = db;
        this.auth = auth;
    }

    public void loadUserData() {
        String uid = auth.getUid();
        if (uid == null) return;

        _isLoading.setValue(true);
        db.collection("USER").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        UserModel user = new UserModel(
                                documentSnapshot.getString("Full Name"),
                                documentSnapshot.getString("mobile"),
                                documentSnapshot.getString("email"),
                                documentSnapshot.getString("profile image"),
                                documentSnapshot.getString("gender")
                        );
                        _userData.setValue(user);
                    }
                    _isLoading.setValue(false);
                })
                .addOnFailureListener(e -> {
                    _error.setValue(e.getMessage());
                    _isLoading.setValue(false);
                });
    }

    public static class UserModel {
        public String fullName;
        public String mobile;
        public String email;
        public String profileImage;
        public String gender;

        public UserModel(String fullName, String mobile, String email, String profileImage, String gender) {
            this.fullName = fullName;
            this.mobile = mobile;
            this.email = email;
            this.profileImage = profileImage;
            this.gender = gender;
        }
    }
}
