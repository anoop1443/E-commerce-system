package com.example.homeadmin.ui.home.edit;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.homeadmin.R;
import com.example.homeadmin.ui.home2.Home3Model;
import com.example.homeadmin.ui.home2.Home3Repository;
import com.example.homeadmin.ui.slideshow.edit.AddAdActivity;
import com.example.homeadmin.ui.slideshow.edit.AddBannerActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManageHomeActivity extends AppCompatActivity implements ManageHomeAdapter.OnItemEditListener {

    private RecyclerView manageHomeRecyclerview;
    private Button saveButton, addButton;
    private FirebaseFirestore db;
    private ManageHomeAdapter adapter;
    private List<Home3Model> homepageModelList;
    private Home3Repository homeRepository;
    
    private String target = "HOMEPAGE";
    private String categoryId = null;
    private String collectionPath = "HOMEPAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_home);

        // Get Target Info from Intent
        if (getIntent().hasExtra("TARGET")) {
            target = getIntent().getStringExtra("TARGET");
            categoryId = getIntent().getStringExtra("CATEGORY_ID");
        }

        if ("CATEGORY_ACTIVITY".equals(target) && categoryId != null) {
            collectionPath = "CATEGORY/" + categoryId + "/CATEGORY_ACTIVITY";
        } else {
            collectionPath = "HOMEPAGE";
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            String title = "HOMEPAGE".equals(target) ? "Manage Home Layout" : "Manage Category Layout";
            getSupportActionBar().setTitle(title);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        db = FirebaseFirestore.getInstance();
        homeRepository = new Home3Repository();
        manageHomeRecyclerview = findViewById(R.id.manage_home_recyclerview);
        saveButton = findViewById(R.id.save_button);
        addButton = findViewById(R.id.add_button);

        homepageModelList = new ArrayList<>();
        adapter = new ManageHomeAdapter(homepageModelList, this);
        adapter.setOnItemEditListener(this);
        manageHomeRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        manageHomeRecyclerview.setAdapter(adapter);

        loadLayoutData();

        saveButton.setOnClickListener(v -> saveChanges());
        addButton.setOnClickListener(v -> {
            // This button navigates to HomeEditActivity, but since we're already managing a specific target,
            // we might want to return or just let the user go back to HomeEditActivity.
            // For now, let's keep it as is, or finish this activity so they go back to the previous one.
            finish(); 
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();
                Collections.swap(homepageModelList, fromPosition, toPosition);
                adapter.notifyItemMoved(fromPosition, toPosition);
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                // No swipe action
            }
        });
        itemTouchHelper.attachToRecyclerView(manageHomeRecyclerview);

        getOnBackPressedDispatcher().addCallback(this, new androidx.activity.OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (!adapter.getDeletedItems().isEmpty()) {
                    new MaterialAlertDialogBuilder(ManageHomeActivity.this)
                            .setTitle("Unsaved Changes")
                            .setMessage("You have unsaved changes. Are you sure you want to leave?")
                            .setPositiveButton("Leave", (dialog, which) -> {
                                setEnabled(false);
                                getOnBackPressedDispatcher().onBackPressed();
                            })
                            .setNegativeButton("Stay", null)
                            .show();
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });
    }

    private void loadLayoutData() {
        homeRepository.getLayout(collectionPath, new Home3Repository.OnDataLoadedListener<List<Home3Model>>() {
            @Override
            public void onSuccess(List<Home3Model> data) {
                homepageModelList.clear();
                homepageModelList.addAll(data);
                adapter.notifyDataSetChanged();
                updateEmptyState();
                
                // Fetch details for each section to show previews
                fetchContentDetailsForPreviews();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(ManageHomeActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                updateEmptyState();
            }
        });
    }

    private void fetchContentDetailsForPreviews() {
        for (int i = 0; i < homepageModelList.size(); i++) {
            Home3Model model = homepageModelList.get(i);
            int finalI = i;

            if (model.getType() == Home3Model.BANNER_SLIDER && model.getContentIds() != null) {
                List<com.example.homeadmin.ui.slideshow.SliderModel> sliderModels = new ArrayList<>();
                for (String id : model.getContentIds()) {
                    db.collection("banners").document(id).get().addOnSuccessListener(doc -> {
                        if (doc.exists()) {
                            sliderModels.add(new com.example.homeadmin.ui.slideshow.SliderModel(id, doc.getString("imageUrl"), doc.getString("backgroundColor")));
                            model.setSliderModelList(new ArrayList<>(sliderModels));
                            adapter.notifyItemChanged(finalI);
                        }
                    });
                }
            } else if (model.getType() == Home3Model.STRIP_AD_BANNER && model.getAdId() != null) {
                db.collection("ads").document(model.getAdId()).get().addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        model.setStripImage(doc.getString("imageUrl"));
                        adapter.notifyItemChanged(finalI);
                    }
                });
            } else if ((model.getType() == Home3Model.HORIZONTAL_PRODUCT || model.getType() == Home3Model.GRID_PRODUCT_VIEW) && model.getContentIds() != null) {
                List<com.example.homeadmin.ui.horizontal.HorizontalProductScrollModel> productModels = new ArrayList<>();
                for (String id : model.getContentIds()) {
                    db.collection("Product_Details").document(id).get().addOnSuccessListener(doc -> {
                        if (doc.exists()) {
                            List<String> images = (List<String>) doc.get("imageUrls");
                            String firstImage = (images != null && !images.isEmpty()) ? images.get(0) : "";
                            productModels.add(new com.example.homeadmin.ui.horizontal.HorizontalProductScrollModel(id, firstImage, doc.getString("productTitle"), "", ""));
                            model.setHorizontalproductscrollModelList(new ArrayList<>(productModels));
                            adapter.notifyItemChanged(finalI);
                        }
                    });
                }
            }
        }
    }

    private void updateEmptyState() {
        View emptyState = findViewById(R.id.empty_state);
        if (homepageModelList.isEmpty()) {
            emptyState.setVisibility(View.VISIBLE);
        } else {
            emptyState.setVisibility(View.GONE);
        }
    }

    private void saveChanges() {
        WriteBatch batch = db.batch();

        // Move deleted items to Trash
        for (Home3Model model : adapter.getDeletedItems()) {
            if (model.getHomeDocumentId() != null) {
                String label = "Section: " + (model.getTitel() != null ? model.getTitel() : "No Title");
                String previewUrl = null;
                if (model.getType() == Home3Model.BANNER_SLIDER && model.getSliderModelList() != null && !model.getSliderModelList().isEmpty()) {
                    previewUrl = model.getSliderModelList().get(0).getBanner();
                } else if (model.getType() == Home3Model.STRIP_AD_BANNER) {
                    previewUrl = model.getStripImage();
                }

                com.example.homeadmin.ui.trash.TrashManager.moveToTrash(
                        collectionPath,
                        model.getHomeDocumentId(),
                        "LAYOUT_SECTION",
                        label,
                        previewUrl,
                        null
                );
            }
        }

        // Update indices and other fields
        for (int i = 0; i < homepageModelList.size(); i++) {
            Home3Model model = homepageModelList.get(i);
            if (model.getHomeDocumentId() != null) {
                Map<String, Object> updates = new HashMap<>();
                updates.put("index", (long) i);

                if (model.getType() == Home3Model.BANNER_SLIDER) {
                    if (model.getContentIds() != null) {
                        updates.put("banners", model.getContentIds());
                    }
                } else if (model.getType() == Home3Model.STRIP_AD_BANNER) {
                    if (model.getAdId() != null) {
                        updates.put("ad_id", model.getAdId());
                    }
                } else if (model.getType() == Home3Model.HORIZONTAL_PRODUCT || model.getType() == Home3Model.GRID_PRODUCT_VIEW) {
                    if (model.getTitel() != null) {
                        updates.put("layout_title", model.getTitel());
                    }
                    if (model.getBackgoundcolor() != null) {
                        updates.put("layout_background", model.getBackgoundcolor());
                    }
                    if (model.getContentIds() != null) {
                        updates.put("products", model.getContentIds());
                    }
                }
                batch.set(db.collection(collectionPath).document(model.getHomeDocumentId()), updates, SetOptions.merge());
            }
        }

        batch.commit().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Changes saved successfully!", Toast.LENGTH_SHORT).show();
                adapter.getDeletedItems().clear(); // Clear the list of deleted items
            } else {
                String errorMessage = "Error saving changes!";
                if (task.getException() != null) {
                    errorMessage += ": " + task.getException().getMessage();
                }
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onEditClick(int position) {
        Home3Model model = homepageModelList.get(position);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Edit Section Details");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(48, 32, 48, 32);

        final com.google.android.material.card.MaterialCardView cardView = new com.google.android.material.card.MaterialCardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 450);
        cardParams.setMargins(0, 0, 0, 32);
        cardView.setLayoutParams(cardParams);
        cardView.setRadius(24f);
        cardView.setCardElevation(0f);
        cardView.setStrokeWidth(2);
        cardView.setStrokeColor(android.graphics.Color.LTGRAY);

        final ImageView previewImage = new ImageView(this);
        previewImage.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        previewImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        previewImage.setBackgroundColor(android.graphics.Color.parseColor("#F5F5F5"));
        cardView.addView(previewImage);
        
        if (model.getType() == Home3Model.BANNER_SLIDER) {
            layout.addView(cardView);
            if (model.getSliderModelList() != null && !model.getSliderModelList().isEmpty()) {
                Glide.with(this).load(model.getSliderModelList().get(0).getBanner()).into(previewImage);
            }
        } else if (model.getType() == Home3Model.STRIP_AD_BANNER) {
            layout.addView(cardView);
            if (model.getStripImage() != null) {
                Glide.with(this).load(model.getStripImage()).into(previewImage);
            }
        }

        final EditText titleInput = new EditText(this);
        titleInput.setHint("Section Title");
        titleInput.setText(model.getTitel());
        if (model.getType() == Home3Model.HORIZONTAL_PRODUCT || model.getType() == Home3Model.GRID_PRODUCT_VIEW) {
            layout.addView(titleInput);
        }

        final TextView colorPreview = new TextView(this);
        colorPreview.setText("Background Color");
        colorPreview.setPadding(16, 32, 16, 32);
        colorPreview.setGravity(android.view.Gravity.CENTER);
        colorPreview.setTextColor(android.graphics.Color.BLACK);
        if (model.getBackgoundcolor() != null && !model.getBackgoundcolor().isEmpty()) {
            try {
                colorPreview.setBackgroundColor(android.graphics.Color.parseColor(model.getBackgoundcolor()));
                colorPreview.setText(model.getBackgoundcolor());
            } catch (Exception ignored) {}
        }
        
        LinearLayout.LayoutParams previewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        previewParams.setMargins(0, 32, 0, 32);
        colorPreview.setLayoutParams(previewParams);

        Button chooseColorBtn = new Button(this);
        chooseColorBtn.setText("Choose Color");
        chooseColorBtn.setOnClickListener(v -> {
            final String[] colors = {"#e0f7fa", "#fff8e1", "#fce4ec", "#c8e6c9", "#e3f2fd", "#ADD8E6", "#FFC0CB", "#FFFFFF", "CUSTOM"};
            final String[] colorNames = {"Light Cyan", "Light Yellow", "Light Pink", "Light Green", "Light Blue", "Sky Blue", "Pink", "White", "Custom Hex Code..."};

            ColorSelectionAdapter colorAdapter = new ColorSelectionAdapter(this, colorNames, colors);

            new MaterialAlertDialogBuilder(this)
                .setTitle("Pick a color")
                .setAdapter(colorAdapter, (dialog, which) -> {
                    if (which == colors.length - 1) {
                        // Custom color input
                        final EditText customColorInput = new EditText(this);
                        customColorInput.setHint("#RRGGBB");
                        customColorInput.setInputType(android.text.InputType.TYPE_CLASS_TEXT);
                        
                        new MaterialAlertDialogBuilder(this)
                            .setTitle("Enter Custom Hex Code")
                            .setView(customColorInput)
                            .setPositiveButton("OK", (dialog2, which2) -> {
                                String hex = customColorInput.getText().toString().trim();
                                if (!hex.startsWith("#")) hex = "#" + hex;
                                try {
                                    int color = android.graphics.Color.parseColor(hex);
                                    colorPreview.setBackgroundColor(color);
                                    colorPreview.setText(hex);
                                    colorPreview.setTag(hex);
                                } catch (Exception e) {
                                    Toast.makeText(this, "Invalid Color Code!", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                    } else {
                        String selectedColor = colors[which];
                        colorPreview.setBackgroundColor(android.graphics.Color.parseColor(selectedColor));
                        colorPreview.setText(selectedColor);
                        colorPreview.setTag(selectedColor);
                    }
                })
                .show();
        });
        
        if (model.getType() == Home3Model.HORIZONTAL_PRODUCT || model.getType() == Home3Model.GRID_PRODUCT_VIEW) {
            layout.addView(colorPreview);
            layout.addView(chooseColorBtn);
        }

        Button changeContentBtn = new Button(this);
        String btnText = "Change Content";
        if (model.getType() == Home3Model.BANNER_SLIDER) btnText = "Change Banners";
        else if (model.getType() == Home3Model.STRIP_AD_BANNER) btnText = "Change Ad";
        else if (model.getType() == Home3Model.HORIZONTAL_PRODUCT || model.getType() == Home3Model.GRID_PRODUCT_VIEW) btnText = "Change Products";
        
        changeContentBtn.setText(btnText);
        changeContentBtn.setOnClickListener(v -> {
            openContentSelection(model, previewImage);
        });
        
        LinearLayout.LayoutParams contentBtnParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        contentBtnParams.setMargins(0, 32, 0, 0);
        changeContentBtn.setLayoutParams(contentBtnParams);
        layout.addView(changeContentBtn);

        builder.setView(layout);

        builder.setPositiveButton("Apply", (dialog, which) -> {
            model.setTitel(titleInput.getText().toString());
            if (colorPreview.getTag() != null) {
                model.setBackgoundcolor(colorPreview.getTag().toString());
            } else if (colorPreview.getText().toString().startsWith("#")) {
                model.setBackgoundcolor(colorPreview.getText().toString());
            }
            adapter.notifyItemChanged(position);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void showAddSectionDialog() {
        String[] types = {"Banner Slider", "Strip Ad Banner", "Horizontal Product List", "Grid Product List"};
        new MaterialAlertDialogBuilder(this)
                .setTitle("Add New Section")
                .setItems(types, (dialog, which) -> {
                    Home3Model newModel = null;
                    switch (which) {
                        case 0: // Banner Slider
                            newModel = new Home3Model(Home3Model.BANNER_SLIDER, "new_doc_" + System.currentTimeMillis(), new ArrayList<>());
                            break;
                        case 1: // Strip Ad
                            newModel = new Home3Model(Home3Model.STRIP_AD_BANNER, "new_doc_" + System.currentTimeMillis(), "", "", "#FFFFFF");
                            break;
                        case 2: // Horizontal
                            newModel = new Home3Model(Home3Model.HORIZONTAL_PRODUCT, "new_doc_" + System.currentTimeMillis(), "New Section", "#FFFFFF", new ArrayList<>(), new ArrayList<>());
                            break;
                        case 3: // Grid
                            newModel = new Home3Model(Home3Model.GRID_PRODUCT_VIEW, "new_doc_" + System.currentTimeMillis(), "New Section", "#FFFFFF", new ArrayList<>());
                            break;
                    }
                    if (newModel != null) {
                        homepageModelList.add(newModel);
                        adapter.notifyItemInserted(homepageModelList.size() - 1);
                        updateEmptyState();
                        // Open edit dialog for the new section
                        onEditClick(homepageModelList.size() - 1);
                    }
                })
                .show();
    }

    @Override
    public void onDeleteClick(int position) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Delete Section?")
                .setMessage("Are you sure you want to remove this section? Changes will be permanent after saving.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    adapter.deleteItem(position);
                    updateEmptyState();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onAddContentClick(int position) {
        Home3Model model = homepageModelList.get(position);
        openContentSelection(model, null);
    }

    private void openContentSelection(Home3Model model, ImageView previewImage) {
        if (model.getType() == Home3Model.BANNER_SLIDER) {
            showBannerSelectionDialog(model, previewImage);
        } else if (model.getType() == Home3Model.STRIP_AD_BANNER) {
            showAdSelectionDialog(model, previewImage);
        } else if (model.getType() == Home3Model.HORIZONTAL_PRODUCT || model.getType() == Home3Model.GRID_PRODUCT_VIEW) {
            showProductSelectionDialog(model);
        }
    }

    private void showBannerSelectionDialog(Home3Model model, ImageView previewImage) {
        db.collection("banners").get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<BannerModel> bannerList = new ArrayList<>();
            List<String> currentIds = model.getContentIds() != null ? model.getContentIds() : new ArrayList<>();
            for (com.google.firebase.firestore.QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                BannerModel banner = new BannerModel(doc.getId(), doc.getString("imageUrl"), doc.getString("backgroundColor"));
                if (currentIds.contains(doc.getId())) {
                    banner.setSelected(true);
                }
                bannerList.add(banner);
            }

            RecyclerView recyclerView = new RecyclerView(this);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            BannerSelectionAdapter selectionAdapter = new BannerSelectionAdapter(bannerList, this);
            recyclerView.setAdapter(selectionAdapter);

            LinearLayout dialogLayout = new LinearLayout(this);
            dialogLayout.setOrientation(LinearLayout.VERTICAL);
            dialogLayout.addView(recyclerView);

            Button manageBannersBtn = new Button(this);
            manageBannersBtn.setText("Manage All Banners (Add/Remove)");
            manageBannersBtn.setOnClickListener(v -> {
                startActivity(new Intent(this, AddBannerActivity.class));
            });
            dialogLayout.addView(manageBannersBtn);

            androidx.appcompat.app.AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                    .setTitle("Select Banners")
                    .setView(dialogLayout)
                    .setPositiveButton("Done", (d, which) -> {
                        List<String> selectedIds = new ArrayList<>();
                        List<com.example.homeadmin.ui.slideshow.SliderModel> newSliderList = new ArrayList<>();
                        for (BannerModel b : selectionAdapter.getSelectedBanners()) {
                            selectedIds.add(b.getDocumentId());
                            newSliderList.add(new com.example.homeadmin.ui.slideshow.SliderModel(b.getDocumentId(), b.getImageUrl(), b.getBackgroundColor()));
                        }
                        model.setContentIds(selectedIds);
                        model.setSliderModelList(newSliderList);
                        if (!newSliderList.isEmpty() && previewImage != null) {
                            Glide.with(this).load(newSliderList.get(0).getBanner()).into(previewImage);
                        }
                        Toast.makeText(this, "Banners updated locally. Click 'Save' to apply.", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancel", null)
                    .create();
            
            dialog.show();
            if (dialog.getWindow() != null) {
                dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            }
        });
    }

    private void showAdSelectionDialog(Home3Model model, ImageView previewImage) {
        db.collection("ads").get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<AdModel> adList = new ArrayList<>();
            String currentAdId = model.getAdId();
            for (com.google.firebase.firestore.QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                AdModel ad = new AdModel(doc.getId(), doc.getString("imageUrl"), doc.getString("backgroundColor"));
                adList.add(ad);
            }

            RecyclerView recyclerView = new RecyclerView(this);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            AdSelectionAdapter selectionAdapter = new AdSelectionAdapter(adList, this);
            selectionAdapter.setInitialSelection(currentAdId);
            recyclerView.setAdapter(selectionAdapter);

            LinearLayout dialogLayout = new LinearLayout(this);
            dialogLayout.setOrientation(LinearLayout.VERTICAL);
            dialogLayout.addView(recyclerView);

            Button manageAdsBtn = new Button(this);
            manageAdsBtn.setText("Manage All Ads (Add/Remove)");
            manageAdsBtn.setOnClickListener(v -> {
                startActivity(new Intent(this, AddAdActivity.class));
            });
            dialogLayout.addView(manageAdsBtn);

            new MaterialAlertDialogBuilder(this)
                    .setTitle("Select Ad")
                    .setView(dialogLayout)
                    .setPositiveButton("Done", (dialog, which) -> {
                        AdModel selected = selectionAdapter.getSelectedAd();
                        if (selected != null) {
                            model.setAdId(selected.getDocumentId());
                            model.setStripImage(selected.getImageUrl());
                            if (previewImage != null) {
                                Glide.with(this).load(selected.getImageUrl()).into(previewImage);
                            }
                            Toast.makeText(this, "Ad updated locally. Click 'Save' to apply.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    private void showProductSelectionDialog(Home3Model model) {
        db.collection("Product_Details").get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<HorizontalProductScrollModel> productList = new ArrayList<>();
            List<String> currentIds = model.getContentIds() != null ? model.getContentIds() : new ArrayList<>();
            
            for (com.google.firebase.firestore.QueryDocumentSnapshot document : queryDocumentSnapshots) {
                List<String> images = (List<String>) document.get("imageUrls");
                String firstImage = (images != null && !images.isEmpty()) ? images.get(0) : "";

                HorizontalProductScrollModel p = new HorizontalProductScrollModel(document.getId(), firstImage,
                        document.getString("productTitle"), document.getString("productDescription"), String.valueOf(document.get("productPrise")));
                
                if (currentIds.contains(document.getId())) {
                    p.setSelected(true);
                }
                productList.add(p);
            }

            View view = getLayoutInflater().inflate(R.layout.upload_layout_type2_3, null);
            // We need a simpler layout or hide unnecessary views for selection
            view.findViewById(R.id.select_color_button_2).setVisibility(View.GONE);
            view.findViewById(R.id.color_hex_text_view_2).setVisibility(View.GONE);
            view.findViewById(R.id.title_edit_text).setVisibility(View.GONE);
            view.findViewById(R.id.upload_type2_button).setVisibility(View.GONE);
            view.findViewById(R.id.upload_type3_button).setVisibility(View.GONE);
            view.findViewById(R.id.included_preview_layout).setVisibility(View.GONE);
            view.findViewById(R.id.my_spinner).setVisibility(View.GONE); // For now hide category spinner to simplify

            EditText searchEt = view.findViewById(R.id.product_search_edit_text);
            RecyclerView recyclerView = view.findViewById(R.id.product_selection_recycler_view);
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
            
            HorizontalProductEditScrollAdapter selectionAdapter = new HorizontalProductEditScrollAdapter(productList, this);
            selectionAdapter.updateFullList(productList);
            recyclerView.setAdapter(selectionAdapter);

            searchEt.addTextChangedListener(new android.text.TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                    selectionAdapter.filter(s.toString());
                }
                @Override public void afterTextChanged(android.text.Editable s) {}
            });

            new MaterialAlertDialogBuilder(this)
                    .setTitle("Select Products")
                    .setView(view)
                    .setPositiveButton("Done", (dialog, which) -> {
                        List<String> selectedIds = new ArrayList<>();
                        for (HorizontalProductScrollModel p : selectionAdapter.getSelectedProducts()) {
                            selectedIds.add(p.getDocumentId());
                        }
                        model.setContentIds(selectedIds);
                        Toast.makeText(this, "Products updated locally. Click 'Save' to apply.", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }
}
