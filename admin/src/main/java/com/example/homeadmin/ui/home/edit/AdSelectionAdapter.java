package com.example.homeadmin.ui.home.edit;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.homeadmin.R;

import java.util.List;

public class AdSelectionAdapter extends RecyclerView.Adapter<AdSelectionAdapter.ViewHolder> {

    private final List<AdModel> adList;
    private final Context context;
    private int lastSelectedPosition = -1;

    public AdSelectionAdapter(List<AdModel> adList, Context context) {
        this.adList = adList;
        this.context = context;
    }

    public void setInitialSelection(String adId) {
        for (int i = 0; i < adList.size(); i++) {
            if (adList.get(i).getDocumentId().equals(adId)) {
                lastSelectedPosition = i;
                break;
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ad_select_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AdModel model = adList.get(position);

        if (model.getImageUrl() != null) {
            Glide.with(context).load(model.getImageUrl()).into(holder.adImageView);
        }

        try {
            holder.container.setBackgroundColor(Color.parseColor(model.getBackgroundColor()));
        } catch (IllegalArgumentException e) {
            // handle error
        }

        holder.radioButton.setChecked(lastSelectedPosition == position);
    }

    @Override
    public int getItemCount() {
        return adList.size();
    }

    public AdModel getSelectedAd() {
        if (lastSelectedPosition != -1) {
            return adList.get(lastSelectedPosition);
        }
        return null;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView adImageView;
        private final LinearLayout container;
        private final RadioButton radioButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            adImageView = itemView.findViewById(R.id.ad_image_view);
            container = itemView.findViewById(R.id.ad_container);
            radioButton = itemView.findViewById(R.id.ad_radio_button);

            View.OnClickListener clickListener = v -> {
                lastSelectedPosition = getAdapterPosition();
                notifyDataSetChanged();
            };
            itemView.setOnClickListener(clickListener);
            radioButton.setOnClickListener(clickListener);
        }
    }
}
