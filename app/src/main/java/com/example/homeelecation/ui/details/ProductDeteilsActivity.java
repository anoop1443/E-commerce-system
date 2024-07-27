package com.example.homeelecation.ui.details;

import static com.example.homeelecation.ui.DbLoadData.cartItemModelList;
import static com.example.homeelecation.ui.DbLoadData.cartLis;
import static com.example.homeelecation.ui.DbLoadData.loadRatingList;
import static com.example.homeelecation.ui.DbLoadData.loadWishList;
import static com.example.homeelecation.ui.DbLoadData.myRatings;
import static com.example.homeelecation.ui.DbLoadData.ratingsId;
import static com.example.homeelecation.ui.DbLoadData.removeFromWishList;
import static com.example.homeelecation.ui.DbLoadData.wishLisT;
import static com.example.homeelecation.ui.DbLoadData.wishlistModelList;


import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager.widget.ViewPager;


import com.example.homeelecation.MainActivity;
import com.example.homeelecation.R;
import com.example.homeelecation.RegisterActivity2;
import com.example.homeelecation.ui.Cart.CartActivity;
import com.example.homeelecation.ui.Cart.CartModel;
import com.example.homeelecation.ui.DbLoadData;
import com.example.homeelecation.ui.place.PLaceActivity3;
import com.example.homeelecation.ui.wishList.WishlistModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductDeteilsActivity extends AppCompatActivity {

    public static boolean ALREADY_ADDED_TO_WISHLIST = false;
    public static boolean ALREADY_ADDED_TO_CART = false;
    //
    public static boolean running_wishlist_query = false;
    public static boolean running_rating_query = false;
    public static boolean running_cart_query = false;

    private Button buyNow, addToCart;
    private TextView productTitle, averageStarRatingUp, totalRatingUp, totalRatingMiddle, productPrise, productCatPrise;
    public static FloatingActionButton addToWishListButton;
    public static int initialRating;
    private Toolbar toolbar;
    ////// product details
    private ViewPager productImageViewpager, productDetailsViewpager;
    private ConstraintLayout onlyDetail;
    private ConstraintLayout tabConstraint;
    private List<productSpecificationModel> productSpecificationModelList = new ArrayList<>();
    private String productDescription;
    private String productMoreInfo;

    private TextView onlyDetailsTextLayout;
    private TextView badgeCount;


    ////// product details

    private TabLayout viewpagerIndicator, productDetailsTabLayout;


    ///// rating layout
    private LinearLayout ratingNoContainer, ratingProgressBar, redeemLayout;
    public static LinearLayout rateNowContainer;
    TextView totalRatingDune, averagestarRatingDune;

    ///// rating layout
    private Dialog singInDialog;
    private Dialog loadingDialog;

    private FirebaseFirestore firebaseFirestore;
    private DocumentSnapshot documentSnapshot;

    public static String productID;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_deteils);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //recentlyRecycler = findViewById(R.id.recently_recyclerView);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Avatar");

        buyNow = findViewById(R.id.deteils_Activity_buyNow_btn);
        addToCart = findViewById(R.id.deteils_add_to_cart_btn);
        addToWishListButton = findViewById(R.id.add_to_wishlist_btn);
        productImageViewpager = findViewById(R.id.product_image_viewpager);
        viewpagerIndicator = findViewById(R.id.viewpager_indicator);
        productDetailsViewpager = findViewById(R.id.product_detalis_viewpager);
        productDetailsTabLayout = findViewById(R.id.product_details_tabLayout);
        productTitle = findViewById(R.id.product_images_layout_Title);
        averageStarRatingUp = findViewById(R.id.product_images_layout_wishlist_StarRating);
        averagestarRatingDune = findViewById(R.id.product_rating_layout_star_rating_text);
        totalRatingUp = findViewById(R.id.product_images_layout_average_rating_textview);
        totalRatingMiddle = findViewById(R.id.product_rating_layout_total_user_rating);
        totalRatingDune = findViewById(R.id.product_rating_layout_total_rating_figure);
        productPrise = findViewById(R.id.product_images_layout_price_textview_fix);
        productCatPrise = findViewById(R.id.product_images_layout_catup_price_textview);
        redeemLayout = findViewById(R.id.product_images_layout_redeem_layout);
        onlyDetail = findViewById(R.id.only_details_contenr);
        onlyDetailsTextLayout = findViewById(R.id.product_simpal_details_textview);
        tabConstraint = findViewById(R.id.tab_constraintLayout);
        ratingNoContainer = findViewById(R.id.rating_number_container);
        ratingProgressBar = findViewById(R.id.rating_progressBar_contenr);

        initialRating = -1;

        // dialog

        singInDialog = new Dialog(this);
        singInDialog.setContentView(R.layout.sing_in_dialog_layout);
        singInDialog.setCancelable(true);

        singInDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        Button dialogSingInBtn = singInDialog.findViewById(R.id.sing_in_btn);
        Button dialogSingUpBtn = singInDialog.findViewById(R.id.sing_up_btn);
        ImageView imageView = singInDialog.findViewById(R.id.dialog_image);
        imageView.setImageResource(R.drawable.address_icon);

        dialogSingInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent login = new Intent(singInDialog.getContext(), MainActivity.class);
                startActivity(login);
                Toast.makeText(singInDialog.getContext(), "Activity login available", Toast.LENGTH_SHORT).show();


            }
        });

        dialogSingUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent register = new Intent(singInDialog.getContext(), RegisterActivity2.class);
                startActivity(register);
                Toast.makeText(singInDialog.getContext(), "Activity Register available", Toast.LENGTH_SHORT).show();


            }

        });
        // dialog

        //loading dialog
        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(true);

        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        // loadingDialog.show();
        //loading dialog


        firebaseFirestore = FirebaseFirestore.getInstance();
        productID = getIntent().getStringExtra("PRODUCT_ID");
        mAuth = FirebaseAuth.getInstance();
        List<String> Images = new ArrayList<>();
        if (!productID.isEmpty()) {
            // loadingDialog.show();


            firebaseFirestore.collection("Product_Details").document(productID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        documentSnapshot = task.getResult();

                        if (documentSnapshot.exists()) {
                            Toast.makeText(ProductDeteilsActivity.this, "exists", Toast.LENGTH_SHORT).show();

                            long no_of_image = (long) documentSnapshot.get("no_of_image");

                            for (long x = 1; x < no_of_image + 1; x++) {
                                Images.add(documentSnapshot.get("product_image_" + x).toString());
                            }


                            ProductImageAdapter productImageAdapter = new ProductImageAdapter(Images);
                            productImageViewpager.setAdapter(productImageAdapter);

                            if ((boolean) documentSnapshot.get("coupon_redeem_layout")) {
                                redeemLayout.setVisibility(View.VISIBLE);

                            } else {
                                redeemLayout.setVisibility(View.GONE);

                            }

                            if ((boolean) documentSnapshot.get("use_tab_layout")) {
                                tabConstraint.setVisibility(View.VISIBLE);
                                onlyDetail.setVisibility(View.GONE);
                                productDescription = documentSnapshot.get("sim-pal_details").toString();
                                for (long x = 1; x < (long) documentSnapshot.get("spec_total_title") + 1; x++) {
                                    productSpecificationModelList.add(new productSpecificationModel(0, documentSnapshot.get("spec_title_" + x).toString()));

                                    for (long y = 1; y < (long) documentSnapshot.get("spec_title_" + x + "_total_fields") + 1; y++) {
                                        productSpecificationModelList.add(new productSpecificationModel(1, documentSnapshot.get("spec_title_" + x + "_field_" + y + "_name").toString(), documentSnapshot.get("spec_title_" + x + "_field_" + y + "_value").toString()));

                                    }

                                }
                                productMoreInfo = documentSnapshot.get("sim-pal_details").toString();
                                productDetailsViewpager.setAdapter(new productDetailsAdapter(getSupportFragmentManager(), productDetailsTabLayout.getTabCount(), productDescription, productSpecificationModelList, productMoreInfo));

                            } else {
                                tabConstraint.setVisibility(View.GONE);
                                onlyDetail.setVisibility(View.VISIBLE);
                                onlyDetailsTextLayout.setText(documentSnapshot.get("sim-pal_details").toString());
                            }


                            productTitle.setText(documentSnapshot.get("product_title").toString());
                            productPrise.setText("Rs." + documentSnapshot.get("product_prise").toString());
                            productCatPrise.setText("Rs." + documentSnapshot.get("product_prise_cat").toString());
                            averageStarRatingUp.setText(documentSnapshot.get("average_rating").toString());
                            averagestarRatingDune.setText(documentSnapshot.get("average_rating").toString());
                            totalRatingUp.setText(documentSnapshot.get("total_ratings").toString() + " Rating");
                            totalRatingMiddle.setText(documentSnapshot.get("total_ratings").toString() + " Rating");
                            totalRatingDune.setText(documentSnapshot.get("total_ratings").toString());


                            for (int x = 0; x < 5; x++) {

                                TextView starNo = (TextView) ratingNoContainer.getChildAt(x);
                                starNo.setText(documentSnapshot.get(5 - x + "_star").toString());

                                ProgressBar progressBar = (ProgressBar) ratingProgressBar.getChildAt(x);
                                int maxProgress = Integer.parseInt(String.valueOf((long) documentSnapshot.get("total_ratings")));
                                progressBar.setMax(maxProgress);
                                progressBar.setProgress(Integer.parseInt(String.valueOf((long) documentSnapshot.get(5 - x + "_star"))));

                            }


                        } else {
                            buyNow.setText("Empty");
                            Toast.makeText(ProductDeteilsActivity.this, "not Exit", Toast.LENGTH_SHORT).show();
                        }

                        if (mAuth.getCurrentUser() != null) {
                            if (myRatings.size() == 0) {
                                loadRatingList(ProductDeteilsActivity.this);

                            }


                            if (wishLisT.size() == 0) {
                                loadWishList(ProductDeteilsActivity.this, loadingDialog, false);
                            } else {
                                loadingDialog.dismiss();
                            }

                            if (ratingsId.contains(productID)) {
                                int index = ratingsId.indexOf(productID);
                                initialRating = Integer.parseInt(String.valueOf(myRatings.get(index))) - 1;
                                setRating(initialRating);
                            }
                            if (cartLis.contains(productID)) {
                                ALREADY_ADDED_TO_CART = true;

                            } else {
                                ALREADY_ADDED_TO_CART = false;

                            }

                            if (cartLis.contains(productID)){
                                ALREADY_ADDED_TO_CART = true;
                            }else {
                                ALREADY_ADDED_TO_CART = false;
                            }

                            if (wishLisT.contains(productID)) {
                                ALREADY_ADDED_TO_WISHLIST = true;
                                addToWishListButton.setSupportImageTintList(getResources().getColorStateList(R.color.wish));

                            } else {
                                ALREADY_ADDED_TO_WISHLIST = false;
                                addToWishListButton.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#A8A7A7")));
                                // addToWishListButton.setSupportImageTintList(getResources().getColorStateList(R.color.black));

                            }

                        } else {
                            loadingDialog.dismiss();
                        }

                        addToWishListButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (mAuth.getCurrentUser() == null) {
                                    singInDialog.show();

                                } else {
                                    loadingDialog.show();
                                    // addToWishListButton.setClickable(false);
                                    running_wishlist_query = true;
                                    if (ALREADY_ADDED_TO_WISHLIST) {
                                        int index = wishLisT.indexOf(productID);
                                        removeFromWishList(index, ProductDeteilsActivity.this, loadingDialog);
                                        addToWishListButton.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#A8A7A7")));
                                    } else {

                                        Map<String, Object> addProductId = new HashMap<>();
                                        addProductId.put("product_ID_" + wishLisT.size(), productID);

                                        firebaseFirestore.collection("USER").document(mAuth.getCurrentUser().getUid()).collection("USER_DATA").document("MY_WISHLIST")
                                                .update(addProductId).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if (task.isSuccessful()) {
                                                    Map<String,Object> listSize = new HashMap<>();
                                                    listSize.put("list_size", (long) wishLisT.size() + 1);
                                                    firebaseFirestore.collection("USER").document(mAuth.getCurrentUser().getUid()).collection("USER_DATA").document("MY_WISHLIST").update(listSize).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {

                                                            if (task.isSuccessful()) {
                                                                if (wishlistModelList.size() != 0) {
                                                                    wishlistModelList.add(new WishlistModel(productID,
                                                                            documentSnapshot.get("product_image_1").toString(),
                                                                            (long) documentSnapshot.get("freeCoupon"),
                                                                            (double) documentSnapshot.get("star_rating"),
                                                                            (long) documentSnapshot.get("total_ratings"),
                                                                            documentSnapshot.get("product_title").toString(),
                                                                            (long) documentSnapshot.get("product_prise"),
                                                                            (long) documentSnapshot.get("product_Prise_cat"),
                                                                            documentSnapshot.get("payment_method_").toString()));

                                                                }

                                                                ALREADY_ADDED_TO_WISHLIST = true;
                                                                addToWishListButton.setSupportImageTintList(getResources().getColorStateList(R.color.wish));
                                                                wishLisT.add(productID);
                                                                loadingDialog.dismiss();
                                                                Toast.makeText(ProductDeteilsActivity.this, "add to wishlist", Toast.LENGTH_SHORT).show();


                                                            } else {
                                                                addToWishListButton.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#A8A7A7")));
                                                                String error = task.getException().getMessage();
                                                                Toast.makeText(ProductDeteilsActivity.this, error, Toast.LENGTH_SHORT).show();

                                                            }
                                                            running_wishlist_query = false;
                                                        }
                                                    });


                                                } else {
                                                    loadingDialog.dismiss();
                                                    String error = task.getException().getMessage();
                                                    Toast.makeText(ProductDeteilsActivity.this, error, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                                    }
                                }
                            }
                        });

                        if ((boolean)documentSnapshot.get("inStock")){
                            addToWishListButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (mAuth.getCurrentUser() == null) {
                                        singInDialog.show();

                                    } else {
                                        loadingDialog.show();
                                        // addToWishListButton.setClickable(false);
                                        running_wishlist_query = true;
                                        if (ALREADY_ADDED_TO_WISHLIST) {
                                            int index = wishLisT.indexOf(productID);
                                            removeFromWishList(index, ProductDeteilsActivity.this, loadingDialog);
                                            addToWishListButton.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#A8A7A7")));
                                        } else {

                                            Map<String, Object> addProductId = new HashMap<>();
                                            addProductId.put("product_ID_" + wishLisT.size(), productID);

                                            firebaseFirestore.collection("USER").document(mAuth.getCurrentUser().getUid()).collection("USER_DATA").document("MY_WISHLIST").update(addProductId).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    if (task.isSuccessful()) {
                                                        Map<String, Object> listSize = new HashMap<>();
                                                        listSize.put("list_size", (long) wishLisT.size() + 1);
                                                        firebaseFirestore.collection("USER").document(mAuth.getCurrentUser().getUid()).collection("USER_DATA").document("MY_WISHLIST").update(listSize).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                if (task.isSuccessful()) {
                                                                    if (wishlistModelList.size() != 0) {
                                                                        wishlistModelList.add(new WishlistModel(productID,
                                                                                documentSnapshot.get("product_image_1").toString(),
                                                                                (long) documentSnapshot.get("freeCoupon"),
                                                                                (double) documentSnapshot.get("star_rating"),
                                                                                (long) documentSnapshot.get("total_ratings"),
                                                                                documentSnapshot.get("product_title").toString(),
                                                                                (long) documentSnapshot.get("product_prise"),
                                                                                (long) documentSnapshot.get("product_Prise_cat"),
                                                                                documentSnapshot.get("payment_method_").toString()));

                                                                    }

                                                                    ALREADY_ADDED_TO_WISHLIST = true;
                                                                    addToWishListButton.setSupportImageTintList(getResources().getColorStateList(R.color.wish));
                                                                    wishLisT.add(productID);
                                                                    loadingDialog.dismiss();
                                                                    Toast.makeText(ProductDeteilsActivity.this, "add to wishlist", Toast.LENGTH_SHORT).show();


                                                                } else {
                                                                    addToWishListButton.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#A8A7A7")));
                                                                    String error = task.getException().getMessage();
                                                                    Toast.makeText(ProductDeteilsActivity.this, error, Toast.LENGTH_SHORT).show();

                                                                }
                                                                running_wishlist_query = false;
                                                            }
                                                        });


                                                    } else {
                                                        loadingDialog.dismiss();
                                                        String error = task.getException().getMessage();
                                                        Toast.makeText(ProductDeteilsActivity.this, error, Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });

                                        }
                                    }
                                }
                            });

                            addToCart.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (mAuth.getCurrentUser() == null) {
                                        singInDialog.show();

                                    } else {
                                        if (!running_cart_query) {
                                            running_cart_query = true;

                                            if (ALREADY_ADDED_TO_CART) {
                                                running_cart_query = false;
                                                Toast.makeText(ProductDeteilsActivity.this, "ALREADY CART LIST", Toast.LENGTH_SHORT).show();

                                            } else {

                                                Map<String, Object> addProduct = new HashMap<>();
                                                addProduct.put("list_size", (long) cartLis.size() + 1);
                                                addProduct.put("product_ID_" + cartLis.size(), productID);

                                                firebaseFirestore.collection("USER").document(mAuth.getCurrentUser().getUid()).collection("USER_DATA").document("MY_CART")
                                                        .update(addProduct).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                if (task.isSuccessful()) {
                                                                    if (cartItemModelList.size() != 0) {

                                                                        long prise = Long.parseLong(documentSnapshot.get("product_prise").toString());
                                                                        long priseCat = Long.parseLong(documentSnapshot.get("product_prise_cat").toString());
                                                                        long freeCoupon = Long.parseLong(documentSnapshot.get("freeCoupon").toString());

                                                                        cartItemModelList.add(0,new CartModel(0, productID, documentSnapshot.get("product_image_1").toString()

                                                                                , documentSnapshot.get("product_title").toString()
                                                                                , String.valueOf(prise) //documentSnapshot.get("product_prise").toString()
                                                                                , String.valueOf(priseCat)//documentSnapshot.get("product_prise_cat").toString()
                                                                                , String.valueOf(freeCoupon)//documentSnapshot.get("freeCoupon").toString()
                                                                                , "Order Place next 36 horse"
                                                                                , documentSnapshot.get("payment_method").toString()
                                                                                ,(boolean)documentSnapshot.get("inStock")));


                                                                    }

                                                                    ALREADY_ADDED_TO_CART = true;
                                                                    cartLis.add(productID);
                                                                    loadingDialog.dismiss();
                                                                    invalidateOptionsMenu();
                                                                    Toast.makeText(ProductDeteilsActivity.this, "add to cartList", Toast.LENGTH_SHORT).show();


                                                                } else {
                                                                    loadingDialog.dismiss();
                                                                    String error = task.getException().getMessage();
                                                                    Toast.makeText(ProductDeteilsActivity.this, error, Toast.LENGTH_SHORT).show();
                                                                }
                                                                running_cart_query = false;

                                                            }
                                                        });

                                            }

                                        }
                                    }
                                }
                            });

                            buyNow.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    loadingDialog.show();
                                    if (mAuth.getCurrentUser() == null) {
                                        singInDialog.show();

                                    } else {
                                        PLaceActivity3.cartModelList = new ArrayList<>();

                                        long prise = Long.parseLong(documentSnapshot.get("product_prise").toString());
                                        long priseCat = Long.parseLong(documentSnapshot.get("product_prise_cat").toString());
                                        long freeCoupon = Long.parseLong(documentSnapshot.get("freeCoupon").toString());

                                        PLaceActivity3.cartModelList.add(0,new CartModel(0, productID, documentSnapshot.get("product_image_1").toString()

                                                , documentSnapshot.get("product_title").toString()
                                                , String.valueOf(prise) //documentSnapshot.get("product_prise").toString()
                                                , String.valueOf(priseCat)//documentSnapshot.get("product_prise_cat").toString()
                                                , String.valueOf(freeCoupon)//documentSnapshot.get("freeCoupon").toString()
                                                , "Order Place next 36 horse"
                                                , documentSnapshot.get("payment_method").toString()
                                                ,(boolean)documentSnapshot.get("inStock")));

                                        PLaceActivity3.cartModelList.add(new CartModel(CartModel.CART_TOTAL_AMOUNT_LAYOUT));

                                        if (DbLoadData.addressesSelectModelList.size()==0) {
                                            DbLoadData.loadAddresses(ProductDeteilsActivity.this, loadingDialog);
                                        }else {
                                            loadingDialog.dismiss();
                                            Intent intent = new Intent(ProductDeteilsActivity.this,PLaceActivity3.class);
                                            startActivity(intent);
                                        }
                                    }
                                }
                            });

                        }else {
                            buyNow.setVisibility(View.GONE);
                            addToCart.setText("Out of Stock");
                            addToCart.setTextColor(Color.parseColor("#000FFF"));
                            addToCart.setTextSize(25);
                            addToCart.setCompoundDrawables(null,null,null,null);
                        }

                    } else {
                        loadingDialog.dismiss();
                        String error = task.getException().getMessage();
                        Toast.makeText(ProductDeteilsActivity.this, error, Toast.LENGTH_SHORT).show();
                    }

                }
            });


            viewpagerIndicator.setupWithViewPager(productImageViewpager, true);




            productDetailsViewpager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(productDetailsTabLayout));
            productDetailsTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    productDetailsViewpager.setCurrentItem(tab.getPosition());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });


            /////rating

            rateNowContainer = findViewById(R.id.rate_now_contenr);
            for (int x = 0; x < rateNowContainer.getChildCount(); x++) {
                final int starPosition = x;
                rateNowContainer.getChildAt(x).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mAuth.getCurrentUser() == null) {
                            singInDialog.show();

                        } else {
                            if (starPosition != initialRating) {

                                if (!running_rating_query) {
                                    running_rating_query = true;


                                    setRating(starPosition);
                                    Map<String, Object> upDateRating = new HashMap<>();

                                    if (DbLoadData.ratingsId.contains(productID)) {
                                        TextView oldRating = (TextView) ratingNoContainer.getChildAt(5 - initialRating - 1);
                                        TextView finalRating = (TextView) ratingNoContainer.getChildAt(5 - starPosition - 1);


                                        upDateRating.put(initialRating + 1 + "_star", Long.parseLong(oldRating.getText().toString()) - 1);
                                        upDateRating.put(starPosition + 1 + "_star", Long.parseLong(finalRating.getText().toString()) + 1);
                                        upDateRating.put("average_rating", calculatorAverageRating((long) starPosition - initialRating, true));
                                    } else {
                                        // Map<String, Object> productRating = new HashMap<>();
                                        upDateRating.put(starPosition + 1 + "_star", (long) documentSnapshot.get(starPosition + 1 + "_star") + 1);
                                        upDateRating.put("average_rating", calculatorAverageRating((long) starPosition + 1, false));
                                        upDateRating.put("total_ratings", (long) documentSnapshot.get("total_ratings") + 1);
                                    }
                                    firebaseFirestore.collection("Product_Details").document(productID).update(upDateRating).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {

                                                Map<String, Object> myRating = new HashMap<>();
                                                if (DbLoadData.ratingsId.contains(productID)) {
                                                    myRating.put("rating_" + DbLoadData.ratingsId.indexOf(productID), (long) starPosition + 1);
                                                } else {
                                                    myRating.put("list_size", (long) DbLoadData.ratingsId.size() + 1);
                                                    myRating.put("product_ID_" + DbLoadData.ratingsId.size(), productID);
                                                    myRating.put("rating_" + DbLoadData.ratingsId.size(), (long) starPosition + 1);
                                                }

                                                firebaseFirestore.collection("USER").document(mAuth.getUid()).collection("USER_DATA").document("MY_RATINGS")
                                                        .update(myRating).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                if (task.isSuccessful()) {
                                                                    if (DbLoadData.ratingsId.contains(productID)) {
                                                                        myRatings.set(ratingsId.indexOf(productID), (long) starPosition + 1);

                                                                        TextView oldRating = (TextView) ratingNoContainer.getChildAt(5 - initialRating - 1);
                                                                        TextView finalRating = (TextView) ratingNoContainer.getChildAt(5 - starPosition - 1);

                                                                        oldRating.setText(String.valueOf(Integer.parseInt(oldRating.getText().toString()) - 1));
                                                                        finalRating.setText(String.valueOf(Integer.parseInt(finalRating.getText().toString()) + 1));

                                                                    } else {

                                                                        DbLoadData.ratingsId.add(productID);
                                                                        myRatings.add((long) starPosition + 1);

                                                                        TextView starNo = (TextView) ratingNoContainer.getChildAt(5 - starPosition - 1);
                                                                        starNo.setText(String.valueOf(Integer.parseInt(starNo.getText().toString()) + 1));

                                                                        totalRatingUp.setText(((long) documentSnapshot.get("total_ratings") + 1) + " Rating");
                                                                        totalRatingMiddle.setText((long) documentSnapshot.get("total_ratings") + 1 + " Rating");
                                                                        totalRatingDune.setText(String.format("%d", (long) documentSnapshot.get("total_ratings") + 1));
                                                                        //totalRatingDune.setText(documentSnapshot.get("total_ratings"));

                                                                        Toast.makeText(ProductDeteilsActivity.this, "THank you ! for Rating", Toast.LENGTH_SHORT).show();
                                                                    }

                                                                    for (int x = 0; x < 5; x++) {
                                                                        TextView starNoFigures = (TextView) ratingNoContainer.getChildAt(x);
                                                                        //starNo.setText(documentSnapshot.get(5 - x + "_star").toString());

                                                                        ProgressBar progressBar = (ProgressBar) ratingProgressBar.getChildAt(x);
                                                                        int maxProgress = Integer.parseInt(totalRatingDune.getText().toString());
                                                                        progressBar.setMax(maxProgress);
                                                                        progressBar.setProgress(Integer.parseInt(starNoFigures.getText().toString()));

                                                                    }
                                                                    initialRating = starPosition;
                                                                    averageStarRatingUp.setText(calculatorAverageRating(0, true));
                                                                    averagestarRatingDune.setText(calculatorAverageRating(0, true));

                                                                } else {
                                                                    setRating(initialRating);
                                                                    String error = task.getException().getMessage();
                                                                    Toast.makeText(ProductDeteilsActivity.this, error, Toast.LENGTH_SHORT).show();

                                                                }

                                                                running_rating_query = false;


                                                            }
                                                        });
                                            } else {
                                                running_rating_query = false;
                                                setRating(initialRating);
                                                String error = task.getException().getMessage();
                                                Toast.makeText(ProductDeteilsActivity.this, error, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                                }
                            }
                        }

                    }
                });
            }


            /////rating

        } else {
            buyNow.setText("Empty");
            loadingDialog.dismiss();
            Toast.makeText(this, "Empty", Toast.LENGTH_SHORT).show();
        }


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(layoutManager.HORIZONTAL);






    }

    /////rating

    public static void setRating(int starPosition) {


        for (int x = 0; x < rateNowContainer.getChildCount(); x++) {
            ImageView starBtn = (ImageView) rateNowContainer.getChildAt(x);
            starBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor("#FFADB1AD")));
            if (x <= starPosition) {
                starBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor("#05A620")));
            }
        }

    }

    private String calculatorAverageRating(long currentUserRating, boolean upDate) {
        Double totalStar = Double.valueOf(0);

        for (int x = 1; x < 6; x++) {
            TextView ratingNo = (TextView) ratingNoContainer.getChildAt(5 - x);
            totalStar = totalStar + (Long.parseLong(ratingNo.getText().toString()) * x);
        }


        totalStar = totalStar + currentUserRating;

        if (upDate) {
            return String.valueOf(totalStar / Long.parseLong(totalRatingDune.getText().toString())).substring(0, 3);
        } else {
            return String.valueOf(totalStar / (Long.parseLong(totalRatingDune.getText().toString()) + 1)).substring(0, 3);
        }

        // return totalStar / ((long) documentSnapshot.get("total_ratings") + 1);
    }
    /////rating


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_cart_icon, menu);

        MenuItem cartItem = menu.findItem(R.id.men_cart);
            cartItem.setActionView(R.layout.badge_layout);
            ImageView badgeIcon = cartItem.getActionView().findViewById(R.id.badge_icon);
            badgeIcon.setImageResource(R.drawable.ic_cart);
            badgeCount = cartItem.getActionView().findViewById(R.id.badge_count);

        if (mAuth.getCurrentUser()!=null){
            if (cartLis.size()==0){
              //  DbLoadData.loadCartList(this,loadingDialog,false,badgeCount);
            }else {
                badgeCount.setVisibility(View.VISIBLE);
                if (cartLis.size()<99){
                    badgeCount.setText(String.valueOf(cartLis.size()));
                }else {
                    badgeCount.setText("99");
                }
            }
        }

            cartItem.getActionView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mAuth.getCurrentUser()!=null){
                        Intent cartIntent = new Intent(ProductDeteilsActivity.this,CartActivity.class);
                        startActivity(cartIntent);
                    }else {
                        singInDialog.show();
                    }
                }
            });



        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.men_search) {
            // search code w
            Toast.makeText(this, "Ok", Toast.LENGTH_SHORT).show();

            return true;


        } else if (id == R.id.men_cart) {

//            Intent intent = new Intent(this, ChekActivity3.class);
//            startActivity(intent);
            //cart code w
            Toast.makeText(this, "please shopping ", Toast.LENGTH_SHORT).show();

            return true;
        } else if (id == android.R.id.home) {
            finish();
            return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        loadingDialog.show();
        if (mAuth.getCurrentUser() != null) {
            if (myRatings.size() == 0) {
                loadRatingList(ProductDeteilsActivity.this);

            }
            if (cartLis.size()==0){
                //loadCartList(ProductDeteilsActivity.this,loadingDialog,false,badgeCount);
            }

            if (wishLisT.size() == 0) {
                loadWishList(ProductDeteilsActivity.this, loadingDialog, false);
            } else {
                loadingDialog.dismiss();
            }

        } else {
            loadingDialog.dismiss();
        }

        if (DbLoadData.ratingsId.contains(productID)) {
            int index = ratingsId.indexOf(productID);
            initialRating = Integer.parseInt(String.valueOf(DbLoadData.myRatings.get(index))) - 1;
            setRating(initialRating);
        }

        if (cartLis.contains(productID)) {
            ALREADY_ADDED_TO_CART = true;

        } else {

            ALREADY_ADDED_TO_CART = false;
        }

        if (cartLis.contains(productID)){
            ALREADY_ADDED_TO_CART = true;
        }else {
            ALREADY_ADDED_TO_CART = false;
        }


        if (wishLisT.contains(productID)) {
            ALREADY_ADDED_TO_WISHLIST = true;
            addToWishListButton.setSupportImageTintList(getResources().getColorStateList(R.color.wish));

        } else {
            ALREADY_ADDED_TO_WISHLIST = false;
            addToWishListButton.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#A8A7A7")));
            // addToWishListButton.setSupportImageTintList(getResources().getColorStateList(R.color.black));

        }

        invalidateOptionsMenu();

    }
}