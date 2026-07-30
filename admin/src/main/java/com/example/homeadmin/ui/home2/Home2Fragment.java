package com.example.homeadmin.ui.home2;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.homeadmin.R;
import com.example.homeadmin.ui.categoryView.CategoryAdapter;
import com.example.homeadmin.ui.home.HomeViewModel;

import java.util.ArrayList;

public class Home2Fragment extends Fragment {

    private HomeViewModel homeViewModel;
    private RecyclerView categoryRecyclerView, homepageRecyclerView;
    private CategoryAdapter categoryAdapter;
    private Home3Adapter homepageAdapter;
    private ProgressBar progressBar; // लोडिंग दिखाने के लिए

    public static Home2Fragment newInstance() {
        return new Home2Fragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home2, container, false);

        // Initialize RecyclerView and adapter
        homepageRecyclerView = view.findViewById(R.id.home2RecyclerView);
        categoryRecyclerView = view.findViewById(R.id.home2recyclerView);
        progressBar = view.findViewById(R.id.progressBar7);
        homepageRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        categoryRecyclerView.setLayoutManager(linearLayoutManager);

        categoryAdapter = new CategoryAdapter(new ArrayList<>());
        homepageAdapter = new Home3Adapter(new ArrayList<>());

        categoryRecyclerView.setAdapter(categoryAdapter);
        homepageRecyclerView.setAdapter(homepageAdapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // ViewModel को इनिशियलाइज़ करें
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // UI State को ऑब्ज़र्व करें
        homeViewModel.uiState.observe(getViewLifecycleOwner(), uiState -> {
            switch (uiState) {
                case LOADING:
                    progressBar.setVisibility(View.VISIBLE);
                    homepageRecyclerView.setVisibility(View.GONE);
                   // errorView.setVisibility(View.GONE);
                    break;
                case SUCCESS:
                    progressBar.setVisibility(View.GONE);
                    homepageRecyclerView.setVisibility(View.VISIBLE);
                   // errorView.setVisibility(View.GONE);
                    break;
                case ERROR:
                case NO_INTERNET:
                    progressBar.setVisibility(View.GONE);
                    homepageRecyclerView.setVisibility(View.GONE);
                   // errorView.setVisibility(View.VISIBLE);
                    break;
            }
        });

        // Categories डेटा को ऑब्ज़र्व करें
        homeViewModel.categories.observe(getViewLifecycleOwner(), categoryList -> {
            if (categoryList != null) {
                categoryAdapter.updateList(categoryList); // मान लीजिए आपके एडाप्टर में यह मेथड है
            }
        });

        // Homepage Items डेटा को ऑब्ज़र्व करें
        homeViewModel.homepageItems.observe(getViewLifecycleOwner(), homepageModelList -> {
            if (homepageModelList != null) {
                homepageAdapter.updateList(homepageModelList); // मान लीजिए आपके एडाॅप्टर में यह मेथड है
            }
        });
    }

//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        mViewModel = new ViewModelProvider(this).get(Home3ViewModel.class);
//        mViewModel.getHome3Data().observe(getViewLifecycleOwner(), data -> {
//            Home3Adapter adapter;
//            // Set an empty adapter if data is null to avoid crashing and the "No adapter attached" warning
//            adapter = new Home3Adapter(Objects.requireNonNullElse(data, Collections.emptyList()));
//            recyclerView.setAdapter(adapter);
//        });
//
//        mViewModel.getCategoryData().observe(getViewLifecycleOwner(), data -> {
//            CategoryAdapter adapter;
//            // Set an empty adapter if data is null to avoid crashing and the "No adapter attached" warning
//            adapter = new CategoryAdapter(Objects.requireNonNullElse(data, Collections.emptyList()));
//            recyclerView2.setAdapter(adapter);
//        });
//
//
//    }

}