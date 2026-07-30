package com.example.homeelecation.ui.details;


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

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager.widget.ViewPager;

import com.example.homeelecation.LoginActivity;
import com.example.homeelecation.MainActivity;
import com.example.homeelecation.R;
import com.example.homeelecation.ui.Cart.CartActivity;
import com.example.homeelecation.ui.Cart.CartViewModel;
import com.example.homeelecation.ui.DbLoadData;
import com.example.homeelecation.ui.address.Add_delivery_address_Activity3;
import com.example.homeelecation.ui.address.AddressViewModel;
import com.example.homeelecation.ui.place.PLaceActivity3;
import com.example.homeelecation.ui.search.SearchActivity;
import com.example.homeelecation.ui.wishList.WishlistViewModel;
import com.example.homeelecation.util.EdgeToEdgeUtils;
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
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint

public class ProductDetailsActivity extends AppCompatActivity {

    private final List<String> Images = new ArrayList<>();
    private String serviceAmountValue = "0";

    public static boolean formSearch;

    private Button buyNow, addToCart;
    private TextView productTitle, productPrise, productCatPrise;
    public static FloatingActionButton addToWishListButton;
    public static int initialRating;
    ////// product details
    private ViewPager productImageViewpager, productDetailsViewpager;
    private CardView onlyDetail;
    private ConstraintLayout tabConstraint;
    private final List<productSpecificationModel> productSpecificationModelList = new ArrayList<>();
    private TextView onlyDetailsTextLayout;
    private TextView badgeCount;

    ////// product details

    private TabLayout viewpagerIndicator, productDetailsTabLayout;


    ///// rating layout
    private LinearLayout ratingNoContainer, ratingProgressBar, redeemLayout;
    public LinearLayout rateNowContainer;
    TextView averageStarRatingUp, totalRatingUp, totalRatingMiddle, totalRatingDune, averageStarRatingDune;

    ///// rating layout
    private Dialog singInDialog;
    private Dialog loadingDialog;

    private FirebaseFirestore firebaseFirestore;
    private DocumentSnapshot documentSnapshot;

    private ProductDetailsViewModel productDetailsViewModel;
    private WishlistViewModel wishlistViewModel;
    private CartViewModel cartViewModel;
    private AddressViewModel addressViewModel;
    public static String productID;
    private FirebaseAuth mAuth;

    private CardView serviceDetailsContainer;
    private TextView serviceChargeText, serviceDescriptionText;

    private String productTitleString, productDescriptionString, simPalDetailsString, paymentMethodString, averageRating;
    private double productPriseLong, productPriseCatLong, freeCouponLong, starRatingLong, totalRatingLong;

    private boolean useTabLayout, inStock, reward_layout, coupon_layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_deteils);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        // Apply Insets
        EdgeToEdgeUtils.applyTopInset(findViewById(R.id.appbar));
       // EdgeToEdgeUtils.applyBottomInset(findViewById(R.id.bottom_action_container));
        EdgeToEdgeUtils.applyBottomInset(findViewById(R.id.product_details_activity));


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
        productPrise = findViewById(R.id.product_images_layout_price_textview_fix);
        productCatPrise = findViewById(R.id.product_images_layout_catup_price_textview);
        redeemLayout = findViewById(R.id.product_images_layout_redeem_layout);
        onlyDetail = findViewById(R.id.only_details_contenr);
        onlyDetailsTextLayout = findViewById(R.id.product_simpal_details_textview);
        tabConstraint = findViewById(R.id.tab_constraintLayout);


        ratingNoContainer = findViewById(R.id.rating_number_container);
        averageStarRatingUp = findViewById(R.id.product_images_layout_wishlist_StarRating);
        averageStarRatingDune = findViewById(R.id.product_rating_layout_star_rating_text);
        totalRatingUp = findViewById(R.id.product_images_layout_average_rating_textview);
        totalRatingMiddle = findViewById(R.id.product_rating_layout_total_user_rating);
        totalRatingDune = findViewById(R.id.product_rating_layout_total_rating_figure);
        ratingProgressBar = findViewById(R.id.rating_progressBar_contenr);

        serviceDetailsContainer = findViewById(R.id.service_details_container);
        serviceChargeText = findViewById(R.id.service_charge_text);
        serviceDescriptionText = findViewById(R.id.service_description_text);
        rateNowContainer = findViewById(R.id.rate_now_contenr);


        //initialRating = -1;

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
                Intent register = new Intent(singInDialog.getContext(), LoginActivity.class);
                startActivity(register);
                Toast.makeText(singInDialog.getContext(), "Activity Register available", Toast.LENGTH_SHORT).show();


            }

        });
        // dialog

        // loading dialog
        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(true);

        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        // loadingDialog.show();
        // loading dialog


        firebaseFirestore = FirebaseFirestore.getInstance();

        productDetailsViewModel = new ViewModelProvider(this).get(ProductDetailsViewModel.class);
        wishlistViewModel = new ViewModelProvider(this).get(WishlistViewModel.class);
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);
        addressViewModel = new ViewModelProvider(this).get(AddressViewModel.class);


        productID = getIntent().getStringExtra("PRODUCT_ID");
        mAuth = FirebaseAuth.getInstance();


        if (productID != null && !productID.isEmpty()) {
            setupViewModelObserversProduct();
            productDetailsViewModel.loadProductDetails(productID);

            if (mAuth.getCurrentUser() != null) {
                wishlistViewModel.loadWishlist(loadingDialog, mAuth.getCurrentUser());
                cartViewModel.loadCart();
            }

            buyNow.setOnClickListener(v -> {


            loadingDialog.show();
            if (mAuth.getCurrentUser() == null) {
                singInDialog.show();
                loadingDialog.dismiss();
            }else {
                productDetailsViewModel.buyNow(serviceAmountValue);
            }

            });
        } else {
            buyNow.setText("Empty");
            loadingDialog.dismiss();
            Toast.makeText(this, "Empty", Toast.LENGTH_SHORT).show();
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);


    }

    // productDetails
    private void setupProductDetails(DocumentSnapshot document) {
        // Safe boolean check
        Boolean useTabLayout = document.getBoolean("useTabLayout");
        if (useTabLayout != null && useTabLayout) {
            tabConstraint.setVisibility(View.VISIBLE);
            onlyDetail.setVisibility(View.GONE);

            String productDescription = document.getString("simPalDetails");
            productSpecificationModelList.clear();

            // 1. Pura specifications array safely nikal lo
            Object specObject = document.get("specifications");
            if (specObject instanceof List) {
                List<?> specList = (List<?>) specObject;

                for (Object item : specList) {
                    if (item instanceof Map) {
                        Map<String, Object> specification = (Map<String, Object>) item;
                        // 2. Title add karo (Type 0)
                        String title = String.valueOf(specification.get("title"));
                        productSpecificationModelList.add(new productSpecificationModel(0, title));

                        // 3. Us title ke andar ke saare fields add karo (Type 1)
                        Object fieldsObject = specification.get("fields");
                        if (fieldsObject instanceof List) {
                            List<Map<String, String>> fields = (List<Map<String, String>>) fieldsObject;
                            for (Map<String, String> field : fields) {
                                productSpecificationModelList.add(new productSpecificationModel(1,
                                        String.valueOf(field.get("name")),
                                        String.valueOf(field.get("value"))));
                            }
                        }
                    }
                }
            }

            String productMoreInfo = document.getString("simPalDetails");
            productDetailsViewpager.setAdapter(new productDetailsAdapter(getSupportFragmentManager(),
                    productDetailsTabLayout.getTabCount(), productDescription != null ? productDescription : "",
                    productSpecificationModelList, productMoreInfo != null ? productMoreInfo : ""));

        } else {
            tabConstraint.setVisibility(View.GONE);
            onlyDetail.setVisibility(View.VISIBLE);
            String simpleDetails = document.getString("simPalDetails");
            onlyDetailsTextLayout.setText(simpleDetails != null ? simpleDetails : "");
        }
    }


    // productDetails

    // install
    private void setupServiceDetails(DocumentSnapshot document) {
        if (document.contains("service_info")) {
            Object serviceObj = document.get("service_info");
            if (serviceObj instanceof Map) {
                Map<String, Object> serviceInfo = (Map<String, Object>) serviceObj;
                if (Boolean.TRUE.equals(serviceInfo.get("is_service"))) {
                    serviceDetailsContainer.setVisibility(View.VISIBLE);

                    Object p = serviceInfo.get("price");
                    serviceAmountValue = (p != null) ? String.valueOf(p) : "0";

                    if (serviceAmountValue.equals("0") || serviceAmountValue.equalsIgnoreCase("free")) {
                        serviceChargeText.setText("Free Installation");
                        serviceChargeText.setTextColor(getResources().getColor(R.color.green));
                        serviceAmountValue = "0"; // कैलकुलेशन के लिए 0 कर दें
                    } else {
                        serviceChargeText.setText("Rs." + serviceAmountValue);
                        serviceChargeText.setTextColor(getResources().getColor(R.color.wish));
                    }

                    // Description with Bullet handling
                    if (serviceInfo.get("details") != null) {
                        String details = serviceInfo.get("details").toString().replace("\\n", "\n");
                        serviceDescriptionText.setText(details);
                    }
                } else {
                    serviceDetailsContainer.setVisibility(View.GONE);
                }
            } else {
                serviceDetailsContainer.setVisibility(View.GONE);
            }
        } else {
            serviceDetailsContainer.setVisibility(View.GONE);
        }
    }

    // install

    /////rating

    public void setRating(int starPosition) {


        for (int x = 0; x < rateNowContainer.getChildCount(); x++) {
            ImageView starBtn = (ImageView) rateNowContainer.getChildAt(x);
            starBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor("#FFADB1AD")));
            if (x <= starPosition) {
                starBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor("#05A620")));
            }
        }

    }

    private void calculateAndDisplayRatings(DocumentSnapshot doc) {
        long[] starCounts = new long[5];
        long totalRatings = 0;
        long totalStarValue = 0;

        for (int i = 0; i < 5; i++) {
            // Firebase se har star ka count padhein (e.g., "5_star", "4_star", etc.)
            // ChildAt(0) is 5-star, ChildAt(4) is 1-star
            TextView starCountTextView = (TextView) ratingNoContainer.getChildAt(i);
            long count = 0;
            Object countObj = doc.get((5 - i) + "_star");
            if (countObj instanceof Number) {
                count = ((Number) countObj).longValue();
            }
            starCounts[i] = count;
            starCountTextView.setText(String.valueOf(count));

            totalRatings += count;
            totalStarValue += count * (5 - i);
        }

        String avgRatingStr = "0.0";
        if (totalRatings > 0) {
            double averageRating = (double) totalStarValue / totalRatings;
            avgRatingStr = String.format("%.1f", averageRating);
        }

        // Saari rating views ko update karein
        averageStarRatingUp.setText(avgRatingStr);
        averageStarRatingDune.setText(avgRatingStr);
        totalRatingUp.setText(totalRatings + " Ratings");
        totalRatingMiddle.setText(totalRatings + " Ratings");
        totalRatingDune.setText(String.valueOf(totalRatings));

        // Progress bars ko update karein
        for (int x = 0; x < 5; x++) {
            ProgressBar progressBar = (ProgressBar) ratingProgressBar.getChildAt(x);
            if (progressBar != null) {
                progressBar.setMax((int) totalRatings);
                progressBar.setProgress((int) starCounts[x]);
            }
        }
    }

    /////rating


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_cart_icon, menu);

        // 1. मेन्यू आइटम और बैज लेआउट को सेटअप करें
        MenuItem cartItem = menu.findItem(R.id.men_cart);
        cartItem.setActionView(R.layout.badge_layout);
        ImageView badgeIcon = cartItem.getActionView().findViewById(R.id.badge_icon);
        badgeIcon.setImageResource(R.drawable.ic_cart);
        TextView badgeCountTextView = cartItem.getActionView().findViewById(R.id.badge_count); // इसका नाम बदलें

        // हम सीधे LiveData se value lete hain kyunki ye menu banane ke time chahiye
        Integer count = 0;
        if (cartViewModel != null && cartViewModel.getBadgeCount().getValue() != null) {
            count = cartViewModel.getBadgeCount().getValue();
        }

        // 3. बैज को अपडेट करें
        if (mAuth.getCurrentUser() != null && count != null && count > 0) {
            badgeCountTextView.setVisibility(View.VISIBLE);
            badgeCountTextView.setText(count < 99 ? String.valueOf(count) : "99");
        } else {
            badgeCountTextView.setVisibility(View.INVISIBLE);
        }
        cartItem.getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAuth.getCurrentUser() != null) {
                    Intent cartIntent = new Intent(ProductDetailsActivity.this, CartActivity.class);
                    startActivity(cartIntent);
                } else {
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
            if (formSearch) {
                finish();
            } else {
                Intent intentSearch = new Intent(ProductDetailsActivity.this, SearchActivity.class);
                startActivity(intentSearch);
            }
            return true;


        } else if (id == R.id.men_cart) {
            Toast.makeText(this, "please shopping ", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == android.R.id.home) {
            finish();
            return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        formSearch = false;
    }


    @Override
    protected void onStart() {
        super.onStart();    // 1. agar user login hai toh data refresh karein
        if (mAuth.getCurrentUser() != null) {
            if (cartViewModel != null) {
                cartViewModel.loadCart();
            }
            if (wishlistViewModel != null) {
                wishlistViewModel.loadWishlist(loadingDialog, mAuth.getCurrentUser());
            }
        }

        // 2. menu refresh karne ke liye
        invalidateOptionsMenu();
    }
    private void updateFlid(Dialog loadingDialog, String productID) {

        Map<String, Object> update = new HashMap<>();
        update.put("productTitle", productTitleString);
        update.put("paymentMethod", paymentMethodString);
        update.put("productDescription", productDescriptionString);
        update.put("simPalDetails", simPalDetailsString);
        update.put("averageRating", averageRating);
        update.put("productPrise", productPriseLong);
        update.put("productCatPrise", productPriseCatLong);
        update.put("freeCoupon", freeCouponLong);
        update.put("starRating", starRatingLong);
        update.put("totalRatings", totalRatingLong);
        update.put("useTabLayout", useTabLayout);
        update.put("couponLayout", coupon_layout);
        update.put("rewardLayout", reward_layout);

        firebaseFirestore.collection("Product_Details").document(productID)
                .update(update)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ProductDetailsActivity.this, "update", Toast.LENGTH_SHORT).show();
                        } else {
                            String error = task.getException() != null ? task.getException().getMessage() : "Unknown Error";
                            Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                        loadingDialog.dismiss();
                    }
                });

    }


    private void setupViewModelObserversProduct() {

        /**
         * ViewModel se aane wale LiveData ko observe karta hai
         */
        productDetailsViewModel.productDetails.observe(this, doc -> {
            if (doc != null && doc.exists()) {
                setupProductUI(doc);
            }
        });

        // 2. loading state
        productDetailsViewModel.isLoading.observe(this, isLoading -> {
            if (isLoading != null && isLoading) {
                loadingDialog.show();
            } else {
                loadingDialog.dismiss();
            }
        });

        // 3. status message
        productDetailsViewModel.statusMessage.observe(this, message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        });

        // 4. user rating
        productDetailsViewModel.userRating.observe(this, rating -> {
            if (rating != null && rating > 0) {
                initialRating = rating;
                setRating(rating - 1);
            } else {
                initialRating = 0;
                setRating(-1);
            }
        });

        // 5. wishlist status
        productDetailsViewModel.isWishlisted.observe(this, isWishlisted -> {
            if (isWishlisted != null && isWishlisted) {
                addToWishListButton.setSupportImageTintList(getResources().getColorStateList(R.color.wish));
            } else {
                addToWishListButton.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#9E9E9E")));
            }
        });

        // 7. Buy Now checkout
        productDetailsViewModel.navigateToCheckout.observe(this, cartItems -> {
            if (cartItems != null && !cartItems.isEmpty()) {
                PLaceActivity3.cartItemModelList = cartItems;
                addressViewModel.loadAddresses(true, true);
            }
        });

        // 8. cart badge count
        cartViewModel.getBadgeCount().observe(this, count -> {
            invalidateOptionsMenu();
        });


        wishlistViewModel.getWishlistedProductIds().observe(this, productIds -> {
            if (productIds != null && productIds.contains(productID)) {
                addToWishListButton.setSupportImageTintList(getResources().getColorStateList(R.color.wish));
            } else {
                addToWishListButton.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#A8A7A7")));
            }
        });

        // --- AddressViewModel Observers ---
        addressViewModel.navigateToAddAddress.observe(this, shouldNavigate -> {
            if (shouldNavigate != null && shouldNavigate) {
                Intent intent = new Intent(this, Add_delivery_address_Activity3.class);
                intent.putExtra("INTENT", "deliveryIntent");
                startActivity(intent);
                addressViewModel.onNavigationComplete();
            }
        });

        addressViewModel.navigateToPayment.observe(this, shouldNavigate -> {
            if (shouldNavigate != null && shouldNavigate) {
                Intent intent = new Intent(this, PLaceActivity3.class);
                startActivity(intent);
                addressViewModel.onNavigationComplete();
            }
        });


    }

    private void setupProductUI(DocumentSnapshot doc) {
        setupProductDetails(doc);
        calculateAndDisplayRatings(doc);
        setupServiceDetails(doc);

        Object imageUrlsObj = doc.get("imageUrls");
        if (imageUrlsObj instanceof List) {
            List<?> imageArray = (List<?>) imageUrlsObj;
            Images.clear();
            for (Object img : imageArray) {
                if (img instanceof String) {
                    Images.add((String) img);
                }
            }
            ProductImageAdapter productImageAdapter = new ProductImageAdapter(Images);
            productImageViewpager.setAdapter(productImageAdapter);
        }


        productTitleString = doc.getString("productTitle");
        productDescriptionString = doc.getString("productDescription");
        simPalDetailsString = doc.getString("simPalDetails");
        paymentMethodString = doc.getString("paymentMethod");
        averageRating = doc.getString("averageRating");
        
        Object pPrice = doc.get("productPrise");
        productPriseLong = (pPrice instanceof Number) ? ((Number) pPrice).doubleValue() : 0.0;
        
        Object pCatPrice = doc.get("productCatPrise");
        productPriseCatLong = (pCatPrice instanceof Number) ? ((Number) pCatPrice).doubleValue() : 0.0;
        
        Object fCoupon = doc.get("freeCoupon");
        freeCouponLong = (fCoupon instanceof Number) ? ((Number) fCoupon).doubleValue() : 0.0;
        
        Object sRating = doc.get("starRating");
        starRatingLong = (sRating instanceof Number) ? ((Number) sRating).doubleValue() : 0.0;
        
        Object tRating = doc.get("totalRatings");
        totalRatingLong = (tRating instanceof Number) ? ((Number) tRating).doubleValue() : 0.0;

        useTabLayout = Boolean.TRUE.equals(doc.getBoolean("useTabLayout"));
        coupon_layout = Boolean.TRUE.equals(doc.getBoolean("couponLayout"));
        reward_layout = Boolean.TRUE.equals(doc.getBoolean("rewardLayout"));


        // textView
        productTitle.setText(productTitleString != null ? productTitleString : "No Title");
        productPrise.setText("Rs." + productPriseLong);
        productCatPrise.setText("Rs." + productPriseCatLong);


        Object inStockObj = doc.get("inStock");
        if (inStockObj instanceof Boolean && (Boolean) inStockObj) {

            addToWishListButton.setOnClickListener(v -> {
                if (mAuth.getCurrentUser() == null) {
                    singInDialog.show();
                } else {
                    wishlistViewModel.toggleWishlistStatus(productID);
                }
            });
            addToCart.setOnClickListener(v -> {
                if (mAuth.getCurrentUser() == null) {
                    singInDialog.show();
                } else {
                    cartViewModel.addProductToCartIfNotExists(productID);
                }
            });

        } else {
            buyNow.setVisibility(View.GONE);
            addToCart.setText("Out of Stock");
            addToCart.setTextColor(Color.RED);
            addToCart.setEnabled(false);
            addToCart.setCompoundDrawables(null, null, null, null);
        }


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

        //rating
        for (int x = 0; x < rateNowContainer.getChildCount(); x++) {
            final int starPosition = x;
            rateNowContainer.getChildAt(x).setOnClickListener(v -> {
                if (mAuth.getCurrentUser() == null) {
                    singInDialog.show();
                    return;
                }

                if ((starPosition + 1) == initialRating) {
                    return;
                }
                setRating(starPosition);

                productDetailsViewModel.submitRating(productID, starPosition, initialRating);
            });
        }


    }



}
