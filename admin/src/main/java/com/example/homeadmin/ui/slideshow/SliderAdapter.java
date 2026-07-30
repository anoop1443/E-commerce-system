package com.example.homeadmin.ui.slideshow;


import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.homeadmin.R;
import com.makeramen.roundedimageview.RoundedImageView;
//import com.example.homeelecation.R;
//import com.makeramen.roundedimageview.RoundedImageView;

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
       private void setImage(String sliderItems,String backGroundColor ){
            if (sliderItems == null || sliderItems.isEmpty()) {
                Log.e("GlideError", "Banner Image URL is null or empty");
                return;
            }

           Glide.with(itemView.getContext())
                   .load(sliderItems)
                   .apply(new RequestOptions().placeholder(R.drawable.ic_home))
                   .listener(new RequestListener<android.graphics.drawable.Drawable>() {
                       @Override
                       public boolean onLoadFailed(@androidx.annotation.Nullable GlideException e, Object model, Target<android.graphics.drawable.Drawable> target, boolean isFirstResource) {
                           String errorMsg = (e != null) ? e.getMessage() : "Unknown Error";
                           Log.e("GlideError", "Banner Load Failed: " + errorMsg);
                           return false;
                       }
                       @Override
                       public boolean onResourceReady(android.graphics.drawable.Drawable resource, Object model, Target<android.graphics.drawable.Drawable> target, DataSource dataSource, boolean isFirstResource) {
                           return false;
                       }
                   })
                   .into(imageView);

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
//
//        PagerAdapter {
//
//
//    private  List<SliderModel>sliderModelList;
//
//    public SliderAdapter(List<SliderModel> sliderModelList) {
//        this.sliderModelList = sliderModelList;
//    }
//
//    @NonNull
//    @Override
//    public Object instantiateItem(@NonNull ViewGroup container, int position) {
//        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.banner_slider_layout,container ,false);
//        ConstraintLayout constraintLayout= view.findViewById(R.id.banner_contenr);
//        constraintLayout.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(sliderModelList.get(position).getBackgruondcolor())));
//        ImageView banner = view.findViewById(R.id.banner_image);
//        banner.setImageResource(sliderModelList.get(position).getBanner());
//        //Glide.with(container.getContext()).load(sliderModelList.get(position).getBanner()).into(banner);
//        container.addView(view,0);
//        return view;
//    }
//
//    @Override
//    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
//        return view==object ;
//    }
//
//    @Override
//    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
//        container.removeView((View) object);
//    }
//
//    @Override
//    public int getCount() {
//        return sliderModelList.size();
//    }
}
