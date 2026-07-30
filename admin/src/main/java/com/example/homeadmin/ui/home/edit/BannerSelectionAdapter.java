package com.example.homeadmin.ui.home.edit;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.homeadmin.R;

import java.util.ArrayList;
import java.util.List;

public class BannerSelectionAdapter extends RecyclerView.Adapter<BannerSelectionAdapter.ViewHolder> {

    private final List<BannerModel> bannerList;
    private final Context context;
    private OnBannerSelectionChangedListener listener;

    public interface OnBannerSelectionChangedListener {
        void onSelectionChanged(int count);
    }

    public void setOnBannerSelectionChangedListener(OnBannerSelectionChangedListener listener) {
        this.listener = listener;
    }

    public BannerSelectionAdapter(List<BannerModel> bannerList, Context context) {
        this.bannerList = bannerList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.banner_select_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BannerModel model = bannerList.get(position);

        if (model.getImageUrl() != null) {
            Glide.with(context).load(model.getImageUrl()).into(holder.bannerImageView);
        }

        try {
            holder.container.setBackgroundColor(Color.parseColor(model.getBackgroundColor()));
        } catch (IllegalArgumentException e) {
            // handle error
        }

        holder.checkBox.setOnCheckedChangeListener(null); // Remove previous listener
        holder.checkBox.setChecked(model.isSelected());
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            model.setSelected(isChecked);
            if (listener != null) {
                listener.onSelectionChanged(getSelectedBanners().size());
            }
        });

        // Ab poore item par click karne se select ho jayega
        holder.itemView.setOnClickListener(v -> {
            boolean newState = !model.isSelected();
            model.setSelected(newState);
            holder.checkBox.setChecked(newState); // Isse upar wala listener trigger ho jayega
        });
    }

    @Override
    public int getItemCount() {
        return bannerList.size();
    }

    public List<BannerModel> getSelectedBanners() {
        List<BannerModel> selectedBanners = new ArrayList<>();
        for (BannerModel model : bannerList) {
            if (model.isSelected()) {
                selectedBanners.add(model);
            }
        }
        return selectedBanners;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView bannerImageView;
        private final CheckBox checkBox;
        private final LinearLayout container;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            bannerImageView = itemView.findViewById(R.id.banner_image_view);
            checkBox = itemView.findViewById(R.id.banner_checkbox);
            container = itemView.findViewById(R.id.banner_container);
        }
    }
}
