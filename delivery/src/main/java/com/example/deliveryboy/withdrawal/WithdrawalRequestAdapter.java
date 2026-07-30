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
import java.util.List;

public class WithdrawalRequestAdapter extends RecyclerView.Adapter<WithdrawalRequestAdapter.RequestViewHolder> {

    private Context context;
    private List<WithdrawalRequest> requestList;

    public WithdrawalRequestAdapter(Context context, List<WithdrawalRequest> requestList) {
        this.context = context;
        this.requestList = requestList;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_withdrawal_history, parent, false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        WithdrawalRequest request = requestList.get(position);
        holder.bind(request);
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public class RequestViewHolder extends RecyclerView.ViewHolder {

        private TextView amountTextView;
        private TextView statusTextView;
        private TextView timeTextView;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            amountTextView = itemView.findViewById(R.id.item_widhrawal_history_HistoryAmount);
            statusTextView = itemView.findViewById(R.id.item_widhrawal_history_HistoryStatus);
            timeTextView = itemView.findViewById(R.id.item_widhrawal_history_timeView);
        }

        public void bind(WithdrawalRequest request) {
            amountTextView.setText("Amount: ₹" + request.getAmount());
            statusTextView.setText("Status: " + request.getStatus());
            timeTextView.setText(request.getFormattedTimestamp());

            // Status के आधार पर text color बदलें
            if ("pending".equals(request.getStatus())) {
                statusTextView.setTextColor(Color.parseColor("#FFC107")); // Yellow
            } else if ("approved".equals(request.getStatus())) {
                statusTextView.setTextColor(Color.parseColor("#4CAF50")); // Green
            } else if ("rejected".equals(request.getStatus())) {
                statusTextView.setTextColor(Color.parseColor("#F44336")); // Red
            }
        }
    }
}
