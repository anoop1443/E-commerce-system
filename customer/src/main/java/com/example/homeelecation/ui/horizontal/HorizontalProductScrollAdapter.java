package com.example.homeelecation.ui.horizontal;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.homeelecation.R;
import com.example.homeelecation.ui.details.ProductDetailsActivity;

import java.util.List;

public class HorizontalProductScrollAdapter extends RecyclerView.Adapter<HorizontalProductScrollAdapter.ViewHolder> {

    private final List<HorizontalProductScrollModel> horizontalProductScrollModelList;

    public HorizontalProductScrollAdapter(List<HorizontalProductScrollModel> horizontalProductScrollModelList) {
        this.horizontalProductScrollModelList = horizontalProductScrollModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_scroll_item_layout,parent ,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String resource = horizontalProductScrollModelList.get(position).getProductImage();
        String title = horizontalProductScrollModelList.get(position).getProductTitle();
        String description = horizontalProductScrollModelList.get(position).getProductDescription();
        String price = horizontalProductScrollModelList.get(position).getProductPrice();
        String productID = horizontalProductScrollModelList.get(position).getProductId();


        holder.setProductDetails(productID,resource,title,description,price);



    }

    @Override
    public int getItemCount() {
        if (horizontalProductScrollModelList.size()>=8){
            return 8;
        }else {
            return horizontalProductScrollModelList.size();
        }
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView productImage;
        private final TextView productTitle;
        private final TextView productDescription;
        private final TextView productPrice;
         private AnimationDrawable animationDrawable;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.h_s_product_image);
            productTitle = itemView.findViewById(R.id.h_s_product_titel);
            productDescription = itemView.findViewById(R.id.h_s_product_description);
            productPrice = itemView.findViewById(R.id.h_s_product_price);





        }
        private void setProductDetails(final String productId,String resource,String title,String description,String price){


            // AnimationDrawable प्राप्त करें
            Glide.with(itemView.getContext()).load(resource).apply(new RequestOptions().placeholder(R.drawable.empt_img1080)).into(productImage);
            productTitle.setText(title);
            productDescription.setText(description);
            productPrice.setText(" ₹ "+price+" /- ");

            if (!resource.equals("")) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(itemView.getContext(), ProductDetailsActivity.class);
                        intent.putExtra("PRODUCT_ID",productId);
                        itemView.getContext().startActivity(intent);
                    }
                });
            }



        }

    }

}
