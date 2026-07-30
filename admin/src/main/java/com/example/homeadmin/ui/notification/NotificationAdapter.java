package com.example.homeadmin.ui.notification;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.homeadmin.R;

import java.util.ArrayList;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private List<NotificationModel> notificationModelList = new ArrayList<>();

    public NotificationAdapter(List<NotificationModel> notificationModelList) {
        this.notificationModelList = notificationModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item,parent,false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String image = notificationModelList.get(position).getImage();
        String textView = notificationModelList.get(position).getTextview();
        boolean read = notificationModelList.get(position).isRead();
        holder.setNotification(image,textView,read);

    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return notificationModelList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView textView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.notification_image);
            textView = itemView.findViewById(R.id.notification_textView);
        }
        private void setNotification(String image, String titleView,boolean read){
            Glide.with(itemView.getContext()).load(image).apply(new RequestOptions().placeholder(R.drawable.ic_notifications_24)).into(imageView);

            textView.setText(titleView);

            if (read){
                textView.setAlpha(0.5f);
            }else {
                textView.setAlpha(1f);

            }


        }
    }
}
