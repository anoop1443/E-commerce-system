package com.example.homeadmin.ui.helpSuppot; // Ise apne package ke naam se badal lein

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homeadmin.R;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class MyTicketsAdapter extends RecyclerView.Adapter<MyTicketsAdapter.ViewHolder> {

    private final List<SupportTicketModel> ticketList;
    private final Context context;

    // Constructor
    public MyTicketsAdapter(List<SupportTicketModel> ticketList, Context context) {
        this.ticketList = ticketList;
        this.context = context;
    }

    // Naya layout (XML) banane ke liye
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_ticket_item_layout, parent, false);
        return new ViewHolder(view);
    }

    // Data ko layout mein set karne ke liye
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // List se current ticket ka data nikalein
        SupportTicketModel model = ticketList.get(position);

        // Data ko TextViews mein set karein
        holder.orderIdText.setText("For Order: #" + model.getOrderId());
        holder.descriptionText.setText(model.getIssue_description());
        holder.statusText.setText(model.getStatus());

        // Status ke according color change karein
        if ("Open".equalsIgnoreCase(model.getStatus())) {
            holder.statusText.setBackgroundResource(R.drawable.status_indicator_open);
            holder.statusText.setTextColor(ContextCompat.getColor(context, R.color.red));
        } else if ("In Progress".equalsIgnoreCase(model.getStatus())) {
            holder.statusText.setBackgroundResource(R.drawable.status_indicator_progress);
            holder.statusText.setTextColor(ContextCompat.getColor(context, R.color.orange));
        } else if ("Closed".equalsIgnoreCase(model.getStatus())) {
            holder.statusText.setBackgroundResource(R.drawable.status_indicator_closed);
            holder.statusText.setTextColor(ContextCompat.getColor(context, R.color.green));
        }

        // Date ko saaf format ("Jan 07, 2026") mein dikhayein
        if (model.getTimestamp() != null) {
            SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            holder.dateText.setText("Submitted on: " + formatter.format(model.getTimestamp()));
        }
    }

    // List mein total kitne items hain, yeh batane ke liye
    @Override
    public int getItemCount() {
        return ticketList.size();
    }

    // ViewHolder Class: XML layout ke UI elements (TextViews) ko hold karta hai
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView orderIdText;
        private final TextView statusText;
        private final TextView descriptionText;
        private final TextView dateText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderIdText = itemView.findViewById(R.id.ticket_order_id);
            statusText = itemView.findViewById(R.id.ticket_status);
            descriptionText = itemView.findViewById(R.id.ticket_description);
            dateText = itemView.findViewById(R.id.ticket_date);
        }
    }
}
