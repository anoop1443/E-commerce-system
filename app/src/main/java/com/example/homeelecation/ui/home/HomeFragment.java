package com.example.homeelecation.ui.home;

import static com.example.homeelecation.ui.DbLoadData.categoryModelList;
import static com.example.homeelecation.ui.DbLoadData.homepageModelList;
import static com.example.homeelecation.ui.DbLoadData.loadCategory;
import static com.example.homeelecation.ui.DbLoadData.loadHomeFrag;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.homeelecation.HomeActivity2;
import com.example.homeelecation.R;
import com.example.homeelecation.databinding.FragmentHomeBinding;
import com.example.homeelecation.ui.categoryView.CategoryAdapter;
import com.example.homeelecation.ui.categoryView.CategoryModel;
import com.example.homeelecation.ui.horizontal.HorizontalProductScrollModel;
import com.example.homeelecation.ui.slideshow.SliderModel;
import com.example.homeelecation.ui.wishList.WishlistModel;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    public static  boolean currentFragment;

    private RecyclerView homeRecyclerView;
    private RecyclerView categoryRecyclerview;
    private CategoryAdapter categoryAdapter;

    private  HomepageAdapter adapter;
    private FirebaseFirestore firebaseFirestore;

    private List<HomepageModel> homepageModelFakeList = new ArrayList<>();
    private List<CategoryModel> categoryFakeList = new ArrayList<>();

      private RecyclerView horizontalRecyclerview;

    ImageView noInternetConnection;


    //////////////Horizontal product layout
    private ConnectivityManager connectivityManager;
    private NetworkInfo networkInfo;
    public static SwipeRefreshLayout swipeRefreshLayout;

    private Button retryButton;



    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home,container,false);
        noInternetConnection = view.findViewById(R.id.no_connection);
        swipeRefreshLayout = view.findViewById(R.id.home_swipeRefreshLayout);
        categoryRecyclerview = view.findViewById(R.id.category_recyclerview);
        homeRecyclerView = view.findViewById(R.id.home_recyclerview);
        retryButton = view.findViewById(R.id.retry_Button);


        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        categoryRecyclerview.setLayoutManager(layoutManager);

        LinearLayoutManager testinglayoutManager = new LinearLayoutManager(getContext());
        testinglayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        homeRecyclerView.setLayoutManager(testinglayoutManager);



        //// category Fake

        categoryFakeList.add(new CategoryModel("null",""));
        categoryFakeList.add(new CategoryModel("null",""));
        categoryFakeList.add(new CategoryModel("null",""));
        categoryFakeList.add(new CategoryModel("null",""));
        categoryFakeList.add(new CategoryModel("null",""));
        categoryFakeList.add(new CategoryModel("null",""));
        categoryFakeList.add(new CategoryModel("null",""));
        categoryFakeList.add(new CategoryModel("null",""));

        categoryAdapter = new CategoryAdapter(categoryFakeList);
        categoryAdapter.notifyDataSetChanged();
        //// category Fake

        //// Home page
        List<SliderModel> sliderModelFakeList = new ArrayList<>();

        sliderModelFakeList.add(new SliderModel("null","#ffffff"));
        sliderModelFakeList.add(new SliderModel("null","#ffffff"));
        sliderModelFakeList.add(new SliderModel("null","#ffffff"));
        sliderModelFakeList.add(new SliderModel("null","#ffffff"));
        sliderModelFakeList.add(new SliderModel("null","#ffffff"));

        List<HorizontalProductScrollModel> horizontalModelFakeList = new ArrayList<>();

        horizontalModelFakeList.add(new HorizontalProductScrollModel("","","","",""));
        horizontalModelFakeList.add(new HorizontalProductScrollModel("","","","",""));
        horizontalModelFakeList.add(new HorizontalProductScrollModel("","","","",""));
        horizontalModelFakeList.add(new HorizontalProductScrollModel("","","","",""));
        horizontalModelFakeList.add(new HorizontalProductScrollModel("","","","",""));
        horizontalModelFakeList.add(new HorizontalProductScrollModel("","","","",""));
        horizontalModelFakeList.add(new HorizontalProductScrollModel("","","","",""));

        homepageModelFakeList.add(new HomepageModel(0,sliderModelFakeList));
        homepageModelFakeList.add(new HomepageModel(1,"","#ffffff"));
        homepageModelFakeList.add(new HomepageModel(2,"","#ffffff",horizontalModelFakeList,new ArrayList<WishlistModel>()));
        homepageModelFakeList.add(new HomepageModel(3,"","#ffffff",horizontalModelFakeList));

        adapter = new HomepageAdapter(homepageModelFakeList);

        //// Home page


        connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();



        if (networkInfo != null && networkInfo.isConnected() == true) {



            categoryRecyclerview.setVisibility(View.VISIBLE);
            homeRecyclerView.setVisibility(View.VISIBLE);
            retryButton.setVisibility(View.GONE);
            noInternetConnection.setVisibility(View.GONE);

            firebaseFirestore = FirebaseFirestore.getInstance();

            if (categoryModelList.size() == 0) {
                loadCategory(categoryRecyclerview, getContext());

            } else {
                categoryAdapter = new CategoryAdapter(categoryModelList);
                categoryRecyclerview.setAdapter(categoryAdapter);
                categoryAdapter.notifyDataSetChanged();
            }



            if (homepageModelList.size() == 0) {
                loadHomeFrag(homeRecyclerView, getContext());
            } else {
                adapter = new HomepageAdapter(homepageModelList);
                homeRecyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                horizontalModelFakeList.clear();
            }


        } else {
           // drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            categoryRecyclerview.setVisibility(View.GONE);
            homeRecyclerView.setVisibility(View.GONE);
            retryButton.setVisibility(View.VISIBLE);
            noInternetConnection.setVisibility(View.VISIBLE);
            Glide.with(getContext()).load(R.drawable.nointernet).into(noInternetConnection);


        }
        /////SwipeRefreshLayout

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                loadPage();

            }
        });

        /////SwipeRefreshLayout
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadPage();
            }
        });



        return view;
    }

    private void loadPage(){
        networkInfo = connectivityManager.getActiveNetworkInfo();
        categoryModelList.clear();
        homepageModelList.clear();

        if (networkInfo != null && networkInfo.isConnected() == true) {

           // drawer.setDrawerLockMode(LOCK_MODE_LOCKED_OPEN);

            categoryRecyclerview.setVisibility(View.VISIBLE);
            homeRecyclerView.setVisibility(View.VISIBLE);

            retryButton.setVisibility(View.GONE);
            noInternetConnection.setVisibility(View.GONE);
            categoryAdapter = new CategoryAdapter(categoryFakeList);
            categoryRecyclerview.setAdapter(categoryAdapter);
            adapter = new HomepageAdapter(homepageModelFakeList);
            homeRecyclerView.setAdapter(adapter);
            loadCategory(categoryRecyclerview, getContext());
            loadHomeFrag(homeRecyclerView, getContext());

        } else {
           // drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            categoryRecyclerview.setVisibility(View.GONE);
            homeRecyclerView.setVisibility(View.GONE);
            noInternetConnection.setVisibility(View.VISIBLE);
            retryButton.setVisibility(View.VISIBLE);
            Glide.with(getContext()).load(R.drawable.nointernet).into(noInternetConnection);
            swipeRefreshLayout.setRefreshing(false);


        }


    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}