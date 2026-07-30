package com.example.homeadmin.ui.management;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homeadmin.R;
import com.example.homeadmin.ui.helpSuppot.SupportTicketModel;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class GlobalSupportAdapter extends RecyclerView.Adapter<GlobalSupportAdapter.ViewHolder> {

    private List<SupportTicketModel> ticketList;

    public GlobalSupportAdapter(List<SupportTicketModel> ticketList) {
        this.ticketList = ticketList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.global_support_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SupportTicketModel model = ticketList.get(position);
        holder.ticketIdText.setText("Ticket #" + model.getTicketId());
        holder.ticketStatusText.setText(model.getStatus());
        holder.ticketIssueText.setText(model.getIssue_description());

        if (model.getTimestamp() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault());
            holder.ticketTimeText.setText(sdf.format(model.getTimestamp()));
        }

        // Fetch user details from Firestore
        FirebaseFirestore.getInstance().collection("USER").document(model.getUserId())
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String name = document.getString("Full Name");
                            String phone = document.getString("mobile");
                            holder.ticketUserText.setText("User: " + (name != null ? name : "Unknown"));
                            holder.ticketPhoneText.setText("Phone: " + (phone != null ? phone : "N/A"));

                            if (phone != null) {
                                holder.callUserBtn.setVisibility(View.VISIBLE);
                                holder.callUserBtn.setOnClickListener(v -> {
                                    Intent intent = new Intent(Intent.ACTION_DIAL);
                                    intent.setData(Uri.parse("tel:" + phone));
                                    holder.itemView.getContext().startActivity(intent);
                                });
                            } else {
                                holder.callUserBtn.setVisibility(View.GONE);
                            }
                        } else {
                            holder.ticketUserText.setText("User ID: " + model.getUserId());
                            holder.ticketPhoneText.setText("Phone: N/A");
                            holder.callUserBtn.setVisibility(View.GONE);
                        }
                    }
                });

        if ("Resolved".equals(model.getStatus())) {
            holder.resolveBtn.setVisibility(View.GONE);
        } else {
            holder.resolveBtn.setVisibility(View.VISIBLE);
            holder.resolveBtn.setOnClickListener(v -> showResolveDialog(holder, model, position));
        }
    }

    private void showResolveDialog(ViewHolder holder, SupportTicketModel model, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
        builder.setTitle("Resolve Ticket #" + model.getTicketId());

        // Layout for dialog
        LinearLayout layout = new LinearLayout(holder.itemView.getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 20, 40, 20);

        final EditText resolutionInput = new EditText(holder.itemView.getContext());
        resolutionInput.setHint("Enter resolution details (e.g. Issue Fixed)");
        layout.addView(resolutionInput);

        // Quick action buttons
        TextView quickNotesLabel = new TextView(holder.itemView.getContext());
        quickNotesLabel.setText("Quick Notes:");
        quickNotesLabel.setPadding(0, 20, 0, 10);
        layout.addView(quickNotesLabel);

        String[] quickNotes = {"Issue Fixed", "Refund Processed", "Replacement Sent", "Talked to User"};
        LinearLayout buttonLayout = new LinearLayout(holder.itemView.getContext());
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
        buttonLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        
        // Using a Flow-like logic or just a horizontal scroll/grid if many, 
        // but for 4 small ones, simple buttons or a vertical list is safer.
        // Let's use a vertical list for clarity in a simple dialog.
        for (String note : quickNotes) {
            Button btn = new Button(holder.itemView.getContext(), null, android.R.attr.buttonStyleSmall);
            btn.setText(note);
            btn.setAllCaps(false);
            btn.setOnClickListener(v -> resolutionInput.setText(note));
            layout.addView(btn);
        }

        builder.setView(layout);

        builder.setPositiveButton("Submit & Resolve", (dialog, which) -> {
            String note = resolutionInput.getText().toString().trim();
            if (TextUtils.isEmpty(note)) {
                Toast.makeText(holder.itemView.getContext(), "Please enter resolution details", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseFirestore.getInstance().collection("SUPPORT_TICKETS")
                    .document(model.getTicketId())
                    .update("status", "Resolved", "resolution_note", note)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            model.setStatus("Resolved");
                            notifyItemChanged(position);
                            Toast.makeText(holder.itemView.getContext(), "Ticket Resolved: " + note, Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    @Override
    public int getItemCount() {
        return ticketList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView ticketIdText, ticketStatusText, ticketUserText, ticketIssueText, ticketTimeText, ticketPhoneText;
        Button resolveBtn, callUserBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ticketIdText = itemView.findViewById(R.id.ticketIdText);
            ticketStatusText = itemView.findViewById(R.id.ticketStatusText);
            ticketUserText = itemView.findViewById(R.id.ticketUserText);
            ticketIssueText = itemView.findViewById(R.id.ticketIssueText);
            ticketTimeText = itemView.findViewById(R.id.ticketTimeText);
            ticketPhoneText = itemView.findViewById(R.id.ticketPhoneText);
            resolveBtn = itemView.findViewById(R.id.resolveBtn);
            callUserBtn = itemView.findViewById(R.id.callUserBtn);
        }
    }
}
