package com.example.homeadmin.ui.trash;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.homeadmin.R;
import com.google.android.material.button.MaterialButton;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TrashAdapter extends RecyclerView.Adapter<TrashAdapter.ViewHolder> {

    private List<TrashModel> trashList;
    private OnTrashActionListener listener;

    public interface OnTrashActionListener {
        void onRestore(TrashModel item, int position);
        void onDelete(TrashModel item, int position);
    }

    public TrashAdapter(List<TrashModel> trashList, OnTrashActionListener listener) {
        this.trashList = trashList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trash, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TrashModel item = trashList.get(position);
        holder.setData(item, position);
    }

    @Override
    public int getItemCount() {
        return trashList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private TextView label, type, expiry;
        private MaterialButton restoreBtn, deleteBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.item_image);
            label = itemView.findViewById(R.id.item_label);
            type = itemView.findViewById(R.id.item_type);
            expiry = itemView.findViewById(R.id.item_expiry);
            restoreBtn = itemView.findViewById(R.id.restore_button);
            deleteBtn = itemView.findViewById(R.id.delete_button);
        }

        public void setData(TrashModel item, int position) {
            label.setText(item.getLabel());
            type.setText("Type: " + item.getType());

            if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
                Glide.with(itemView.getContext()).load(item.getImageUrl()).into(image);
            } else {
                image.setImageResource(R.drawable.ic_home);
            }

            // Calculate remaining days
            if (item.getExpiryDate() != null) {
                long diffInMs = item.getExpiryDate().getTime() - new Date().getTime();
                long diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMs);
                if (diffInDays <= 0) {
                    expiry.setText("Expires today");
                } else {
                    expiry.setText("Expires in " + diffInDays + " days");
                }
            }

            restoreBtn.setOnClickListener(v -> listener.onRestore(item, position));
            deleteBtn.setOnClickListener(v -> listener.onDelete(item, position));
        }
    }
}
