package com.example.homeadmin.ui.home;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.TooltipCompat;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.homeadmin.R;
import com.example.homeadmin.ui.Cart.CartActivity;
import com.example.homeadmin.ui.categoryView.CategoryAdapter;
import com.example.homeadmin.ui.home2.Home3Adapter;
import com.example.homeadmin.ui.notification.NotificationActivity;
import com.example.homeadmin.ui.search.SearchActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private RecyclerView horizontalRecyclerview;
    private RecyclerView categoryRecyclerview;
    private CategoryAdapter categoryAdapter;
    private Home3Adapter homepageAdapter;
    private ImageView noInternetConnection;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Button retryButton;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        noInternetConnection = view.findViewById(R.id.no_connection);
        swipeRefreshLayout = view.findViewById(R.id.home_swipeRefreshLayout);
        categoryRecyclerview = view.findViewById(R.id.category_recyclerview);
        horizontalRecyclerview = view.findViewById(R.id.home_recyclerview);
        retryButton = view.findViewById(R.id.retry_Button);

        categoryRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        horizontalRecyclerview.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        categoryAdapter = new CategoryAdapter(new ArrayList<>());
        homepageAdapter = new Home3Adapter(new ArrayList<>());

        categoryRecyclerview.setAdapter(categoryAdapter);
        horizontalRecyclerview.setAdapter(homepageAdapter);

        setupMenu();

        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(true);
            homeViewModel.refreshData();
            swipeRefreshLayout.setRefreshing(false);
        });

        retryButton.setOnClickListener(v -> homeViewModel.refreshData());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        homeViewModel.uiState.observe(getViewLifecycleOwner(), uiState -> {
            switch (uiState) {
                case LOADING:
                    swipeRefreshLayout.setRefreshing(true);
                    horizontalRecyclerview.setVisibility(View.GONE);
                    categoryRecyclerview.setVisibility(View.GONE);
                    retryButton.setVisibility(View.GONE);
                    noInternetConnection.setVisibility(View.GONE);
                    break;
                case SUCCESS:
                    swipeRefreshLayout.setRefreshing(false);
                    horizontalRecyclerview.setVisibility(View.VISIBLE);
                    categoryRecyclerview.setVisibility(View.VISIBLE);
                    retryButton.setVisibility(View.GONE);
                    noInternetConnection.setVisibility(View.GONE);
                    break;
                case ERROR:
                case NO_INTERNET:
                    swipeRefreshLayout.setRefreshing(false);
                    horizontalRecyclerview.setVisibility(View.GONE);
                    categoryRecyclerview.setVisibility(View.GONE);
                    retryButton.setVisibility(View.VISIBLE);
                    noInternetConnection.setVisibility(View.VISIBLE);
                    Glide.with(getContext()).load(R.drawable.nointernet).into(noInternetConnection);
                    break;
            }
        });

        homeViewModel.categories.observe(getViewLifecycleOwner(), categoryList -> {
            if (categoryList != null) {
                categoryAdapter.updateList(categoryList);
            }
        });

        homeViewModel.homepageItems.observe(getViewLifecycleOwner(), homepageModelList -> {
            if (homepageModelList != null) {
                homepageAdapter.updateList(homepageModelList);
            }
        });
    }

    private void setupMenu() {
        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.home_activity2, menu);

                MenuItem cartItem = menu.findItem(R.id.menu_add);
                cartItem.setActionView(R.layout.badge_layout);
                View cartActionView = cartItem.getActionView();
                ((ImageView) cartActionView.findViewById(R.id.badge_icon)).setImageResource(R.drawable.ic_cart);
                // Badge logic can be added here if needed for Admin app

                cartActionView.setOnClickListener(v -> {
                    if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                        startActivity(new Intent(getContext(), CartActivity.class));
                    } else {
                        Toast.makeText(getContext(), "Please login first", Toast.LENGTH_SHORT).show();
                    }
                });

                MenuItem notifyItem = menu.findItem(R.id.men_bel);
                notifyItem.setActionView(R.layout.badge_layout);
                View notifyActionView = notifyItem.getActionView();
                ((ImageView) notifyActionView.findViewById(R.id.badge_icon)).setImageResource(R.drawable.ic_notifications_24);
                TooltipCompat.setTooltipText(notifyActionView, "Notifications");

                notifyActionView.setOnClickListener(v -> {
                    if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                        startActivity(new Intent(getContext(), NotificationActivity.class));
                    } else {
                        Toast.makeText(getContext(), "Please login first", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.men_search) {
                    startActivity(new Intent(getContext(), SearchActivity.class));
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }
}
