package com.example.homeadmin.ui.details;

import static android.view.View.VISIBLE;
import static com.example.homeadmin.ui.DbLoadData.cartItemModelList;
import static com.example.homeadmin.ui.DbLoadData.cartLis;
//import static com.example.homeadmin.ui.DbLoadData.loadRatingList;
import static com.example.homeadmin.ui.DbLoadData.myRatings;
import static com.example.homeadmin.ui.DbLoadData.ratingsId;
import static com.example.homeadmin.ui.DbLoadData.wishLisT;
import static com.example.homeadmin.ui.DbLoadData.wishlistModelList;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
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
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager.widget.ViewPager;

import com.example.homeadmin.MainActivity;
import com.example.homeadmin.R;
import com.example.homeadmin.databinding.ProductSpecificationItemEditLayoutBinding;
import com.example.homeadmin.ui.Cart.CartItemModel;
import com.example.homeadmin.ui.DbLoadData;
import com.example.homeadmin.ui.addProduct.AddProductActivity;
import com.example.homeadmin.ui.addProduct.EditProductActivity;
import com.example.homeadmin.ui.place.PLaceActivity3;
import com.example.homeadmin.ui.search.SearchActivity;
import com.example.homeadmin.ui.wishList.WishlistModel;
import com.example.homeadmin.util.EdgeToEdgeUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ProductDetailsActivity extends AppCompatActivity {
    private static final String TAG = "ProductDetailsActivity";
        public static boolean ALREADY_ADDED_TO_WISHLIST = false;
        public static boolean ALREADY_ADDED_TO_CART = false;
        public static boolean running_wishlist_query = false;
        public static boolean running_rating_query = false;
        public static boolean running_cart_query = false;
        public static boolean formSearch;

        private Button buyNow, addToCart;
        private TextView productTitle, productPrise, productCatPrise;
        public static FloatingActionButton addToWishListButton;
        public static int initialRating;
        private Toolbar toolbar;
        ////// product details
        private ViewPager productImageViewpager, productDetailsViewpager;
        private CardView onlyDetail;
        private ConstraintLayout tabConstraint;
        private final List<productSpecificationModel> productSpecificationModelList = new ArrayList<>();
        private String productDescription;
        private String productMoreInfo;
        private TextView onlyDetailsTextLayout;
        private TextView badgeCount;

        ////// product details

        private TabLayout viewpagerIndicator, productDetailsTabLayout;


        ///// rating layout
        private LinearLayout ratingNoContainer, ratingProgressBar, redeemLayout;
        public static LinearLayout rateNowContainer;
        TextView averageStarRatingUp, totalRatingUp, totalRatingMiddle, totalRatingDune, averageStarRatingDune;

        ///// rating layout
        private Dialog singInDialog;
        private Dialog loadingDialog;

        private FirebaseFirestore firebaseFirestore;
        private DocumentSnapshot documentSnapshot;

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
            setContentView(R.layout.activity_product_details);
            toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            //recentlyRecycler = findViewById(R.id.recently_recyclerView);


            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle("Avatar");

            buyNow = findViewById(R.id.details_activity_buy_now_btn);
            addToCart = findViewById(R.id.details_add_to_cart_btn);
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
//                    Intent register = new Intent(singInDialog.getContext(), RegisterActivity2.class);
//                    startActivity(register);
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
            if (productID != null && !productID.isEmpty()) {
                // loadingDialog.show();


                firebaseFirestore.collection("Product_Details").document(productID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            documentSnapshot = task.getResult();
                            if (documentSnapshot.exists()) {
                                Toast.makeText(ProductDetailsActivity.this, "Product found", Toast.LENGTH_SHORT).show();

//                                productTitleString = documentSnapshot.get("product_title").toString();
//                                productDescriptionString = documentSnapshot.get("product_description").toString();
//                                simPalDetailsString = documentSnapshot.get("sim-pal_details").toString();
//                                paymentMethodString = documentSnapshot.get("payment_method").toString();
//                                averageRating = String.valueOf(documentSnapshot.get("star_rating"));
//
//                                productPriseLong = Double.parseDouble(documentSnapshot.get("product_prise").toString());
//                                productPriseCatLong = Long.parseLong(documentSnapshot.get("product_prise_cat").toString());
//                                freeCouponLong = Long.parseLong(documentSnapshot.get("freeCoupon").toString());
//                                starRatingLong = Double.parseDouble(documentSnapshot.get("star_rating").toString());
//                                totalRatingLong = Long.parseLong(documentSnapshot.get("total_ratings").toString());
//
//                                useTabLayout = (boolean) documentSnapshot.get("use_tab_layout");
//                                inStock = (boolean) documentSnapshot.get("inStock");
//                                coupon_layout = (boolean) documentSnapshot.get("coupon_redeem_layout");
//                                reward_layout = (boolean) documentSnapshot.get("reward_layout");
//

                                // 1. Tab Layout Setup
                                setupProductDetails(documentSnapshot);

                                // 2. Service/Installation Setup
                                setupServiceDetails(documentSnapshot);

                                // 3. Ratings Setup
                                calculateAndDisplayRatings(documentSnapshot);


                                List<String> imageArray = (List<String>) documentSnapshot.get("imageUrls");
                                Images.addAll(imageArray);

                                ProductImageAdapter productImageAdapter = new ProductImageAdapter(Images);
                                productImageViewpager.setAdapter(productImageAdapter);

                                if ((boolean) documentSnapshot.get("rewardLayout")) {
                                    redeemLayout.setVisibility(VISIBLE);

                                } else {
                                    redeemLayout.setVisibility(View.GONE);

                                }


                                productTitle.setText(documentSnapshot.get("productTitle").toString());
                                productPrise.setText("₹" + documentSnapshot.get("productPrise").toString());
                                productCatPrise.setText("₹" + documentSnapshot.get("productCatPrise").toString());
                                productCatPrise.setPaintFlags(productCatPrise.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                            } else {
                                buyNow.setText("Empty");
                                Toast.makeText(ProductDetailsActivity.this, "not Exit", Toast.LENGTH_SHORT).show();
                            }

                            // documentSnapshot के अंदर जहाँ आपने caret लगाया था, वहां ये लिखें:

                            if (mAuth.getCurrentUser() != null) {
                                if (myRatings.size() == 0) {
                                    //loadRatingList(ProductDetailsActivity.this);
                                    //loadRatingListNew(ProductDetailsActivity.this);

                                }


                                if (wishLisT.size() == 0) {
                                   // loadWishList(ProductDetailsActivity.this, loadingDialog, false);
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

                                if (cartLis.contains(productID)) {
                                    ALREADY_ADDED_TO_CART = true;
                                } else {
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

                        } else {
                            loadingDialog.dismiss();
                            String error = task.getException().getMessage();
                            Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
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
                    rateNowContainer.getChildAt(x).setOnClickListener(v -> {
                        if (mAuth.getCurrentUser() == null) {
                            singInDialog.show();
                            return;
                        }

                        if (starPosition == initialRating) {
                            // User ne usi star par dobara click kiya, kuch na karein
                            return;
                        }

                        if (running_rating_query) return;
                        running_rating_query = true;
                        loadingDialog.show();

                        setRating(starPosition); // UI turant update karein

                        // Product ke star count ko update karein
                        Map<String, Object> productRatingUpdate = new HashMap<>();
                        productRatingUpdate.put((starPosition + 1) + "_star", FieldValue.increment(1));

                        boolean isFirstTimeRating = initialRating == -1;

                        if (isFirstTimeRating) {
                            // Pehli baar rating, total ratings badhao
                            productRatingUpdate.put("totalRatings", FieldValue.increment(1));
                        } else {
                            // Rating badal rahe hain, purani rating ka count ghatao
                            productRatingUpdate.put((initialRating + 1) + "_star", FieldValue.increment(-1));
                        }

                        firebaseFirestore.collection("Product_Details").document(productID)
                                .update(productRatingUpdate)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        // Ab User ki personal rating ko naye Map structure me save karein
                                        Map<String, Object> userRatingUpdate = new HashMap<>();
                                        // Dot notation ka istemal karke seedhe map ke andar field update karein
                                        userRatingUpdate.put("ratings_map." + productID, (long) starPosition + 1);

                                        firebaseFirestore.collection("USER").document(mAuth.getUid()).collection("USER_DATA").document("MY_RATINGS")
                                                .update(userRatingUpdate)
                                                .addOnCompleteListener(userTask -> {
                                                    if (userTask.isSuccessful()) {
                                                        // UI update karne ke liye product ka naya data fetch karein
                                                        firebaseFirestore.collection("Product_Details").document(productID).get()
                                                                .addOnCompleteListener(refreshTask -> {
                                                                    running_rating_query = false;
                                                                    loadingDialog.dismiss();
                                                                    if (refreshTask.isSuccessful() && refreshTask.getResult() != null) {
                                                                        calculateAndDisplayRatings(refreshTask.getResult()); // Naye method se UI update karein
                                                                        initialRating = starPosition;
                                                                        Toast.makeText(ProductDetailsActivity.this, "Thank you for rating!", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                    } else {
                                                        // Agar user rating save na ho to UI ko wapas purani state me le jayein
                                                        running_rating_query = false;
                                                        loadingDialog.dismiss();
                                                        setRating(initialRating);
                                                        Toast.makeText(ProductDetailsActivity.this, "Failed to save your rating.", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    } else {
                                        running_rating_query = false;
                                        loadingDialog.dismiss();
                                        setRating(initialRating); // Agar product rating update na ho to UI ko wapas purani state me le jayein
                                        Toast.makeText(ProductDetailsActivity.this, "Failed to update product rating.", Toast.LENGTH_SHORT).show();
                                    }
                                });
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

        // productDetails
        private void setupProductDetails(DocumentSnapshot document) {
            // Safe boolean check
            Boolean useTabLayout = document.getBoolean("useTabLayout");
            if (useTabLayout != null && useTabLayout) {
                tabConstraint.setVisibility(VISIBLE);
                onlyDetail.setVisibility(View.GONE);

                productDescription = document.getString("productDescription"); // Corrected to Full Description
                productSpecificationModelList.clear();

                // 1. Pura specifications array safely nikal lo
                Object specObject = document.get("specifications");
                if (specObject instanceof List) {
                    List<Map<String, Object>> specList = (List<Map<String, Object>>) specObject;

                    for (Map<String, Object> specification : specList) {
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

                // Add More Info (Type 2) for Manufacturer Details
                String manufacturerName = document.getString("manufacturerName");
                String manufacturerAddress = document.getString("manufacturerAddress");
                if (manufacturerName != null && manufacturerAddress != null) {
                    productSpecificationModelList.add(new productSpecificationModel(2, manufacturerName, manufacturerAddress, ""));
                }

                productMoreInfo = document.getString("productDescription"); // Can be same as description if no specific field exists
                productDetailsViewpager.setAdapter(new productDetailsAdapter(getSupportFragmentManager(),
                        productDetailsTabLayout.getTabCount(), productDescription,
                        productSpecificationModelList, productMoreInfo));

            } else {
                tabConstraint.setVisibility(View.GONE);
                onlyDetail.setVisibility(VISIBLE);
                String simpleDetails = document.getString("simPalDetails"); // Short Description
                onlyDetailsTextLayout.setText(simpleDetails != null ? simpleDetails : "");
            }
        }


        // productDetails

        // install
        private void setupServiceDetails(DocumentSnapshot document) {
                if (document.contains("service_info")) {
                    Map<String, Object> serviceInfo = (Map<String, Object>) document.get("service_info");
                    if (serviceInfo != null && Boolean.TRUE.equals(serviceInfo.get("is_service"))) {
                        serviceDetailsContainer.setVisibility(VISIBLE);
                        // Price handling
                        String price = String.valueOf(serviceInfo.get("price"));
                        if (price.equalsIgnoreCase("free") || price.equals("0")) {
                            serviceChargeText.setText("Free Installation");
                            serviceChargeText.setTextColor(getResources().getColor(R.color.green));
                        } else {
                            serviceChargeText.setText("Rs. " + price);
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

        }

        // install

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

        private void calculateAndDisplayRatings(DocumentSnapshot doc) {
            long[] starCounts = new long[5];
            long totalRatings = 0;
            long totalStarValue = 0;

            for (int i = 0; i < 5; i++) {
                // Firebase se har star ka count padhein (e.g., "5_star", "4_star", etc.)
                // ChildAt(0) is 5-star, ChildAt(4) is 1-star
                TextView starCountTextView = (TextView) ratingNoContainer.getChildAt(i);
                long count = doc.getLong((5 - i) + "_star");
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
                TextView starNo = (TextView) ratingNoContainer.getChildAt(x);
                progressBar.setMax((int) totalRatings);
                // progressBar.setProgress(Integer.parseInt(starNo.getText().toString()));
                progressBar.setProgress((int) starCounts[x]);
            }
        }

        /////rating


        @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_cart_icon, menu);

//        MenuItem cartItem = menu.findItem(R.id.menu_add);
//        cartItem.setActionView(R.layout.badge_layout);
//        ImageView badgeIcon = cartItem.getActionView().findViewById(R.id.badge_icon);
//        badgeIcon.setImageResource(R.drawable.ic_cart);
//        badgeCount = cartItem.getActionView().findViewById(R.id.badge_count);
//
//        if (mAuth.getCurrentUser() != null) {
//            if (cartLis.size() == 0) {
//                //  DbLoadData.loadCartList(this,loadingDialog,false,badgeCount);
//            } else {
//                badgeCount.setVisibility(View.VISIBLE);
//                if (cartLis.size() < 99) {
//                    badgeCount.setText(String.valueOf(cartLis.size()));
//                } else {
//                    badgeCount.setText("99");
//                }
//            }
//        }

//        cartItem.getActionView().setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mAuth.getCurrentUser() != null) {
//                    Intent cartIntent = new Intent(ProductDetailsActivity.this, AddProductActivity.class);
//                    startActivity(cartIntent);
//                } else {
//                    singInDialog.show();
//                }
//            }
//        });


        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.men_search) {
            // search code w

            Intent intent = new Intent(ProductDetailsActivity.this, EditProductSpecificationsActivity.class);
            startActivity(intent);
//            if (formSearch) {
//                finish();
//            } else {
//                Intent intentSearch = new Intent(ProductDetailsActivity.this, SearchActivity.class);
//                startActivity(intentSearch);
//            }
            Toast.makeText(this, "Ok", Toast.LENGTH_SHORT).show();

            return true;


        } else if (id == R.id.menu_add) {

            Intent intent = new Intent(this, AddProductActivity.class);
            startActivity(intent);
            //cart code w
            Toast.makeText(this, "please shopping ", Toast.LENGTH_SHORT).show();

            return true;
        }else if (id == R.id.menu_edit){
            Intent intent = new Intent(ProductDetailsActivity.this, EditProductActivity.class);
            intent.putExtra(EditProductActivity.EXTRA_PRODUCT_ID, productID); // 'your_product_id' Firestore document ID है
            startActivity(intent);

        }else if (id == R.id.menu_delete){
            deleteProduct();
            return true;
        }else if (id == android.R.id.home) {
            finish();
            return true;

        }

        return super.onOptionsItemSelected(item);
    }

    private void deleteProduct() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Delete Product")
                .setMessage("Are you sure you want to move this product to the Recycle Bin?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    loadingDialog.show();
                    String label = "Product: " + (productTitle != null ? productTitle.getText().toString() : productID);
                    
                    // Get first image for trash preview
                    String previewUrl = null;
                    if (documentSnapshot != null && documentSnapshot.contains("imageUrls")) {
                        List<String> images = (List<String>) documentSnapshot.get("imageUrls");
                        if (images != null && !images.isEmpty()) {
                            previewUrl = images.get(0);
                        }
                    }

                    com.example.homeadmin.ui.trash.TrashManager.moveToTrash(
                            "Product_Details",
                            productID,
                            "PRODUCT",
                            label,
                            previewUrl,
                            new com.example.homeadmin.ui.trash.TrashManager.OnTrashOperationListener() {
                                @Override
                                public void onSuccess() {
                                    loadingDialog.dismiss();
                                    Toast.makeText(ProductDetailsActivity.this, "Product moved to Recycle Bin", Toast.LENGTH_SHORT).show();
                                    finish();
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    loadingDialog.dismiss();
                                    Toast.makeText(ProductDetailsActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                    );
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        formSearch = false;
    }

    @Override
    protected void onStart() {
        super.onStart();

        loadingDialog.show();
        if (mAuth.getCurrentUser() != null) {
            if (myRatings.isEmpty()) {
                //loadRatingList(ProductDetailsActivity.this);

            }
            if (cartLis.isEmpty()) {
                //loadCartList(ProductDetailsActivity.this,loadingDialog,false,badgeCount);
            }

            if (wishLisT.isEmpty()) {
                // loadWishList(ProductDetailsActivity.this, loadingDialog, false);
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

        if (cartLis.contains(productID)) {
            ALREADY_ADDED_TO_CART = true;
        } else {
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

        loadingDialog.dismiss();
        invalidateOptionsMenu();

    }

    private void updateFlid(Dialog loadingDialog,String productID) {

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
                            String error = task.getException().getMessage();
                            Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_LONG).show();
                        }
                        loadingDialog.dismiss();
                    }
                });

    }

}