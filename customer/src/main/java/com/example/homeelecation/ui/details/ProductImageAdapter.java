package com.example.homeelecation.ui.details;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.homeelecation.R;

import java.util.ArrayList;
import java.util.List;

public class ProductImageAdapter extends PagerAdapter {

    List<String> ProductImages;

    public ProductImageAdapter(List<String> productImages) {
        ProductImages = productImages;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ImageView imageView = new ImageView(container.getContext());
       // productImage.setImageResource(ProductImages.get(position));
        //imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        Glide.with(container.getContext())
                .load(ProductImages
                .get(position))
                .apply(new RequestOptions().placeholder(R.drawable.empty_img))
                .into(imageView);

        container.addView(imageView,0);

        imageView.setOnClickListener(v -> {

            Intent intent = new Intent(container.getContext(), FullScreenImageActivity.class);
            intent.putStringArrayListExtra("image_urls", new ArrayList<>(ProductImages));
            intent.putExtra("position", position);
            container.getContext().startActivity(intent);


        });

        return imageView;
    }


    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ImageView)object);
    }



    @Override
    public int getCount() {
        return ProductImages.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
}
