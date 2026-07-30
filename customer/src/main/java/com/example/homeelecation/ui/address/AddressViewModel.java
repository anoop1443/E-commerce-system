package com.example.homeelecation.ui.address;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.homeelecation.ui.DbLoadData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class AddressViewModel extends ViewModel {

    private final FirebaseFirestore db;
    private final FirebaseAuth auth;

    private final MutableLiveData<List<AddressesSelectModel>> _addresses = new MutableLiveData<>();
    public final LiveData<List<AddressesSelectModel>> addresses = _addresses;

    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>();
    public final LiveData<Boolean> isLoading = _isLoading;

    private final MutableLiveData<String> _error = new MutableLiveData<>();
    public final LiveData<String> error = _error;

    private final MutableLiveData<Boolean> _navigateToAddAddress = new MutableLiveData<>();
    public final LiveData<Boolean> navigateToAddAddress = _navigateToAddAddress;

    private final MutableLiveData<Boolean> _navigateToPayment = new MutableLiveData<>();
    public final LiveData<Boolean> navigateToPayment = _navigateToPayment;

    private final MutableLiveData<Boolean> _selectionUpdated = new MutableLiveData<>();
    public final LiveData<Boolean> selectionUpdated = _selectionUpdated;

    @Inject
    public AddressViewModel(FirebaseFirestore db, FirebaseAuth auth) {
        this.db = db;
        this.auth = auth;
    }

    public void loadAddresses(boolean openAddIfEmpty, boolean goToPayment) {
        String uid = auth.getUid();
        if (uid == null) return;

        _isLoading.setValue(true);
        db.collection("USER").document(uid).collection("MY_ADDRESSES").get()
                .addOnCompleteListener(task -> {
                    _isLoading.setValue(false);
                    if (task.isSuccessful()) {
                        if (task.getResult().isEmpty()) {
                            if (openAddIfEmpty) _navigateToAddAddress.setValue(true);
                            _addresses.setValue(new ArrayList<>());
                        } else {
                            List<AddressesSelectModel> list = new ArrayList<>();
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                boolean isSelected = doc.getBoolean("selected") != null && doc.getBoolean("selected");
                                list.add(new AddressesSelectModel(
                                        doc.getString("fullName"), doc.getString("mobile"),
                                        doc.getString("pinCode"), doc.getString("state"),
                                        doc.getString("city"), doc.getString("house"),
                                        doc.getString("area"), isSelected, doc.getId()
                                ));
                                if (isSelected) DbLoadData.selectedAddresses = list.size() - 1;
                            }
                            if (DbLoadData.selectedAddresses == -1 && !list.isEmpty()) DbLoadData.selectedAddresses = 0;
                            
                            DbLoadData.addressesSelectModelList.clear();
                            DbLoadData.addressesSelectModelList.addAll(list);
                            _addresses.setValue(list);
                            if (goToPayment) _navigateToPayment.setValue(true);
                        }
                    } else {
                        _error.setValue(task.getException().getMessage());
                    }
                });
    }

    /**
     * Firestore में सिलेक्टेड एड्रेस को अपडेट करता है।
     */
    public void updateSelectedAddress(String oldID, String newID) {
        String uid = auth.getUid();
        if (uid == null) return;

        _isLoading.setValue(true);
        Map<String, Object> unselect = new HashMap<>();
        unselect.put("selected", false);

        db.collection("USER").document(uid).collection("MY_ADDRESSES").document(oldID).update(unselect)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Map<String, Object> select = new HashMap<>();
                        select.put("selected", true);
                        db.collection("USER").document(uid).collection("MY_ADDRESSES").document(newID).update(select)
                                .addOnCompleteListener(task1 -> {
                                    _isLoading.setValue(false);
                                    if (task1.isSuccessful()) {
                                        _selectionUpdated.setValue(true);
                                    } else {
                                        _error.setValue(task1.getException().getMessage());
                                    }
                                });
                    } else {
                        _isLoading.setValue(false);
                        _error.setValue(task.getException().getMessage());
                    }
                });
    }

    public void onNavigationComplete() {
        _navigateToAddAddress.setValue(false);
        _navigateToPayment.setValue(false);
        _selectionUpdated.setValue(false);
    }
}
