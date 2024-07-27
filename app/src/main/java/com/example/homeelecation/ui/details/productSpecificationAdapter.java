package com.example.homeelecation.ui.details;


import static com.example.homeelecation.ui.details.productSpecificationModel.GENERAL_TEXT_VIEW;
import static com.example.homeelecation.ui.details.productSpecificationModel.MORE_INFO_DETAILS;
import static com.example.homeelecation.ui.details.productSpecificationModel.SPECIFICATION_DETAILS;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homeelecation.R;

import java.util.List;

public class
productSpecificationAdapter extends RecyclerView.Adapter {

    List<productSpecificationModel> productSpecificationModelList;
    public productSpecificationAdapter(List<productSpecificationModel> productSpecificationModelList) {
        this.productSpecificationModelList = productSpecificationModelList;
    }


    @Override
    public int getItemViewType(int position) {
        switch (productSpecificationModelList.get(position).getViewType()){
            case 0:

                return GENERAL_TEXT_VIEW;

            case 1:
                return SPECIFICATION_DETAILS;

            case 2:
                return MORE_INFO_DETAILS;

            default:

                return -1;
        }
    }



    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       switch (viewType){
           case GENERAL_TEXT_VIEW:
               View text = LayoutInflater.from(parent.getContext()).inflate(R.layout.title_one_text_layout,parent,false);
               return new  Textonly(text);
           case SPECIFICATION_DETAILS:
               View specification = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_specification_item_layout,parent,false);
               return new Specification(specification);

           case MORE_INFO_DETAILS:
               View more = LayoutInflater.from(parent.getContext()).inflate(R.layout.more_info_item_layout,parent,false);
               return new MoreInfo(more);
           default:
               return null;

       }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        switch (productSpecificationModelList.get(position).getViewType()){
            case GENERAL_TEXT_VIEW:
                String only = productSpecificationModelList.get(position).getGeneraltext();
                ((Textonly)holder).setonly(only);
                break;

            case SPECIFICATION_DETAILS:
                String feature = productSpecificationModelList.get(position).getFeatureName();
                String featurevalue = productSpecificationModelList.get(position).getFeatureValue();
                ((Specification)holder).setFeatures(feature,featurevalue);
                break;

            case MORE_INFO_DETAILS:
                String manufacture = productSpecificationModelList.get(position).getManufactureText();
                String manufactureAdd = productSpecificationModelList.get(position).getManufactureAdd();
                ((MoreInfo)holder).setmore(manufacture,manufactureAdd);
                break;
            default:
                return;

        }

    }

    @Override
    public int getItemCount() {
        return productSpecificationModelList.size();
    }

    public class Textonly extends RecyclerView.ViewHolder{
        TextView general;

        public Textonly(@NonNull View itemView) {
            super(itemView);
            general = itemView.findViewById(R.id.General_textView);
        }
        private void setonly(String title){
            general.setText(title);
        }
    }

    public class Specification extends RecyclerView.ViewHolder {

        TextView featureName,featureValue;


        public Specification(@NonNull View itemView) {
            super(itemView);

            featureName =itemView.findViewById(R.id.feature_name);
            featureValue = itemView.findViewById(R.id.feature_value);
        }
        private void setFeatures(String featureTitle,String featureDetails ){
            featureName.setText(featureTitle);
            featureValue.setText(featureDetails);
        }
    }

    public class MoreInfo extends RecyclerView.ViewHolder{
        TextView  Manufacture,ManufactureAdd;

        public MoreInfo(@NonNull View itemView) {
            super(itemView);
            Manufacture = itemView.findViewById(R.id.manufacturText);
            ManufactureAdd = itemView.findViewById(R.id.manufacturAddtext);
        }
        private void setmore(String manufaturetitle,String manufatureAdd){
            Manufacture.setText(manufaturetitle);
            ManufactureAdd.setText(manufatureAdd);
        }
    }
}
