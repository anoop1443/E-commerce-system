package com.example.homeelecation.ui.home;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.homeelecation.ui.categoryView.CategoryModel;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class HomeViewModel extends AndroidViewModel {

    private final HomeRepository homeRepository;

    // UI State ke liye LiveData (Loading, Success, Error, No Internet)
    private final MutableLiveData<UiStatus> _uiState = new MutableLiveData<>(UiStatus.LOADING);
    public final LiveData<UiStatus> uiState = _uiState;

    // Category data ke liye LiveData
    private final MutableLiveData<List<CategoryModel>> _categories = new MutableLiveData<>();
    public final LiveData<List<CategoryModel>> categories = _categories;

    // Homepage data ke liye LiveData
    private final MutableLiveData<List<HomepageModel>> _homepageItems = new MutableLiveData<>();
    public final LiveData<List<HomepageModel>> homepageItems = _homepageItems;


    @Inject
    public HomeViewModel(@NonNull Application application,HomeRepository homeRepository) {
        super(application);
        this.homeRepository = homeRepository;
        loadData();
    }

    // Data load karne ka main method
    public void loadData() {
        if (isInternetAvailable()) {
            _uiState.setValue(UiStatus.LOADING);

            // Categories load karein
            homeRepository.getCategories(new HomeRepository.OnDataLoadedListener<List<CategoryModel>>() {
                @Override
                public void onSuccess(List<CategoryModel> data) {
                    _categories.setValue(data);
                    // Ab homepage data load karein
                    loadHomepageLayout();
                }

                @Override
                public void onFailure(String error) {
                    _uiState.setValue(UiStatus.ERROR);
                }
            });
        }else {
            _uiState.setValue(UiStatus.NO_INTERNET);

        }

    }

    private void loadHomepageLayout() {
        homeRepository.getHomepageLayout(new HomeRepository.OnDataLoadedListener<List<HomepageModel>>() {
            @Override
            public void onSuccess(List<HomepageModel> data) {
                _homepageItems.setValue(data);
                _uiState.setValue(UiStatus.SUCCESS); // Sab kuch safal
            }

            @Override
            public void onFailure(String error) {
                // Agar homepage fail hota hai, toh bhi categories dikha sakte hain
                _uiState.setValue(UiStatus.ERROR);
            }
        });
    }

    // Swipe-to-refresh ke liye method
    public  void refreshData(){
        loadData();

    }

    // Internet check karne ka helper method
    private boolean isInternetAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    // UI state ko represent karne ke liye ek enum
    public enum UiStatus {
        LOADING,
        SUCCESS,
        ERROR,
        NO_INTERNET
    }
}