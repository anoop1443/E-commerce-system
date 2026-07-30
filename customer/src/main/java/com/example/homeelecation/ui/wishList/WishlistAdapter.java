package com.example.homeelecation.ui.wishList;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.homeelecation.R;
import com.example.homeelecation.ui.details.ProductDetailsActivity;

import java.util.ArrayList;
import java.util.List;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.ViewHolder> {


    private boolean formSearch = false;
    private final boolean wishlistRemoveBtn;
    private  List<WishlistModel> wishlistModelList;

    public boolean isFormSearch() {
        return formSearch;
    }

    public void setFormSearch(boolean formSearch) {
        this.formSearch = formSearch;
    }

    public WishlistAdapter(List<WishlistModel> wishlistModelList, boolean wishlist) {
        this.wishlistModelList = wishlistModelList;
        this.wishlistRemoveBtn = wishlist;

    }


    public List<WishlistModel> getWishlistModelList() {
        return wishlistModelList;
    }

    public void setWishlistModelList(List<WishlistModel> wishlistModelList) {
        this.wishlistModelList = wishlistModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wishlist_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position < wishlistModelList.size()) {
            WishlistModel model = wishlistModelList.get(position);
            String productId = model.getProductID();
            String image = model.getProductImage();
            double freeCoupon = model.getFreeCoupon();
            double Rating = model.getStarRating();
            long totalRating = model.getTotalRating();
            String title = model.getProductTitle();
            long prise = model.getPrise();
            long catPrise = model.getCatPrise();
            String paymentMethod = model.getPaymentMethod();
            
            holder.setWishlist(productId, image, title, freeCoupon, Rating, totalRating, prise, catPrise, paymentMethod, position);
        }
    }

    @Override
    public int getItemCount() {
        return wishlistModelList != null ? wishlistModelList.size() : 0;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateList(ArrayList<WishlistModel> wishlistItems) {
        if (wishlistModelList != null) {
            wishlistModelList.clear();
            if (wishlistItems != null) {
                wishlistModelList.addAll(wishlistItems);
            }
            notifyDataSetChanged();
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView, couponIcon;
        ImageButton remove;
        TextView title, freeCoupon, starRating, totalRating, prise, catPrise, paymentMethod, percentOf;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.wishlist_product_image);
            couponIcon = itemView.findViewById(R.id.wishlist_coupen_icon);
            remove = itemView.findViewById(R.id.wishlist_delete_btn);
            title = itemView.findViewById(R.id.wishlist_product_title);
            freeCoupon = itemView.findViewById(R.id.wishlist_coupen_title);
            starRating = itemView.findViewById(R.id.product_images_layout_wishlist_StarRating);
            totalRating = itemView.findViewById(R.id.wishlist_total_rating);
            prise = itemView.findViewById(R.id.wishlist_product_prise);
            catPrise = itemView.findViewById(R.id.wishlist_product_cat_prise);
            paymentMethod = itemView.findViewById(R.id.wishlist_payment_mathod);
            percentOf = itemView.findViewById(R.id.wishlist_percentOF);
        }

        @SuppressLint("SetTextI18n")
        private void setWishlist(String productID, String productImage, String productTitle, double FreeCouponNo, double Star_rating, long TotalRating, long productPrise, long CatPrise, String PaymentMethod, int index) {
            
            if (imageView != null) {
                Glide.with(itemView.getContext())
                        .load(productImage)
                        .apply(new RequestOptions().placeholder(R.drawable.ic_home))
                        .into(imageView);
            }
            
            if (title != null) title.setText(productTitle != null ? productTitle : "Product");
            
            if (FreeCouponNo > 0) {
                if (couponIcon != null) couponIcon.setVisibility(View.VISIBLE);
                if (freeCoupon != null) {
                    freeCoupon.setVisibility(View.VISIBLE);
                    freeCoupon.setText("free " + (int)FreeCouponNo + " coupon");
                }
            } else {
                if (couponIcon != null) couponIcon.setVisibility(View.INVISIBLE);
                if (freeCoupon != null) freeCoupon.setVisibility(View.INVISIBLE);
            }
            
            if (starRating != null) starRating.setText(String.valueOf(Star_rating));
            if (totalRating != null) totalRating.setText("(" + TotalRating + ") rating");
            if (prise != null) prise.setText("Rs." + productPrise + "/-");
            if (catPrise != null) catPrise.setText("Rs." + CatPrise + "/-");
            
            if (percentOf != null) {
                if (CatPrise > 0) {
                    double sho = CatPrise - productPrise;
                    double showPercent = (sho / CatPrise) * 100;
                    @SuppressLint("DefaultLocale") String foo = String.format("%.0f", showPercent);
                    percentOf.setText(foo + "% off");
                } else {
                    percentOf.setText("0% off");
                }
            }
            
            if (paymentMethod != null) paymentMethod.setText(PaymentMethod != null ? PaymentMethod : "");

            if (remove != null) {
                remove.setVisibility(wishlistRemoveBtn ? View.VISIBLE : View.GONE);
                remove.setOnClickListener(v -> {
                    if (Wishlist_Fragment.wishlistViewModel != null) {
                        Wishlist_Fragment.wishlistViewModel.removeFromWishlist(productID);
                        Toast.makeText(itemView.getContext(), "Removing from Wishlist...", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            if (imageView != null) {
                imageView.setOnClickListener(v -> {
                    if (formSearch) {
                        ProductDetailsActivity.formSearch = true;
                    }
                    Intent productDetailIntent = new Intent(itemView.getContext(), ProductDetailsActivity.class);
                    productDetailIntent.putExtra("PRODUCT_ID", productID);
                    itemView.getContext().startActivity(productDetailIntent);
                });
            }
        }
    }
}
