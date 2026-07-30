package com.example.deliveryboy.withdrawal;

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

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    private List<TransactionRecord> transactionList;
    private Context context;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());

    public TransactionAdapter(Context context, List<TransactionRecord> transactionList) {
        this.context = context;
        this.transactionList = transactionList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TransactionRecord txn = transactionList.get(position);

        holder.amount.setText("₹ " + String.format(Locale.getDefault(), "%.2f", txn.getAmount()));
        holder.id.setText("ID: #" + txn.getTransactionId());
        
        if (txn.getRequestDate() != null) {
            holder.date.setText(sdf.format(txn.getRequestDate().toDate()));
        }

        String status = txn.getStatus() != null ? txn.getStatus() : "PENDING";
        holder.status.setText(status.toUpperCase());

        // Status Styling
        switch (status.toLowerCase()) {
            case "approved":
            case "success":
                holder.status.getBackground().setTint(0xFFE8F5E9);
                holder.status.setTextColor(0xFF2E7D32);
                holder.utrLabel.setVisibility(View.VISIBLE);
                holder.utrValue.setVisibility(View.VISIBLE);
                holder.utrValue.setText(txn.getUtrId() != null ? txn.getUtrId() : "Processing...");
                holder.remark.setVisibility(View.GONE);
                break;
            case "rejected":
            case "failed":
                holder.status.getBackground().setTint(0xFFFFEBEE);
                holder.status.setTextColor(0xFFC62828);
                holder.utrLabel.setVisibility(View.GONE);
                holder.utrValue.setVisibility(View.GONE);
                if (txn.getAdminRemark() != null && !txn.getAdminRemark().isEmpty()) {
                    holder.remark.setVisibility(View.VISIBLE);
                    holder.remark.setText("Reason: " + txn.getAdminRemark());
                } else {
                    holder.remark.setVisibility(View.GONE);
                }
                break;
            default: // Pending
                holder.status.getBackground().setTint(0xFFFFF3E0);
                holder.status.setTextColor(0xFFE65100);
                holder.utrLabel.setVisibility(View.GONE);
                holder.utrValue.setVisibility(View.GONE);
                holder.remark.setVisibility(View.GONE);
                break;
        }

        // Bank Details
        String bankInfo = "Bank: " + (txn.getBankName() != null ? txn.getBankName() : "N/A") +
                "\nAcc: " + maskAccount(txn.getAccountNumber()) +
                "\nIFSC: " + (txn.getIfscCode() != null ? txn.getIfscCode() : "N/A") +
                "\nHolder: " + (txn.getHolderName() != null ? txn.getHolderName() : "N/A");
        holder.bankInfo.setText(bankInfo);

        // Click to expand
        holder.itemView.setOnClickListener(v -> {
            boolean isVisible = holder.detailsContainer.getVisibility() == View.VISIBLE;
            holder.detailsContainer.setVisibility(isVisible ? View.GONE : View.VISIBLE);
        });
    }

    private String maskAccount(String acc) {
        if (acc == null || acc.length() < 4) return "****";
        return "******" + acc.substring(acc.length() - 4);
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView amount, status, id, date, bankInfo, utrLabel, utrValue, remark;
        View detailsContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            amount = itemView.findViewById(R.id.txn_amount);
            status = itemView.findViewById(R.id.txn_status);
            id = itemView.findViewById(R.id.txn_id);
            date = itemView.findViewById(R.id.txn_date);
            bankInfo = itemView.findViewById(R.id.txn_bank_info);
            utrLabel = itemView.findViewById(R.id.txn_utr_label);
            utrValue = itemView.findViewById(R.id.txn_utr_value);
            remark = itemView.findViewById(R.id.txn_remark);
            detailsContainer = itemView.findViewById(R.id.txn_details_container);
        }
    }
}