package com.example.homeadmin.ui.home2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class Home2Adapter extends RecyclerView.Adapter<Home2Adapter.ViewHolder>{

    private final List<DataModel> dataModelList;

    public Home2Adapter(List<DataModel> dataModelList) {
        this.dataModelList = dataModelList;
    }




    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DataModel dataModel = dataModelList.get(position);
        holder.textViewName.setText(dataModel.getName());
        holder.textViewEmail.setText(dataModel.getEmail());
        // Bind other data to the view holder

    }

    @Override
    public int getItemCount() {
        return dataModelList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView textViewName;
        private final TextView textViewEmail;
        // Add other views as needed

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(android.R.id.text1);
            textViewEmail = itemView.findViewById(android.R.id.text2);
            // Initialize other views as needed
        }
    }
}
