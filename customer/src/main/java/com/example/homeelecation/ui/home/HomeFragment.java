package com.example.homeelecation.ui.home;

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
import android.widget.TextView;
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
import com.example.homeelecation.R;
import com.example.homeelecation.databinding.FragmentHomeBinding;
import com.example.homeelecation.ui.Cart.CartActivity;
import com.example.homeelecation.ui.Cart.CartViewModel;
import com.example.homeelecation.ui.categoryView.CategoryAdapter;
import com.example.homeelecation.ui.categoryView.CategoryModel;
import com.example.homeelecation.ui.horizontal.HorizontalProductScrollModel;
import com.example.homeelecation.ui.notification.NotificationActivity;
import com.example.homeelecation.ui.notification.NotificationViewModel;
import com.example.homeelecation.ui.search.SearchActivity;
import com.example.homeelecation.ui.slideshow.SliderModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private CartViewModel cartViewModel;
    private NotificationViewModel notificationViewModel;

    private HomepageAdapter2 homepageAdapter2;
    private boolean currentFragment;

    private RecyclerView homeRecyclerView;
    private RecyclerView categoryRecyclerview;
    private CategoryAdapter categoryAdapter;

    private HomepageAdapter adapter;
    private FirebaseFirestore firebaseFirestore;

    private List<HomepageModel> homepageModelFakeList = new ArrayList<>();
    private List<CategoryModel> categoryFakeList = new ArrayList<>();

    private RecyclerView horizontalRecyclerview;

    ImageView noInternetConnection;

    private TextView badgeCountTextView;
    private TextView notifyCountTextView;


    //////////////Horizontal product layout
    private ConnectivityManager connectivityManager;
    private NetworkInfo networkInfo;
    private SwipeRefreshLayout swipeRefreshLayout;

    private Button retryButton;



    private FragmentHomeBinding binding;
    private View view;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_home,container,false);
        noInternetConnection = view.findViewById(R.id.no_connection);
        swipeRefreshLayout = view.findViewById(R.id.home_swipeRefreshLayout);
        categoryRecyclerview = view.findViewById(R.id.category_recyclerview);
        homeRecyclerView = view.findViewById(R.id.home_recyclerview);
        retryButton = view.findViewById(R.id.retry_Button);



        connectivityManager = (ConnectivityManager)requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();

       // loadPage();




        return view;
    }

    private void setupFakeLists() {
        // Category Fake List
        categoryFakeList.clear();
        for (int i = 0; i < 8; i++) {
            categoryFakeList.add(new CategoryModel("", "null", ""));
        }

        // Home page Fake List
        homepageModelFakeList.clear();
        List<SliderModel> sliderModelFakeList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            sliderModelFakeList.add(new SliderModel("","null", "#FFFFF0"));
        }

        List<HorizontalProductScrollModel> horizontalModelFakeList = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            horizontalModelFakeList.add(new HorizontalProductScrollModel("", "", "", "", ""));
        }

        homepageModelFakeList.add(new HomepageModel(0,"", sliderModelFakeList));
        homepageModelFakeList.add(new HomepageModel(1,"","", "", "#FFFFF0"));
        homepageModelFakeList.add(new HomepageModel(2,"", "", "#FFFFF0", horizontalModelFakeList, new ArrayList<>()));
        homepageModelFakeList.add(new HomepageModel(3,"", "", "#FFFFF0", horizontalModelFakeList));
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onStart() {
        super.onStart();
       // DbLoadData.checkNotification(false);
    }

    @Override
    public void onPause() {
        super.onPause();
        //DbLoadData.checkNotification(true);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // ViewModel को इनिशियलाइज़ करें







        ////home Fake list
        setupFakeLists();
        //// home  Fake list


        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        categoryRecyclerview.setLayoutManager(layoutManager);

        LinearLayoutManager testinglayoutManager = new LinearLayoutManager(getContext());
        testinglayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        homeRecyclerView.setLayoutManager(testinglayoutManager);

        categoryAdapter = new CategoryAdapter(categoryFakeList);
        categoryRecyclerview.setAdapter(categoryAdapter);

        homepageAdapter2 = new HomepageAdapter2(homepageModelFakeList);
        homeRecyclerView.setAdapter(homepageAdapter2);

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        cartViewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);
        notificationViewModel = new ViewModelProvider(requireActivity()).get(NotificationViewModel.class);

        setupMenu();



        /////SwipeRefreshLayout

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                //loadPage();
                setupFakeLists();
                homeViewModel.refreshData();


            }
        });

        /////SwipeRefreshLayout
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  loadPage();
                setupFakeLists();
                homeViewModel.refreshData();
            }
        });




        // UI State को ऑब्ज़र्व करें
        homeViewModel.uiState.observe(getViewLifecycleOwner(), uiState -> {
            switch (uiState) {
                case LOADING:
                    //progressBar.setVisibility(View.VISIBLE);
                    // errorView.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(true);
                    noInternetConnection.setVisibility(View.GONE);
                    retryButton.setVisibility(View.GONE);
                    categoryRecyclerview.setVisibility(View.VISIBLE);
                    homeRecyclerView.setVisibility(View.VISIBLE);


                    break;
                case SUCCESS:
                    //progressBar.setVisibility(View.GONE);
                    // errorView.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                    noInternetConnection.setVisibility(View.GONE);
                    retryButton.setVisibility(View.GONE);
                    categoryRecyclerview.setVisibility(View.VISIBLE);
                    homeRecyclerView.setVisibility(View.VISIBLE);


                    break;
                case ERROR:
                    swipeRefreshLayout.setRefreshing(false);
                    break;

                case NO_INTERNET:
                    //progressBar.setVisibility(View.GONE);
                    // errorView.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setRefreshing(false);
                    categoryRecyclerview.setVisibility(View.GONE);
                    homeRecyclerView.setVisibility(View.GONE);
                    noInternetConnection.setVisibility(View.VISIBLE);
                    Glide.with(this).load(R.drawable.nointernet).into(noInternetConnection);
                    retryButton.setVisibility(View.VISIBLE);

                    break;
            }
        });

        // Categories डेटा को ऑब्ज़र्व करें
        homeViewModel.categories.observe(getViewLifecycleOwner(), categoryList -> {
            if (categoryList != null) {
                categoryAdapter.updateList(categoryList);
            }
        });


        homeViewModel.homepageItems.observe(getViewLifecycleOwner(), homepageModelList -> {
            if (homepageModelList != null) {
                homepageAdapter2.updateList(homepageModelList);
            }
        });
    }

    private void setupMenu() {
        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.home_activity2, menu);

                MenuItem cartItem = menu.findItem(R.id.men_cart);
                cartItem.setActionView(R.layout.badge_layout);
                View cartActionView = cartItem.getActionView();
                ((ImageView) cartActionView.findViewById(R.id.badge_icon)).setImageResource(R.drawable.ic_cart);
                badgeCountTextView = cartActionView.findViewById(R.id.badge_count);

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
                ((ImageView) notifyActionView.findViewById(R.id.badge_icon)).setImageResource(R.drawable.ic_notifications);
                notifyCountTextView = notifyActionView.findViewById(R.id.badge_count);
                TooltipCompat.setTooltipText(notifyActionView, "Notifications");

                notifyActionView.setOnClickListener(v -> {
                    if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                        startActivity(new Intent(getContext(), NotificationActivity.class));
                    } else {
                        Toast.makeText(getContext(), "Please login first", Toast.LENGTH_SHORT).show();
                    }
                });

                updateBadges();
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

    private void updateBadges() {
        cartViewModel.getBadgeCount().observe(getViewLifecycleOwner(), count -> {
            if (badgeCountTextView != null) {
                if (FirebaseAuth.getInstance().getCurrentUser() != null && count != null && count > 0) {
                    badgeCountTextView.setVisibility(View.VISIBLE);
                    badgeCountTextView.setText(count < 99 ? String.valueOf(count) : "99");
                } else {
                    badgeCountTextView.setVisibility(View.INVISIBLE);
                }
            }
        });

        notificationViewModel.unreadCount.observe(getViewLifecycleOwner(), count -> {
            if (notifyCountTextView != null) {
                if (FirebaseAuth.getInstance().getCurrentUser() != null && count != null && count > 0) {
                    notifyCountTextView.setVisibility(View.VISIBLE);
                    notifyCountTextView.setText(count < 99 ? String.valueOf(count) : "99");
                } else {
                    notifyCountTextView.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

}
