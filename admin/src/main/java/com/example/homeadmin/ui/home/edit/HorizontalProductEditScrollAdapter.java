package com.example.homeadmin.ui.home.edit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.homeadmin.R;

import java.util.ArrayList;
import java.util.List;

public class HorizontalProductEditScrollAdapter extends RecyclerView.Adapter<HorizontalProductEditScrollAdapter.ViewHolder> {

    private List<HorizontalProductScrollModel> productList;
    private final List<HorizontalProductScrollModel> productListFull;
    private final Context context;
    private List<HorizontalProductScrollModel> selectedProducts = new ArrayList<>();

    public HorizontalProductEditScrollAdapter(List<HorizontalProductScrollModel> productList, Context context) {
        this.productList = productList;
        this.productListFull = new ArrayList<>(productList);
        this.context = context;
    }

    public void filter(String text) {
        productList = new ArrayList<>();
        if (text.isEmpty()) {
            productList.addAll(productListFull);
        } else {
            text = text.toLowerCase();
            for (HorizontalProductScrollModel item : productListFull) {
                if (item.getProductTitle().toLowerCase().contains(text) ||
                        item.getProductDescription().toLowerCase().contains(text)) {
                    productList.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void updateFullList(List<HorizontalProductScrollModel> newList) {
        this.productListFull.clear();
        this.productListFull.addAll(newList);
        this.productList = new ArrayList<>(newList);
        
        this.selectedProducts.clear();
        for (HorizontalProductScrollModel model : newList) {
            if (model.isSelected()) {
                selectedProducts.add(model);
            }
        }
        notifyDataSetChanged();
    }

    public List<HorizontalProductScrollModel> getSelectedProducts() {
        return selectedProducts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_scroll_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HorizontalProductScrollModel product = productList.get(position);

        holder.selectionOverlay.setVisibility(product.isSelected() ? View.VISIBLE : View.GONE);

        holder.itemView.setOnClickListener(v -> {
            product.setSelected(!product.isSelected());
            if (product.isSelected()) {
                selectedProducts.add(product);
            } else {
                selectedProducts.remove(product);
            }
            notifyItemChanged(position);
        });

        if (product.getProductImage() != null) {
            Glide.with(holder.itemView.getContext())
                    .load(product.getProductImage())
                    .into(holder.productImage);
        }

        holder.productTitle.setText(product.getProductTitle());
        holder.productDescription.setText(product.getProductDescription() + " ," + product.getDocumentId());
        holder.productPrice.setText(product.getProductPrice());
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productTitle;
        TextView productDescription;
        TextView productPrice;
        FrameLayout selectionOverlay;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.h_s_product_image);
            productTitle = itemView.findViewById(R.id.h_s_product_titel);
            productDescription = itemView.findViewById(R.id.h_s_product_description);
            productPrice = itemView.findViewById(R.id.h_s_product_price);
            selectionOverlay = itemView.findViewById(R.id.selection_overlay);
        }
    }
}
