package com.example.deliveryboy.callDailer;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deliveryboy.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RecentCallsFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView emptyView;

    public RecentCallsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recent_calls, container, false);
        recyclerView = view.findViewById(R.id.recent_calls_recycler_view);
        emptyView = view.findViewById(R.id.empty_view_recent_calls);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        loadRecentCalls();

        return view;
    }

    private void loadRecentCalls() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
            List<CallLogItem> callLogList = getCallLog();
            if (callLogList.isEmpty()) {
                emptyView.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                emptyView.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                CallLogAdapter adapter = new CallLogAdapter(callLogList);
                recyclerView.setAdapter(adapter);
            }
        } else {
            emptyView.setText("कॉल लॉग पढ़ने की अनुमति नहीं है।");
            emptyView.setVisibility(View.VISIBLE);
        }
    }

    private List<CallLogItem> getCallLog() {
        List<CallLogItem> list = new ArrayList<>();
        ContentResolver contentResolver = getContext().getContentResolver();
        Cursor cursor = contentResolver.query(CallLog.Calls.CONTENT_URI, null, null, null, CallLog.Calls.DATE + " DESC");

        if (cursor != null && cursor.moveToFirst()) {
            int numberIndex = cursor.getColumnIndex(CallLog.Calls.NUMBER);
            int nameIndex = cursor.getColumnIndex(CallLog.Calls.CACHED_NAME);
            int dateIndex = cursor.getColumnIndex(CallLog.Calls.DATE);
            int durationIndex = cursor.getColumnIndex(CallLog.Calls.DURATION);
            int typeIndex = cursor.getColumnIndex(CallLog.Calls.TYPE);

            do {
                String number = cursor.getString(numberIndex);
                String name = cursor.getString(nameIndex);
                long date = cursor.getLong(dateIndex);
                int duration = cursor.getInt(durationIndex);
                int type = cursor.getInt(typeIndex);

                list.add(new CallLogItem(name, number, new Date(date), duration, type));
            } while (cursor.moveToNext());

            cursor.close();
        }
        return list;
    }

    // CallLogItem डेटा मॉडल
    private static class CallLogItem {
        String name;
        String number;
        Date date;
        int duration;
        int type;

        public CallLogItem(String name, String number, Date date, int duration, int type) {
            this.name = (name == null || name.isEmpty()) ? "अज्ञात" : name;
            this.number = number;
            this.date = date;
            this.duration = duration;
            this.type = type;
        }
    }

    // RecyclerView.Adapter
    private static class CallLogAdapter extends RecyclerView.Adapter<CallLogAdapter.ViewHolder> {
        private final List<CallLogItem> callLogList;

        public CallLogAdapter(List<CallLogItem> callLogList) {
            this.callLogList = callLogList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_call_log, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            CallLogItem item = callLogList.get(position);
            holder.callerName.setText(item.name);
            holder.callerNumber.setText(item.number);
            holder.callDate.setText(item.date.toString());
            // आप कॉल का प्रकार (type) भी दिखा सकते हैं
        }

        @Override
        public int getItemCount() {
            return callLogList.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView callerName;
            TextView callerNumber;
            TextView callDate;

            ViewHolder(View itemView) {
                super(itemView);
                callerName = itemView.findViewById(R.id.callerName);
                callerNumber = itemView.findViewById(R.id.callerNumber);
                callDate = itemView.findViewById(R.id.callDate);
            }
        }
    }
}
