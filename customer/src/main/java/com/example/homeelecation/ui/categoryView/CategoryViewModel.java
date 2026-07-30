package com.example.homeelecation.ui.categoryView;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.homeelecation.ui.home.HomepageModel;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class CategoryViewModel extends AndroidViewModel {

    private final CategoryRepository categoryRepository;

    private final MutableLiveData<UiStatus> _uiState = new MutableLiveData<>(UiStatus.LOADING);
    public final LiveData<UiStatus> uiState = _uiState;

    private final MutableLiveData<List<HomepageModel>> _categoryPageItems = new MutableLiveData<>();
    public final LiveData<List<HomepageModel>> categoryPageItems = _categoryPageItems;

    @Inject
    public CategoryViewModel(Application application, CategoryRepository categoryRepository) {
        super(application);
        this.categoryRepository = categoryRepository;
    }

    public void loadCategoryPage(String categoryName) {
        if (isInternetAvailable()) {
            _uiState.setValue(UiStatus.LOADING);
            categoryRepository.getCategoryActivityLayout(categoryName, new CategoryRepository.OnDataLoadedListener<List<HomepageModel>>() {
                @Override
                public void onSuccess(List<HomepageModel> data) {
                    _categoryPageItems.setValue(data);
                    _uiState.setValue(UiStatus.SUCCESS);
                }

                @Override
                public void onFailure(String error) {
                    _uiState.setValue(UiStatus.ERROR);
                }
            });
        } else {
            _uiState.setValue(UiStatus.NO_INTERNET);
        }
    }

    private boolean isInternetAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    public enum UiStatus {
        LOADING,
        SUCCESS,
        ERROR,
        NO_INTERNET
    }
}
