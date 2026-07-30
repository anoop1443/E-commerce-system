package com.example.homeadmin.ui.home2;

import android.util.Log;

import com.example.homeadmin.ui.categoryView.CategoryModel;
import com.example.homeadmin.ui.horizontal.HorizontalProductScrollModel;
import com.example.homeadmin.ui.slideshow.SliderModel;
import com.example.homeadmin.ui.wishList.WishlistModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Home3Repository {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public interface OnDataLoadedListener<T> {
        void onSuccess(T data);
        void onFailure(String error);
    }

    public void getCategories(OnDataLoadedListener<List<CategoryModel>> listener) {
        db.collection("CATEGORY").orderBy("index").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<CategoryModel> categoryList = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String icon = doc.getString("icon");
                        if (icon == null) icon = doc.getString("categoryIconLink"); // Fallback
                        
                        Log.d("RepoDebug", "Category Icon URL: " + icon);
                        
                        String name = doc.getString("categoryName");
                        
                        categoryList.add(new CategoryModel(
                                doc.getId(),
                                Objects.toString(icon, ""),
                                Objects.toString(name, "Unknown")
                        ));
                    }
                    listener.onSuccess(categoryList);
                })
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    public void getLayout(String collectionPath, OnDataLoadedListener<List<Home3Model>> listener) {
        Log.d("Home3Repository", "getLayout started for path: " + collectionPath);
        db.collection(collectionPath).orderBy("index").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Home3Model> homepageList = new ArrayList<>();

                    for (QueryDocumentSnapshot documentHome : queryDocumentSnapshots) {
                        Long viewType = documentHome.getLong("view_type");
                        if (viewType == null) continue;

                        String docId = documentHome.getId();

                        if (viewType == 0) { // Banner Slider
                            List<SliderModel> sliderModelList1 = new ArrayList<>();
                            List<String> bannerIds = (List<String>) documentHome.get("banners");
                            if (bannerIds == null) {
                                bannerIds = (List<String>) documentHome.get("bannersId");
                            }
                            if (bannerIds != null) {
                                for (String id : bannerIds) {
                                    sliderModelList1.add(new SliderModel(id, "", ""));
                                }
                            }
                            Home3Model model = new Home3Model(0, docId, sliderModelList1);
                            model.setContentIds(bannerIds);
                            homepageList.add(model);

                        } else if (viewType == 1) { // Strip Ad
                            String adId = documentHome.getString("ad_id");
                            if (adId == null) adId = documentHome.getString("strip_ads"); // Fallback
                            
                            Log.d("RepoDebug", "Strip Ad ID/URL: " + adId);
                            
                            String bg = documentHome.getString("ad_background");
                            if (bg == null) bg = documentHome.getString("stirp_ad_background"); // Fallback
                            
                            Home3Model model = new Home3Model(1, docId, adId, "", Objects.toString(bg, "#FFFFFF"));
                            model.setStripDocumentId(adId);
                            homepageList.add(model);

                        } else if (viewType == 2) { // Horizontal Product Scroll
                            List<WishlistModel> viewAllProductList = new ArrayList<>();
                            List<HorizontalProductScrollModel> horizontalList = new ArrayList<>();
                            List<String> productsIds = (List<String>) documentHome.get("products");
                            if (productsIds != null) {
                                for (String productId : productsIds) {
                                    Log.d("RepoDebug", "Product ID: " + productId);
                                    horizontalList.add(new HorizontalProductScrollModel(productId, "", "", "", ""));
                                    viewAllProductList.add(new WishlistModel(productId, "", 0.0, 1.0, 1L, "", 0L, 0L, ""));
                                }
                            }
                            String title = documentHome.getString("layout_title");
                            String bg = documentHome.getString("layout_background");
                            if (bg == null) bg = documentHome.getString("layout_backgrond");
                            
                            Home3Model model = new Home3Model(2, docId, Objects.toString(title, ""), Objects.toString(bg, "#FFFFFF"), horizontalList, viewAllProductList);
                            model.setContentIds(productsIds);
                            homepageList.add(model);

                        } else if (viewType == 3) { // Grid Layout
                            List<HorizontalProductScrollModel> gridList = new ArrayList<>();
                            List<String> productsIds = (List<String>) documentHome.get("products");
                            if (productsIds != null) {
                                for (String productId : productsIds) {
                                    gridList.add(new HorizontalProductScrollModel(productId, "", "", "", ""));
                                }
                            }
                            String title = documentHome.getString("layout_title");
                            String bg = documentHome.getString("layout_background");
                            if (bg == null) bg = documentHome.getString("layout_backgrond");
                            
                            Home3Model model = new Home3Model(3, docId, Objects.toString(title, ""), Objects.toString(bg, "#FFFFFF"), gridList);
                            model.setContentIds(productsIds);
                            homepageList.add(model);
                        }
                    }
                    listener.onSuccess(homepageList);
                })
                .addOnFailureListener(e -> {
                    Log.e("Home3Repository", "Error getting layout", e);
                    listener.onFailure(e.getMessage());
                });
    }

    public void getHomepageLayout(OnDataLoadedListener<List<Home3Model>> listener) {
        getLayout("HOMEPAGE", listener);
    }
}
