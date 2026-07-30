package com.example.homeadmin.ui.home;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.homeadmin.R;
import com.example.homeadmin.ui.categoryView.CategoryModel;
import com.example.homeadmin.ui.home2.Home3Model;
import com.example.homeadmin.ui.home2.Home3Repository;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends AndroidViewModel {

    private final Home3Repository homeRepository;

    // UI State ke liye LiveData (Loading, Success, Error, No Internet)
    private final MutableLiveData<UiState> _uiState = new MutableLiveData<>(UiState.LOADING);
    public final LiveData<UiState> uiState = _uiState;

    // Category data ke liye LiveData
    private final MutableLiveData<List<CategoryModel>> _categories = new MutableLiveData<>();
    public final LiveData<List<CategoryModel>> categories = _categories;

    // Homepage data ke liye LiveData
    private final MutableLiveData<List<Home3Model>> _homepageItems = new MutableLiveData<>();
    public final LiveData<List<Home3Model>> homepageItems = _homepageItems;


    public HomeViewModel(@NonNull Application application) {
        super(application);
        this.homeRepository = new Home3Repository();
        loadData();
    }

    // Data load karne ka main method
    public void loadData() {
        if (!isInternetAvailable()) {
            _uiState.setValue(UiState.NO_INTERNET);
            return;
        }

        _uiState.setValue(UiState.LOADING);

        // Categories load karein
        homeRepository.getCategories(new Home3Repository.OnDataLoadedListener<List<CategoryModel>>() {
            @Override
            public void onSuccess(List<CategoryModel> data) {
                _categories.setValue(data);
                // Ab homepage data load karein
                loadHomepageLayout();
            }

            @Override
            public void onFailure(String error) {
                _uiState.setValue(UiState.ERROR);
            }
        });
    }

    private void loadHomepageLayout() {
        homeRepository.getHomepageLayout(new Home3Repository.OnDataLoadedListener<List<Home3Model>>() {
            @Override
            public void onSuccess(List<Home3Model> data) {
                _homepageItems.setValue(data);
                _uiState.setValue(UiState.SUCCESS); // Sab kuch safal
            }

            @Override
            public void onFailure(String error) {
                // Agar homepage fail hota hai, toh bhi categories dikha sakte hain
                _uiState.setValue(UiState.ERROR);
            }
        });
    }

    // Swipe-to-refresh ke liye method
    public  void refreshData() {
        loadData();
    }

    // Internet check karne ka helper method
    private boolean isInternetAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    // UI state ko represent karne ke liye ek enum
    public enum UiState {
        LOADING,
        SUCCESS,
        ERROR,
        NO_INTERNET
    }
}
