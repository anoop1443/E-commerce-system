package com.example.homeelecation.ui.coupons;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homeelecation.R;

import java.util.List;

public class CouponAdapter extends RecyclerView.Adapter<CouponAdapter.ViewHolder> {

    private List<CouponModel> couponModelList;

    public CouponAdapter(List<CouponModel> couponModelList) {
        this.couponModelList = couponModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.coupons_item_layout,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String title = couponModelList.get(position).getCouponTitle();
        String valid = couponModelList.get(position).getCouponValid();
        String body = couponModelList.get(position).getCouponBody();

        holder.setCoupon(title,valid,body);

    }

    @Override
    public int getItemCount() {
        return couponModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView couponIcon;
        TextView couponTitle,couponValid,couponBody;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            couponTitle = itemView.findViewById(R.id.coupon_title);
            couponValid = itemView.findViewById(R.id.coupon_valid);
            couponBody = itemView.findViewById(R.id.coupon_body);


        }

        private void setCoupon(String Title,String Valid,String Body){

            couponTitle.setText(Title);
            couponValid.setText(Valid);
            couponBody.setText(Body);

        }
    }
}
