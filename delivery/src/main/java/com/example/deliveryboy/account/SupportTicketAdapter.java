package com.example.deliveryboy.account;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.deliveryboy.R;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SupportTicketAdapter extends RecyclerView.Adapter<SupportTicketAdapter.ViewHolder> {

    private List<Map<String, Object>> ticketList;
    private Context context;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

    public SupportTicketAdapter(Context context, List<Map<String, Object>> ticketList) {
        this.context = context;
        this.ticketList = ticketList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_support_ticket, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String, Object> ticket = ticketList.get(position);

        holder.id.setText("Ticket #" + ticket.get("ticketId"));
        holder.reason.setText("Reason: " + ticket.get("reason"));
        
        Object createdDate = ticket.get("createdDate");
        if (createdDate instanceof com.google.firebase.Timestamp) {
            holder.date.setText("Raised on: " + sdf.format(((com.google.firebase.Timestamp) createdDate).toDate()));
        }

        String status = (String) ticket.get("status");
        if (status == null) status = "OPEN";
        holder.status.setText(status.toUpperCase());

        // Status Styling
        switch (status.toUpperCase()) {
            case "OPEN":
                holder.status.getBackground().setTint(0xFFFFF3E0);
                holder.status.setTextColor(0xFFE65100);
                break;
            case "RESOLVED":
                holder.status.getBackground().setTint(0xFFE8F5E9);
                holder.status.setTextColor(0xFF2E7D32);
                break;
            case "CLOSED":
                holder.status.getBackground().setTint(0xFFF5F5F5);
                holder.status.setTextColor(0xFF757575);
                break;
        }

        String adminNote = (String) ticket.get("adminNote");
        if (adminNote != null && !adminNote.isEmpty()) {
            holder.divider.setVisibility(View.VISIBLE);
            holder.adminNote.setVisibility(View.VISIBLE);
            holder.adminNote.setText("Admin Note: " + adminNote);
        } else {
            holder.divider.setVisibility(View.GONE);
            holder.adminNote.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return ticketList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView id, status, reason, date, adminNote;
        View divider;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            id = itemView.findViewById(R.id.ticket_id_text);
            status = itemView.findViewById(R.id.ticket_status_badge);
            reason = itemView.findViewById(R.id.ticket_reason_text);
            date = itemView.findViewById(R.id.ticket_date_text);
            adminNote = itemView.findViewById(R.id.ticket_admin_note);
            divider = itemView.findViewById(R.id.ticket_divider);
        }
    }
}