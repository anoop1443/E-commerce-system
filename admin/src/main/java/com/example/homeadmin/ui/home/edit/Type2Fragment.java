package com.example.homeadmin.ui.home.edit;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homeadmin.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Type2Fragment extends Fragment {

    private Button selectColorButton2;
    private TextView colorHexTextView2;
    private EditText titleEt, searchEt;
    private RecyclerView recyclerView;
    private HorizontalProductEditScrollAdapter adapter;
    private List<HorizontalProductScrollModel> productList;
    private FirebaseFirestore db;
    private Spinner mySpinner;

    // Preview views
    private TextView previewTitle;
    private ConstraintLayout previewBackground;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.upload_layout_type2_3, container, false);

        db = FirebaseFirestore.getInstance();
        productList = new ArrayList<>();
        adapter = new HorizontalProductEditScrollAdapter(productList, getContext());

        // Initialize UI components
        selectColorButton2 = view.findViewById(R.id.select_color_button_2);
        colorHexTextView2 = view.findViewById(R.id.color_hex_text_view_2);
        titleEt = view.findViewById(R.id.title_edit_text);
        searchEt = view.findViewById(R.id.product_search_edit_text);
        recyclerView = view.findViewById(R.id.product_selection_recycler_view);
        mySpinner = view.findViewById(R.id.my_spinner);

        // Preview initialization
        View previewLayout = view.findViewById(R.id.included_preview_layout);
        previewTitle = previewLayout.findViewById(R.id.horizontal_scroll_layout_titel);
        // The root ID 'horizontal_scroll_layout_constraintlayout' is overridden by 'included_preview_layout'
        if (previewLayout instanceof ConstraintLayout) {
            previewBackground = (ConstraintLayout) previewLayout;
        } else {
            previewBackground = previewLayout.findViewById(R.id.horizontal_scroll_layout_constraintlayout);
        }

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(adapter);

        // Setup real-time preview for title
        titleEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                previewTitle.setText(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Setup search filtering
        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        selectColorButton2.setOnClickListener(v -> showColorPicker());

        view.findViewById(R.id.upload_type2_button).setOnClickListener(v -> uploadData());
        view.findViewById(R.id.upload_type3_button).setVisibility(View.GONE);

        loadCategories();

        return view;
    }

    private void showColorPicker() {
        final String[] colors = {"#e0f7fa", "#fff8e1", "#fce4ec", "#c8e6c9", "#e3f2fd", "#ADD8E6", "#FFC0CB", "#FFFFFF", "CUSTOM"};
        final String[] colorNames = {"Light Cyan", "Light Yellow", "Light Pink", "Light Green", "Light Blue", "Sky Blue", "Pink", "White", "Custom Hex Code..."};

        ColorSelectionAdapter colorAdapter = new ColorSelectionAdapter(getContext(), colorNames, colors);

        new com.google.android.material.dialog.MaterialAlertDialogBuilder(getContext())
                .setTitle("Select Background Color")
                .setAdapter(colorAdapter, (dialog, which) -> {
                    if (which == colors.length - 1) {
                        final EditText customColorInput = new EditText(getContext());
                        customColorInput.setHint("#RRGGBB");
                        new com.google.android.material.dialog.MaterialAlertDialogBuilder(getContext())
                                .setTitle("Enter Custom Hex Code")
                                .setView(customColorInput)
                                .setPositiveButton("OK", (dialog2, which2) -> {
                                    String hex = customColorInput.getText().toString().trim();
                                    if (!hex.startsWith("#")) hex = "#" + hex;
                                    try {
                                        previewBackground.setBackgroundColor(Color.parseColor(hex));
                                        colorHexTextView2.setText(hex);
                                    } catch (Exception e) {
                                        Toast.makeText(getContext(), "Invalid Color Code!", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .setNegativeButton("Cancel", null)
                                .show();
                    } else {
                        String selectedColorHex = colors[which];
                        colorHexTextView2.setText(selectedColorHex);
                        previewBackground.setBackgroundColor(Color.parseColor(selectedColorHex));
                    }
                })
                .show();
    }

    private void loadProducts(String category) {
        Query productsQuery = db.collection("Product_Details");

        if (!"Home".equals(category)) {
            productsQuery = productsQuery.whereEqualTo("category", category);
        } else {
            productsQuery = productsQuery.orderBy("fastTime", Query.Direction.DESCENDING);
        }

        productsQuery.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        productList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            List<String> images = (List<String>) document.get("imageUrls");
                            String firstImage = (images != null && !images.isEmpty()) ? images.get(0) : "";

                            HorizontalProductScrollModel model = new HorizontalProductScrollModel(document.getId(), firstImage,
                                    document.getString("productTitle"), document.getString("productDescription"), String.valueOf(document.get("productPrise")));
                            productList.add(model);
                        }
                        adapter.updateFullList(productList);
                    } else {
                        Log.w("FirestoreError", "Error getting documents.", task.getException());
                        Toast.makeText(getContext(), "Error fetching products.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadCategories() {
        db.collection("CATEGORY").orderBy("index").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<String> categoryList = new ArrayList<>();
                categoryList.add("Home");
                for (QueryDocumentSnapshot document : task.getResult()) {
                    categoryList.add(document.getString("categoryName"));
                }
                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categoryList);
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mySpinner.setAdapter(spinnerAdapter);

                mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String selectedCategory = parent.getItemAtPosition(position).toString();
                        loadProducts(selectedCategory);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        loadProducts("Home");
                    }
                });

            } else {
                Log.w("FirestoreError", "Error getting categories.", task.getException());
            }
        });
    }

    private void uploadData() {
        Bundle args = getArguments();
        String target = args != null ? args.getString("TARGET") : "HOMEPAGE";
        String categoryId = args != null ? args.getString("CATEGORY_ID") : null;

        List<String> selectedDocumentIds = new ArrayList<>();
        for (HorizontalProductScrollModel item : adapter.getSelectedProducts()) {
            selectedDocumentIds.add(item.getDocumentId());
        }

        if (selectedDocumentIds.isEmpty()) {
            Toast.makeText(getContext(), "Please select at least one product!", Toast.LENGTH_SHORT).show();
            return;
        }

        CollectionReference targetCollection;
        if ("HOMEPAGE".equals(target)) {
            targetCollection = db.collection("HOMEPAGE");
        } else if ("CATEGORY_ACTIVITY".equals(target) && categoryId != null) {
            targetCollection = db.collection("CATEGORY").document(categoryId).collection("CATEGORY_ACTIVITY");
        } else {
            Toast.makeText(getContext(), "Invalid target!", Toast.LENGTH_SHORT).show();
            return;
        }

        targetCollection.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                long index = task.getResult().size();
                Map<String, Object> data = new HashMap<>();
                data.put("view_type", 2L);
                data.put("layout_title", titleEt.getText().toString());
                data.put("layout_background", colorHexTextView2.getText().toString());
                data.put("products", selectedDocumentIds);
                data.put("index", index);

                targetCollection.add(data)
                        .addOnSuccessListener(documentReference -> {
                            Toast.makeText(getContext(), "Data Uploaded Successfully!", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Error uploading data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(getContext(), "Error getting collection size: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
