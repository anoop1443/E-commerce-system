package com.example.homeadmin.ui.home2;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.homeadmin.ui.categoryView.CategoryModel;

import java.util.List;

public class Home3ViewModel extends ViewModel {

    private Home3Repository home3Repository;

    private MutableLiveData<List<Home3Model>> home3Data;
    private MutableLiveData<List<CategoryModel>> categoryData;


    public Home3ViewModel() {
        this.home3Repository = new Home3Repository();
    }

    public MutableLiveData<List<Home3Model>> getHome3Data() {
        if (home3Data == null) {
            home3Data = new MutableLiveData<>();
            loadHomeLayout();

        }
        return home3Data;
    }
    public MutableLiveData<List<CategoryModel>> getCategoryData(){
            if (categoryData == null) {
                categoryData = new MutableLiveData<>();
                loadData();
            }
            return categoryData;

    }

    private void loadData(){
        home3Repository.getCategories(new Home3Repository.OnDataLoadedListener<List<CategoryModel>>() {
            @Override
            public void onSuccess(List<CategoryModel> data) {
                categoryData.setValue(data);
            }

            @Override
            public void onFailure(String error) {
                Log.e("Home3ViewModel", "Error loading home layout: " + error);
                categoryData.setValue(null);

            }

            });
    }

    private void loadHomeLayout(){
        home3Repository.getHomepageLayout(new Home3Repository.OnDataLoadedListener<List<Home3Model>>() {
            @Override
            public void onSuccess(List<Home3Model> data) {
                home3Data.setValue(data);
            }

            @Override
            public void onFailure(String error) {
                Log.e("Home3ViewModel", "Error loading home layout: " + error);
                home3Data.setValue(null);
            }
        });

    }

}
