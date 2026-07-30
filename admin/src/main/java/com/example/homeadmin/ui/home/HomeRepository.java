package com.example.homeadmin.ui.home;

import com.example.homeadmin.ui.categoryView.CategoryModel;
import com.example.homeadmin.ui.horizontal.HorizontalProductScrollModel;
import com.example.homeadmin.ui.slideshow.SliderModel;
import com.example.homeadmin.ui.wishList.WishlistModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeRepository {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Callback interface taaki ViewModel ko data bhej sakein
    public interface OnDataLoadedListener<T> {
        void onSuccess(T data);
        void onFailure(String error);
    }

    // Category data fetch karne ka method
    public void getCategories(OnDataLoadedListener<List<CategoryModel>> listener) {
        db.collection("CATEGORY").orderBy("index").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<CategoryModel> categoryList = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        categoryList.add(new CategoryModel(
                                doc.getString("icon"),
                                doc.getString("categoryName")
                        ));
                    }
                    listener.onSuccess(categoryList);
                })
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    // Homepage layout data fetch karne ka method
    public void getHomepageLayout(OnDataLoadedListener<List<HomepageModel>> listener) {
        db.collection("HOMEPAGE").orderBy("index").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<HomepageModel> homepageList = new ArrayList<>();

                    // Yahan aapka poora logic aayega jo aapne DbLoadData.loadHomeFrag mein likha tha
                    // Main ek simplified version de raha hoon

                    for (QueryDocumentSnapshot documentHome : queryDocumentSnapshots) {
                        long viewType = documentHome.getLong("view_type");
                        if ((long) documentHome.get("view_type") == 0) {
                            List<SliderModel> sliderModelList1 = new ArrayList<>();

                            ArrayList<String> productsIds =(ArrayList<String>) documentHome.get("bannersId");

                            for (String productID: productsIds) {
                                sliderModelList1.add(new SliderModel(productID,
                                        "",
                                        ""));

                            }

                            homepageList.add(new HomepageModel(0, sliderModelList1));


                        } else if ((long) documentHome.get("view_type") == 1) {

                            homepageList.add(new HomepageModel(1, documentHome.get("strip_ads").toString(),
                                    documentHome.get("stirp_ad_background").toString()));


                        } else if ((long) documentHome.get("view_type") == 2) {

                            List<WishlistModel> viewAllProductList = new ArrayList<>();
                            List<HorizontalProductScrollModel> horizontalproductscrollModelList = new ArrayList<>();

                            ArrayList<String> productsIds =(ArrayList<String>) documentHome.get("products");

                            for (String productId : productsIds){

                                horizontalproductscrollModelList.add(new HorizontalProductScrollModel(productId,
                                        "",
                                        "",
                                        "",
                                        ""));

                                viewAllProductList.add(new WishlistModel(productId,
                                        "",
                                        (long) 0,
                                        (double) 1,
                                        (long) 1,
                                        "",
                                        (long) 0,
                                        (long) 0,
                                        ""));
                            }
//
                            homepageList.add(new HomepageModel(2, documentHome.get("layout_title").toString(), documentHome.get("layout_backgrond").toString(), horizontalproductscrollModelList, viewAllProductList));


                        } else if ((long) documentHome.get("view_type") == 3) {
                            List<HorizontalProductScrollModel> gridLayoutModelList = new ArrayList<>();

                            ArrayList<String> productsIds =(ArrayList<String>) documentHome.get("products");

                            for (String productId : productsIds) {

                                gridLayoutModelList.add(new HorizontalProductScrollModel(productId,
                                        "",
                                        "",
                                        "",
                                        ""));

                            }
//                                    }

                            homepageList.add(new HomepageModel(3, Objects.requireNonNull(documentHome.get("layout_title")).toString(), Objects.requireNonNull(documentHome.get("layout_backgrond")).toString(), gridLayoutModelList));


                        }

                    }
                    // Abhi ke liye, main ise khali chhod raha hoon
                    // listener.onSuccess(homepageList);
                    listener.onFailure("Homepage parsing not fully implemented in Repository.");

                })
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }
}
