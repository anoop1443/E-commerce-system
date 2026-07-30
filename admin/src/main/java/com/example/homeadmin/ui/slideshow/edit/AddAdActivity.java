package com.example.homeadmin.ui.slideshow.edit;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.homeadmin.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class AddAdActivity extends AppCompatActivity implements ColorPaletteAdapter.OnColorSelectedListener {

    private EditText adColorEditText, productIdEditText, categoryNameEditText;
    private Button addAdButton;
    private RecyclerView adsRecyclerView, colorPaletteRecyclerView;
    private CardView selectImageCard;
    private ImageView selectedImageView;
    private View selectImagePlaceholder, previewBackgroundFrame;

    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private Uri selectedImageUri;
    private AdsAdapter adsAdapter;
    private List<DocumentSnapshot> adList;
    private ColorPaletteAdapter colorPaletteAdapter;
    private List<Integer> colorPalette;
    private DocumentSnapshot editingAd = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ad);

        Toolbar toolbar = findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Manage Ads");

        adColorEditText = findViewById(R.id.ad_color_edittext);
        productIdEditText = findViewById(R.id.product_id_edittext);
        categoryNameEditText = findViewById(R.id.category_name_edittext);
        addAdButton = findViewById(R.id.add_ad_button);
        adsRecyclerView = findViewById(R.id.ads_recyclerview);
        selectImageCard = findViewById(R.id.select_image_card);
        selectedImageView = findViewById(R.id.selected_image_view);
        selectImagePlaceholder = findViewById(R.id.select_image_placeholder);
        previewBackgroundFrame = findViewById(R.id.preview_background_frame);
        colorPaletteRecyclerView = findViewById(R.id.color_palette_recyclerview);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        adList = new ArrayList<>();
        adsAdapter = new AdsAdapter(adList);
        adsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adsRecyclerView.setAdapter(adsAdapter);

        colorPalette = new ArrayList<>();
        colorPaletteAdapter = new ColorPaletteAdapter(colorPalette, this);
        colorPaletteRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        colorPaletteRecyclerView.setAdapter(colorPaletteAdapter);

        addAdButton.setOnClickListener(v -> {
            if (editingAd != null) {
                updateAdInFirestore();
            } else {
                addAdToFirestore();
            }
        });
        selectImageCard.setOnClickListener(v -> selectImage());

        adColorEditText.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    previewBackgroundFrame.setBackgroundColor(Color.parseColor(s.toString()));
                } catch (Exception ignored) {}
            }
            @Override public void afterTextChanged(android.text.Editable s) {}
        });

        fetchAdsFromFirestore();
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null && result.getData().getData() != null) {
                    selectedImageUri = result.getData().getData();
                    Glide.with(this).load(selectedImageUri).into(selectedImageView);
                    selectImagePlaceholder.setVisibility(View.GONE);
                    createPaletteAsync(selectedImageUri);
                }
            });

    private void createPaletteAsync(Uri uri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            generatePaletteFromBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void generatePaletteFromBitmap(Bitmap bitmap) {
        Palette.from(bitmap).generate(palette -> {
            if (palette != null) {
                colorPalette.clear();
                colorPalette.add(palette.getVibrantColor(0x000000));
                colorPalette.add(palette.getLightVibrantColor(0x000000));
                colorPalette.add(palette.getDarkVibrantColor(0x000000));
                colorPalette.add(palette.getMutedColor(0x000000));
                colorPalette.add(palette.getLightMutedColor(0x000000));
                colorPalette.add(palette.getDarkMutedColor(0x000000));
                colorPaletteAdapter.notifyDataSetChanged();

                if (editingAd == null && !colorPalette.isEmpty()) {
                    onColorSelected(colorPalette.get(0));
                }
            }
        });
    }

    @Override
    public void onColorSelected(int color) {
        adColorEditText.setText(String.format("#%06X", (0xFFFFFF & color)));
    }

    private void addAdToFirestore() {
        String color = adColorEditText.getText().toString().trim();
        String categoryName = categoryNameEditText.getText().toString().trim();
        if (selectedImageUri == null || color.isEmpty()) {
            Toast.makeText(this, "Please select an image and enter a color.", Toast.LENGTH_SHORT).show();
            return;
        }

        String path = "ads/";
        if (!categoryName.isEmpty()) {
            path += categoryName + "/";
        }
        String fileName = System.currentTimeMillis() + ".jpg";

        StorageReference storageRef = storage.getReference().child(path + fileName);
        storageRef.putFile(selectedImageUri)
                .addOnSuccessListener(taskSnapshot -> taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                    saveAdDataToFirestore(uri.toString(), color);
                }))
                .addOnFailureListener(e -> {
                    Log.e("AddAdActivity", "Upload failed", e);
                    Toast.makeText(this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void saveAdDataToFirestore(String imageUrl, String backgroundColor) {
        String productId = productIdEditText.getText().toString().trim();
        String categoryName = categoryNameEditText.getText().toString().trim();

        Map<String, Object> adData = new HashMap<>();
        adData.put("imageUrl", imageUrl);
        adData.put("backgroundColor", backgroundColor);
        adData.put("dateTime", com.google.firebase.firestore.FieldValue.serverTimestamp());

        if (!productId.isEmpty()) {
            adData.put("productID", productId);
        }
        if (!categoryName.isEmpty()) {
            adData.put("categoryName", categoryName);
        }

        db.collection("ads").add(adData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Ad added successfully!", Toast.LENGTH_SHORT).show();
                    resetForm();
                    fetchAdsFromFirestore();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error adding ad.", Toast.LENGTH_SHORT).show());
    }

    private void updateAdInFirestore() {
        String color = adColorEditText.getText().toString().trim();
        String categoryName = categoryNameEditText.getText().toString().trim();
        if (editingAd == null) return;

        Map<String, Object> adData = new HashMap<>();
        adData.put("backgroundColor", color);
        adData.put("productID", productIdEditText.getText().toString().trim());
        adData.put("categoryName", categoryName);
        adData.put("dateTime", com.google.firebase.firestore.FieldValue.serverTimestamp());

        if (selectedImageUri != null) {
            // Delete old image first
            String oldImageUrl = editingAd.getString("imageUrl");
            if (oldImageUrl != null && oldImageUrl.contains("firebase")) {
                try {
                    StorageReference oldRef = FirebaseStorage.getInstance().getReferenceFromUrl(oldImageUrl);
                    oldRef.delete();
                } catch (Exception ignored) {}
            }

            String path = "ads/";
            if (!categoryName.isEmpty()) {
                path += categoryName + "/";
            }
            String fileName = System.currentTimeMillis() + ".jpg";

            StorageReference storageRef = storage.getReference().child(path + fileName);
            storageRef.putFile(selectedImageUri)
                    .addOnSuccessListener(taskSnapshot -> taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                        adData.put("imageUrl", uri.toString());
                        db.collection("ads").document(editingAd.getId()).update(adData)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this, "Ad updated successfully!", Toast.LENGTH_SHORT).show();
                                    resetForm();
                                    fetchAdsFromFirestore();
                                })
                                .addOnFailureListener(e -> Toast.makeText(this, "Error updating ad.", Toast.LENGTH_SHORT).show());
                    }))
                    .addOnFailureListener(e -> Toast.makeText(this, "Image upload failed.", Toast.LENGTH_SHORT).show());
        } else {
            db.collection("ads").document(editingAd.getId()).update(adData)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Ad updated successfully!", Toast.LENGTH_SHORT).show();
                        resetForm();
                        fetchAdsFromFirestore();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Error updating ad.", Toast.LENGTH_SHORT).show());
        }
    }

    private void fetchAdsFromFirestore() {
        db.collection("ads").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    adList.clear();
                    adList.addAll(queryDocumentSnapshots.getDocuments());
                    adsAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error fetching ads.", Toast.LENGTH_SHORT).show());
    }

    private void removeAdFromFirestore(String documentId) {
        db.collection("ads").document(documentId).get().addOnSuccessListener(documentSnapshot -> {
            String imageUrl = documentSnapshot.getString("imageUrl");
            String productId = documentSnapshot.getString("productID");
            String label = "Strip Ad" + (productId != null ? " (Product: " + productId + ")" : "");

            com.example.homeadmin.ui.trash.TrashManager.moveToTrash(
                    "ads",
                    documentId,
                    "AD",
                    label,
                    imageUrl,
                    new com.example.homeadmin.ui.trash.TrashManager.OnTrashOperationListener() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(AddAdActivity.this, "Moved to Recycle Bin", Toast.LENGTH_SHORT).show();
                            fetchAdsFromFirestore();
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(AddAdActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
            );
        });
    }

    private void resetForm() {
        editingAd = null;
        adColorEditText.setText("");
        productIdEditText.setText("");
        categoryNameEditText.setText("");
        selectedImageView.setImageDrawable(null);
        selectImagePlaceholder.setVisibility(View.VISIBLE);
        previewBackgroundFrame.setBackgroundColor(Color.parseColor("#EEEEEE"));
        selectedImageUri = null;
        colorPalette.clear();
        colorPaletteAdapter.notifyDataSetChanged();
        addAdButton.setText("Save Strip Ad");
    }

    private void startEditing(DocumentSnapshot ad) {
        editingAd = ad;
        adColorEditText.setText(ad.getString("backgroundColor"));
        productIdEditText.setText(ad.getString("productID"));
        categoryNameEditText.setText(ad.getString("categoryName"));

        String imageUrl = ad.getString("imageUrl");
        if (imageUrl != null) {
            selectImagePlaceholder.setVisibility(View.GONE);
            Glide.with(this)
                    .asBitmap()
                    .load(imageUrl)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            selectedImageView.setImageBitmap(resource);
                            generatePaletteFromBitmap(resource);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                        }
                    });
        }
        addAdButton.setText("Update Ad");
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private class AdsAdapter extends RecyclerView.Adapter<AdsAdapter.ViewHolder> {
        private final List<DocumentSnapshot> ads;

        AdsAdapter(List<DocumentSnapshot> ads) {
            this.ads = ads;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ad_list_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            DocumentSnapshot doc = ads.get(position);
            String imageUrl = doc.getString("imageUrl");
            String color = doc.getString("backgroundColor");
            String productId = doc.getString("productID");
            String categoryName = doc.getString("categoryName");

            if (imageUrl != null) {
                Glide.with(holder.itemView.getContext()).load(imageUrl).into(holder.adImageView);
            }
            holder.adColorTextView.setText(color);
            holder.adProductIdTextView.setText("ProductID: " + (productId != null ? productId : "N/A"));
            holder.adCategoryNameTextView.setText("Category: " + (categoryName != null ? categoryName : "N/A"));

            try {
                DrawableCompat.setTint(
                        DrawableCompat.wrap(holder.adColorPreview.getBackground()),
                        Color.parseColor(color)
                );
            } catch (Exception e) {
                // Invalid color
            }

            holder.removeButton.setOnClickListener(v -> removeAdFromFirestore(doc.getId()));
            holder.editButton.setOnClickListener(v -> startEditing(doc));

        }

        @Override
        public int getItemCount() {
            return ads.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView adImageView;
            TextView adColorTextView, adProductIdTextView, adCategoryNameTextView;
            Button removeButton, editButton;
            View adColorPreview;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                adImageView = itemView.findViewById(R.id.ad_image_view);
                adColorTextView = itemView.findViewById(R.id.ad_color_text_view);
                adColorPreview = itemView.findViewById(R.id.ad_color_preview);
                adProductIdTextView = itemView.findViewById(R.id.ad_product_id_text_view);
                adCategoryNameTextView = itemView.findViewById(R.id.ad_category_name_text_view);
                removeButton = itemView.findViewById(R.id.remove_button);
                editButton = itemView.findViewById(R.id.edit_button);
            }
        }
    }
}
