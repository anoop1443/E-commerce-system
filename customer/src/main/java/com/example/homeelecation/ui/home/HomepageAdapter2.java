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
import com.example.homeelecation.R;
import com.example.homeelecation.ui.allView.ViewAllActivity2;
import com.example.homeelecation.ui.details.ProductDetailsActivity;
import com.example.homeelecation.ui.horizontal.HorizontalProductScrollAdapter;
import com.example.homeelecation.ui.horizontal.HorizontalProductScrollModel;
import com.example.homeelecation.ui.slideshow.SliderAdapter;
import com.example.homeelecation.ui.slideshow.SliderModel;
import com.example.homeelecation.ui.wishList.WishlistModel;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class HomepageAdapter2 extends RecyclerView.Adapter {


    private final List<HomepageModel> homepageModellist;
    private static final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    private static RecyclerView.RecycledViewPool recycledViewPool;


    public HomepageAdapter2(List<HomepageModel> homepageModels) {
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
                return new HorizontalProductViewHolder(horizontalprotuctview);

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
                String documentId = homepageModellist.get(position).getStripDocumentId();
                ((StripAdViewHolder) viewHolder).StripAd(documentId);
                return;
            case HomepageModel.HORIZONTAL_PRODUCT:
                String horizontalTitle = homepageModellist.get(position).getTitle();
                String background = homepageModellist.get(position).getBackGroundColor();
                List<HorizontalProductScrollModel> horizontalproductscrollModelList = homepageModellist.get(position).getHorizontalproductscrollModelList();
                List<WishlistModel> viewAllProductList = homepageModellist.get(position).getViewAllProductList();

                ((HorizontalProductViewHolder) viewHolder).setHorizontalproductLayouta(horizontalproductscrollModelList, horizontalTitle, background, viewAllProductList);
                return;
            case HomepageModel.GRID_PRODUCT_VIEW:
                String girdTitle = homepageModellist.get(position).getTitle();
                String backColor = homepageModellist.get(position).getBackGroundColor();
                List<HorizontalProductScrollModel> girdProductScrollModelList1 = homepageModellist.get(position).getHorizontalproductscrollModelList();
                ((GripProductViewHolder) viewHolder).setGridlayout(girdProductScrollModelList1, girdTitle, backColor);

                return;
            default:
                return;
        }


    }

    @Override
    public int getItemCount() {
        return homepageModellist.size();
    }

    public void updateList(List<HomepageModel> homepageModelList) {

        homepageModellist.clear();
        homepageModellist.addAll(homepageModelList);
        notifyDataSetChanged();
    }

    public static class BannerSliderViewHolder extends RecyclerView.ViewHolder {
        ViewPager2 viewPager2;
        private final Handler sliderHandler = new Handler();
        private final Runnable sliderRunnable = new Runnable() {
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
            final int listSize = sliderModelList.size();
            if (listSize == 0) {
                viewPager2.setVisibility(View.GONE);
                return;
            }
            viewPager2.setVisibility(View.VISIBLE);
            
            final int[] loadedCount = {0};

            for (SliderModel model : sliderModelList) {
                // If banner URL is already there (e.g. manually set), skip DB call
                if (model.getBanner() != null && !model.getBanner().isEmpty() && !model.getBanner().equals("null")) {
                    loadedCount[0]++;
                    if (loadedCount[0] == listSize) {
                        setupViewPager(sliderModelList);
                    }
                    continue;
                }

                if (model.getDocumentID() != null && !model.getDocumentID().isEmpty() && !model.getDocumentID().equals("null")) {
                    firebaseFirestore.collection("banners")
                            .document(model.getDocumentID())
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document != null && document.exists()) {
                                        String imageUrl = document.getString("imageUrl");
                                        String bgColor = document.getString("backgroundColor");
                                        
                                        model.setBanner(imageUrl);
                                        model.setBackGroundColor(bgColor != null ? bgColor : "#FFFFFF");
                                    }
                                }
                                loadedCount[0]++;
                                if (loadedCount[0] == listSize) {
                                    setupViewPager(sliderModelList);
                                }
                            });
                } else {
                    loadedCount[0]++;
                    if (loadedCount[0] == listSize) {
                        setupViewPager(sliderModelList);
                    }
                }
            }
        }

        private void setupViewPager(List<SliderModel> sliderModelList) {
            SliderAdapter sliderAdapter = new SliderAdapter(sliderModelList, viewPager2);
            viewPager2.setAdapter(sliderAdapter);
            viewPager2.setClipToPadding(false);
            viewPager2.setClipChildren(false);
            viewPager2.setOffscreenPageLimit(3);
            viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

            CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
            compositePageTransformer.addTransformer(new MarginPageTransformer(40));
            compositePageTransformer.addTransformer((page, position) -> {
                float r = 1 - Math.abs(position);
                page.setScaleY(0.85f + r * 0.15f);
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
        ImageView stripAdImage;
        ConstraintLayout stripAdContent;

        public StripAdViewHolder(@NonNull View itemView) {
            super(itemView);
            stripAdImage = itemView.findViewById(R.id.strip_ad_image);
            stripAdContent = itemView.findViewById(R.id.strip_ad_constraint);
        }

        private void StripAd(String documentId) {
            itemView.setVisibility(View.VISIBLE);
            if (documentId != null && !documentId.isEmpty()) {
                firebaseFirestore.collection("ads")
                        .document(documentId)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document != null && document.exists()) {
                                    String imageUrl = document.getString("imageUrl");
                                    String backgroundColor = document.getString("backgroundColor");

                                    if (backgroundColor != null && !backgroundColor.isEmpty()) {
                                        try {
                                            stripAdContent.setBackgroundColor(Color.parseColor(backgroundColor));
                                        } catch (IllegalArgumentException e) {
                                            stripAdContent.setBackgroundColor(Color.parseColor("#FFFFFF"));
                                        }
                                    } else {
                                        stripAdContent.setBackgroundColor(Color.parseColor("#FFFFFF"));
                                    }

                                    Glide.with(itemView.getContext())
                                            .load(imageUrl)
                                            .apply(new RequestOptions().placeholder(R.drawable.ic_home))
                                            .into(stripAdImage);
                                } else {
                                    stripAdContent.setBackgroundColor(Color.parseColor("#FFFFFF"));
                                    Glide.with(itemView.getContext()).load(R.drawable.ic_home).into(stripAdImage);
                                }
                            } else {
                                Toast.makeText(itemView.getContext(), "Error loading ad data.", Toast.LENGTH_SHORT).show();
                                stripAdContent.setBackgroundColor(Color.parseColor("#FFFFFF"));
                                Glide.with(itemView.getContext()).load(R.drawable.ic_home).into(stripAdImage);
                            }
                        });
            } else {
                stripAdContent.setBackgroundColor(Color.parseColor("#FFFFFF"));
                Glide.with(itemView.getContext()).load(R.drawable.ic_home).into(stripAdImage);
            }
        }
    }


    public static class HorizontalProductViewHolder extends RecyclerView.ViewHolder {
        private final TextView horizontalLayoutTitle;
        private final Button horizontalViewAll;
        private final RecyclerView horizontalRecyclerview;
        private final ConstraintLayout constraintLayout;

        public HorizontalProductViewHolder(@NonNull View itemView) {
            super(itemView);
            horizontalLayoutTitle = itemView.findViewById(R.id.horizontal_scroll_layout_titel);
            horizontalViewAll = itemView.findViewById(R.id.horizontal_scroll_viewall_btn);
            horizontalRecyclerview = itemView.findViewById(R.id.horizontal_scroll_layout_recylerview);
            constraintLayout = itemView.findViewById(R.id.horizontal_scroll_layout_constraintlayout);
            horizontalRecyclerview.setRecycledViewPool(recycledViewPool);
        }

        private void setHorizontalproductLayouta(List<HorizontalProductScrollModel> horizontalproductscrollModelList, String Title, String background, List<WishlistModel> viewAllProductList) {
            horizontalLayoutTitle.setText(Title);
            if (background != null && !background.isEmpty()) {
                constraintLayout.setBackgroundColor(Color.parseColor(background));
            }

            HorizontalProductScrollAdapter horizontalproductscrollAdapter = new HorizontalProductScrollAdapter(horizontalproductscrollModelList);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(itemView.getContext());
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            horizontalRecyclerview.setLayoutManager(linearLayoutManager);
            horizontalRecyclerview.setAdapter(horizontalproductscrollAdapter);

            final int listSize = horizontalproductscrollModelList.size();
            if (listSize == 0) return;
            final int[] loadedCount = {0};

            for (HorizontalProductScrollModel model : horizontalproductscrollModelList) {
                if (!model.getProductId().isEmpty() && model.getProductTitle().isEmpty()) {
                    firebaseFirestore.collection("Product_Details")
                            .document(model.getProductId())
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        List<String> images = (List<String>) document.get("imageUrls");
                                        String firstImage = (images != null && !images.isEmpty()) ? images.get(0) : "";

                                        model.setProductImage(firstImage);
                                        model.setProductTitle(document.getString("productTitle"));
                                        model.setProductPrice(String.valueOf(document.getLong("productPrise")));
                                        model.setProductDescription(document.getString("productDescription"));

                                        int modelIndex = horizontalproductscrollModelList.indexOf(model);
                                        if (viewAllProductList != null && !viewAllProductList.isEmpty() && modelIndex < viewAllProductList.size()) {
                                            WishlistModel wishlistModel = viewAllProductList.get(modelIndex);
                                            wishlistModel.setProductImage(firstImage);
                                            wishlistModel.setProductTitle(document.getString("productTitle"));
                                            wishlistModel.setFreeCoupon(Double.valueOf(document.getLong("freeCoupon")));
                                            wishlistModel.setStarRating(document.getDouble("starRating"));
                                            wishlistModel.setTotalRating(document.getLong("totalRatings"));
                                            wishlistModel.setPrise(document.getLong("productPrise"));
                                            wishlistModel.setCatPrise(document.getLong("productCatPrise"));
                                            wishlistModel.setPaymentMethod(document.getString("paymentMethod"));
                                        }
                                    }
                                } else {
                                    Toast.makeText(itemView.getContext(), "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                                loadedCount[0]++;
                                if (loadedCount[0] == listSize) {
                                    horizontalproductscrollAdapter.notifyDataSetChanged();
                                }
                            });
                } else {
                    loadedCount[0]++;
                    if (loadedCount[0] == listSize) {
                        horizontalproductscrollAdapter.notifyDataSetChanged();
                    }
                }
            }

            if (horizontalproductscrollModelList.size() > 6) {
                horizontalViewAll.setVisibility(View.VISIBLE);
                if (!Title.isEmpty()) {
                    horizontalViewAll.setOnClickListener(v -> {
                        ViewAllActivity2.viewAllProductList = viewAllProductList;
                        Intent viewIntent = new Intent(itemView.getContext(), ViewAllActivity2.class);
                        viewIntent.putExtra("layout_code", 0);
                        viewIntent.putExtra("title", Title);
                        itemView.getContext().startActivity(viewIntent);
                    });
                }
            } else {
                horizontalViewAll.setVisibility(View.INVISIBLE);
            }
        }
    }

    public static class GripProductViewHolder extends RecyclerView.ViewHolder {
        TextView gridlayoutTitel;
        Button gridlayoutviewall;
        GridLayout gridLayout;
        ConstraintLayout constraintLayout;

        public GripProductViewHolder(@NonNull View itemView) {
            super(itemView);
            gridlayoutTitel = itemView.findViewById(R.id.grid_product_layout_titel);
            gridlayoutviewall = itemView.findViewById(R.id.grid_product_layout_viewall_btn);
            gridLayout = itemView.findViewById(R.id.grid_product_Layout);
            constraintLayout = itemView.findViewById(R.id.grid_product_layout_constrintLayout);
        }

        private void setGridlayout(List<HorizontalProductScrollModel> horizontalproductscrollModelList, String Title, String color) {
            gridlayoutTitel.setText(Title);
            if (color != null && !color.isEmpty()) {
                constraintLayout.setBackgroundColor(Color.parseColor(color));
            }
            final int listSize = horizontalproductscrollModelList.size();
            if (listSize == 0) return;

            final int[] loadedCount = {0};

            for (HorizontalProductScrollModel model : horizontalproductscrollModelList) {
                if (!model.getProductId().isEmpty() && model.getProductTitle().isEmpty()) {
                    firebaseFirestore.collection("Product_Details")
                            .document(model.getProductId()).
                            get().addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document != null && document.exists()) {
                                        List<String> images = (List<String>) document.get("imageUrls");
                                        String firstImage = (images != null && !images.isEmpty()) ? images.get(0) : "";

                                        model.setProductImage(firstImage);
                                        model.setProductTitle(document.getString("productTitle"));
                                        model.setProductDescription(document.getString("productDescription"));
                                        if (document.get("productPrise") != null) {
                                            model.setProductPrice(document.get("productPrise").toString());
                                        }
                                    }
                                }
                                loadedCount[0]++;
                                if (loadedCount[0] == listSize) {
                                    setGirdData(horizontalproductscrollModelList, Title);
                                }
                            });
                } else {
                    loadedCount[0]++;
                    if (loadedCount[0] == listSize) {
                        setGirdData(horizontalproductscrollModelList, Title);
                    }
                }
            }

            if (!Title.isEmpty()) {
                gridlayoutviewall.setOnClickListener(v -> {
                    ViewAllActivity2.horizontalproductscrollModelList = horizontalproductscrollModelList;
                    Intent viewIntent = new Intent(itemView.getContext(), ViewAllActivity2.class);
                    viewIntent.putExtra("title", Title);
                    viewIntent.putExtra("layout_code", 1);
                    itemView.getContext().startActivity(viewIntent);
                });
            }
        }

        private void setGirdData(List<HorizontalProductScrollModel> horizontalProductScrollModelList, String Title) {
            for (int x = 0; x < Math.min(horizontalProductScrollModelList.size(), 4); x++) {
                View productView = gridLayout.getChildAt(x);
                if (productView == null) continue;

                ImageView productImage = productView.findViewById(R.id.h_s_product_image);
                TextView productTitle = productView.findViewById(R.id.h_s_product_titel);
                TextView productDescription = productView.findViewById(R.id.h_s_product_description);
                TextView productPrice = productView.findViewById(R.id.h_s_product_price);

                Glide.with(itemView.getContext()).load(horizontalProductScrollModelList.get(x).getProductImage()).apply(new RequestOptions().placeholder(R.drawable.ic_home)).into(productImage);
                productTitle.setText(horizontalProductScrollModelList.get(x).getProductTitle());
                productDescription.setText(horizontalProductScrollModelList.get(x).getProductDescription());
                if (horizontalProductScrollModelList.get(x).getProductPrice() != null) {
                    productPrice.setText(" ₹ " + horizontalProductScrollModelList.get(x).getProductPrice() + "/-");
                }

                if (!Title.isEmpty()) {
                    final int finalX = x;
                    productView.setOnClickListener(v -> {
                        Intent productDetails = new Intent(itemView.getContext(), ProductDetailsActivity.class);
                        productDetails.putExtra("PRODUCT_ID", horizontalProductScrollModelList.get(finalX).getProductId());
                        itemView.getContext().startActivity(productDetails);
                    });
                }
            }
        }
    }


}
