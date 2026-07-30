package com.example.homeadmin.ui.home.edit;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
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

public class Type0Fragment extends Fragment implements BannerSelectionAdapter.OnBannerSelectionChangedListener {

    private RecyclerView bannersRecyclerView;
    private BannerSelectionAdapter adapter;
    private List<BannerModel> bannerList;
    private FirebaseFirestore db;
    private TextView selectedBannersCount;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.upload_layout_type0, container, false);

        db = FirebaseFirestore.getInstance();
        bannerList = new ArrayList<>();
        adapter = new BannerSelectionAdapter(bannerList, getContext());
        adapter.setOnBannerSelectionChangedListener(this);

        bannersRecyclerView = view.findViewById(R.id.banners_recycler_view);
        selectedBannersCount = view.findViewById(R.id.selected_banners_count);
        bannersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        bannersRecyclerView.setAdapter(adapter);

        view.findViewById(R.id.upload_type0_button).setOnClickListener(v -> uploadBannerIds());

        loadBanners();

        return view;
    }

    private void loadBanners() {
        db.collection("banners").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        bannerList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            bannerList.add(new BannerModel(document.getId(), document.getString("imageUrl"), document.getString("backgroundColor")));
                        }
                        adapter.notifyDataSetChanged();
                        onSelectionChanged(0); // Initially 0 selected
                    } else {
                        Log.w("FirestoreError", "Error getting documents.", task.getException());
                    }
                });
    }

    private void uploadBannerIds() {
        Bundle args = getArguments();
        String target = args != null ? args.getString("TARGET") : "HOMEPAGE";
        String categoryId = args != null ? args.getString("CATEGORY_ID") : null;

        List<String> selectedBannerIds = new ArrayList<>();
        for (BannerModel item : adapter.getSelectedBanners()) {
            selectedBannerIds.add(item.getDocumentId());
        }

        if (selectedBannerIds.isEmpty()) {
            Toast.makeText(getContext(), "Please select at least one banner!", Toast.LENGTH_SHORT).show();
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
                data.put("view_type", 0L);
                data.put("banners", selectedBannerIds);
                data.put("index", index);

                targetCollection.add(data)
                        .addOnSuccessListener(documentReference -> {
                            Toast.makeText(getContext(), "Banners uploaded successfully!", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Error uploading banners: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(getContext(), "Error getting collection size: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onSelectionChanged(int count) {
        selectedBannersCount.setText(count + " banners selected");
    }
}
