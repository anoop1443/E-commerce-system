package com.example.homeelecation.ui.slideshow;


import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.homeelecation.R;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.SliderViewHolder> {

    private List<SliderModel> sliderModelList;
    private ViewPager2 viewPager2;

    public SliderAdapter(List<SliderModel> sliderModelList, ViewPager2 viewPager2) {
        this.sliderModelList = sliderModelList;
        this.viewPager2 = viewPager2;
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.banner_slider_layout,parent,false);
        return new SliderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        String image = sliderModelList.get(position).getBanner();
        String background = sliderModelList.get(position).getBackGroundColor();
        //holder.constraintLayout.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(sliderModelList.get(position).getBackGroundColor())));

        holder.setImage(image,background);


        if (position == sliderModelList.size()- 2){
            viewPager2.post(holder.runnable);
        }
    }



    @Override
    public int getItemCount() {
        return sliderModelList.size();
    }

    public class SliderViewHolder extends RecyclerView.ViewHolder {

        private RoundedImageView imageView;
        private  ConstraintLayout constraintLayout;
        TextView textView;
        public SliderViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.banner_image);
            constraintLayout  = itemView.findViewById(R.id.banner_contenr);

        }
       private void setImage(String sliderItems,String backGroundColor){
            Glide.with(itemView.getContext()).load(sliderItems).apply(new RequestOptions()).placeholder(R.drawable.empty_img).into(imageView);

           try {
               int parsedColor = Color.parseColor(backGroundColor);
               constraintLayout.setBackgroundColor(parsedColor);
           }catch (Exception e){

           }


       }
        private Runnable runnable = new Runnable() {
            @Override
            public void run() {
                sliderModelList.addAll(sliderModelList);
                notifyDataSetChanged();
            }

        };
    }
}
