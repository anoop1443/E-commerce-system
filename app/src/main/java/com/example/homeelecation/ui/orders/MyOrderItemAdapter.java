package com.example.homeelecation.ui.orders;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homeelecation.R;

import java.util.List;

public class MyOrderItemAdapter extends RecyclerView.Adapter<MyOrderItemAdapter.ViewHolder> {


    List<MyOrderItemModel> myOrderItemModelList;

    public MyOrderItemAdapter(List<MyOrderItemModel> myOrderItemModelList) {
        this.myOrderItemModelList = myOrderItemModelList;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.my_orders_layout,parent,false);


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int image = myOrderItemModelList.get(position).getProductImage();
        int rating = myOrderItemModelList.get(position).getRating();
        String title = myOrderItemModelList.get(position).getProductTitle();
        String deliveryStatus = myOrderItemModelList.get(position).getDeliveryStatus();

        holder.setMY(image,rating,title,deliveryStatus);


    }

    @Override
    public int getItemCount() {
        return myOrderItemModelList.size();
    }

    public static class  ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageResource;
        public final ImageView Indicator;
        private final TextView title;
        private final TextView deliveryStu;
        private final LinearLayout rateNowContainer;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageResource = itemView.findViewById(R.id.product_image);
            Indicator = itemView.findViewById(R.id.orDerIndicator);
            title = itemView.findViewById(R.id.product_title);
            deliveryStu = itemView.findViewById(R.id.order_delivered_date);
            rateNowContainer =itemView.findViewById(R.id.rate_now_contenr);
        }
        private void setMY(int resource,int rating,String ProTitle, String deliveryDate){
            imageResource.setImageResource(resource);

            title.setText(ProTitle);

            if (deliveryDate.equals("Cancelled")){
                Indicator.setImageTintList(ColorStateList.valueOf(itemView.getContext().getResources().getColor(R.color.red)));

            }else {
                Indicator.setImageTintList(ColorStateList.valueOf(itemView.getContext().getResources().getColor(R.color.green)));

            }
            deliveryStu.setText(deliveryDate);


            /////rating
            for (int x = 0;x < rateNowContainer.getChildCount();x++){
                SetRating(rating);
                 int starPosition = x;
                rateNowContainer.getChildAt(x).setOnClickListener(v -> SetRating(starPosition));
            }
            /////rating
        }
        /////rating
        private void SetRating( int starPosition ) {
            for (int x =0;x< rateNowContainer.getChildCount();x++){
                ImageView starBtn = (ImageView) rateNowContainer.getChildAt(x);
                starBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor("#FFADB1AD")));
                if (x <= starPosition){
                    starBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor("#05A620")));
                }
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(itemView.getContext(),Orders_DetailsActivity3.class);
                    imageResource.setDrawingCacheEnabled(true);
                    Bitmap b=imageResource.getDrawingCache();
                    intent.putExtra("Bitmap",b);
                    itemView.getContext().startActivities(new Intent[]{intent});
                }
            });
        }

        /////rating
    }
}
