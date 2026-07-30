package com.example.homeelecation.ui.home;


import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.example.homeelecation.R;
import com.example.homeelecation.ui.allView.ViewAllActivity2;
import com.example.homeelecation.ui.details.ProductDetailsActivity;
import com.example.homeelecation.ui.horizontal.HorizontalProductScrollModel;
import com.example.homeelecation.ui.horizontal.HorizontalProductScrollAdapter;
import com.example.homeelecation.ui.slideshow.SliderAdapter;
import com.example.homeelecation.ui.slideshow.SliderModel;
import com.example.homeelecation.ui.wishList.WishlistModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class HomepageAdapter extends RecyclerView.Adapter {

    private List<HomepageModel> homepageModellist;
    private static FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

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
                return;
            case HomepageModel.STRIP_AD_BANNER:
                String resource = homepageModellist.get(position).getStripImage();
                String backGoundColor = homepageModellist.get(position).getBackGroundColor();
                ((StripAdViewHolder) viewHolder).StripAd(resource, backGoundColor);
                return;
            case HomepageModel.HORIZONTAL_PRODUCT:
                String horizontalTitel = homepageModellist.get(position).getTitle();
                String background = homepageModellist.get(position).getBackGroundColor();
                List<HorizontalProductScrollModel> horizontalproductscrollModelList = homepageModellist.get(position).getHorizontalproductscrollModelList();
                List<WishlistModel> viewAllProductList = homepageModellist.get(position).getViewAllProductList();

                ((HorizontalProtuctViewhoder) viewHolder).setHorizontalproductLayouta(horizontalproductscrollModelList, horizontalTitel, background, viewAllProductList);
                return;
            case HomepageModel.GRID_PRODUCT_VIEW:
                String griptitel = homepageModellist.get(position).getTitle();
                String backColor = homepageModellist.get(position).getBackGroundColor();
                List<HorizontalProductScrollModel> gripproductscrollModelList1 = homepageModellist.get(position).getHorizontalproductscrollModelList();
                ((GripProductViewHolder) viewHolder).setGridlayout(gripproductscrollModelList1, griptitel, backColor);
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


        public BannerSliderViewHolder(@NonNull View itemView) {
            super(itemView);
            viewPager2 = itemView.findViewById(R.id.banner_view_pagerr);
        }

        private void setBannerSliderViewpagerpage(List<SliderModel> sliderModelList) {
            SliderAdapter sliderAdapter = new SliderAdapter(sliderModelList, viewPager2);
            viewPager2.setAdapter(sliderAdapter);


            viewPager2.setClipToPadding(false);
            viewPager2.setClipChildren(false);
            viewPager2.setOffscreenPageLimit(3);
            viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

            CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
            compositePageTransformer.addTransformer(new MarginPageTransformer(40));
            compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
                @Override
                public void transformPage(@org.checkerframework.checker.nullness.qual.NonNull View page, float position) {
                    float r = 1 - Math.abs(position);
                    page.setScaleY(0.85f + r * 0.15f);
                }
            });

            viewPager2.setPageTransformer(compositePageTransformer);

            viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    sliderHandler.removeCallbacks(sliderRunnable);
                    sliderHandler.postDelayed(sliderRunnable, 4000); // slide duration 4 seconds
                }
            });


        }

        private Runnable sliderRunnable = new Runnable() {
            @Override
            public void run() {
                viewPager2.setCurrentItem(viewPager2.getCurrentItem() + 1);
            }
        };

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
            Glide.with(itemView.getContext()).load(resource).apply(new RequestOptions().placeholder(R.drawable.empty_img)).into(stripAdimage);

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

            for (HorizontalProductScrollModel model: horizontalproductscrollModelList){

                if (!model.getProductId().isEmpty() && model.getProductTitle().isEmpty()){
                    firebaseFirestore.collection("Product_Details")
                            .document(model.getProductId()).
                            get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()){
                                        List<String> images = (List<String>) task.getResult().get("imageUrls");
                                        String firstImage = (images != null && !images.isEmpty()) ? images.get(0) : "";

                                        model.setProductImage(firstImage);
                                        model.setProductTitle(task.getResult().get("productTitle").toString());
                                        model.setProductPrice(task.getResult().getLong("productPrise").toString());
                                        model.setProductDescription(task.getResult().get("productDescription").toString());

                                        WishlistModel wishlistModel = viewAllProductList
                                                .get(horizontalproductscrollModelList.indexOf(model));

                                        Object freeCoupon = task.getResult().get("freeCoupon");
                                        double coupon ;
                                        Object totalRatings = task.getResult().get("totalRatings");
                                        long totalRating ;

                                        wishlistModel.setProductImage(firstImage);
                                        wishlistModel.setProductTitle(task.getResult().get("productTitle").toString());
                                        if (freeCoupon instanceof Number) {
                                            coupon = ((Number) freeCoupon).doubleValue();
                                            wishlistModel.setFreeCoupon(coupon);
                                        }

                                        wishlistModel.setStarRating((double)task.getResult().get("starRating"));

                                        if (totalRatings instanceof Number) {
                                            totalRating = ((Number) totalRatings).longValue();
                                            wishlistModel.setTotalRating(totalRating);
                                        }

                                        Object price = task.getResult().get("productPrise");
                                        if (price instanceof Number) {
                                            wishlistModel.setPrise(((Number) price).longValue());
                                        }
                                        Object catPrice = task.getResult().get("productCatPrise");

                                        if (catPrice instanceof Number) {
                                            wishlistModel.setCatPrise(((Number) catPrice).longValue());
                                        }
                                        wishlistModel.setPaymentMethod(task.getResult().get("paymentMethod").toString());

                                        if (horizontalproductscrollModelList.indexOf(model) == horizontalproductscrollModelList.size()-1){
                                            if (horizontalRecyclerview.getAdapter()!=null){
                                                horizontalRecyclerview.getAdapter().notifyDataSetChanged();
                                            }
                                        }
                                    }else {
                                        // do not code
                                    }
                                }
                            });

                }
            }
            if (horizontalproductscrollModelList.size() > 7 || horizontalproductscrollModelList.isEmpty()) {
                horizontalViewall.setVisibility(View.VISIBLE);
                if (!Title.equals("")) {
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

            HorizontalProductScrollAdapter horizontalproductscrollAdapter = new HorizontalProductScrollAdapter(horizontalproductscrollModelList);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(itemView.getContext());
            linearLayoutManager.setOrientation(linearLayoutManager.HORIZONTAL);
            horizontalRecyclerview.setLayoutManager(linearLayoutManager);

            horizontalRecyclerview.setAdapter(horizontalproductscrollAdapter);
            horizontalproductscrollAdapter.notifyDataSetChanged();
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


                                        if (horizontalproductscrollModelList.indexOf(model) == horizontalproductscrollModelList.size() - 1) {

                                            setGirdData(Title,horizontalproductscrollModelList);

                                            if (!Title.isEmpty()) {

                                                gridlayoutviewall.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        ViewAllActivity2.horizontalproductscrollModelList = horizontalproductscrollModelList;
                                                        Intent viewIntent = new Intent(itemView.getContext(), ViewAllActivity2.class);
                                                        viewIntent.putExtra("layout_code", 1);
                                                        viewIntent.putExtra("title", Title);
                                                        itemView.getContext().startActivity(viewIntent);
                                                    }
                                                });
                                            }
                                        }
                                    } else {
                                        // do not code
                                    }
                                }
                            });

                }
                setGirdData(Title,horizontalproductscrollModelList);
            }




        }
        private  void setGirdData(String Title, List<HorizontalProductScrollModel> horizontalProductScrollModelList){

            for (int x = 0; x < 4; x++) {
                ImageView productImage = gridLayout.getChildAt(x).findViewById(R.id.h_s_product_image);
                TextView productTitle = gridLayout.getChildAt(x).findViewById(R.id.h_s_product_titel);
                TextView productDescription = gridLayout.getChildAt(x).findViewById(R.id.h_s_product_description);
                TextView productPrice = gridLayout.getChildAt(x).findViewById(R.id.h_s_product_price);

                //productImage.setImageResource(horizontalproductscrollModelList.get(x).getProductImage());
                Glide.with(itemView.getContext()).load(horizontalProductScrollModelList.get(x).getProductImage()).apply(new RequestOptions().placeholder(R.drawable.empt_img1080)).into(productImage);
                productTitle.setText(horizontalProductScrollModelList.get(x).getProductTitle());
                productDescription.setText(horizontalProductScrollModelList.get(x).getProductDescription());
                productPrice.setText(" ₹ " + horizontalProductScrollModelList.get(x).getProductPrice() + "/-");
                gridLayout.getChildAt(x).setBackgroundColor(x);

                if (!Title.equals("")) {
                    String id = "";
                    String goId = horizontalProductScrollModelList.get(x).getProductId();

                    int finalX = x;
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

}

