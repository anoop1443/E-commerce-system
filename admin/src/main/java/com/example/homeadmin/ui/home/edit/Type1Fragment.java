package com.example.homeadmin.ui.home.edit;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homeadmin.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Type1Fragment extends Fragment {

    private RecyclerView adsRecyclerView;
    private AdSelectionAdapter adapter;
    private List<AdModel> adList;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.upload_layout_type1, container, false);

        db = FirebaseFirestore.getInstance();
        adList = new ArrayList<>();
        adapter = new AdSelectionAdapter(adList, getContext());

        adsRecyclerView = view.findViewById(R.id.ads_recycler_view);
        adsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adsRecyclerView.setAdapter(adapter);

        view.findViewById(R.id.upload_type1_button).setOnClickListener(v -> uploadAdId());

        loadAds();

        return view;
    }

    private void loadAds() {
        db.collection("ads").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        adList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            adList.add(new AdModel(document.getId(), document.getString("imageUrl"), document.getString("backgroundColor")));
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.w("FirestoreError", "Error getting documents.", task.getException());
                    }
                });
    }

    private void uploadAdId() {
        Bundle args = getArguments();
        String target = args != null ? args.getString("TARGET") : "HOMEPAGE";
        String categoryId = args != null ? args.getString("CATEGORY_ID") : null;

        AdModel selectedAd = adapter.getSelectedAd();
        if (selectedAd == null) {
            Toast.makeText(getContext(), "Please select an ad!", Toast.LENGTH_SHORT).show();
            return;
        }

        CollectionReference targetCollection;
        if ("HOMEPAGE".equals(target)) {
            targetCollection = db.collection("HOMEPAGE");
        } else if ("CATEGORY_ACTIVITY".equals(target) && categoryId != null) {
            targetCollection = db.collection("CATEGORY").document(categoryId).collection("CATEGORY_ACTIVITY");
        } else {
            Toast.makeText(getContext(), "Invalid target!", Toast.LENGTH_SHORT).show();
            return;
        }

        targetCollection.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                long index = task.getResult().size();
                Map<String, Object> data = new HashMap<>();
                data.put("view_type", 1L);
                data.put("ad_id", selectedAd.getDocumentId());
                data.put("index", index);

                targetCollection.add(data)
                        .addOnSuccessListener(documentReference -> {
                            Toast.makeText(getContext(), "Ad uploaded successfully!", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Error uploading ad: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(getContext(), "Error getting collection size: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
