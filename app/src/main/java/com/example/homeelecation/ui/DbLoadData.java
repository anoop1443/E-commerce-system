package com.example.homeelecation.ui;

import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;

import static com.example.homeelecation.ui.details.ProductDeteilsActivity.ALREADY_ADDED_TO_CART;
import static com.example.homeelecation.ui.details.ProductDeteilsActivity.ALREADY_ADDED_TO_WISHLIST;
import static com.example.homeelecation.ui.details.ProductDeteilsActivity.addToWishListButton;
import static com.example.homeelecation.ui.details.ProductDeteilsActivity.productID;
import static com.example.homeelecation.ui.details.ProductDeteilsActivity.running_cart_query;
import static com.example.homeelecation.ui.details.ProductDeteilsActivity.running_wishlist_query;
import static com.example.homeelecation.ui.wishList.Wishlist_Fragment.wishlistAdapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.homeelecation.R;
import com.example.homeelecation.ui.Cart.CartFragment;
import com.example.homeelecation.ui.Cart.CartModel;
import com.example.homeelecation.ui.address.Add_delivery_address_Activity3;
import com.example.homeelecation.ui.address.AddressesSelectModel;
import com.example.homeelecation.ui.categoryView.CategoryAdapter;
import com.example.homeelecation.ui.categoryView.CategoryModel;
import com.example.homeelecation.ui.details.ProductDeteilsActivity;
import com.example.homeelecation.ui.home.HomeFragment;
import com.example.homeelecation.ui.home.HomepageAdapter;
import com.example.homeelecation.ui.home.HomepageModel;
import com.example.homeelecation.ui.horizontal.HorizontalProductScrollModel;
import com.example.homeelecation.ui.place.PLaceActivity3;
import com.example.homeelecation.ui.slideshow.SliderModel;
import com.example.homeelecation.ui.wishList.WishlistModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DbLoadData {

    @SuppressLint("StaticFieldLeak")
    public static FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
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
    public static List<CartModel> cartItemModelList = new ArrayList<>();


    public static int selectedAddresses = -1;
    public static List<AddressesSelectModel> addressesSelectModelList = new ArrayList<>();


    public static void loadCategory(RecyclerView recyclerView, Context context) {

        firebaseFirestore.collection("CATEGORY").orderBy("index")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Log.d(TAG, document.getId() + " => " + document.getData());
                                categoryModelList.add(new CategoryModel(Objects.requireNonNull(document.get("icon")).toString()
                                        , document.get("categoryName").toString()));

                            }
                            CategoryAdapter categoryAdapter = new CategoryAdapter(categoryModelList);
                            recyclerView.setAdapter(categoryAdapter);
                            categoryAdapter.notifyDataSetChanged();
                            HomeFragment.swipeRefreshLayout.setRefreshing(false);


                        } else {

                            String error = task.getException().getMessage();
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();

                            // Log.w(TAG, "Error getting documents.", task.getException());
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
                                // List<SliderModel> sliderModelList = new ArrayList<>();
                                if ((long) documentHome.get("view_type") == 0) {

                                    long no_of_banner = (long) documentHome.get("no_of_banner");
                                    for (long x = 1; x < no_of_banner + 1; x++) {
                                        sliderModelList.add(new SliderModel(documentHome.get("banner_" + x).toString(),
                                                documentHome.get("banner_" + x + "_background").toString()));
                                        //document.get("banner_"+x+"background").toString()));
                                    }
                                    homepageModelList.add(new HomepageModel(0, sliderModelList));


                                } else if ((long) documentHome.get("view_type") == 1) {

                                    homepageModelList.add(new HomepageModel(1, documentHome.get("strip_ads").toString(),
                                            documentHome.get("stirp_ad_background").toString()));


                                } else if ((long) documentHome.get("view_type") == 2) {

                                    List<WishlistModel> viewAllProductList = new ArrayList<>();
                                    List<HorizontalProductScrollModel> horizontalproductscrollModelList = new ArrayList<>();
                                    long no_of_product = (long) documentHome.get("no_of_product");
                                    for (long x = 1; x < no_of_product + 1; x++) {
                                        horizontalproductscrollModelList.add(new HorizontalProductScrollModel(documentHome.get("product_ID_" + x).toString(),
                                                documentHome.get("product_image_" + x).toString(), documentHome.get("product_title_" + x).toString(),
                                                documentHome.get("product_destitle_" + x).toString()
                                                , documentHome.get("product_prise_" + x).toString()));

                                        viewAllProductList.add(new WishlistModel(documentHome.get("product_ID_" + x).toString(), documentHome.get("product_image_" + x).toString(),
                                                (long) documentHome.get("freeCoupon_" + x),
                                                (double) documentHome.get("starRating_" + x),
                                                (long) documentHome.get("total_rating_" + x),
                                                documentHome.get("product_title_" + x).toString(),
                                                (long) documentHome.get("wishlist_prise_" + x),
                                                (long) documentHome.get("product_catPrise_" + x),
                                                documentHome.get("payment_method_" + x).toString()));
                                        // (boolean) documentHome.get("")

                                    }

                                    homepageModelList.add(new HomepageModel(2, documentHome.get("layout_title").toString(), documentHome.get("layout_backgrond").toString(), horizontalproductscrollModelList, viewAllProductList));


                                } else if ((long) documentHome.get("view_type") == 3) {
                                    List<HorizontalProductScrollModel> gridLayoutModelList = new ArrayList<>();
                                    long no_of_product = (long) documentHome.get("no_of_product");
                                    for (long x = 1; x < no_of_product + 1; x++) {
                                        gridLayoutModelList.add(new HorizontalProductScrollModel(documentHome.get("product_ID_" + x).toString(),
                                                documentHome.get("product_image_" + x).toString(), documentHome.get("product_title_" + x).toString(),
                                                documentHome.get("product_destitle_" + x).toString()
                                                , documentHome.get("product_prise_" + x).toString()));
                                    }

                                    homepageModelList.add(new HomepageModel(3, documentHome.get("layout_title").toString(), documentHome.get("layout_backgrond").toString(), gridLayoutModelList));


                                }


                            }


                            HomeFragment.swipeRefreshLayout.setRefreshing(false);
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

    public static void loadCategoryActivity(RecyclerView categoryActivityRecycler, Context context, final int index, @androidx.annotation.NonNull String categoryName) {
        firebaseFirestore.collection("CATEGORY")
                .document(categoryName.toUpperCase())
                .collection("CATEGORY_ACTIVITY")
                .orderBy("index")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentHome : task.getResult()) {
                                //List<SliderModel> sliderModelList = new ArrayList<>();
                                if ((long) documentHome.get("view_type") == 0) {

                                    long no_of_banner = (long) documentHome.get("no_of_banner");
                                    for (long x = 1; x < no_of_banner + 1; x++) {
                                        sliderModelList.add(new SliderModel(documentHome.get("banner_" + x).toString(),
                                                documentHome.get("banner_" + x + "_background").toString()));
                                        //document.get("banner_"+x+"background").toString()));
                                    }
                                    lists.get(index).add(new HomepageModel(0, sliderModelList));


                                } else if ((long) documentHome.get("view_type") == 1) {

                                    lists.get(index).add(new HomepageModel(1, documentHome.get("strip_ads").toString(),
                                            documentHome.get("stirp_ad_background").toString()));


                                } else if ((long) documentHome.get("view_type") == 2) {

                                    List<WishlistModel> viewAllProductList = new ArrayList<>();
                                    List<HorizontalProductScrollModel> horizontalproductscrollModelList = new ArrayList<>();
                                    long no_of_product = (long) documentHome.get("no_of_product");
                                    for (long x = 1; x < no_of_product + 1; x++) {
                                        horizontalproductscrollModelList.add(new HorizontalProductScrollModel(documentHome.get("product_ID_" + x).toString(),
                                                documentHome.get("product_image_" + x).toString(), documentHome.get("product_title_" + x).toString(),
                                                documentHome.get("product_destitle_" + x).toString()
                                                , documentHome.get("product_prise_" + x).toString()));

                                        viewAllProductList.add(new WishlistModel(documentHome.get("product_ID_" + x).toString(), documentHome.get("product_image_" + x).toString(),
                                                (long) documentHome.get("freeCoupon_" + x),
                                                (double) documentHome.get("starRating_" + x),
                                                (long) documentHome.get("total_rating_" + x),
                                                documentHome.get("product_title_" + x).toString(),
                                                (long) documentHome.get("wishlist_prise_" + x),
                                                (long) documentHome.get("product_catPrise_" + x),
                                                documentHome.get("payment_method_" + x).toString()));
                                        // (boolean) documentHome.get("")

                                    }

                                    lists.get(index).add(new HomepageModel(2, documentHome.get("layout_title").toString(), documentHome.get("layout_backgrond").toString(), horizontalproductscrollModelList, viewAllProductList));


                                } else if ((long) documentHome.get("view_type") == 3) {
                                    List<HorizontalProductScrollModel> gridLayoutModelList = new ArrayList<>();
                                    long no_of_product = (long) documentHome.get("no_of_product");
                                    for (long x = 1; x < no_of_product + 1; x++) {
                                        gridLayoutModelList.add(new HorizontalProductScrollModel(documentHome.get("product_ID_" + x).toString(),
                                                documentHome.get("product_image_" + x).toString(), documentHome.get("product_title_" + x).toString(),
                                                documentHome.get("product_destitle_" + x).toString()
                                                , documentHome.get("product_prise_" + x).toString()));
                                    }

                                    lists.get(index).add(new HomepageModel(3, documentHome.get("layout_title").toString(), documentHome.get("layout_backgrond").toString(), gridLayoutModelList));


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

    public static void loadWishList(Context context, Dialog dialog, final boolean loadingFragment) {

        wishLisT.clear();
        firebaseFirestore.collection("USER").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_WISHLIST")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@androidx.annotation.NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()) {

                            for (long x = 0; x < (long) task.getResult().get("list_size"); x++) {
                                wishLisT.add(task.getResult().get("product_ID_" + x).toString());
                                if (wishLisT.contains(productID)) {
                                    ALREADY_ADDED_TO_WISHLIST = true;
                                    if (addToWishListButton != null) {
                                        addToWishListButton.setSupportImageTintList(context.getResources().getColorStateList(R.color.wish));
                                    }

                                } else {
                                    ALREADY_ADDED_TO_WISHLIST = false;
                                    if (addToWishListButton != null) {
                                        addToWishListButton.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#A8A7A7")));
                                    }
                                }

                                if (loadingFragment) {
                                    String productID = task.getResult().get("product_ID_" + x).toString();
                                    firebaseFirestore.collection("Product_Details").document(productID)
                                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@androidx.annotation.NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {

                                                        wishlistModelList.add(new WishlistModel(productID, task.getResult().get("product_image_1").toString(),
                                                                (long) task.getResult().get("freeCoupon"),
                                                                Double.parseDouble(task.getResult().get("average_rating").toString()),
                                                                (long) task.getResult().get("total_ratings"),
                                                                task.getResult().get("product_title").toString(),
                                                                (long) task.getResult().get("product_prise"),
                                                                (long) task.getResult().get("product_prise_cat"),
                                                                task.getResult().get("payment_method").toString()));

                                                        wishlistAdapter.notifyDataSetChanged();
                                                    } else {

                                                        String error = task.getException().getMessage();
                                                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }

                            }

                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                        }

                        dialog.dismiss();

                    }
                });
    }

    public static void removeFromWishList(int index, Context context, Dialog dialog) {

       final String removeProductID = wishLisT.get(index);
        wishLisT.remove(index);

        Map<String, Object> upDataWishlist = new HashMap<>();

        for (int x = 0; x < wishLisT.size(); x++) {
            upDataWishlist.put("product_ID_" + x, wishLisT.get(x));
        }
        upDataWishlist.put("list_size", (long) wishLisT.size());

        firebaseFirestore.collection("USER").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_WISHLIST")
                .set(upDataWishlist).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@androidx.annotation.NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            if (wishlistModelList.size() != 0) {
                                wishlistModelList.remove(index);
                                wishlistAdapter.notifyDataSetChanged();
                            }
                            ALREADY_ADDED_TO_WISHLIST = false;
                            Toast.makeText(context, "Product remove", Toast.LENGTH_SHORT).show();

                            dialog.dismiss();
                        } else {
                            wishLisT.add(index,removeProductID);
                            dialog.dismiss();
                            addToWishListButton.setSupportImageTintList(context.getResources().getColorStateList(R.color.wish));
                            String error = task.getException().getMessage();
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                        }
//                        if (addToWishListButton!=null) {
//                            addToWishListButton.setClickable(true);
//                        }
                        running_wishlist_query = false;
                    }
                });
    }

    public static void loadRatingList(Context context) {
        // if (!ProductDeteilsActivity.running_rating_query) {
        //  ProductDeteilsActivity.running_rating_query =true;
        ratingsId.clear();
        myRatings.clear();

        firebaseFirestore.collection("USER").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_RATINGS")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@androidx.annotation.NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {

                            for (long x = 0; x < (long) task.getResult().get("list_size"); x++) {
                                ratingsId.add(task.getResult().get("product_ID_" + x).toString());
                                myRatings.add((long) task.getResult().get("rating_" + x));

                                if (task.getResult().get("product_ID_" + x).toString().equals(productID)) {
                                    ProductDeteilsActivity.initialRating = Integer.parseInt(String.valueOf((long) task.getResult().get("rating_" + x))) - 1;
                                    if (ProductDeteilsActivity.rateNowContainer != null) {
                                        ProductDeteilsActivity.setRating(ProductDeteilsActivity.initialRating);
                                    }
                                }

                            }

                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();

                        }
                    }
                });
        // }

    }

    public static void loadCartList(final Context context, Dialog dialog, boolean loadingFragment, final TextView badgeCount,TextView totalAmount) {

        cartLis.clear();


        firebaseFirestore.collection("USER").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_CART")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@androidx.annotation.NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()) {

                            for (long x = 0; x < (long) task.getResult().get("list_size"); x++) {
                                cartLis.add(task.getResult().get("product_ID_" + x).toString());
                                if (cartLis.contains(productID)) {
                                    ALREADY_ADDED_TO_CART = true;

                                } else {
                                    ALREADY_ADDED_TO_CART = false;

                                }

                                if (loadingFragment) {
                                    cartItemModelList.clear();
                                    String productID = task.getResult().get("product_ID_"+x).toString();
                                    firebaseFirestore.collection("Product_Details").document(productID)
                                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@androidx.annotation.NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {

                                                        int index =0;
                                                        if (cartLis.size() >= 2){
                                                            index = cartLis.size() -2;
                                                        }

                                                        cartItemModelList.add(index,new CartModel(0, productID, task.getResult().get("product_image_1").toString()
                                                                , task.getResult().get("product_title").toString()
                                                                , task.getResult().get("product_prise").toString()
                                                                , task.getResult().get("product_prise_cat").toString()
                                                                , task.getResult().get("freeCoupon").toString()
                                                                , "Order Place next 36 horse"
                                                                , task.getResult().get("payment_method").toString()
                                                        ,(boolean)task.getResult().get("inStock")));


                                                        if (cartLis.size()==1){
                                                            cartItemModelList.add(new CartModel(CartModel.CART_TOTAL_AMOUNT_LAYOUT));
                                                            LinearLayout parent = (LinearLayout) totalAmount.getParent();
                                                            parent.setVisibility(View.VISIBLE);
                                                        }
                                                        if (cartLis.size()==0){
                                                            cartItemModelList.clear();
                                                        }
                                                        CartFragment.cartAdapter.notifyDataSetChanged();
                                                        //CartActivity.cartAdapter.notifyDataSetChanged();


                                                    } else {

                                                        String error = task.getException().getMessage();
                                                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }

                            }
                            if (cartLis.size()!= 0){
                                badgeCount.setVisibility(View.VISIBLE);
                            }else {
                                badgeCount.setVisibility(View.INVISIBLE);
                            }

                            if (DbLoadData.cartLis.size() < 99) {
                                badgeCount.setText(String.valueOf(cartLis.size()));
                            }else {
                                badgeCount.setText("99");

                            }

                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                        }
                         dialog.dismiss();

                    }


                });


    }

    public static void removeFromCartList(int index, Context context, Dialog dialog,TextView totalAmount) {

       final String removeProductID = cartLis.get(index);
        cartLis.remove(index);

        Map<String, Object> upDataCartList = new HashMap<>();

        for (int x = 0; x < cartLis.size(); x++) {
            upDataCartList.put("product_ID_" + x, cartLis.get(x));
        }
        upDataCartList.put("list_size", (long) cartLis.size());

        firebaseFirestore.collection("USER").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_CART")
                .set(upDataCartList).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@androidx.annotation.NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            if (cartItemModelList.size() != 0) {
                                cartItemModelList.remove(index);
                                CartFragment.cartAdapter.notifyDataSetChanged();
                            }
                            ALREADY_ADDED_TO_CART = false;
                            if (cartLis.size()==0) {
                                LinearLayout parent = (LinearLayout) totalAmount.getParent();
                                parent.setVisibility(View.GONE);
                                cartItemModelList.clear();
                            }
                            Toast.makeText(context, "Product remove", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();

                        } else {
                            cartLis.add(index,removeProductID);
                            dialog.dismiss();
                            String error = task.getException().getMessage();
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                        }

                        running_cart_query = false;

                    }
                });

    }

    public static void loadAddresses(Context context,Dialog lodingDialog){

        addressesSelectModelList.clear();

        firebaseFirestore.collection("USER").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_ADDRESSES")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@androidx.annotation.NonNull Task<DocumentSnapshot> task) {

                       long check = (long) task.getResult().get("list_size");
                        if (task.isSuccessful()){

                            Intent deliveryIntent;
                            if (check==0){
                                deliveryIntent = new Intent(context, Add_delivery_address_Activity3.class);
                                deliveryIntent.putExtra("INTENT","deliveryIntent");

                            }else {

                                for (int x = 1;x< (long)task.getResult().get("list_size")+1;x++){

                                    addressesSelectModelList.add(new AddressesSelectModel(task.getResult().get("fullName_"+x).toString()
                                    ,task.getResult().get("addresses_"+x).toString()
                                    ,task.getResult().get("addresses_phone_"+x).toString()
                                    ,(boolean)task.getResult().get("selected_"+x)));

                                    if ((boolean)task.getResult().get("selected_"+x)){
                                        selectedAddresses =x-1;

                                    }
                                }

                                deliveryIntent = new Intent(context, PLaceActivity3.class);
                            }

                            context.startActivity(deliveryIntent);
                        }else {
                            String error = task.getException().getMessage();
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();

                        }
                        lodingDialog.dismiss();

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
    }

}
