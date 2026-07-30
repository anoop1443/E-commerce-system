package com.example.homeadmin.ui.details;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homeadmin.R;

import java.util.List;

/**
 * यह RecyclerView के लिए एक कस्टम एडाप्टर है जो विशेष रूप से
 * product_specification_item_edit_layout.xml लेआउट का उपयोग करता है
 * ताकि उपयोगकर्ता डेटा इनपुट और संपादित कर सकें।
 */
/**
 * यह RecyclerView के लिए एक कस्टम एडाप्टर है जो विशेष रूप से
 * product_specification_item_edit_layout.xml लेआउट का उपयोग करता है
 * ताकि उपयोगकर्ता डेटा इनपुट और संपादित कर सकें।
 */
public class productSpecificationEditAdapter extends RecyclerView.Adapter<productSpecificationEditAdapter.SpecificationEditViewHolder> {

    private List<productSpecificationEditModel> productSpecificationEditModels;

    /**
     * एडाप्टर कंस्ट्रक्टर
     * @param productSpecificationModelList डेटा मॉडल की सूची
     */
    public productSpecificationEditAdapter(List<productSpecificationEditModel> productSpecificationModelList) {
        this.productSpecificationEditModels = productSpecificationModelList;
    }

    @NonNull
    @Override
    public SpecificationEditViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_specification_item_edit_layout, parent, false);
        return new SpecificationEditViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SpecificationEditViewHolder holder, int position) {
        productSpecificationEditModel model = productSpecificationEditModels.get(position);
        holder.setFeatures(model.getFeatureName(), model.getFeatureValue(), position);
    }

    @Override
    public int getItemCount() {
        return productSpecificationEditModels.size();
    }




    /**
     * डेटा इनपुट/संपादित करने के लिए ViewHolder
     */
    public class SpecificationEditViewHolder extends RecyclerView.ViewHolder {
        EditText featureName, featureValue;

        public SpecificationEditViewHolder(@NonNull View itemView) {
            super(itemView);
            featureName = itemView.findViewById(R.id.editText_feature_name);
            featureValue = itemView.findViewById(R.id.editText_feature_value);
        }

        public void setFeatures(String featureTitle, String featureDetails, final int position) {
            featureName.setText(featureTitle);
            featureValue.setText(featureDetails);

            // TextWatcher जोड़ें ताकि EditText में बदलाव होने पर मॉडल तुरंत अपडेट हो जाए।
            featureName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    productSpecificationEditModels.get(position).setFeatureName(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });

            featureValue.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    productSpecificationEditModels.get(position).setFeatureValue(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }
    }
}
