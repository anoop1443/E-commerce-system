package com.example.homeadmin.ui.home2;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
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
import com.bumptech.glide.load.HttpException;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
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
import java.util.Objects;

public class Home3Adapter extends RecyclerView.Adapter {

    private final List<Home3Model> home3Modellist;
    private static final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private static RecyclerView.RecycledViewPool recycledViewPool;

    public Home3Adapter(List<Home3Model> home3Models) {
        this.home3Modellist = home3Models;
        recycledViewPool = new RecyclerView.RecycledViewPool();
    }

    @Override
    public int getItemViewType(int position) {
        switch (home3Modellist.get(position).getType()) {
            case 0: return Home3Model.BANNER_SLIDER;
            case 1: return Home3Model.STRIP_AD_BANNER;
            case 2: return Home3Model.HORIZONTAL_PRODUCT;
            case 3: return Home3Model.GRID_PRODUCT_VIEW;
            default: return -1;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case Home3Model.BANNER_SLIDER:
                View bannersliderview = LayoutInflater.from(parent.getContext()).inflate(R.layout.banner_slider_view_layout, parent, false);
                return new BannerSliderViewHolder(bannersliderview);
            case Home3Model.STRIP_AD_BANNER:
                View stripadview = LayoutInflater.from(parent.getContext()).inflate(R.layout.strip_ad_layout, parent, false);
                return new StripAdViewHolder(stripadview);
            case Home3Model.HORIZONTAL_PRODUCT:
                View horizontalprotuctview = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_scroll_layout, parent, false);
                return new HorizontalProtuctViewhoder(horizontalprotuctview);
            case Home3Model.GRID_PRODUCT_VIEW:
                View gripproductview = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_product_layout, parent, false);
                return new GripProductViewHolder(gripproductview);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        Home3Model model = home3Modellist.get(position);
        switch (model.getType()) {
            case Home3Model.BANNER_SLIDER:
                ((BannerSliderViewHolder) viewHolder).setBannerSliderViewpagerpage(model.getSliderModelList());
                return;
            case Home3Model.STRIP_AD_BANNER:
                ((StripAdViewHolder) viewHolder).StripAd(model.getStripDocumentId());
                return;
            case Home3Model.HORIZONTAL_PRODUCT:
                ((HorizontalProtuctViewhoder) viewHolder).setHorizontalproductLayouta(model.getHorizontalproductscrollModelList(), model.getTitel(), model.getBackgoundcolor(), model.getViewAllProductList());
                return;
            case Home3Model.GRID_PRODUCT_VIEW:
                ((GripProductViewHolder) viewHolder).setGridlayout(model.getHorizontalproductscrollModelList(), model.getTitel(), model.getBackgoundcolor());
                return;
        }
    }

    @Override
    public int getItemCount() {
        return home3Modellist.size();
    }

    public void updateList(List<Home3Model> homepageModelList) {
        home3Modellist.clear();
        home3Modellist.addAll(homepageModelList);
        notifyDataSetChanged();
    }

    public static class BannerSliderViewHolder extends RecyclerView.ViewHolder {
        ViewPager2 viewPager2;
        private Handler sliderHandler = new Handler();
        private Runnable sliderRunnable = () -> viewPager2.setCurrentItem(viewPager2.getCurrentItem() + 1);

        public BannerSliderViewHolder(@NonNull View itemView) {
            super(itemView);
            viewPager2 = itemView.findViewById(R.id.banner_view_pagerr);
        }

        private void setBannerSliderViewpagerpage(List<SliderModel> sliderModelList) {
            if (sliderModelList == null || sliderModelList.isEmpty()) return;
            final int listSize = sliderModelList.size();
            final int[] loadedCount = {0};

            for (SliderModel model : sliderModelList) {
                if (model.getDocumentID() != null && !model.getDocumentID().isEmpty()) {
                    firebaseFirestore.collection("banners").document(model.getDocumentID()).get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful() && task.getResult().exists()) {
                                    DocumentSnapshot doc = task.getResult();
                                    model.setBanner(doc.getString("imageUrl"));
                                    model.setBackgroundColor(Objects.toString(doc.get("backgroundColor"), "#FFFFFF"));
                                }
                                loadedCount[0]++;
                                if (loadedCount[0] == listSize) setupViewPager(sliderModelList);
                            });
                } else {
                    loadedCount[0]++;
                    if (loadedCount[0] == listSize) setupViewPager(sliderModelList);
                }
            }
        }

        private void setupViewPager(List<SliderModel> sliderModelList) {
            viewPager2.setAdapter(new SliderAdapter(sliderModelList, viewPager2));
            viewPager2.setClipToPadding(false);
            viewPager2.setClipChildren(false);
            viewPager2.setOffscreenPageLimit(3);
            viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

            CompositePageTransformer transformer = new CompositePageTransformer();
            transformer.addTransformer(new MarginPageTransformer(40));
            transformer.addTransformer((page, position) -> {
                float r = 1 - Math.abs(position);
                page.setScaleY(0.85f + r * 0.15f);
            });
            viewPager2.setPageTransformer(transformer);
            viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
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

        private void StripAd(String documentId) {
            if (documentId == null || documentId.isEmpty()) {
                itemView.setVisibility(View.GONE);
                return;
            }
            firebaseFirestore.collection("ads").document(documentId).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult().exists()) {
                            DocumentSnapshot doc = task.getResult();
                            String imageUrl = doc.getString("imageUrl");
                            String bgColor = doc.getString("backgroundColor");
                            
                            if (bgColor != null && !bgColor.isEmpty()) {
                                try {
                                    stripAdcontenar.setBackgroundColor(Color.parseColor(bgColor));
                                } catch (Exception e) {
                                    Log.e("ColorError", "Invalid color: " + bgColor);
                                }
                            }
                            
                            if (imageUrl == null || imageUrl.isEmpty()) {
                                Log.e("GlideError", "Image URL is null for ad: " + documentId);
                                itemView.setVisibility(View.GONE);
                                return;
                            }
                            
                            Log.d("GlideDebug", "Loading Ad Image: " + imageUrl);
                            Glide.with(itemView.getContext())
                                    .load(imageUrl)
                                    .apply(new RequestOptions().placeholder(R.drawable.ic_home))
                                    .listener(new RequestListener<android.graphics.drawable.Drawable>() {
                                        @Override
                                        public boolean onLoadFailed(@androidx.annotation.Nullable GlideException e, Object model, Target<android.graphics.drawable.Drawable> target, boolean isFirstResource) {
                                            String errorMsg = "Load Failed";
                                            if (e != null) {
                                                List<Throwable> causes = e.getRootCauses();
                                                for (Throwable t : causes) {
                                                    if (t instanceof HttpException) {
                                                        errorMsg = "HTTP " + ((HttpException) t).getStatusCode();
                                                        break;
                                                    }
                                                    errorMsg = t.getMessage();
                                                }
                                            }
                                            Log.e("GlideError", "Ad Image Load Failed: " + errorMsg, e);
                                            Toast.makeText(itemView.getContext(), "Error: " + errorMsg, Toast.LENGTH_LONG).show();
                                            return false;
                                        }
                                        @Override
                                        public boolean onResourceReady(android.graphics.drawable.Drawable resource, Object model, Target<android.graphics.drawable.Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                            return false;
                                        }
                                    })
                                    .into(stripAdimage);
                        } else {
                            itemView.setVisibility(View.GONE);
                        }
                    });
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
            if (background != null && !background.isEmpty()) constraintLayout.setBackgroundColor(Color.parseColor(background));

            HorizontalProductScrollAdapter adapter = new HorizontalProductScrollAdapter(horizontalproductscrollModelList);
            horizontalRecyclerview.setLayoutManager(new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
            horizontalRecyclerview.setAdapter(adapter);

            if (horizontalproductscrollModelList == null) return;
            final int listSize = horizontalproductscrollModelList.size();
            final int[] loadedCount = {0};

            for (HorizontalProductScrollModel model : horizontalproductscrollModelList) {
                if (model.getProductID() != null && !model.getProductID().isEmpty() && (model.getProductTitle() == null || model.getProductTitle().isEmpty())) {
                    firebaseFirestore.collection("Product_Details").document(model.getProductID()).get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful() && task.getResult().exists()) {
                                    DocumentSnapshot doc = task.getResult();
                                    
                                    // Robust Image Fetching with multiple fallbacks
                                    String firstImage = "";
                                    Object imagesObj = doc.get("imageUrls");
                                    if (imagesObj instanceof List) {
                                        List<?> imagesList = (List<?>) imagesObj;
                                        if (!imagesList.isEmpty()) firstImage = String.valueOf(imagesList.get(0));
                                    }
                                    
                                    if (firstImage.isEmpty()) {
                                        firstImage = doc.getString("product_image_1");
                                        if (firstImage == null) firstImage = doc.getString("image");
                                    }

                                    model.setProductImage(firstImage);
                                    model.setProductTitle(doc.getString("productTitle"));
                                    model.setProductPrice(String.valueOf(doc.get("productPrice") != null ? doc.get("productPrice") : doc.get("product_prise")));
                                    model.setProductDescription(doc.getString("productDescription"));

                                    int idx = horizontalproductscrollModelList.indexOf(model);
                                    if (viewAllProductList != null && idx < viewAllProductList.size()) {
                                        WishlistModel wModel = viewAllProductList.get(idx);
                                        wModel.setProductImage(firstImage);
                                        wModel.setProductTitle(doc.getString("productTitle"));
                                        wModel.setFreeCoupon(doc.getDouble("freeCoupon") != null ? doc.getDouble("freeCoupon") : 0.0);
                                        wModel.setStarRating(doc.getDouble("starRating") != null ? doc.getDouble("starRating") : 0.0);
                                        wModel.setTotalRating(doc.getLong("totalRatings") != null ? doc.getLong("totalRatings") : 0L);
                                        wModel.setPrise(doc.getLong("productPrice") != null ? doc.getLong("productPrice") : 0L);
                                        wModel.setCatPrise(doc.getLong("cutPrice") != null ? doc.getLong("cutPrice") : 0L);
                                        wModel.setPaymentMethod(doc.getString("paymentMethod"));
                                    }
                                }
                                loadedCount[0]++;
                                if (loadedCount[0] == listSize) adapter.notifyDataSetChanged();
                            });
                } else {
                    loadedCount[0]++;
                    if (loadedCount[0] == listSize) adapter.notifyDataSetChanged();
                }
            }

            if (horizontalproductscrollModelList.size() > 6) {
                horizontalViewall.setVisibility(View.VISIBLE);
                horizontalViewall.setOnClickListener(v -> {
                    ViewAllActivity2.viewAllProductList = viewAllProductList;
                    Intent intent = new Intent(itemView.getContext(), ViewAllActivity2.class);
                    intent.putExtra("layout_code", 0);
                    intent.putExtra("title", Title);
                    itemView.getContext().startActivity(intent);
                });
            } else {
                horizontalViewall.setVisibility(View.INVISIBLE);
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

        private void setGridlayout(List<HorizontalProductScrollModel> list, String title, String color) {
            gridlayoutTitel.setText(title);
            if (color != null && !color.isEmpty()) constraintLayout.setBackgroundColor(Color.parseColor(color));
            if (list == null || list.isEmpty()) return;
            final int listSize = list.size();
            final int[] loadedCount = {0};

            for (HorizontalProductScrollModel model : list) {
                if (model.getProductID() != null && !model.getProductID().isEmpty() && (model.getProductTitle() == null || model.getProductTitle().isEmpty())) {
                    firebaseFirestore.collection("Product_Details").document(model.getProductID()).get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful() && task.getResult().exists()) {
                                    DocumentSnapshot doc = task.getResult();
                                    String firstImage = "";
                                    List<String> images = (List<String>) doc.get("imageUrls");
                                    if (images != null && !images.isEmpty()) firstImage = images.get(0);
                                    else if (doc.getString("product_image_1") != null) firstImage = doc.getString("product_image_1");

                                    model.setProductImage(firstImage);
                                    model.setProductTitle(doc.getString("productTitle"));
                                    model.setProductDescription(doc.getString("productDescription"));
                                    model.setProductPrice(String.valueOf(doc.get("productPrice") != null ? doc.get("productPrice") : doc.get("product_prise")));
                                }
                                loadedCount[0]++;
                                if (loadedCount[0] == listSize) setGridData(list, title);
                            });
                } else {
                    loadedCount[0]++;
                    if (loadedCount[0] == listSize) setGridData(list, title);
                }
            }
            gridlayoutviewall.setOnClickListener(v -> {
                ViewAllActivity2.horizontalproductscrollModelList = list;
                Intent intent = new Intent(itemView.getContext(), ViewAllActivity2.class);
                intent.putExtra("title", title);
                intent.putExtra("layout_code", 1);
                itemView.getContext().startActivity(intent);
            });
        }

        private void setGridData(List<HorizontalProductScrollModel> list, String title) {
            for (int x = 0; x < Math.min(list.size(), 4); x++) {
                View productView = gridLayout.getChildAt(x);
                if (productView == null) continue;
                ImageView img = productView.findViewById(R.id.h_s_product_image);
                TextView tit = productView.findViewById(R.id.h_s_product_titel);
                TextView desc = productView.findViewById(R.id.h_s_product_description);
                TextView price = productView.findViewById(R.id.h_s_product_price);

                String imageUrl = list.get(x).getProductImage();
                Log.d("GlideDebug", "Loading Grid Product Image: " + imageUrl);
                
                Glide.with(itemView.getContext())
                        .load(imageUrl)
                        .apply(new RequestOptions().placeholder(R.drawable.ic_home))
                        .listener(new RequestListener<android.graphics.drawable.Drawable>() {
                            @Override
                            public boolean onLoadFailed(@androidx.annotation.Nullable GlideException e, Object model, Target<android.graphics.drawable.Drawable> target, boolean isFirstResource) {
                                Log.e("GlideError", "Grid Product Image Failed: " + imageUrl, e);
                                return false;
                            }
                            @Override
                            public boolean onResourceReady(android.graphics.drawable.Drawable resource, Object model, Target<android.graphics.drawable.Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                return false;
                            }
                        })
                        .into(img);
                tit.setText(list.get(x).getProductTitle());
                desc.setText(list.get(x).getProductDescription());
                price.setText(" ₹ " + list.get(x).getProductPrice() + "/-");

                final String pid = list.get(x).getProductID();
                productView.setOnClickListener(v -> {
                    Intent intent = new Intent(itemView.getContext(), ProductDetailsActivity.class);
                    intent.putExtra("PRODUCT_ID", pid);
                    itemView.getContext().startActivity(intent);
                });
            }
        }
    }
}
