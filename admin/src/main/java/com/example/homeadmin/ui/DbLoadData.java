package com.example.homeadmin.ui;

import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.homeadmin.ui.Cart.CartItemModel;
import com.example.homeadmin.ui.address.AddressesSelectModel;
import com.example.homeadmin.ui.categoryView.CategoryAdapter;
import com.example.homeadmin.ui.categoryView.CategoryModel;
import com.example.homeadmin.ui.home.HomepageAdapter;
import com.example.homeadmin.ui.home.HomepageModel;
import com.example.homeadmin.ui.horizontal.HorizontalProductScrollModel;
import com.example.homeadmin.ui.notification.NotificationModel;
import com.example.homeadmin.ui.orders.MyOrderItemModel;
import com.example.homeadmin.ui.slideshow.SliderModel;
import com.example.homeadmin.ui.wishList.WishlistModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DbLoadData {

    @SuppressLint("StaticFieldLeak")
    public static FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    public static String fullName,mobile,email,profileImage,gender;

    public static List<CategoryModel> categoryModelList = new ArrayList<CategoryModel>();
    public static List<String> loadedCategoriesName = new ArrayList<>();

    public static List<HomepageModel> homepageModelList = new ArrayList<>();
    public static List<SliderModel> sliderModelList = new ArrayList<>();

    public static List<List<HomepageModel>> lists = new ArrayList<>();

    public static List<String> wishLisT = new ArrayList<>();
    public static List<WishlistModel> wishlistModelList = new ArrayList<>();

    public static List<String> ratingsId = new ArrayList<>();
    public static List<Long> myRatings = new ArrayList<>();

    public static List<String> cartLis = new ArrayList<>();
    public static List<CartItemModel> cartItemModelList = new ArrayList<>();

    public static int selectedAddresses = -1;
    public static List<AddressesSelectModel> addressesSelectModelList = new ArrayList<>();

    public static List<MyOrderItemModel> myOrderItemModelList = new ArrayList<>();

    public  static List<NotificationModel> notificationModelList = new ArrayList<>();
    private  static ListenerRegistration registration;


    public static void loadCategory(RecyclerView recyclerView, Context context) {
        firebaseFirestore.collection("CATEGORY").orderBy("index")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                categoryModelList.add(new CategoryModel(
                                        document.getId(),
                                        Objects.requireNonNull(document.get("icon")).toString(),
                                        document.get("categoryName").toString()
                                ));

                            }
                            CategoryAdapter categoryAdapter = new CategoryAdapter(categoryModelList);
                            recyclerView.setAdapter(categoryAdapter);
                            categoryAdapter.notifyDataSetChanged();

                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public static void loadHomeFrag(RecyclerView homePageRecyclerView, Context context) {
        firebaseFirestore.collection("HOMEPAGE").orderBy("index")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentHome : task.getResult()) {
                                Long viewType = (Long) documentHome.get("view_type");
                                if (viewType == null) continue;

                                if (viewType == 0) {
                                    List<SliderModel> sliderModelList1 = new ArrayList<>();
                                    ArrayList<String> productsIds = (ArrayList<String>) documentHome.get("banners");
                                    if (productsIds == null) {
                                        productsIds = (ArrayList<String>) documentHome.get("bannersId");
                                    }

                                    if (productsIds != null) {
                                        for (String bannerId : productsIds) {
                                            sliderModelList1.add(new SliderModel(bannerId, "", ""));
                                        }
                                    } else {
                                        Object noBannerObj = documentHome.get("no_of_banner");
                                        if (noBannerObj != null) {
                                            long no_of_banner = (long) noBannerObj;
                                            for (long x = 1; x <= no_of_banner; x++) {
                                                Object bannerIcon = documentHome.get("banner_" + x);
                                                Object bannerBg = documentHome.get("banner_" + x + "_background");
                                                if (bannerIcon != null) {
                                                    sliderModelList1.add(new SliderModel("", bannerIcon.toString(),
                                                            bannerBg != null ? bannerBg.toString() : "#FFFFFF"));
                                                }
                                            }
                                        }
                                    }
                                    homepageModelList.add(new HomepageModel(0, documentHome.getId(), sliderModelList1));

                                } else if (viewType == 1) {
                                    String stripAd = documentHome.getString("ad_id");
                                    if (stripAd == null) stripAd = documentHome.getString("strip_ads");

                                    String stripColor = documentHome.getString("ad_background");
                                    if (stripColor == null) stripColor = documentHome.getString("stirp_ad_background");

                                    homepageModelList.add(new HomepageModel(1, documentHome.getId(), stripAd, "", stripColor));

                                } else if (viewType == 2) {
                                    List<WishlistModel> viewAllProductList = new ArrayList<>();
                                    List<HorizontalProductScrollModel> horizontalproductscrollModelList = new ArrayList<>();

                                    ArrayList<String> productsIds = (ArrayList<String>) documentHome.get("products");

                                    if (productsIds != null) {
                                        for (String productId : productsIds) {
                                            horizontalproductscrollModelList.add(new HorizontalProductScrollModel(productId, "", "", "", ""));
                                            viewAllProductList.add(new WishlistModel(productId, "", 0.0, 1.0, 1L, "", 0L, 0L, ""));
                                        }
                                    }

                                    String layoutTitle = documentHome.getString("layout_title");
                                    String layoutBackground = documentHome.getString("layout_background");
                                    if (layoutBackground == null) layoutBackground = documentHome.getString("layout_backgrond");

                                    homepageModelList.add(new HomepageModel(2, documentHome.getId(), layoutTitle, layoutBackground, horizontalproductscrollModelList, viewAllProductList));

                                } else if (viewType == 3) {
                                    List<HorizontalProductScrollModel> gridLayoutModelList = new ArrayList<>();
                                    ArrayList<String> productsIds = (ArrayList<String>) documentHome.get("products");

                                    if (productsIds != null) {
                                        for (String productId : productsIds) {
                                            gridLayoutModelList.add(new HorizontalProductScrollModel(productId, "", "", "", ""));
                                        }
                                    }

                                    String layoutTitle = documentHome.getString("layout_title");
                                    String layoutBackground = documentHome.getString("layout_background");
                                    if (layoutBackground == null) layoutBackground = documentHome.getString("layout_backgrond");

                                    homepageModelList.add(new HomepageModel(3, documentHome.getId(), layoutTitle, layoutBackground, gridLayoutModelList));
                                }
                            }

                            HomepageAdapter homepageAdapter = new HomepageAdapter(homepageModelList);
                            homePageRecyclerView.setAdapter(homepageAdapter);
                            homepageAdapter.notifyDataSetChanged();

                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    public static void loadCategoryActivity(RecyclerView categoryActivityRecycler, Context context, final int index, String categoryName) {
        firebaseFirestore.collection("CATEGORY")
                .document(categoryName.toUpperCase())
                .collection("CATEGORY_ACTIVITY")
                .orderBy("index")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentCate : task.getResult()) {
                                Long viewType = (Long) documentCate.get("view_type");
                                if (viewType == null) continue;

                                if (viewType == 0) {
                                    List<SliderModel> catsliderModelList = new ArrayList<>();
                                    ArrayList<String> productsIds = (ArrayList<String>) documentCate.get("banners");
                                    if (productsIds == null) {
                                        productsIds = (ArrayList<String>) documentCate.get("bannersId");
                                    }

                                    if (productsIds != null) {
                                        for (String bannerId : productsIds) {
                                            catsliderModelList.add(new SliderModel(bannerId, "", ""));
                                        }
                                    } else {
                                        Object noBannerObj = documentCate.get("no_of_banner");
                                        if (noBannerObj != null) {
                                            long no_banner = (long) noBannerObj;
                                            for (long x = 1; x < no_banner + 1; x++) {
                                                catsliderModelList.add(new SliderModel("", documentCate.get("banner_" + x).toString(),
                                                        documentCate.get("banner_" + x + "_background").toString()));
                                            }
                                        }
                                    }
                                    lists.get(index).add(new HomepageModel(0, documentCate.getId(), catsliderModelList));

                                } else if (viewType == 1) {
                                    String stripAd = documentCate.getString("ad_id");
                                    if (stripAd == null) stripAd = documentCate.getString("strip_ads");
                                    
                                    String stripColor = documentCate.getString("ad_background");
                                    if (stripColor == null) stripColor = documentCate.getString("stirp_ad_background");

                                    lists.get(index).add(new HomepageModel(1, documentCate.getId(), "", stripAd, stripColor));

                                } else if (viewType == 2) {
                                    List<WishlistModel> viewAllProductList = new ArrayList<>();
                                    List<HorizontalProductScrollModel> horizontalproductscrollModelList = new ArrayList<>();

                                    ArrayList<String> productsIds = (ArrayList<String>) documentCate.get("products");

                                    if (productsIds != null) {
                                        for (String productId : productsIds) {
                                            horizontalproductscrollModelList.add(new HorizontalProductScrollModel(productId, "", "", "", ""));
                                            viewAllProductList.add(new WishlistModel(productId, "", 0.0, 0.0, 0L, "", 0L, 0L, ""));
                                        }
                                    }

                                    String layoutTitle = documentCate.getString("layout_title");
                                    String layoutBackground = documentCate.getString("layout_background");
                                    if (layoutBackground == null) layoutBackground = documentCate.getString("layout_backgrond");

                                    lists.get(index).add(new HomepageModel(2, documentCate.getId(), layoutTitle, layoutBackground, horizontalproductscrollModelList, viewAllProductList));

                                } else if (viewType == 3) {
                                    List<HorizontalProductScrollModel> gridLayoutModelList = new ArrayList<>();
                                    ArrayList<String> productsIds = (ArrayList<String>) documentCate.get("products");

                                    if (productsIds != null) {
                                        for (String productId : productsIds) {
                                            gridLayoutModelList.add(new HorizontalProductScrollModel(productId, "", "", "", ""));
                                        }
                                    }

                                    String layoutTitle = documentCate.getString("layout_title");
                                    String layoutBackground = documentCate.getString("layout_background");
                                    if (layoutBackground == null) layoutBackground = documentCate.getString("layout_backgrond");

                                    lists.get(index).add(new HomepageModel(3, documentCate.getId(), layoutTitle, layoutBackground, gridLayoutModelList));
                                }
                            }

                            HomepageAdapter adapters = new HomepageAdapter(lists.get(index));
                            categoryActivityRecycler.setAdapter(adapters);
                            adapters.notifyDataSetChanged();

                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    public static void clearData(){
        categoryModelList.clear();
        loadedCategoriesName.clear();
        homepageModelList.clear();
        sliderModelList.clear();
        lists.clear();
        wishLisT.clear();
        wishlistModelList.clear();
        cartLis.clear();
        cartItemModelList.clear();
        myOrderItemModelList.clear();
        notificationModelList.clear();
        addressesSelectModelList.clear();
        selectedAddresses = -1;
    }

    public static void loadAddresses(final Context context, final Dialog loadingDialog, final boolean openActivityIfEmpty) {
        addressesSelectModelList.clear();
        selectedAddresses = -1;

        com.google.firebase.auth.FirebaseAuth mAuth = com.google.firebase.auth.FirebaseAuth.getInstance();
        if (mAuth.getUid() == null) return;

        firebaseFirestore.collection("USER").document(mAuth.getUid())
                .collection("MY_ADDRESSES")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().isEmpty()) {
                            if (openActivityIfEmpty) {
                                Intent addAddressIntent = new Intent(context, com.example.homeadmin.ui.address.Add_delivery_address_Activity3.class);
                                addAddressIntent.putExtra("INTENT", "deliveryIntent");
                                context.startActivity(addAddressIntent);
                            }
                        } else {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                boolean selected = Boolean.TRUE.equals(documentSnapshot.getBoolean("selected"));
                                String addressID = documentSnapshot.getId();
                                
                                AddressesSelectModel model = new AddressesSelectModel(
                                        documentSnapshot.getString("fullName"),
                                        documentSnapshot.getString("mobile"),
                                        documentSnapshot.getString("pinCode"),
                                        documentSnapshot.getString("state"),
                                        documentSnapshot.getString("city"),
                                        documentSnapshot.getString("house"),
                                        documentSnapshot.getString("area"),
                                        selected,
                                        addressID
                                );
                                model.setAddressType(documentSnapshot.getString("addressType"));
                                addressesSelectModelList.add(model);

                                if (selected) {
                                    selectedAddresses = addressesSelectModelList.size() - 1;
                                }
                            }
                            if (selectedAddresses == -1 && !addressesSelectModelList.isEmpty()) {
                                selectedAddresses = 0;
                            }
                        }
                    } else {
                        String error = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                    }
                    if (loadingDialog != null) loadingDialog.dismiss();
                });
    }
}
