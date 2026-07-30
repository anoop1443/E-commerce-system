package com.example.homeadmin.ui.home;


import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.gridlayout.widget.GridLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.homeadmin.R;
import com.example.homeadmin.ui.allView.ViewAllActivity2;
import com.example.homeadmin.ui.details.ProductDetailsActivity;
import com.example.homeadmin.ui.horizontal.HorizontalProductScrollAdapter;
import com.example.homeadmin.ui.horizontal.HorizontalProductScrollModel;
import com.example.homeadmin.ui.slideshow.SliderAdapter;
import com.example.homeadmin.ui.slideshow.SliderModel;
import com.example.homeadmin.ui.wishList.WishlistModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class HomepageAdapter extends RecyclerView.Adapter {

    private final List<HomepageModel> homepageModellist;
    private static final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    private static RecyclerView.RecycledViewPool recycledViewPool;

    public HomepageAdapter(List<HomepageModel> homepageModels) {
        this.homepageModellist = homepageModels;
        recycledViewPool = new RecyclerView.RecycledViewPool();
    }

    @Override
    public int getItemViewType(int position) {
        switch (homepageModellist.get(position).getType()) {
            case 0:
                return HomepageModel.BANNER_SLIDER;
            case 1:
                return HomepageModel.STRIP_AD_BANNER;
            case 2:
                return HomepageModel.HORIZONTAL_PRODUCT;
            case 3:
                return HomepageModel.GRID_PRODUCT_VIEW;

            default:
                return -1;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case HomepageModel.BANNER_SLIDER:
                View bannersliderview = LayoutInflater.from(parent.getContext()).inflate(R.layout.banner_slider_view_layout, parent, false);
            return new BannerSliderViewHolder(bannersliderview);
            case HomepageModel.STRIP_AD_BANNER:
                View stripadview = LayoutInflater.from(parent.getContext()).inflate(R.layout.strip_ad_layout, parent, false);
                return new StripAdViewHolder(stripadview);

            case HomepageModel.HORIZONTAL_PRODUCT:
                View horizontalprotuctview = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_scroll_layout, parent, false);
                return new HorizontalProtuctViewhoder(horizontalprotuctview);

            case HomepageModel.GRID_PRODUCT_VIEW:
                View gripproductview = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_product_layout, parent, false);
                return new GripProductViewHolder(gripproductview);
            default:
                return null;


        }

    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        switch (homepageModellist.get(position).getType()) {
            case HomepageModel.BANNER_SLIDER:
                List<SliderModel> sliderModelList = homepageModellist.get(position).getSliderModelList();
                ((BannerSliderViewHolder) viewHolder).setBannerSliderViewpagerpage(sliderModelList);
                //animateView(viewHolder.itemView);
                return;
            case HomepageModel.STRIP_AD_BANNER:
                String resource = homepageModellist.get(position).getResouce();
                String backGroundColor = homepageModellist.get(position).getBackgoundcolor();
               // List<SliderModel> sliderModelList1 = homepageModellist.get(position).getSliderModelList();
                ((StripAdViewHolder) viewHolder).StripAd(resource,backGroundColor);
                return;
            case HomepageModel.HORIZONTAL_PRODUCT:
                String horizontalTitle = homepageModellist.get(position).getTitel();
                String background = homepageModellist.get(position).getBackgoundcolor();
                List<HorizontalProductScrollModel> horizontalproductscrollModelList = homepageModellist.get(position).getHorizontalproductscrollModelList();
                List<WishlistModel> viewAllProductList = homepageModellist.get(position).getViewAllProductList();

                ((HorizontalProtuctViewhoder) viewHolder).setHorizontalproductLayouta(horizontalproductscrollModelList, horizontalTitle, background, viewAllProductList);
                return;
            case HomepageModel.GRID_PRODUCT_VIEW:
                String girdTitle = homepageModellist.get(position).getTitel();
                String backColor = homepageModellist.get(position).getBackgoundcolor();
                List<HorizontalProductScrollModel> girdProductScrollModelList1 = homepageModellist.get(position).getHorizontalproductscrollModelList();
                ((GripProductViewHolder) viewHolder).setGridlayout(girdProductScrollModelList1, girdTitle, backColor);
                //scaleUpAndFadeIn(viewHolder.itemView);

                return;
            default:
                return;
        }

    }


    @Override
    public int getItemCount() {
        return homepageModellist.size();
    }



        public static class BannerSliderViewHolder extends RecyclerView.ViewHolder {
        ViewPager2 viewPager2;
        private Handler sliderHandler = new Handler();
        private Runnable sliderRunnable = new Runnable() {
            @Override
            public void run() {
                viewPager2.setCurrentItem(viewPager2.getCurrentItem() + 1);
            }
        };

        public BannerSliderViewHolder(@NonNull View itemView) {
            super(itemView);
            viewPager2 = itemView.findViewById(R.id.banner_view_pagerr);
        }

        private void setBannerSliderViewpagerpage(List<SliderModel> sliderModelList) {
            // Data load hone ka count track karne ke liye counter banayein.
            final int[] loadedCount = {0};

            // Loop ke har item par Firestore se data fetch karein
            for (SliderModel model : sliderModelList) {
                if (!model.getDocumentId().isEmpty()) {
                    firebaseFirestore.collection("banners")
                            .document(model.getDocumentId())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            model.setBanner(document.getString("imageUrl"));
                                            model.setBackGroundColor(document.getString("backgroundColor"));
                                            loadedCount[0]++;

                                            // Jab saare items load ho jaayen, tab adapter set karein
                                            if (loadedCount[0] == sliderModelList.size()) {
                                                setupViewPager(sliderModelList);
                                            }
                                        }
                                    } else {
                                        // Handle error
                                        Toast.makeText(itemView.getContext(), "Error loading banner data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    // Agar documentId empty hai, tab bhi count badhayein taaki loop aage badh sake
                    loadedCount[0]++;
                    if (loadedCount[0] == sliderModelList.size()) {
                        setupViewPager(sliderModelList);
                    }
                }
            }
        }

        // ViewPager setup ke liye ek alag method banayein
        private void setupViewPager(List<SliderModel> sliderModelList) {
            SliderAdapter sliderAdapter = new SliderAdapter(sliderModelList, viewPager2);
            viewPager2.setAdapter(sliderAdapter);

            // ViewPager ke UI settings
            viewPager2.setClipToPadding(false);
            viewPager2.setClipChildren(false);
            viewPager2.setOffscreenPageLimit(3);
            viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

            CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
            compositePageTransformer.addTransformer(new MarginPageTransformer(40));
            compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
                @Override
                public void transformPage(@NonNull View page, float position) {
                    float r = 1;
                    Math.abs(position);
                    page.setScaleY(0.85f + r * 0.15f);
                }
            });

            viewPager2.setPageTransformer(compositePageTransformer);

            viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    sliderHandler.removeCallbacks(sliderRunnable);
                    sliderHandler.postDelayed(sliderRunnable, 4000);
                }
            });
        }
    }

    public static class StripAdViewHolder extends RecyclerView.ViewHolder {
        ImageView stripAdimage;
        ConstraintLayout stripAdcontenar;


        public StripAdViewHolder(@NonNull View itemView) {
            super(itemView);
            stripAdimage = itemView.findViewById(R.id.strip_ad_image);
            stripAdcontenar = itemView.findViewById(R.id.strip_ad_constraint);

        }

        private void StripAd(String resource, String color) {
            // stripAdimage.setImageResource(resource);
            stripAdcontenar.setBackgroundColor(Color.parseColor(color));
            Glide.with(itemView.getContext()).load(resource).apply(new RequestOptions().placeholder(R.drawable.ic_home)).into(stripAdimage);


        }
    }

    public static class HorizontalProtuctViewhoder extends RecyclerView.ViewHolder {
        private TextView horizontalLayouttitel;
        private Button horizontalViewall;
        private RecyclerView horizontalRecyclerview;
        private ConstraintLayout constraintLayout;

        public HorizontalProtuctViewhoder(@NonNull View itemView) {
            super(itemView);
            horizontalLayouttitel = itemView.findViewById(R.id.horizontal_scroll_layout_titel);
            horizontalViewall = itemView.findViewById(R.id.horizontal_scroll_viewall_btn);
            horizontalRecyclerview = itemView.findViewById(R.id.horizontal_scroll_layout_recylerview);
            constraintLayout = itemView.findViewById(R.id.horizontal_scroll_layout_constraintlayout);

            horizontalRecyclerview.setRecycledViewPool(recycledViewPool);

        }



        private void setHorizontalproductLayouta(List<HorizontalProductScrollModel> horizontalproductscrollModelList, String Title, String background, List<WishlistModel> viewAllProductList) {
            horizontalLayouttitel.setText(Title);
            constraintLayout.setBackgroundColor(Color.parseColor(background));

            HorizontalProductScrollAdapter horizontalproductscrollAdapter = new HorizontalProductScrollAdapter(horizontalproductscrollModelList);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(itemView.getContext());
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            horizontalRecyclerview.setLayoutManager(linearLayoutManager);
            horizontalRecyclerview.setAdapter(horizontalproductscrollAdapter);

            // Data load hone ka count track karne ke liye counter banayein.
            final int[] loadedCount = {0};

            // Firebase se data load karne ke liye loop chalaein
            for (HorizontalProductScrollModel model : horizontalproductscrollModelList) {
                if (!model.getProductId().isEmpty() && model.getProductTitle().isEmpty()) {
                    firebaseFirestore.collection("Product_Details")
                            .document(model.getProductId())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            List<String> images = (List<String>) document.get("imageUrls");
                                            String firstImage = (images != null && !images.isEmpty()) ? images.get(0) : "";

                                            model.setProductImage(firstImage);
                                            model.setProductTitle(document.getString("productTitle"));
                                            model.setProductPrice(document.getLong("productPrise").toString()); // Long to String
                                            model.setProductDescription(document.getString("productDescription"));

                                            // Check if the WishlistModel list is not empty before accessing
                                            if (viewAllProductList != null && !viewAllProductList.isEmpty()) {
                                                WishlistModel wishlistModel = viewAllProductList.get(horizontalproductscrollModelList.indexOf(model));

                                                wishlistModel.setProductImage(firstImage);
                                                wishlistModel.setProductTitle(document.getString("productTitle"));
                                                wishlistModel.setFreeCoupon(document.getLong("freeCoupon"));
                                                wishlistModel.setStarRating(document.getDouble("starRating"));
                                                wishlistModel.setTotalRating(document.getLong("totalRatings"));
                                                wishlistModel.setPrise(document.getLong("productPrise"));
                                                wishlistModel.setCatPrise(document.getLong("productCatPrise"));
                                                wishlistModel.setPaymentMethod(document.getString("paymentMethod"));
                                            }

                                            loadedCount[0]++; // Har successful load par counter badhayein

                                            // Jab saara data load ho jaye tab adapter ko notify karein
                                            if (loadedCount[0] == horizontalproductscrollModelList.size()) {
                                                horizontalproductscrollAdapter.notifyDataSetChanged();
                                            }
                                        }
                                    } else {
                                        Toast.makeText(itemView.getContext(), "Error loading data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }

            // "View All" button ko visibility aur listener set karein
            if (horizontalproductscrollModelList.size() > 6) {
                horizontalViewall.setVisibility(View.VISIBLE);
                if (!Title.isEmpty()) {
                    horizontalViewall.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ViewAllActivity2.viewAllProductList = viewAllProductList;
                            Intent viewIntent = new Intent(itemView.getContext(), ViewAllActivity2.class);
                            viewIntent.putExtra("layout_code", 0);
                            viewIntent.putExtra("title", Title);
                            itemView.getContext().startActivity(viewIntent);
                        }
                    });
                }
            } else {
                horizontalViewall.setVisibility(View.INVISIBLE);
            }
        }
    }

    public static class GripProductViewHolder extends RecyclerView.ViewHolder {
        TextView gridlayoutTitel;
        Button gridlayoutviewall;
        //GridView gridView;
        GridLayout gridLayout;
        ConstraintLayout constraintLayout;


        public GripProductViewHolder(@NonNull View itemView) {
            super(itemView);
            gridlayoutTitel = itemView.findViewById(R.id.grid_product_layout_titel);
            gridlayoutviewall = itemView.findViewById(R.id.grid_product_layout_viewall_btn);
            // gridView = itemView.findViewById(R.id.grid_product_layout_gridview);
            gridLayout = itemView.findViewById(R.id.grid_product_Layout);
            constraintLayout = itemView.findViewById(R.id.grid_product_layout_constrintLayout);


        }

        private void setGridlayout(List<HorizontalProductScrollModel> horizontalproductscrollModelList, String Title, String color) {
            gridlayoutTitel.setText(Title);
            constraintLayout.setBackgroundColor(Color.parseColor(color));

            // Data load hone ka count track karne ke liye ek counter variable banayein.
            final int[] loadedCount = {0};

            for (HorizontalProductScrollModel model: horizontalproductscrollModelList) {

                if (!model.getProductId().isEmpty() && model.getProductTitle().isEmpty()) {
                    firebaseFirestore.collection("Product_Details")
                            .document(model.getProductId()).
                            get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        List<String> images = (List<String>) task.getResult().get("imageUrls");
                                        String firstImage = (images != null && !images.isEmpty()) ? images.get(0) : "";


                                        model.setProductImage(firstImage);
                                        model.setProductTitle(task.getResult().get("productTitle").toString());
                                        model.setProductDescription(task.getResult().get("productDescription").toString());
                                        model.setProductPrice(task.getResult().get("productPrise").toString());


                                        loadedCount[0]++; // Har successful load par counter badhayein

                                        // Jab saara data load ho jaye tab setGirdData call karein.
                                        if (loadedCount[0] == horizontalproductscrollModelList.size()) {
                                            setGirdData(horizontalproductscrollModelList, Title);
                                        }
                                    } else {
                                        // do not code
                                    }
                                }
                            });

                }
               // setGirdData(horizontalproductscrollModelList,Title);

            }
            if (!Title.equals("")) {

                gridlayoutviewall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ViewAllActivity2.horizontalproductscrollModelList = horizontalproductscrollModelList;
                        Intent viewIntent = new Intent(itemView.getContext(), ViewAllActivity2.class);
                        viewIntent.putExtra("title", Title);
                        viewIntent.putExtra("layout_code", 1);
                        itemView.getContext().startActivity(viewIntent);
                    }
                });
            }




        }
        private  void setGirdData( List<HorizontalProductScrollModel> horizontalProductScrollModelList,String Title){


                for (int x = 0; x < 4; x++) {
                    View productView = gridLayout.getChildAt(x);
                    ImageView productImage = productView.findViewById(R.id.h_s_product_image);
                    TextView productTitle = productView.findViewById(R.id.h_s_product_titel);
                    TextView productDescription = productView.findViewById(R.id.h_s_product_description);
                    TextView productPrice = productView.findViewById(R.id.h_s_product_price);

                    //productImage.setImageResource(horizontalproductscrollModelList.get(x).getProductImage());
                    Glide.with(itemView.getContext()).load(horizontalProductScrollModelList.get(x).getProductImage()).apply(new RequestOptions().placeholder(R.drawable.ic_home)).into(productImage);
                    productTitle.setText(horizontalProductScrollModelList.get(x).getProductTitle());
                    productDescription.setText(horizontalProductScrollModelList.get(x).getProductDescription());
                    productPrice.setText(" ₹ " + horizontalProductScrollModelList.get(x).getProductPrice() + "/-");
                    gridLayout.getChildAt(x).setBackgroundColor(x);

                    if (!Title.isEmpty()) {
                        String goId = horizontalProductScrollModelList.get(x).getProductId();

                        gridLayout.getChildAt(x).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent productDetails = new Intent(itemView.getContext(), ProductDetailsActivity.class);
                                productDetails.putExtra("PRODUCT_ID", goId);
                                // productDetails.putExtra("PRODUCT_ID", horizontalproductscrollModelList.get(finalX).getProductId());
                                itemView.getContext().startActivity(productDetails);
                            }
                        });

                    }
                }


        }


    }
    // HomepageAdapter.java ke andar kahi bhi likhein
    private void animateView(View view) {
        // Create an animation object
        AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(2000); // 1 second
        fadeIn.setFillAfter(true); // Keep the animation state after it ends

        // Apply the animation to the view
        view.startAnimation(fadeIn);
    }

    // HomepageAdapter.java ke andar kahi bhi likhein
    private void scaleUpAndFadeIn(View view) {
        // Initial state: scaled down and invisible
        view.setScaleX(0.8f);
        view.setScaleY(0.8f);
        view.setAlpha(0.0f);

        // Animate to final state
        view.animate()
                .scaleX(1.0f)
                .scaleY(1.0f)
                .alpha(1.0f)
                .setDuration(2000) // 2 seconds
                .setStartDelay(100); // Start with a small delay
    }


}

