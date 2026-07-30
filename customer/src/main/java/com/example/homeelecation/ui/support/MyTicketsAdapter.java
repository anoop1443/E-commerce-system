package com.example.homeelecation.ui.support;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.homeelecation.R;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class MyTicketsAdapter extends RecyclerView.Adapter<MyTicketsAdapter.ViewHolder> {

    private final List<SupportTicketModel> ticketList;
    private final Context context;

    public MyTicketsAdapter(List<SupportTicketModel> ticketList, Context context) {
        this.ticketList = ticketList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_ticket_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SupportTicketModel model = ticketList.get(position);

        holder.ticketIdText.setText("Ticket ID: #" + model.getTicketId());
        holder.orderIdText.setText("Order: #" + model.getOrderId());
        holder.descriptionText.setText(model.getIssue_description());
        
        String status = model.getStatus() != null ? model.getStatus() : "OPEN";
        holder.statusText.setText(status);

        // Status Based Coloring
        if ("OPEN".equalsIgnoreCase(status)) {
            holder.statusText.setTextColor(Color.parseColor("#FF0000")); // Red for open
        } else if ("CLOSED".equalsIgnoreCase(status)) {
            holder.statusText.setTextColor(Color.parseColor("#008000")); // Green for closed
        } else {
            holder.statusText.setTextColor(Color.parseColor("#FFA500")); // Orange for others
        }

        // Admin Note handling
        if (model.getAdminNote() != null && !model.getAdminNote().isEmpty()) {
            holder.adminNoteText.setVisibility(View.VISIBLE);
            holder.adminNoteText.setText("Admin: " + model.getAdminNote());
        } else {
            holder.adminNoteText.setVisibility(View.GONE);
        }

        if (model.getCreatedDate() != null) {
            SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
            holder.dateText.setText(formatter.format(model.getCreatedDate()));
        }
    }

    @Override
    public int getItemCount() {
        return ticketList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView ticketIdText, orderIdText, statusText, descriptionText, dateText, adminNoteText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ticketIdText = itemView.findViewById(R.id.ticket_id_display); // Layout mein ye IDs confirm karein
            orderIdText = itemView.findViewById(R.id.ticket_order_id);
            statusText = itemView.findViewById(R.id.ticket_status);
            descriptionText = itemView.findViewById(R.id.ticket_description);
            dateText = itemView.findViewById(R.id.ticket_date);
            adminNoteText = itemView.findViewById(R.id.ticket_admin_note);
        }
    }
}
