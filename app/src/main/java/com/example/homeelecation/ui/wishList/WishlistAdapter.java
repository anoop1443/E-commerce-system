package com.example.homeelecation.ui.wishList;

import static com.example.homeelecation.ui.details.ProductDeteilsActivity.running_wishlist_query;
import static com.example.homeelecation.ui.wishList.Wishlist_Fragment.loadingDialog;

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
import com.example.homeelecation.ui.DbLoadData;
import com.example.homeelecation.ui.details.ProductDeteilsActivity;

import java.util.List;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.ViewHolder> {


    private final boolean wishlistRemoveBtn;
    private final List<WishlistModel> wishlistModelList;

    public WishlistAdapter(List<WishlistModel> wishlistModelList, boolean wishlist) {
        this.wishlistModelList = wishlistModelList;
        this.wishlistRemoveBtn = wishlist;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wishlist_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String productId = wishlistModelList.get(position).getProductID();
        String image = wishlistModelList.get(position).getProductImage();
        long freeCoupon = wishlistModelList.get(position).getFreeCoupon();
        double Rating = wishlistModelList.get(position).getStarRating();
        long totalRating = wishlistModelList.get(position).getTotalRating();
        String title = wishlistModelList.get(position).getProductTitle();
        long prise = wishlistModelList.get(position).getPrise();
        long catPrise = wishlistModelList.get(position).getCatPrise();
        String paymentMethod = wishlistModelList.get(position).getPaymentMethod();
        //boolean pm = wishlistModelList.get(position).isPM();
        holder.setWishlist(productId,image, title, freeCoupon, Rating, totalRating, prise, catPrise, paymentMethod,position);


    }

    @Override
    public int getItemCount() {
        return wishlistModelList.size();
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
        private void setWishlist(String productID, String productImage, String productTitle, long FreeCouponNo, double Star_rating, long TotalRating, long productPrise, long CatPrise, String PaymentMethod, int index) {
            // imageView.setImageResource(productImage);
            Glide.with(itemView.getContext()).load(productImage).apply(new RequestOptions().placeholder(R.drawable.ic_home)).into(imageView);
            title.setText(productTitle);
            if (FreeCouponNo != 0) {
                couponIcon.setVisibility(View.VISIBLE);

                if (FreeCouponNo == 1) {
                    freeCoupon.setText("free " + FreeCouponNo + " coupon");
                } else {
                    freeCoupon.setText("free " + FreeCouponNo + " coupon");


                }


            } else {
                couponIcon.setVisibility(View.INVISIBLE);
                freeCoupon.setVisibility(View.INVISIBLE);


            }
            starRating.setText(String.valueOf(Star_rating));


            totalRating.setText("(" + TotalRating + ") rating");
            prise.setText("Rs." + productPrise + "/-");
            catPrise.setText("Rs." + CatPrise + "/-");
            double sho = CatPrise - productPrise;
            double showPercent = sho / CatPrise * 100;
            @SuppressLint("DefaultLocale") String foo = String.format("%.0f", showPercent);

            percentOf.setText(foo + "%of");
            paymentMethod.setText(PaymentMethod);


            if (wishlistRemoveBtn) {
                remove.setVisibility(View.VISIBLE);
            } else {
                remove.setVisibility(View.GONE);
            }

            remove.setOnClickListener(v -> {
                if (!ProductDeteilsActivity.running_wishlist_query) {
                    running_wishlist_query = true;
                    DbLoadData.removeFromWishList(index, itemView.getContext(), loadingDialog);
                    Toast.makeText(itemView.getContext(), "Wishlist removed ", Toast.LENGTH_SHORT).show();
                }
            });

            imageView.setOnClickListener(v -> {
                Intent productDetailIntent = new Intent(itemView.getContext(), ProductDeteilsActivity.class);
                productDetailIntent.putExtra("PRODUCT_ID",productID);
                itemView.getContext().startActivity(productDetailIntent);
            });


        }


    }
}

