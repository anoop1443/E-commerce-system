package com.example.homeelecation.ui.address;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.example.homeelecation.R;
import com.example.homeelecation.ui.DbLoadData;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class Add_delivery_address_Activity3 extends AppCompatActivity {
    private Toolbar toolbar;
    public Dialog loadingDialog;
    private EditText fullName, phoneNumber, pinCode, state, city, house, roadAreaColony;

    private RadioButton radioHome, radioOffice, radioLocation;
    private Button saveAddresBtn;
    private boolean updateAddress = false;
    private AddressesSelectModel addressesSelectModel;
    private int position;

    @Inject
    FirebaseFirestore db;

    @Inject
    FirebaseAuth auth;

    private FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_delivary_addres3);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle("Address");
        }

        //loading dialog
        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        if (loadingDialog.getWindow() != null) {
            loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        fullName = findViewById(R.id.add_delivery_address_fullName);
        phoneNumber = findViewById(R.id.add_delivery_address_phone);
        pinCode = findViewById(R.id.add_delivery_address_pinCode);
        state = findViewById(R.id.add_delivery_address_state);
        city = findViewById(R.id.add_delivery_address_city);
        house = findViewById(R.id.add_delivery_address_house);
        roadAreaColony = findViewById(R.id.add_delivery_address_road);

        radioHome = findViewById(R.id.add_delivery_address_home);
        radioOffice = findViewById(R.id.add_delivery_address_office);
        radioLocation = findViewById(R.id.add_delivery_address_location_btn);
        saveAddresBtn = findViewById(R.id.add_delivery_address_save_btn);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        radioLocation.setOnClickListener(v -> checkLocationPermissionAndFetch());

        radioHome.setOnClickListener(v -> updateSelectionUI(true));
        radioOffice.setOnClickListener(v -> updateSelectionUI(false));

        String intentType = getIntent().getStringExtra("INTENT");
        if ("update_address".equals(intentType)) {
            updateAddress = true;
            position = getIntent().getIntExtra("index", -1);
            
            if (position != -1 && position < DbLoadData.addressesSelectModelList.size()) {
                addressesSelectModel = DbLoadData.addressesSelectModelList.get(position);

                fullName.setText(addressesSelectModel.getFullName());
                
                String mobile = addressesSelectModel.getMobileNumber();
                if (mobile != null && mobile.startsWith("+91")) {
                    phoneNumber.setText(mobile.substring(3));
                } else {
                    phoneNumber.setText(mobile);
                }
                
                pinCode.setText(addressesSelectModel.getPinCode());
                state.setText(addressesSelectModel.getState());
                city.setText(addressesSelectModel.getCity());
                house.setText(addressesSelectModel.getHouse());
                roadAreaColony.setText(addressesSelectModel.getRoadAreaColony());

                saveAddresBtn.setText("UPDATE ADDRESS");

                // Set initial selection
                String type = addressesSelectModel.getAddressType();
                if ("HOME".equals(type)) {
                    radioHome.setChecked(true);
                    updateSelectionUI(true);
                } else if ("OFFICE".equals(type)) {
                    radioOffice.setChecked(true);
                    updateSelectionUI(false);
                }

            } else {
                Toast.makeText(this, "Invalid address index", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

        saveAddresBtn.setOnClickListener(v -> {
            if (validateInputs()) {
                loadingDialog.show();
                saveAddressToFirebase();
            }
        });
    }

    private void updateSelectionUI(boolean isHome) {
        if (isHome) {
            radioHome.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#356DCC")));
            radioHome.setTextColor(Color.WHITE);
            radioHome.setCompoundDrawableTintList(ColorStateList.valueOf(Color.WHITE));

            radioOffice.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F0F0F0")));
            radioOffice.setTextColor(Color.BLACK);
            radioOffice.setCompoundDrawableTintList(ColorStateList.valueOf(Color.BLACK));
        } else {
            radioOffice.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#356DCC")));
            radioOffice.setTextColor(Color.WHITE);
            radioOffice.setCompoundDrawableTintList(ColorStateList.valueOf(Color.WHITE));

            radioHome.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F0F0F0")));
            radioHome.setTextColor(Color.BLACK);
            radioHome.setCompoundDrawableTintList(ColorStateList.valueOf(Color.BLACK));
        }
    }

    private void checkLocationPermissionAndFetch() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        } else {
            fetchLocation();
        }
    }

    @SuppressLint("MissingPermission")
    private void fetchLocation() {
        loadingDialog.show();

        // Check if GPS is enabled
        android.location.LocationManager lm = (android.location.LocationManager) getSystemService(android.content.Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        try {
            gps_enabled = lm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
        } catch (Exception ignored) {}

        if (!gps_enabled) {
            loadingDialog.dismiss();
            Toast.makeText(this, "Please turn on Location (GPS) and try again", Toast.LENGTH_LONG).show();
            radioLocation.setChecked(false);
            return;
        }

        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                fillAddress(task.getResult().getLatitude(), task.getResult().getLongitude());
                radioLocation.setChecked(false);
            } else {
                // Try to get fresh location if last location is null
                fusedLocationProviderClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                        .addOnCompleteListener(locationTask -> {
                            if (locationTask.isSuccessful() && locationTask.getResult() != null) {
                                fillAddress(locationTask.getResult().getLatitude(), locationTask.getResult().getLongitude());
                            } else {
                                Toast.makeText(this, "Unable to get current location. Please try again.", Toast.LENGTH_SHORT).show();
                                loadingDialog.dismiss();
                            }
                            radioLocation.setChecked(false);
                        });
            }
        });
    }

    private void fillAddress(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                pinCode.setText(address.getPostalCode());
                city.setText(address.getLocality());
                state.setText(address.getAdminArea());
                
                // Get House/Building No.
                String houseNo = address.getFeatureName();
                house.setText(houseNo);
                
                // Get Road/Area/Colony without duplicating houseNo
                String road = address.getSubLocality(); // Try sub-locality first
                if (road == null) {
                    road = address.getThoroughfare(); // Then try street name
                }
                if (road == null || road.equals(houseNo)) {
                    // Fallback to address line but try to remove the house number if it's at the start
                    road = address.getAddressLine(0);
                    if (road != null && houseNo != null && road.startsWith(houseNo)) {
                        road = road.replaceFirst(houseNo, "").trim();
                        if (road.startsWith(",")) {
                            road = road.substring(1).trim();
                        }
                    }
                }
                roadAreaColony.setText(road);

                Toast.makeText(this, "Location detected!", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Toast.makeText(this, "Error fetching address details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        loadingDialog.dismiss();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            fetchLocation();
        }
    }

    private boolean validateInputs() {
        if (TextUtils.isEmpty(fullName.getText())) {
            fullName.setError("Full name is required!");
            fullName.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(phoneNumber.getText()) || phoneNumber.getText().length() < 10) {
            phoneNumber.setError("Enter a valid 10-digit phone number!");
            phoneNumber.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(pinCode.getText()) || pinCode.getText().length() != 6) {
            pinCode.setError("Enter a valid 6-digit Pincode!");
            pinCode.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(state.getText())) {
            state.setError("State is required!");
            state.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(city.getText())) {
            city.setError("City is required!");
            city.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(house.getText())) {
            house.setError("House/Building no. is required!");
            house.requestFocus();
            return false;
        }
        if (!radioHome.isChecked() && !radioOffice.isChecked()) {
            Toast.makeText(this, "Please select an address type (Home/Office)", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void saveAddressToFirebase() {
        String addressType = radioHome.isChecked() ? "HOME" : "OFFICE";

        Map<String, Object> addressesMap = new HashMap<>();
        addressesMap.put("fullName", fullName.getText().toString().trim());
        addressesMap.put("mobile", "+91" + phoneNumber.getText().toString().trim());
        addressesMap.put("pinCode", pinCode.getText().toString().trim());
        addressesMap.put("state", state.getText().toString().trim());
        addressesMap.put("city", city.getText().toString().trim());
        addressesMap.put("house", house.getText().toString().trim());
        addressesMap.put("area", roadAreaColony.getText().toString().trim());
        addressesMap.put("addressType", addressType);

        if (!updateAddress) {
            addressesMap.put("selected", DbLoadData.addressesSelectModelList.isEmpty());
        } else {
            addressesMap.put("selected", addressesSelectModel != null && addressesSelectModel.getSelectAddresses());
        }

        String uid = auth.getUid();
        if (uid == null) {
            loadingDialog.dismiss();
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (updateAddress && addressesSelectModel != null) {
            db.collection("USER")
                    .document(uid)
                    .collection("MY_ADDRESSES")
                    .document(addressesSelectModel.getAddressID())
                    .set(addressesMap)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            updateLocalList(addressType);
                            finish();
                        } else {
                            String err = task.getException() != null ? task.getException().getMessage() : "Update Failed";
                            Toast.makeText(Add_delivery_address_Activity3.this, err, Toast.LENGTH_SHORT).show();
                        }
                        loadingDialog.dismiss();
                    });
        } else {
            db.collection("USER")
                    .document(uid)
                    .collection("MY_ADDRESSES")
                    .add(addressesMap)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            String newID = task.getResult().getId();
                            addNewToLocalList(newID, addressType);
                            finish();
                        } else {
                            String err = task.getException() != null ? task.getException().getMessage() : "Save Failed";
                            Toast.makeText(Add_delivery_address_Activity3.this, err, Toast.LENGTH_SHORT).show();
                        }
                        loadingDialog.dismiss();
                    });
        }
    }

    private void updateLocalList(String type) {
        DbLoadData.addressesSelectModelList.set(position, new AddressesSelectModel(
                fullName.getText().toString().trim(),
                "+91" + phoneNumber.getText().toString().trim(),
                pinCode.getText().toString().trim(),
                state.getText().toString().trim(),
                city.getText().toString().trim(),
                house.getText().toString().trim(),
                roadAreaColony.getText().toString().trim(),
                addressesSelectModel.getSelectAddresses(),
                addressesSelectModel.getAddressID(),
                type
        ));
        if (Select_Address_Activity3.addressAdapter != null) {
            Select_Address_Activity3.addressAdapter.notifyDataSetChanged();
        }
    }

    private void addNewToLocalList(String newID, String type) {
        boolean isSelected = DbLoadData.addressesSelectModelList.isEmpty();
        DbLoadData.addressesSelectModelList.add(new AddressesSelectModel(
                fullName.getText().toString().trim(),
                "+91" + phoneNumber.getText().toString().trim(),
                pinCode.getText().toString().trim(),
                state.getText().toString().trim(),
                city.getText().toString().trim(),
                house.getText().toString().trim(),
                roadAreaColony.getText().toString().trim(),
                isSelected,
                newID,
                type
        ));
        if (isSelected) {
            DbLoadData.selectedAddresses = DbLoadData.addressesSelectModelList.size() - 1;
        }
        if (Select_Address_Activity3.addressAdapter != null) {
            Select_Address_Activity3.addressAdapter.notifyDataSetChanged();
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
}
