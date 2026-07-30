package com.example.homeadmin.ui.home2;

import android.content.Context;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Home2ViewModel extends ViewModel {
    private FirebaseFirestore db;

    private MutableLiveData<List<DataModel>> data;

    public MutableLiveData<List<DataModel>> getData(){
        db = FirebaseFirestore.getInstance();
        if (data == null){
            data = new MutableLiveData<>();
            loadData();
        }
        return data;
    }


    private void loadData() {
        //
        // Do an asynchronous operation to fetch data.
        //

        db.collection("USER").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<DataModel> dataModelList = new ArrayList<>();
                for (DocumentSnapshot document : task.getResult()) {
                    String name = document.getString("Full Name");
                    String email = document.getString("mobile");
                    dataModelList.add(new DataModel(name, email));
                }
                data.setValue(dataModelList);
            } else {
                // Handle error
                String errorMessage = Objects.requireNonNull(task.getException()).getMessage();
                // Handle the error


            }
        });


    }


}