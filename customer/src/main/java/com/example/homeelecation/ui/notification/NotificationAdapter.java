package com.example.homeelecation.ui.notification;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.homeelecation.R;

import java.util.ArrayList;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private final List<NotificationModel> notificationModelList;

    public NotificationAdapter(List<NotificationModel> notificationModelList) {
        this.notificationModelList = notificationModelList != null ? notificationModelList : new ArrayList<>();
    }

    @NonNull
    @Override
    public NotificationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.ViewHolder holder, int position) {
        if (position < notificationModelList.size()) {
            NotificationModel model = notificationModelList.get(position);
            if (model != null) {
                holder.setData(model);
            }
        }
    }

    @Override
    public int getItemCount() {
        return notificationModelList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imageView;
        private final TextView textView;
        private final View unreadIndicator;
        private final View container;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.notification_image);
            textView = itemView.findViewById(R.id.notification_textView);
            unreadIndicator = itemView.findViewById(R.id.unread_indicator);
            container = itemView.findViewById(R.id.notification_container);
        }

        private void setData(NotificationModel model) {
            String image = model.getImage();
            String text = model.getTextview();
            boolean read = model.isRead();

            Glide.with(itemView.getContext())
                    .load(image)
                    .apply(new RequestOptions().placeholder(R.drawable.ic_notifications))
                    .into(imageView);

            textView.setText(text != null ? text : "");
            
            if (read) {
                unreadIndicator.setVisibility(View.GONE);
                container.setBackgroundColor(Color.WHITE);
                textView.setTextColor(Color.parseColor("#555555"));
                textView.setAlpha(0.8f);
            } else {
                unreadIndicator.setVisibility(View.VISIBLE);
                container.setBackgroundColor(Color.parseColor("#F0F7FF")); // Light blue tint for unread
                textView.setTextColor(Color.BLACK);
                textView.setAlpha(1.0f);
            }
        }
    }
}
