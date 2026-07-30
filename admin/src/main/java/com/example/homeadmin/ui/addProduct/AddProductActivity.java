package com.example.homeadmin.ui.addProduct;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
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
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddProductActivity extends AppCompatActivity {

    private static final String TAG = "AddProductActivity";
    private Toolbar toolbar;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();


    // --- UI Elements ---

    // Core Details
    private EditText productNameEditText;
    private EditText productTagsEditText;

    // Pricing & Stock
    private EditText productMrpPriceEditText;
    private EditText productSellPriceEditText;
    private EditText productQuantityEditText;
    private SwitchMaterial switchInStock;

    // Descriptions
    private EditText productSimPalDetailsEditText;
    private EditText productFullDescriptionEditText;


    // Advanced Options
    private SwitchMaterial switchUseTabLayout;
    private SwitchMaterial switchCouponLayout;
    private SwitchMaterial switchRewardLayout;
    private SwitchMaterial switchServiceEnabled;

    // Service Details (Conditional)
    private CardView cardServiceDetails;
    private EditText editTextServicePrice;
    private EditText editTextServiceDetails;

    // Specifications (Conditional)
    private CardView cardSpecifications;
    private EditText editTextSpecificationsJson;

    // Images
    private RecyclerView imagesRecyclerView;
    private Button addImageButton;

    // Actions
    private Button addProductButton;

    // Categories
    private TextView textViewSelectedCategories;
    private Button buttonSelectCategories;
    private final List<String> allCategoryNames = new ArrayList<>();
    private final List<String> selectedCategories = new ArrayList<>();

    // --- Firebase & Data ---
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private AlertDialog progressDialog;
    private TextView progressDialogMessage;

    private ArrayList<Uri> selectedImageUris = new ArrayList<>();
    private ImagesAdapter imagesAdapter;
    private int croppingPosition = -1;

    private final ActivityResultLauncher<Intent> pickImagesLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    if (data.getClipData() != null) {
                        int count = data.getClipData().getItemCount();
                        for (int i = 0; i < count; i++) {
                            Uri imageUri = data.getClipData().getItemAt(i).getUri();
                            selectedImageUris.add(imageUri);
                        }
                    } else if (data.getData() != null) {
                        Uri imageUri = data.getData();
                        selectedImageUris.add(imageUri);
                    }
                    imagesAdapter.notifyDataSetChanged();
                } else {
                    Log.d(TAG, "Image selection cancelled or failed.");
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

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle("Add Product");
        }

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        initViews();

        View progressView = LayoutInflater.from(this).inflate(R.layout.loading_progress_dialog, null);
        progressDialogMessage = progressView.findViewById(R.id.textView25);
        if (progressDialogMessage != null) {
            progressDialogMessage.setText("Adding product...");
        }
        progressDialog = new AlertDialog.Builder(this)
                .setView(progressView)
                .setCancelable(false)
                .create();

        imagesAdapter = new ImagesAdapter(selectedImageUris, this::removeImage, this::startCrop, this::setAsTitle);
        imagesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        imagesRecyclerView.setAdapter(imagesAdapter);

        // Setup button and switch listeners
        setupListeners();
        loadAllCategories();
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

        new AlertDialog.Builder(this)
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

    private void initViews() {
        // Core
        productNameEditText = findViewById(R.id.editTextProductName);
        productTagsEditText = findViewById(R.id.editTextProductTags);

        // Pricing & Stock
        productMrpPriceEditText = findViewById(R.id.editTextProductMrpPrice);
        productSellPriceEditText = findViewById(R.id.editTextProductSellingPrice);
        productQuantityEditText = findViewById(R.id.editTextProductQuantity);
        switchInStock = findViewById(R.id.switchInStock);

        // Descriptions
        productSimPalDetailsEditText = findViewById(R.id.editTextProductSimPalDetails);
        productFullDescriptionEditText = findViewById(R.id.editTextProductDescription);


        // Advanced Options
        switchUseTabLayout = findViewById(R.id.switchUseTabLayout);
        switchCouponLayout = findViewById(R.id.switchCouponLayout);
        switchRewardLayout = findViewById(R.id.switchRewardLayout);
        switchServiceEnabled = findViewById(R.id.switchServiceEnabled);

        // Service Details
        cardServiceDetails = findViewById(R.id.cardServiceDetails);
        editTextServicePrice = findViewById(R.id.editTextServicePrice);
        editTextServiceDetails = findViewById(R.id.editTextServiceDetails);

        // Specifications
        cardSpecifications = findViewById(R.id.cardSpecifications);
        editTextSpecificationsJson = findViewById(R.id.editTextSpecificationsJson);

        // Categories
        textViewSelectedCategories = findViewById(R.id.textViewSelectedCategories);
        buttonSelectCategories = findViewById(R.id.buttonSelectCategories);

        // Images & Actions
        imagesRecyclerView = findViewById(R.id.recyclerViewProductImages);
        addImageButton = findViewById(R.id.buttonAddImage);
        addProductButton = findViewById(R.id.buttonAddProduct);
    }

    private void setupListeners() {
        addImageButton.setOnClickListener(v -> openImagePicker());
        addProductButton.setOnClickListener(v -> handleAddProduct());
        buttonSelectCategories.setOnClickListener(v -> showCategorySelectionDialog());

        switchUseTabLayout.setOnCheckedChangeListener((buttonView, isChecked) -> {
            cardSpecifications.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });

        switchServiceEnabled.setOnCheckedChangeListener((buttonView, isChecked) -> {
            cardServiceDetails.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        pickImagesLauncher.launch(intent);
    }

    private void handleAddProduct() {
        // --- Validation ---
        if (selectedImageUris.isEmpty()) {
            Toast.makeText(this, "Please select at least one product image.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedCategories.isEmpty()) {
            Toast.makeText(this, "Please select at least one category.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!validateEditText(productNameEditText, "Product name cannot be empty")) return;
        if (!validateEditText(productMrpPriceEditText, "MRP price cannot be empty")) return;
        if (!validateEditText(productSellPriceEditText, "Selling price cannot be empty")) return;
        if (!validateEditText(productQuantityEditText, "Quantity cannot be empty")) return;
        if (!validateEditText(productSimPalDetailsEditText, "Simple details cannot be empty")) return;


        long productMrpPrice, productSellPrice;
        try {
            productMrpPrice = Long.parseLong(productMrpPriceEditText.getText().toString().trim());
            productSellPrice = Long.parseLong(productSellPriceEditText.getText().toString().trim());
        } catch (NumberFormatException e) {
            productMrpPriceEditText.setError("Invalid number format");
            return;
        }

        if (productSellPrice > productMrpPrice) {
            productSellPriceEditText.setError("Selling price cannot be greater than MRP");
            return;
        }

        // --- Get Data from Form ---
        String productName = productNameEditText.getText().toString().trim();
        String productTags = productTagsEditText.getText().toString().trim();
        String productQuantity = productQuantityEditText.getText().toString().trim();
        String simpleDetails = productSimPalDetailsEditText.getText().toString().trim();
        String fullDescription = productFullDescriptionEditText.getText().toString().trim();

        boolean inStock = switchInStock.isChecked();
        boolean useTabLayout = switchUseTabLayout.isChecked();
        boolean couponLayout = switchCouponLayout.isChecked();
        boolean rewardLayout = switchRewardLayout.isChecked();
        boolean serviceEnabled = switchServiceEnabled.isChecked();

        String servicePrice = serviceEnabled ? editTextServicePrice.getText().toString().trim() : "";
        String serviceDetails = serviceEnabled ? editTextServiceDetails.getText().toString().trim() : "";
        String specificationsJson = useTabLayout ? editTextSpecificationsJson.getText().toString().trim() : "";

        if (useTabLayout && !validateEditText(editTextSpecificationsJson, "Specifications JSON cannot be empty")) return;
        if (serviceEnabled && !validateEditText(editTextServicePrice, "Service price cannot be empty")) return;


        progressDialogMessage.setText("Adding product...");
        progressDialog.show();
        uploadImagesAndSaveProduct(productName, productSellPrice, productMrpPrice, simpleDetails, fullDescription, productQuantity, productTags,
                inStock, useTabLayout, couponLayout, rewardLayout, serviceEnabled, servicePrice, serviceDetails, specificationsJson);
    }

    private boolean validateEditText(EditText editText, String errorMessage) {
        if (TextUtils.isEmpty(editText.getText().toString().trim())) {
            editText.setError(errorMessage);
            editText.requestFocus();
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

    private void uploadImagesAndSaveProduct(String productName, long productPrice, long productMrpPrice, String simpleDetails, String fullDescription, String productQuantity, String productTags, boolean inStock, boolean useTabLayout, boolean couponLayout, boolean rewardLayout, boolean serviceEnabled, String servicePrice, String serviceDetails, String specificationsJson) {
        executorService.execute(() -> {
            List<Task<Uri>> uploadTasks = new ArrayList<>();

            // Generate a new document reference to get the product ID first
            com.google.firebase.firestore.DocumentReference productRef = db.collection("Product_Details").document();
            String productId = productRef.getId();

            StorageReference storageRef = storage.getReference().child("product_images/" + productId);

            for (int i = 0; i < selectedImageUris.size(); i++) {
                Uri imageUri = selectedImageUris.get(i);
                StorageReference imageRef = storageRef.child("img_" + System.currentTimeMillis() + "_" + i + ".jpg");

                try {
                    InputStream inputStream = getContentResolver().openInputStream(imageUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                    // Compression logic
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

            if (uploadTasks.isEmpty()) {
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "No valid images to upload", Toast.LENGTH_SHORT).show();
                });
                return;
            }

            Tasks.whenAllSuccess(uploadTasks)
                    .addOnSuccessListener(downloadUris -> {
                        List<String> imageUrls = new ArrayList<>();
                        for (Object uri : downloadUris) {
                            imageUrls.add(uri.toString());
                        }
                        saveProductToFirestore(productName, productPrice, productMrpPrice, simpleDetails, fullDescription, productQuantity, productTags,
                                inStock, useTabLayout, couponLayout, rewardLayout, serviceEnabled, servicePrice, serviceDetails, specificationsJson, imageUrls, productRef);
                    })
                    .addOnFailureListener(e -> {
                        runOnUiThread(() -> {
                            progressDialog.dismiss();
                            Toast.makeText(AddProductActivity.this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                    });
        });
    }

    private void saveProductToFirestore(String productName, long productPrice, long productMrpPrice, String simpleDetails, String fullDescription, String productQuantity, String productTags, boolean inStock, boolean useTabLayout, boolean couponLayout, boolean rewardLayout, boolean serviceEnabled, String servicePrice, String serviceDetails, String specificationsJson, List<String> downloadUrls, com.google.firebase.firestore.DocumentReference productRef) {
        Map<String, Object> product = new HashMap<>();

        long quantityValue = 0;
        try {
            quantityValue = Long.parseLong(productQuantity);
        } catch (NumberFormatException e) {
            Log.e(TAG, "Error parsing quantity: " + productQuantity, e);
        }

        // --- Basic Info ---
        product.put("productTitle", productName);
        product.put("productPrise", productPrice); // Corresponds to product_prise
        product.put("productCatPrise", productMrpPrice); // Corresponds to product_prise_cat
        product.put("quantity", quantityValue);
        product.put("imageUrls", downloadUrls);
        product.put("fastTime", FieldValue.serverTimestamp());

        // --- Descriptions ---
        product.put("simPalDetails", simpleDetails); // Corresponds to sim-pal_details (Short Description)
        product.put("productDescription", fullDescription); // Corresponds to product_description (Full Description)

        // --- Categories ---
        product.put("categories", selectedCategories);
        if (!selectedCategories.isEmpty()) {
            product.put("category", selectedCategories.get(0)); // Backward compatibility
        }

        // --- Tags ---
        List<String> tagsList = new ArrayList<>();
        if (!productTags.isEmpty()) {
            tagsList = Arrays.asList(productTags.split("\\s*,\\s*"));
        }
        product.put("tags", tagsList);

        // --- Boolean Flags ---
        product.put("inStock", inStock);
        product.put("useTabLayout", useTabLayout);
        product.put("couponLayout", couponLayout); // Corresponds to coupon_redeem_layout
        product.put("rewardLayout", rewardLayout);

        // --- Ratings (Initialize) ---
        product.put("1_star", 0L);
        product.put("2_star", 0L);
        product.put("3_star", 0L);
        product.put("4_star", 0L);
        product.put("5_star", 0L);
        product.put("totalRatings", 0L);
        product.put("starRating", 0.0d); // or average rating
        product.put("averageRating", "0.0");


        // --- Other defaults from user app ---
        product.put("freeCoupon", 0L);
        product.put("paymentMethod", "Cash on delivery available");


        // --- Service Info (Conditional) ---
        Map<String, Object> serviceInfo = new HashMap<>();
        serviceInfo.put("is_service", serviceEnabled);
        if (serviceEnabled) {
            serviceInfo.put("price", servicePrice);
            serviceInfo.put("details", serviceDetails);
        }
        product.put("service_info", serviceInfo);


        // --- Specifications (Conditional & Parsed) ---
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
                product.put("specifications", specList);
            } catch (JSONException e) {
                progressDialog.dismiss();
                Toast.makeText(AddProductActivity.this, "Invalid JSON format in specifications. Product not saved.", Toast.LENGTH_LONG).show();
                Log.e(TAG, "JSON Parsing error", e);
                return; // Stop saving
            }
        }


        // Save to Firestore using the pre-generated reference
        productRef.set(product)
                .addOnSuccessListener(aVoid -> {
                    progressDialog.dismiss();
                    Toast.makeText(AddProductActivity.this, "Product added successfully!", Toast.LENGTH_SHORT).show();
                    clearForm();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(AddProductActivity.this, "Failed to save product: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }

    private void removeImage(int position) {
        if (position >= 0 && position < selectedImageUris.size()) {
            selectedImageUris.remove(position);
            imagesAdapter.notifyItemRemoved(position);
            imagesAdapter.notifyItemRangeChanged(position, selectedImageUris.size());
        }
    }

    private void setAsTitle(int position) {
        if (position > 0 && position < selectedImageUris.size()) {
            Uri uri = selectedImageUris.remove(position);
            selectedImageUris.add(0, uri);
            imagesAdapter.notifyDataSetChanged();
            Toast.makeText(this, "Set as main image", Toast.LENGTH_SHORT).show();
            imagesRecyclerView.scrollToPosition(0);
        } else if (position == 0) {
            Toast.makeText(this, "Already the main image", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearForm() {
        productNameEditText.setText("");
        productMrpPriceEditText.setText("");
        productSellPriceEditText.setText("");
        productSimPalDetailsEditText.setText("");
        productFullDescriptionEditText.setText("");
        productQuantityEditText.setText("");
        productTagsEditText.setText("");
        editTextServicePrice.setText("");
        editTextServiceDetails.setText("");
        editTextSpecificationsJson.setText("");

        switchInStock.setChecked(true);
        switchUseTabLayout.setChecked(false);
        switchCouponLayout.setChecked(false);
        switchRewardLayout.setChecked(false);
        switchServiceEnabled.setChecked(false);

        selectedCategories.clear();
        textViewSelectedCategories.setText("No categories selected");
        selectedImageUris.clear();
        imagesAdapter.notifyDataSetChanged();
    }

    // --- RecyclerView Adapter for Images ---
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
            ImageButton deleteButton, cropButton, titleButton;

            public ImageViewHolder(@NonNull View itemView, ImageRemoveListener removeListener, ImageCropListener cropListener, ImageTitleListener titleListener) {
                super(itemView);
                imageView = itemView.findViewById(R.id.product_image);
                deleteButton = itemView.findViewById(R.id.delete_image_button);
                cropButton = itemView.findViewById(R.id.crop_image_button);
                titleButton = itemView.findViewById(R.id.set_as_title_button);

                deleteButton.setOnClickListener(v -> {
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

    // Interface to communicate from Adapter to Activity
    public interface ImageRemoveListener {
        void onImageRemoved(int position);
    }

    public interface ImageCropListener {
        void onImageCrop(int position);
    }

    public interface ImageTitleListener {
        void onSetAsTitle(int position);
    }
}
