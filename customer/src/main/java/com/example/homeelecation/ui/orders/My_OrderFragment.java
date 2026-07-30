package com.example.homeelecation.ui.orders;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.homeelecation.LoginActivity;
import com.example.homeelecation.MainActivity;
import com.example.homeelecation.R;
import com.example.homeelecation.ui.DbLoadData;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class My_OrderFragment extends Fragment {

    private ViewPager2 viewPager;
    private OrderPagerAdapter orderPagerAdapter;
    private TabLayout tabLayout;
    private Dialog singInDialog, loadingDialog;
    private MyOrdersViewModel myOrdersViewModel;
    
    private boolean isFirstLoad = true;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my__order, container, false);
        
        viewPager = view.findViewById(R.id.order_view_pager);
        tabLayout = view.findViewById(R.id.order_tab_layout);

        // Sign-in Dialog
        singInDialog = new Dialog(view.getContext());
        singInDialog.setContentView(R.layout.sing_in_dialog_layout);
        singInDialog.setCancelable(true);
        singInDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        Button dialogSingInBtn = singInDialog.findViewById(R.id.sing_in_btn);
        Button dialogSingUpBtn = singInDialog.findViewById(R.id.sing_up_btn);
        ImageView imageView = singInDialog.findViewById(R.id.dialog_image);
        imageView.setImageResource(R.drawable.address_icon);

        dialogSingInBtn.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), MainActivity.class));
            singInDialog.dismiss();
        });

        dialogSingUpBtn.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), LoginActivity.class));
            singInDialog.dismiss();
        });

        // Loading Dialog
        loadingDialog = new Dialog(requireContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        myOrdersViewModel = new ViewModelProvider(this).get(MyOrdersViewModel.class);
        orderPagerAdapter = new OrderPagerAdapter();
        viewPager.setAdapter(orderPagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(position == 0 ? "Products" : "Quick Orders");
        }).attach();

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            setupObservers();
            // Load both initially but only show dialog if truly first load
            myOrdersViewModel.loadMyOrders();
            myOrdersViewModel.loadQuickOrders();
        } else {
            singInDialog.show();
        }

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                // Optimized Refresh: Only reload if data is missing, or it's not the first load
                if (!isFirstLoad) {
                    if (position == 0) myOrdersViewModel.loadMyOrders();
                    else myOrdersViewModel.loadQuickOrders();
                }
            }
        });
    }

    private void setupObservers() {
        myOrdersViewModel.getOrdersLiData().observe(getViewLifecycleOwner(), orders -> {
            if (orders != null) {
                orderPagerAdapter.getProductsAdapter().updateList(orders);
                // Mark as loaded once data (even empty) is received
                orderPagerAdapter.setProductsLoaded(true);
            }
        });

        myOrdersViewModel.getQuickOrdersLiveData().observe(getViewLifecycleOwner(), quickOrders -> {
            if (quickOrders != null) {
                orderPagerAdapter.getQuickOrdersAdapter().updateList(quickOrders);
                // Mark as loaded once data (even empty) is received
                orderPagerAdapter.setQuickOrdersLoaded(true);
            }
        });

        myOrdersViewModel.getIsLoadingData().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                if (isFirstLoad) loadingDialog.show();
            } else {
                loadingDialog.dismiss();
                isFirstLoad = false;
            }
        });

        myOrdersViewModel.getErrorLiveData().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                isFirstLoad = false;
                loadingDialog.dismiss();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (DbLoadData.myOrderItemModelList != null) {
            DbLoadData.myOrderItemModelList.clear();
        }
    }
}
