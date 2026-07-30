package com.example.homeadmin.ui.management;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.homeadmin.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserManagementAdapter extends RecyclerView.Adapter<UserManagementAdapter.ViewHolder> {

    private List<StaffProfileModel> userList;

    public UserManagementAdapter(List<StaffProfileModel> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_management_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StaffProfileModel model = userList.get(position);
        holder.userName.setText(model.getName());
        holder.userMobile.setText(model.getMobile());
        holder.userStatus.setText(model.getStatus() != null ? model.getStatus() : "N/A");

        Glide.with(holder.itemView.getContext())
                .load(model.getProfileImage())
                .apply(new RequestOptions().placeholder(R.drawable.ic_person))
                .into(holder.userProfileImage);

        holder.itemView.setOnClickListener(v -> {
            if ("Delivery Boy".equalsIgnoreCase(model.getRole()) || "Electrician Boys".equalsIgnoreCase(model.getRole())) {
                Intent intent = new Intent(holder.itemView.getContext(), ElectricianProfileActivity.class);
                intent.putExtra("uid", model.getUid());
                holder.itemView.getContext().startActivity(intent);
            } else if ("Admin".equalsIgnoreCase(model.getRole())) {
                Intent intent = new Intent(holder.itemView.getContext(), StaffProfileEditActivity.class);
                intent.putExtra("uid", model.getUid());
                intent.putExtra("name", model.getName());
                intent.putExtra("mobile", model.getMobile());
                intent.putExtra("role", model.getRole());
                intent.putExtra("status", model.getStatus());
                intent.putExtra("image", model.getProfileImage());
                holder.itemView.getContext().startActivity(intent);
            } else {
                Intent intent = new Intent(holder.itemView.getContext(), com.example.homeadmin.user.UserProfileActivity.class);
                intent.putExtra("uid", model.getUid());
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView userProfileImage;
        TextView userName, userMobile, userStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userProfileImage = itemView.findViewById(R.id.userProfileImage);
            userName = itemView.findViewById(R.id.userName);
            userMobile = itemView.findViewById(R.id.userMobile);
            userStatus = itemView.findViewById(R.id.userStatus);
        }
    }
}
