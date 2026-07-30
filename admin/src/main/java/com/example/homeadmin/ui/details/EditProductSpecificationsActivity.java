package com.example.homeadmin.ui.details;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homeadmin.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class EditProductSpecificationsActivity extends AppCompatActivity {

    private RecyclerView specificationRecyclerView,specificationRecyclerViewDialog;
    private Dialog addSpecificationDialog;
    private Button saveButton;
    private Button addNewItemButton;
    private productSpecificationEditAdapter adapter;
    private List<productSpecificationEditModel> productSpecificationEditModels;

    // This ID will be used to save data to Firebase.
    private String productId = "NMjNKdmWgAdkn01LabJl"; // Replace with your actual product ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product_specifications);

        // 1. Get RecyclerView and Buttons from layout
        specificationRecyclerView = findViewById(R.id.product_specification_recycler_view);
        saveButton = findViewById(R.id.save_button);
        addNewItemButton = findViewById(R.id.add_new_item_button);

        // 2. Create a list of data models
        productSpecificationEditModels = new ArrayList<>();
        // Add some example data
        //specificationList.add(new productSpecificationModel(productSpecificationModel.SPECIFICATION_DETAILS,"",""));
        productSpecificationEditModels.add(new productSpecificationEditModel(productSpecificationEditModel.SPECIFICATION_DETAILS, "रंग", "काला"));
        productSpecificationEditModels.add(new productSpecificationEditModel(1, "वजन", "250g"));
        productSpecificationEditModels.add(new productSpecificationEditModel(1, "बैटरी लाइफ", "10 घंटे"));

        //test code
        // Initialize the dialog
//        addSpecificationDialog = new Dialog(this);
//        addSpecificationDialog.setContentView(R.layout.dialog_add_specification);
//        addSpecificationDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        addSpecificationDialog.setCancelable(true);
//
//        // Find views from the dialog layout
//        specificationRecyclerViewDialog = addSpecificationDialog.findViewById(R.id.specifications_recyclerview_dialog);
//        ImageButton addFieldButton = addSpecificationDialog.findViewById(R.id.add_field_button);
//        ImageButton removeFieldButton = addSpecificationDialog.findViewById(R.id.remove_field_button);
//        EditText featureNameEditText = addSpecificationDialog.findViewById(R.id.editText_feature_name);
//        EditText featureValueEditText = addSpecificationDialog.findViewById(R.id.editText_feature_value);
//        Button cancel = addSpecificationDialog.findViewById(R.id.specification_dialog_cancelBtn);
//        Button save = addSpecificationDialog.findViewById(R.id.specification_dialog_saveBtn);
//
//        // Setup RecyclerView for specifications
//        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this);
//        layoutManager2.setOrientation(LinearLayoutManager.VERTICAL);
//        specificationRecyclerViewDialog.setLayoutManager(layoutManager2);
//
//
//        // Add initial specification field
//        //specificationList.add(new productSpecificationModel(1, "featureName", "featureValue"));
//        adapter = new productSpecificationEditAdapter(specificationList);
//        specificationRecyclerViewDialog.setAdapter(adapter);
//        adapter.notifyDataSetChanged();
//        // Listener for Add Field Button
//        addFieldButton.setOnClickListener(v -> {
//            // Add a new empty specification field
//            specificationList.add(new productSpecificationModel(1, "", ""));
//            adapter.notifyItemInserted(specificationList.size() - 1);
//            specificationRecyclerViewDialog.scrollToPosition(specificationList.size() - 1);
//        });
//
//        // Listener for Remove Field Button
//        removeFieldButton.setOnClickListener(v -> {
//            if (specificationList.size() > 1) {
//                // Remove the last specification field
//                specificationList.remove(specificationList.size() - 1);
//                adapter.notifyItemRemoved(specificationList.size());
//            } else {
//                Toast.makeText(this, "कम से कम एक फ़ील्ड आवश्यक है।", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        save.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String featureName = featureNameEditText.getText().toString().trim();
//                String featureValue = featureValueEditText.getText().toString().trim();
//                // if (!featureName.isEmpty() && !featureValue.isEmpty()){
//                specificationList.add(new productSpecificationModel(1, "",""));
//                //specificationList.add(new productSpecificationModel(1, featureName,featureValue));
//                adapter.notifyItemInserted(specificationList.size()-1);
//                specificationRecyclerView.smoothScrollToPosition(specificationList.size()-1);
//                Toast.makeText(EditProductSpecificationsActivity.this, "List add successfully", Toast.LENGTH_SHORT).show();
//                addSpecificationDialog.dismiss();
//                adapter.notifyDataSetChanged();
//
//               }else {
//                    Toast.makeText(EditProductSpecificationsActivity.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//
//        cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                addSpecificationDialog.dismiss();
//            }
//        });
//test code


        // 3. Initialize the adapter
        adapter = new productSpecificationEditAdapter(productSpecificationEditModels);

        // 4. Set the layout manager for the RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        specificationRecyclerView.setLayoutManager(layoutManager);

        // 5. Attach the adapter to the RecyclerView
        specificationRecyclerView.setAdapter(adapter);

        // 6. Set an onClickListener for the Save button
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call the saveDataToFirebase() method
                saveDataToFirebase(productId, productSpecificationEditModels);
            }
        });

        addNewItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSpecificationDialog.show();
            }
        });

        // 7. Set an onClickListener to show a dialog and add a new item to the list
        addNewItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an AlertDialog.Builder
                AlertDialog.Builder builder = new AlertDialog.Builder(EditProductSpecificationsActivity.this);
                builder.setTitle("नई विशेषता जोड़ें");

                // Inflate the custom layout for the dialog
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_add_specification, null);
                builder.setView(dialogView);

                final EditText featureNameEditText = dialogView.findViewById(R.id.feature_name_input);
                final EditText featureValueEditText = dialogView.findViewById(R.id.feature_value_input);


//
                // Set up the Add button
                builder.setPositiveButton("जोड़ें", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String featureName = featureNameEditText.getText().toString().trim();
                        String featureValue = featureValueEditText.getText().toString().trim();

                        if (!featureName.isEmpty() && !featureValue.isEmpty()) {
                            // Add a new item to the list with the user's input
                            productSpecificationEditModels.add(new productSpecificationEditModel(1, featureName, featureValue));
                            // Notify the adapter that the dataset has changed
                            adapter.notifyItemInserted(productSpecificationEditModels.size() - 1);
                            // Scroll to the new item
                            specificationRecyclerView.smoothScrollToPosition(productSpecificationEditModels.size() - 1);
                            Toast.makeText(EditProductSpecificationsActivity.this, "विशेषता जोड़ी गई", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(EditProductSpecificationsActivity.this, "कृपया दोनों फ़ील्ड भरें", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                // Set up the Cancel button
                builder.setNegativeButton("रद्द करें", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                // Show the dialog
                builder.show();
            }
        });
    }

    /**
     * फ़ायरबेस में डेटा सहेजता है।
     * @param productId जिस उत्पाद से विनिर्देश जुड़े हैं, उसका आईडी।
     */
    public void saveDataToFirebase(String productId, List<productSpecificationEditModel> productSpecificationEditModels) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> productDetailsData = new HashMap<>();
        for (int x =1 ; x < productSpecificationEditModels.size()+1;x++){
            productDetailsData.put("featureName"+x,productSpecificationEditModels.get(x-1).getFeatureName());
            productDetailsData.put("featureValue"+x,productSpecificationEditModels.get(x-1).getFeatureValue());
        }
        productDetailsData.put("on_of_list",productSpecificationEditModels.size());

        db.collection("Product_Details").document(productId)
                .update("specifications", productDetailsData)
                .addOnSuccessListener(aVoid -> Toast.makeText(
                        // यहाँ Context को सीधे adapter से नहीं देना चाहिए।
                        // यह उदाहरण के लिए है, इसे Activity/Fragment से कॉल करें।
                        this, "डेटा सफलतापूर्वक सहेजा गया!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "त्रुटि: डेटा सहेजने में विफल रहा।", Toast.LENGTH_SHORT).show());
    }

}
