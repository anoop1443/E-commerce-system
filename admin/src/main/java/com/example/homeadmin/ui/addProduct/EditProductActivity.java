package com.example.homeadmin.ui.addProduct;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;
import com.canhub.cropper.CropImageView;
import com.example.homeadmin.R;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EditProductActivity extends AppCompatActivity {

    private static final String TAG = "EditProductActivity";
    public static final String EXTRA_PRODUCT_ID = "extra_product_id";
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    // --- UI Elements ---
    private EditText productNameEditText, productTagsEditText;
    private EditText productMrpPriceEditText, productSellPriceEditText, productQuantityEditText;
    private EditText productDescriptionEditText, productFullDetailsEditText;
    private EditText editTextServicePrice, editTextServiceDetails, editTextSpecificationsJson;

    private SwitchMaterial switchInStock, switchUseTabLayout, switchCouponLayout, switchRewardLayout, switchServiceEnabled;

    private CardView cardServiceDetails, cardSpecifications;
    private RecyclerView imagesRecyclerView;
    private Button addImageButton, saveChangesButton;

    // --- Firebase & Data ---
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private AlertDialog progressDialog;
    private TextView progressDialogMessage;
    private String productId;
    private final ArrayList<Uri> selectedImageUris = new ArrayList<>();
    private ImagesAdapter imagesAdapter;
    private int croppingPosition = -1;

    // Categories
    private TextView textViewSelectedCategories;
    private Button buttonSelectCategories;
    private final List<String> allCategoryNames = new ArrayList<>();
    private final List<String> selectedCategories = new ArrayList<>();

    private final ActivityResultLauncher<Intent> pickImagesLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    if (data.getClipData() != null) {
                        for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                            selectedImageUris.add(data.getClipData().getItemAt(i).getUri());
                        }
                    } else if (data.getData() != null) {
                        selectedImageUris.add(data.getData());
                    }
                    imagesAdapter.notifyDataSetChanged();
                } else {
                    Log.d(TAG, "Image selection cancelled.");
                }
            }
    );

    private final ActivityResultLauncher<CropImageContractOptions> cropImageLauncher = registerForActivityResult(
            new CropImageContract(),
            result -> {
                if (result.isSuccessful() && croppingPosition != -1) {
                    Uri croppedImageUri = result.getUriContent();
                    if (croppedImageUri != null) {
                        selectedImageUris.set(croppingPosition, croppedImageUri);
                        imagesAdapter.notifyItemChanged(croppingPosition);
                    }
                }
                croppingPosition = -1;
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle("Update Product");
        }

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        initViews();
        setupListeners();

        String changes = "Save Changes";
        if (saveChangesButton != null) {
            saveChangesButton.setText(changes);
        }

        View progressView = LayoutInflater.from(this).inflate(R.layout.loading_progress_dialog, null);
        progressDialogMessage = progressView.findViewById(R.id.textView25);
        progressDialog = new AlertDialog.Builder(this)
                .setView(progressView)
                .setCancelable(false)
                .create();

        imagesAdapter = new ImagesAdapter(selectedImageUris, this::removeImage, this::startCrop, this::setAsTitle);
        if (imagesRecyclerView != null) {
            imagesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            imagesRecyclerView.setAdapter(imagesAdapter);
        }

        // Categories UI
        textViewSelectedCategories = findViewById(R.id.textViewSelectedCategories);
        buttonSelectCategories = findViewById(R.id.buttonSelectCategories);
        
        loadAllCategories();

        productId = getIntent().getStringExtra(EXTRA_PRODUCT_ID);
        if (productId != null && !productId.isEmpty()) {
            loadProductData(productId);
        } else {
            Toast.makeText(this, "No Product ID provided for editing.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
    }

    private void initViews() {
        productNameEditText = findViewById(R.id.editTextProductName);
        productTagsEditText = findViewById(R.id.editTextProductTags);
        productMrpPriceEditText = findViewById(R.id.editTextProductMrpPrice);
        productSellPriceEditText = findViewById(R.id.editTextProductSellingPrice);
        productQuantityEditText = findViewById(R.id.editTextProductQuantity);
        productDescriptionEditText = findViewById(R.id.editTextProductSimPalDetails);
        productFullDetailsEditText = findViewById(R.id.editTextProductDescription);
        editTextServicePrice = findViewById(R.id.editTextServicePrice);
        editTextServiceDetails = findViewById(R.id.editTextServiceDetails);
        editTextSpecificationsJson = findViewById(R.id.editTextSpecificationsJson);

        switchInStock = findViewById(R.id.switchInStock);
        switchUseTabLayout = findViewById(R.id.switchUseTabLayout);
        switchCouponLayout = findViewById(R.id.switchCouponLayout);
        switchRewardLayout = findViewById(R.id.switchRewardLayout);
        switchServiceEnabled = findViewById(R.id.switchServiceEnabled);

        cardServiceDetails = findViewById(R.id.cardServiceDetails);
        cardSpecifications = findViewById(R.id.cardSpecifications);

        imagesRecyclerView = findViewById(R.id.recyclerViewProductImages);
        addImageButton = findViewById(R.id.buttonAddImage);
        saveChangesButton = findViewById(R.id.buttonAddProduct);
        buttonSelectCategories = findViewById(R.id.buttonSelectCategories);
    }

    private void setupListeners() {
        addImageButton.setOnClickListener(v -> openImagePicker());
        saveChangesButton.setOnClickListener(v -> handleSaveChanges());
        buttonSelectCategories.setOnClickListener(v -> showCategorySelectionDialog());

        switchUseTabLayout.setOnCheckedChangeListener((cb, isChecked) -> cardSpecifications.setVisibility(isChecked ? View.VISIBLE : View.GONE));
        switchServiceEnabled.setOnCheckedChangeListener((cb, isChecked) -> cardServiceDetails.setVisibility(isChecked ? View.VISIBLE : View.GONE));
    }

    private void loadProductData(String productId) {
        if (progressDialogMessage != null) {
            progressDialogMessage.setText("Loading product data...");
        }
        if (progressDialog != null) {
            progressDialog.show();
        }

        db.collection("Product_Details").document(productId).get().addOnSuccessListener(doc -> {
            if (doc.exists()) {
                // Simple fields
                productNameEditText.setText(doc.getString("productTitle"));
                
                Long catPrice = doc.getLong("productCatPrise");
                productMrpPriceEditText.setText(catPrice != null ? String.valueOf(catPrice) : "");
                
                Long sellPrice = doc.getLong("productPrise");
                productSellPriceEditText.setText(sellPrice != null ? String.valueOf(sellPrice) : "");
                
                Long qty = doc.getLong("quantity");
                productQuantityEditText.setText(qty != null ? String.valueOf(qty) : "");

                // Categories
                Object categoriesObj = doc.get("categories");
                if (categoriesObj instanceof List) {
                    List<String> categories = (List<String>) categoriesObj;
                    selectedCategories.clear();
                    selectedCategories.addAll(categories);
                    textViewSelectedCategories.setText(TextUtils.join(", ", selectedCategories));
                } else {
                    String singleCategory = doc.getString("category");
                    if (singleCategory != null) {
                        selectedCategories.add(singleCategory);
                        textViewSelectedCategories.setText(singleCategory);
                    }
                }

                // Descriptions (Fixed mapping)
                productDescriptionEditText.setText(doc.getString("simPalDetails")); // sim-pal_details is Short Description
                productFullDetailsEditText.setText(doc.getString("productDescription")); // productDescription is Full Description

                // Tags
                Object tagsObj = doc.get("tags");
                if (tagsObj instanceof List) {
                    List<String> tags = (List<String>) tagsObj;
                    productTagsEditText.setText(TextUtils.join(", ", tags));
                }

                // Switches (Safe Unboxing)
                switchInStock.setChecked(Boolean.TRUE.equals(doc.getBoolean("inStock")));
                switchUseTabLayout.setChecked(Boolean.TRUE.equals(doc.getBoolean("useTabLayout")));
                switchCouponLayout.setChecked(Boolean.TRUE.equals(doc.getBoolean("couponLayout")));
                switchRewardLayout.setChecked(Boolean.TRUE.equals(doc.getBoolean("rewardLayout")));

                // Service Info
                if (doc.contains("service_info")) {
                    Map<String, Object> serviceInfo = (Map<String, Object>) doc.get("service_info");
                    if (serviceInfo != null && Boolean.TRUE.equals(serviceInfo.get("is_service"))) {
                        switchServiceEnabled.setChecked(true);
                        editTextServicePrice.setText(String.valueOf(serviceInfo.get("price")));
                        editTextServiceDetails.setText(String.valueOf(serviceInfo.get("details")));
                    }
                }

                // Specifications
                if (Boolean.TRUE.equals(doc.getBoolean("useTabLayout"))) {
                    Object specObj = doc.get("specifications");
                    if (specObj instanceof List) {
                        List<Map<String, Object>> specList = (List<Map<String, Object>>) specObj;
                        try {
                            // Convert List<Map> to pretty printed JSON string for display
                            JSONArray jsonArray = new JSONArray();
                            for (Map<String, Object> specMap : specList) {
                                jsonArray.put(new JSONObject(specMap));
                            }
                            editTextSpecificationsJson.setText(jsonArray.toString(2));
                        } catch (JSONException e) {
                             editTextSpecificationsJson.setText("Error loading specs");
                            Log.e(TAG, "Error creating JSON string from specs", e);
                        }
                    }
                }

                // Images
                Object imageUrlsObj = doc.get("imageUrls");
                if (imageUrlsObj instanceof List) {
                    List<String> imageUrls = (List<String>) imageUrlsObj;
                    for (String url : imageUrls) {
                        selectedImageUris.add(Uri.parse(url));
                    }
                    imagesAdapter.notifyDataSetChanged();
                }

                progressDialog.dismiss();
            } else {
                progressDialog.dismiss();
                Toast.makeText(this, "Product not found.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            Toast.makeText(this, "Error loading product: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        pickImagesLauncher.launch(intent);
    }

    private void handleSaveChanges() {
        if (selectedImageUris.isEmpty()) {
            Toast.makeText(this, "Product must have at least one image.", Toast.LENGTH_SHORT).show(); return;
        }
        if (!validateEditText(productNameEditText, "Product name is required")) return;

        String productName = productNameEditText.getText().toString().trim();
        long productMrpPrice = Long.parseLong(productMrpPriceEditText.getText().toString().trim());
        long productSellPrice = Long.parseLong(productSellPriceEditText.getText().toString().trim());
        String productQuantity = productQuantityEditText.getText().toString().trim();
        String simpleDetails = productDescriptionEditText.getText().toString().trim();
        String fullDescription = productFullDetailsEditText.getText().toString().trim();
        String productTags = productTagsEditText.getText().toString().trim();

        boolean inStock = switchInStock.isChecked();
        boolean useTabLayout = switchUseTabLayout.isChecked();
        boolean couponLayout = switchCouponLayout.isChecked();
        boolean rewardLayout = switchRewardLayout.isChecked();
        boolean serviceEnabled = switchServiceEnabled.isChecked();

        String servicePrice = serviceEnabled ? editTextServicePrice.getText().toString().trim() : "";
        String serviceDetails = serviceEnabled ? editTextServiceDetails.getText().toString().trim() : "";
        String specificationsJson = useTabLayout ? editTextSpecificationsJson.getText().toString().trim() : "";

        progressDialogMessage.setText("Saving changes...");
        progressDialog.show();

        updateImagesAndSaveProduct(
                productName, productSellPrice, productMrpPrice, simpleDetails, fullDescription, productQuantity, productTags, 
                inStock, useTabLayout, couponLayout, rewardLayout, serviceEnabled, servicePrice, serviceDetails, specificationsJson
        );
    }

    private boolean validateEditText(EditText editText, String error) {
        if (TextUtils.isEmpty(editText.getText())) {
            editText.setError(error);
            return false;
        }
        return true;
    }

    private void startCrop(int position) {
        croppingPosition = position;
        Uri imageUri = selectedImageUris.get(position);
        
        CropImageOptions options = new CropImageOptions();
        options.guidelines = CropImageView.Guidelines.ON;
        options.aspectRatioX = 1;
        options.aspectRatioY = 1;
        options.fixAspectRatio = true;
        options.outputCompressFormat = Bitmap.CompressFormat.JPEG;
        options.activityTitle = "Crop Product Image";
        options.cropMenuCropButtonTitle = "Done";
        
        cropImageLauncher.launch(new CropImageContractOptions(imageUri, options));
    }

    private void updateImagesAndSaveProduct(
            String productName, long productSellPrice, long productMrpPrice,
            String simpleDetails, String fullDescription, String productQuantity, String productTags,
            boolean inStock, boolean useTabLayout, boolean couponLayout, boolean rewardLayout, boolean serviceEnabled,
            String servicePrice, String serviceDetails, String specificationsJson) {

        executorService.execute(() -> {
            List<String> finalImageUrls = new ArrayList<>();
            List<Task<Uri>> uploadTasks = new ArrayList<>();
            StorageReference storageRef = storage.getReference().child("product_images/" + productId);

            for (int i = 0; i < selectedImageUris.size(); i++) {
                Uri imageUri = selectedImageUris.get(i);
                if (imageUri.toString().startsWith("http")) {
                    finalImageUrls.add(imageUri.toString());
                } else {
                    StorageReference imageRef = storageRef.child("img_" + System.currentTimeMillis() + "_" + i + ".jpg");

                    try {
                        InputStream inputStream = getContentResolver().openInputStream(imageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                        // Compression
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
                        byte[] data = baos.toByteArray();

                        UploadTask uploadTask = imageRef.putBytes(data);
                        uploadTasks.add(uploadTask.continueWithTask(task -> {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return imageRef.getDownloadUrl();
                        }));
                    } catch (Exception e) {
                        Log.e(TAG, "Error compressing image at position " + i, e);
                    }
                }
            }

            if (uploadTasks.isEmpty() && finalImageUrls.isEmpty()) {
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Product must have at least one image.", Toast.LENGTH_SHORT).show();
                });
                return;
            }

            if (uploadTasks.isEmpty()) {
                saveUpdatedProductToFirestore(productName, productSellPrice, productMrpPrice, simpleDetails, fullDescription, productQuantity, productTags, inStock, useTabLayout, couponLayout, rewardLayout, serviceEnabled, servicePrice, serviceDetails, specificationsJson, finalImageUrls);
            } else {
                Tasks.whenAllSuccess(uploadTasks).addOnSuccessListener(downloadUris -> {
                    for (Object uri : downloadUris) {
                        finalImageUrls.add(uri.toString());
                    }
                    saveUpdatedProductToFirestore(productName, productSellPrice, productMrpPrice, simpleDetails, fullDescription, productQuantity, productTags, inStock, useTabLayout, couponLayout, rewardLayout, serviceEnabled, servicePrice, serviceDetails, specificationsJson, finalImageUrls);
                }).addOnFailureListener(e -> {
                    runOnUiThread(() -> {
                        progressDialog.dismiss();
                        Toast.makeText(EditProductActivity.this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }

    private void saveUpdatedProductToFirestore(
            String productName, long productSellPrice, long productMrpPrice,
            String simpleDetails, String fullDescription, String productQuantity, String productTags,
            boolean inStock, boolean useTabLayout, boolean couponLayout, boolean rewardLayout, boolean serviceEnabled,
            String servicePrice, String serviceDetails, String specificationsJson, List<String> finalImageUrls) {

        Map<String, Object> productUpdates = new HashMap<>();

        long quantityValue = 0;
        try {
            quantityValue = Long.parseLong(productQuantity);
        } catch (NumberFormatException e) {
            Log.e(TAG, "Error parsing quantity: " + productQuantity, e);
        }

        productUpdates.put("productTitle", productName);
        productUpdates.put("productPrise", productSellPrice);
        productUpdates.put("productCatPrise", productMrpPrice);
        productUpdates.put("quantity", quantityValue);
        productUpdates.put("imageUrls", finalImageUrls);
        productUpdates.put("productDescription", fullDescription); // Full Description
        productUpdates.put("simPalDetails", simpleDetails); // Short Description

        productUpdates.put("categories", selectedCategories);
        if (!selectedCategories.isEmpty()) {
            productUpdates.put("category", selectedCategories.get(0));
        }

        List<String> tagsList = new ArrayList<>();
        if (!productTags.isEmpty()) {
            tagsList = Arrays.asList(productTags.split("\\s*,\\s*"));
        }
        productUpdates.put("tags", tagsList);

        productUpdates.put("inStock", inStock);
        productUpdates.put("useTabLayout", useTabLayout);
        productUpdates.put("couponLayout", couponLayout);
        productUpdates.put("rewardLayout", rewardLayout);

        Map<String, Object> serviceInfo = new HashMap<>();
        serviceInfo.put("is_service", serviceEnabled);
        if (serviceEnabled) {
            serviceInfo.put("price", servicePrice);
            serviceInfo.put("details", serviceDetails);
        }
        productUpdates.put("service_info", serviceInfo);

        if (useTabLayout) {
            try {
                List<Map<String, Object>> specList = new ArrayList<>();
                JSONArray jsonArray = new JSONArray(specificationsJson);
                 for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject specObjectJson = jsonArray.getJSONObject(i);
                    Map<String, Object> specMap = new HashMap<>();
                    specMap.put("title", specObjectJson.getString("title"));

                    List<Map<String, String>> fieldsList = new ArrayList<>();
                    JSONArray fieldsArrayJson = specObjectJson.getJSONArray("fields");
                    for (int j = 0; j < fieldsArrayJson.length(); j++) {
                        JSONObject fieldObjectJson = fieldsArrayJson.getJSONObject(j);
                        Map<String, String> fieldMap = new HashMap<>();
                        fieldMap.put("name", fieldObjectJson.getString("name"));
                        fieldMap.put("value", fieldObjectJson.getString("value"));
                        fieldsList.add(fieldMap);
                    }
                    specMap.put("fields", fieldsList);
                    specList.add(specMap);
                }
                productUpdates.put("specifications", specList);
            } catch (JSONException e) {
                progressDialog.dismiss();
                Toast.makeText(this, "Invalid JSON format in specifications. Changes not saved.", Toast.LENGTH_LONG).show();
                return;
            }
        } else {
             productUpdates.put("specifications", FieldValue.delete());
        }


        db.collection("Product_Details").document(productId).update(productUpdates)
                .addOnSuccessListener(aVoid -> {
                    progressDialog.dismiss();
                    Toast.makeText(EditProductActivity.this, "Product updated successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(EditProductActivity.this, "Failed to update product: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Method to remove imageUrls from the list
     private void removeImage(int position){
        if (position >= 0 && position < selectedImageUris.size()){
            selectedImageUris.remove(position);
            imagesAdapter.notifyItemRemoved(position);
            imagesAdapter.notifyItemRangeChanged(position,selectedImageUris.size());
        }
     }

     private void setAsTitle(int position) {
         if (position > 0 && position < selectedImageUris.size()) {
             Uri uri = selectedImageUris.remove(position);
             selectedImageUris.add(0, uri);
             imagesAdapter.notifyDataSetChanged();
             Toast.makeText(this, "Set as main image", Toast.LENGTH_SHORT).show();
             if (imagesRecyclerView != null) imagesRecyclerView.scrollToPosition(0);
         } else if (position == 0) {
             Toast.makeText(this, "Already the main image", Toast.LENGTH_SHORT).show();
         }
     }


        @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadAllCategories() {
        db.collection("CATEGORY").orderBy("index").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                allCategoryNames.clear();
                for (com.google.firebase.firestore.QueryDocumentSnapshot doc : task.getResult()) {
                    allCategoryNames.add(doc.getString("categoryName"));
                }
            }
        });
    }

    private void showCategorySelectionDialog() {
        if (allCategoryNames.isEmpty()) {
            Toast.makeText(this, "Loading categories, please wait...", Toast.LENGTH_SHORT).show();
            loadAllCategories();
            return;
        }

        boolean[] checkedItems = new boolean[allCategoryNames.size()];
        for (int i = 0; i < allCategoryNames.size(); i++) {
            if (selectedCategories.contains(allCategoryNames.get(i))) {
                checkedItems[i] = true;
            }
        }

        new android.app.AlertDialog.Builder(this)
                .setTitle("Select Categories")
                .setMultiChoiceItems(allCategoryNames.toArray(new String[0]), checkedItems, (dialog, which, isChecked) -> {
                    String category = allCategoryNames.get(which);
                    if (isChecked) {
                        if (!selectedCategories.contains(category)) {
                            selectedCategories.add(category);
                        }
                    } else {
                        selectedCategories.remove(category);
                    }
                })
                .setPositiveButton("OK", (dialog, which) -> {
                    if (selectedCategories.isEmpty()) {
                        textViewSelectedCategories.setText("No categories selected");
                    } else {
                        textViewSelectedCategories.setText(TextUtils.join(", ", selectedCategories));
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }


        // Interface for the remove button callback
        public interface ImageRemoveListener {
            void onImageRemoved(int position);
        }

        public interface ImageCropListener {
            void onImageCrop(int position);
        }

        public interface ImageTitleListener {
            void onSetAsTitle(int position);
        }

        private static class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ImageViewHolder> {
        private final ArrayList<Uri> imageUris;
        private final ImageRemoveListener removeListener;
        private final ImageCropListener cropListener;
        private final ImageTitleListener titleListener;


        public ImagesAdapter(ArrayList<Uri> imageUris, ImageRemoveListener removeListener, ImageCropListener cropListener, ImageTitleListener titleListener) {
            this.imageUris = imageUris;
            this.removeListener = removeListener;
            this.cropListener = cropListener;
            this.titleListener = titleListener;
        }

        @NonNull
        @Override
        public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_image, parent, false);
            return new ImageViewHolder(view, removeListener, cropListener, titleListener);
        }

        @Override
        public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
            Glide.with(holder.itemView.getContext())
                    .load(imageUris.get(position))
                    .centerCrop()
                    .into(holder.imageView);

            if (position == 0) {
                holder.titleButton.setImageResource(android.R.drawable.btn_star_big_on);
            } else {
                holder.titleButton.setImageResource(android.R.drawable.btn_star_big_off);
            }
        }

        @Override
        public int getItemCount() {
            return imageUris.size();
        }

        static class ImageViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            ImageButton removeButton, cropButton, titleButton;


            public ImageViewHolder(@NonNull View itemView, ImageRemoveListener removeListener, ImageCropListener cropListener, ImageTitleListener titleListener) {
                super(itemView);
                imageView = itemView.findViewById(R.id.product_image);
                removeButton = itemView.findViewById(R.id.delete_image_button);
                cropButton = itemView.findViewById(R.id.crop_image_button);
                titleButton = itemView.findViewById(R.id.set_as_title_button);

                removeButton.setOnClickListener(v -> {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        removeListener.onImageRemoved(position);
                    }
                });

                if (cropButton != null) {
                    cropButton.setOnClickListener(v -> {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            cropListener.onImageCrop(position);
                        }
                    });
                }

                if (titleButton != null) {
                    titleButton.setOnClickListener(v -> {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            titleListener.onSetAsTitle(position);
                        }
                    });
                }
            }
        }
    }
}
