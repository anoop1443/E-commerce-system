package com.example.homeelecation.ui.home;

import android.util.Log;

import com.example.homeelecation.ui.categoryView.CategoryModel;
import com.example.homeelecation.ui.horizontal.HorizontalProductScrollModel;
import com.example.homeelecation.ui.slideshow.SliderModel;
import com.example.homeelecation.ui.wishList.WishlistModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class HomeRepository {

    private final FirebaseFirestore db;

    @Inject
    public HomeRepository(FirebaseFirestore db) {
        this.db = db;
    }

    public interface OnDataLoadedListener<T> {
        void onSuccess(T data);
        void onFailure(String error);
    }

    public void getCategories(OnDataLoadedListener<List<CategoryModel>> listener) {
        db.collection("CATEGORY").orderBy("index").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<CategoryModel> categoryList = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            String icon = doc.getString("icon");
                            String name = doc.getString("categoryName");
                            categoryList.add(new CategoryModel(
                                    doc.getId(),
                                    icon != null ? icon : "",
                                    name != null ? name : "Unknown"
                            ));
                        }
                        listener.onSuccess(categoryList);
                    } else {
                        listener.onFailure(task.getException() != null ? task.getException().getMessage() : "Unknown error");
                    }
                });
    }

    public void getHomepageLayout(OnDataLoadedListener<List<HomepageModel>> listener) {
        Log.d("Home3Repository", "getHomepageLayout started");
        db.collection("HOMEPAGE").orderBy("index").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Log.d("Home3Repository", "Success: Found " + queryDocumentSnapshots.size() + " documents.");
                    List<HomepageModel> homepageList = new ArrayList<>();

                    for (QueryDocumentSnapshot documentHome : queryDocumentSnapshots) {
                        Object viewTypeObj = documentHome.get("view_type");
                        if (!(viewTypeObj instanceof Number)) continue;

                        long viewType = ((Number) viewTypeObj).longValue();
                        String docId = documentHome.getId();

                        if (viewType == 0) { // Banner Slider
                            List<SliderModel> sliderModelList = new ArrayList<>();
                            Object bannersObj = documentHome.get("bannersId");
                            if (bannersObj == null){
                                bannersObj = documentHome.get("banners");
                            }
                            if (bannersObj instanceof List) {
                                List<?> bannersList = (List<?>) bannersObj;
                                for (Object item : bannersList) {
                                    if (item instanceof String) {
                                        sliderModelList.add(new SliderModel((String) item, "", ""));
                                    }
                                }
                            }
                            homepageList.add(new HomepageModel(0, docId, sliderModelList));

                        } else if (viewType == 1) { // Strip Ad
                            String adId = documentHome.getString("ad_id");
                            if (adId != null) {
                                homepageList.add(new HomepageModel(1, docId, adId, "", ""));
                            }

                        } else if (viewType == 2) { // Horizontal Product Scroll
                            List<WishlistModel> viewAllProductList = new ArrayList<>();
                            List<HorizontalProductScrollModel> horizontalList = new ArrayList<>();
                            Object productsObj = documentHome.get("products");
                            if (productsObj instanceof List) {
                                List<?> productsList = (List<?>) productsObj;
                                for (Object item : productsList) {
                                    if (item instanceof String) {
                                        String pId = (String) item;
                                        horizontalList.add(new HorizontalProductScrollModel(pId, "", "", "", ""));
                                        viewAllProductList.add(new WishlistModel(pId, "", 0L, 1.0, 1L, "", 0L, 0L, ""));
                                    }
                                }
                            }
                            String title = documentHome.getString("layout_title");
                            String bg = documentHome.getString("layout_background");
                            homepageList.add(new HomepageModel(2, docId, title != null ? title : "", bg != null ? bg : "", horizontalList, viewAllProductList));

                        } else if (viewType == 3) { // Grid Layout
                            List<HorizontalProductScrollModel> gridList = new ArrayList<>();
                            Object productsObj = documentHome.get("products");
                            if (productsObj instanceof List) {
                                List<?> productsList = (List<?>) productsObj;
                                for (Object item : productsList) {
                                    if (item instanceof String) {
                                        gridList.add(new HorizontalProductScrollModel((String) item, "", "", "", ""));
                                    }
                                }
                            }
                            String title = documentHome.getString("layout_title");
                            String bg = documentHome.getString("layout_background");
                            homepageList.add(new HomepageModel(3, docId, title != null ? title : "", bg != null ? bg : "", gridList));
                        }
                    }
                    listener.onSuccess(homepageList);
                })
                .addOnFailureListener(e -> {
                    Log.e("Home3Repository", "Error getting homepage layout", e);
                    listener.onFailure(e.getMessage());
                });
    }
}
