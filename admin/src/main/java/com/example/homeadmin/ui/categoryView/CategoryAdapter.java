package com.example.homeadmin.ui.categoryView;



import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.homeadmin.R;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private List<CategoryModel> categoryModelList;

    public CategoryAdapter(List<CategoryModel> categoryModelList) {
        this.categoryModelList = categoryModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.category_item,viewGroup,false);


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        String icon = categoryModelList.get(position).getCategoryIconLink();
        String name = categoryModelList.get(position).getCategoryName();
        viewHolder.setCategory(name);
        viewHolder.setCategoryIcon(icon);


    }

    @Override
    public int getItemCount() {
        return categoryModelList.size();
    }

    public void updateList(List<CategoryModel> categoryModels) {
        // Step 1: Purani list ko clear karein
        categoryModelList.clear();
        // Step 2: Naya data add karein
        categoryModelList.addAll(categoryModels);
        // Step 3: Adapter ko UI refresh karne ke liye inform karein
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView categoryIcon;
        private TextView categoryName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryIcon = itemView.findViewById(R.id.category_icon);
            categoryName = itemView.findViewById(R.id.category_name);

        }
        private void setCategoryIcon(String iconUrl){
            Log.d("GlideDebug", "Loading Category Icon: " + iconUrl);
            Glide.with(itemView.getContext())
                    .load(iconUrl)
                    .apply(new RequestOptions().placeholder(R.drawable.ic_home))
                    .listener(new RequestListener<android.graphics.drawable.Drawable>() {
                        @Override
                        public boolean onLoadFailed(@androidx.annotation.Nullable GlideException e, Object model, Target<android.graphics.drawable.Drawable> target, boolean isFirstResource) {
                            Log.e("GlideError", "Category Icon Load Failed: " + iconUrl, e);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(android.graphics.drawable.Drawable resource, Object model, Target<android.graphics.drawable.Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(categoryIcon);
        }
        private void setCategory(String name){
            categoryName.setText(name);
            if (!name.equals("")) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent categoryClass = new Intent(itemView.getContext(), Category_activity.class);
                        categoryClass.putExtra("CategoryName", name);
                        itemView.getContext().startActivity(categoryClass);
                        // startActivities(new Intent[]{category});

                    }
                });
            }
        }
    }
}
