package com.example.homeelecation.ui.orders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homeelecation.R;

import java.util.ArrayList;

public class OrderPagerAdapter extends RecyclerView.Adapter<OrderPagerAdapter.ViewHolder> {

    private final MyOrderItemAdapter productsAdapter;
    private final QuickOrderAdapter quickOrdersAdapter;
    
    private boolean productsLoaded = false;
    private boolean quickOrdersLoaded = false;

    public OrderPagerAdapter() {
        this.productsAdapter = new MyOrderItemAdapter(new ArrayList<>());
        this.quickOrdersAdapter = new QuickOrderAdapter(new ArrayList<>());
    }

    public void setProductsLoaded(boolean loaded) {
        this.productsLoaded = loaded;
        notifyItemChanged(0);
    }

    public void setQuickOrdersLoaded(boolean loaded) {
        this.quickOrdersLoaded = loaded;
        notifyItemChanged(1);
    }

    public MyOrderItemAdapter getProductsAdapter() {
        return productsAdapter;
    }

    public QuickOrderAdapter getQuickOrdersAdapter() {
        return quickOrdersAdapter;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_order_page, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position == 0) {
            holder.recyclerView.setAdapter(productsAdapter);
            updateEmptyState(holder, "No Product Orders", "You haven't ordered any products yet.", productsAdapter.getItemCount() == 0, productsLoaded);
            
            productsAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onChanged() {
                    updateEmptyState(holder, "No Product Orders", "You haven't ordered any products yet.", productsAdapter.getItemCount() == 0, productsLoaded);
                }
            });
        } else {
            holder.recyclerView.setAdapter(quickOrdersAdapter);
            updateEmptyState(holder, "No Quick Orders", "Your quick service history is empty.", quickOrdersAdapter.getItemCount() == 0, quickOrdersLoaded);
            
            quickOrdersAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onChanged() {
                    updateEmptyState(holder, "No Quick Orders", "Your quick service history is empty.", quickOrdersAdapter.getItemCount() == 0, quickOrdersLoaded);
                }
            });
        }
    }

    private void updateEmptyState(ViewHolder holder, String title, String desc, boolean isEmpty, boolean isLoaded) {
        // Show empty state ONLY if loading is finished AND the list is truly empty
        if (isLoaded && isEmpty) {
            holder.emptyStateLayout.setVisibility(View.VISIBLE);
            holder.recyclerView.setVisibility(View.GONE);
            holder.emptyTitle.setText(title);
            holder.emptyDesc.setText(desc);
            holder.emptyImage.setImageResource(R.drawable.empty_img);
            holder.emptyButton.setOnClickListener(v -> {
                if (v.getContext() instanceof android.app.Activity) {
                    ((android.app.Activity) v.getContext()).finish();
                }
            });
        } else {
            // Hide empty state if loading or if we have items
            holder.emptyStateLayout.setVisibility(View.GONE);
            holder.recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        RecyclerView recyclerView;
        View emptyStateLayout;
        TextView emptyTitle, emptyDesc;
        ImageView emptyImage;
        Button emptyButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.order_recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
            
            emptyStateLayout = itemView.findViewById(R.id.empty_orders_layout);
            emptyTitle = itemView.findViewById(R.id.empty_state_title);
            emptyDesc = itemView.findViewById(R.id.empty_state_desc);
            emptyImage = itemView.findViewById(R.id.empty_state_image);
            emptyButton = itemView.findViewById(R.id.empty_state_button);
        }
    }
}
