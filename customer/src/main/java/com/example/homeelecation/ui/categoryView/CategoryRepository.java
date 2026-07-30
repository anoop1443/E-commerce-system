package com.example.homeelecation.ui.categoryView;

import android.util.Log;

import com.example.homeelecation.ui.home.HomepageModel;
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
public class CategoryRepository {

    private final FirebaseFirestore db;

    @Inject
    public CategoryRepository(FirebaseFirestore db) {
        this.db = db;
    }

    public interface OnDataLoadedListener<T> {
        void onSuccess(T data);
        void onFailure(String error);
    }

    public void getCategoryActivityLayout(String categoryName, OnDataLoadedListener<List<HomepageModel>> listener) {
        if (categoryName == null) {
            listener.onFailure("Category name is null");
            return;
        }

        db.collection("CATEGORY")
                .document(categoryName)
                .collection("CATEGORY_ACTIVITY")
                .orderBy("index")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<HomepageModel> homepageModelList = new ArrayList<>();
                    for (QueryDocumentSnapshot documentCate : queryDocumentSnapshots) {

                        Object viewTypeObj = documentCate.get("view_type");
                        if (!(viewTypeObj instanceof Number)) continue;

                        long viewType = ((Number) viewTypeObj).longValue();
                        String documentId = documentCate.getId();

                        if (viewType == 0) { // Banner Slider
                            List<SliderModel> sliderModelList = new ArrayList<>();
                            Object bannersObj = documentCate.get("bannersId");
                            if (bannersObj == null){
                                bannersObj = documentCate.get("banners");
                            }
                            if (bannersObj instanceof List) {
                                List<?> bannersList = (List<?>) bannersObj;
                                for (Object item : bannersList) {
                                    if (item instanceof String) {
                                        sliderModelList.add(new SliderModel((String) item, "", ""));
                                    }
                                }
                            }
                            homepageModelList.add(new HomepageModel(0, documentId, sliderModelList));

                        } else if (viewType == 1) { // Strip Ad
                            String adId = documentCate.getString("ad_id");
                            if (adId != null) {
                                homepageModelList.add(new HomepageModel(1, documentId, adId, "", ""));
                            }

                        } else if (viewType == 2) { // Horizontal Product Scroll
                            List<WishlistModel> viewAllProductList = new ArrayList<>();
                            List<HorizontalProductScrollModel> horizontalList = new ArrayList<>();

                            Object productsObj = documentCate.get("products");
                            if (productsObj instanceof List) {
                                List<?> productsList = (List<?>) productsObj;
                                for (Object item : productsList) {
                                    if (item instanceof String) {
                                        String productId = (String) item;
                                        // Initially add empty models, Adapter will load details
                                        horizontalList.add(new HorizontalProductScrollModel(productId, "", "", "", ""));
                                        viewAllProductList.add(new WishlistModel(productId, "", 0L, 1.0, 1L, "", 0L, 0L, ""));
                                    }
                                }
                            }

                            String layoutTitle = documentCate.getString("layout_title");
                            String layoutBackground = documentCate.getString("layout_background");
                            homepageModelList.add(new HomepageModel(2, documentId,
                                    layoutTitle != null ? layoutTitle : "",
                                    layoutBackground != null ? layoutBackground : "",
                                    horizontalList, viewAllProductList));

                        } else if (viewType == 3) { // Grid Product Layout
                            List<HorizontalProductScrollModel> gridList = new ArrayList<>();

                            Object productsObj = documentCate.get("products");
                            if (productsObj instanceof List) {
                                List<?> productsList = (List<?>) productsObj;
                                for (Object item : productsList) {
                                    if (item instanceof String) {
                                        gridList.add(new HorizontalProductScrollModel((String) item, "", "", "", ""));
                                    }
                                }
                            }

                            String layoutTitle = documentCate.getString("layout_title");
                            String layoutBackground = documentCate.getString("layout_background");
                            homepageModelList.add(new HomepageModel(3, documentId,
                                    layoutTitle != null ? layoutTitle : "",
                                    layoutBackground != null ? layoutBackground : "",
                                    gridList));
                        }
                    }
                    listener.onSuccess(homepageModelList);
                })
                .addOnFailureListener(e -> {
                    Log.e("CategoryRepository", "Error getting category activity layout", e);
                    listener.onFailure(e.getMessage());
                });
    }
}
