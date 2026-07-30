package com.example.homeadmin.ui.address;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.homeadmin.R;
import com.example.homeadmin.ui.DbLoadData;
import com.example.homeadmin.ui.place.PLaceActivity3;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Add_delivery_address_Activity3 extends AppCompatActivity {
    private Toolbar toolbar;
    private EditText fullName, phoneNumber, pinCode, state, city, house, area;

    private RadioGroup addressTypeGroup;
    private Button saveAddressBtn;
    private boolean isUpdate = false;
    private AddressesSelectModel modelToUpdate;
    private int position;
    private Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_delivary_addres3);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle("Add Address");
        }

        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        fullName = findViewById(R.id.add_delivery_address_fullName);
        phoneNumber = findViewById(R.id.add_delivery_address_phone);
        pinCode = findViewById(R.id.add_delivery_address_pinCode);
        state = findViewById(R.id.add_delivery_address_state);
        city = findViewById(R.id.add_delivery_address_city);
        house = findViewById(R.id.add_delivery_address_house);
        area = findViewById(R.id.add_delivery_address_road);
        addressTypeGroup = findViewById(R.id.add_delivery_address_type_group);
        saveAddressBtn = findViewById(R.id.add_delivery_address_save_btn);

        String intentType = getIntent().getStringExtra("INTENT");
        if ("update_address".equals(intentType)) {
            isUpdate = true;
            position = getIntent().getIntExtra("index", -1);
            if (position != -1 && position < DbLoadData.addressesSelectModelList.size()) {
                modelToUpdate = DbLoadData.addressesSelectModelList.get(position);

                fullName.setText(modelToUpdate.getFullName());
                // Remove +91 prefix if exists for editing
                String mobile = modelToUpdate.getMobile();
                if (mobile != null && mobile.startsWith("+91")) {
                    phoneNumber.setText(mobile.substring(3));
                } else {
                    phoneNumber.setText(mobile);
                }
                pinCode.setText(modelToUpdate.getPinCode());
                state.setText(modelToUpdate.getState());
                city.setText(modelToUpdate.getCity());
                house.setText(modelToUpdate.getHouse());
                area.setText(modelToUpdate.getArea());
                
                if ("Office".equalsIgnoreCase(modelToUpdate.getAddressType())) {
                    addressTypeGroup.check(R.id.add_delivery_address_office);
                } else {
                    addressTypeGroup.check(R.id.add_delivery_address_home);
                }

                saveAddressBtn.setText("Update Address");
                getSupportActionBar().setTitle("Update Address");
            }
        }

        saveAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAndSave();
            }
        });
    }

    private void validateAndSave() {
        if (TextUtils.isEmpty(fullName.getText())) {
            fullName.setError("Required");
            return;
        }
        if (TextUtils.isEmpty(phoneNumber.getText()) || phoneNumber.getText().length() < 10) {
            phoneNumber.setError("Invalid mobile number");
            return;
        }
        if (TextUtils.isEmpty(pinCode.getText()) || pinCode.getText().length() != 6) {
            pinCode.setError("Invalid PinCode");
            return;
        }
        if (TextUtils.isEmpty(state.getText())) {
            state.setError("Required");
            return;
        }
        if (TextUtils.isEmpty(city.getText())) {
            city.setError("Required");
            return;
        }
        if (TextUtils.isEmpty(house.getText())) {
            house.setError("Required");
            return;
        }
        if (TextUtils.isEmpty(area.getText())) {
            area.setError("Required");
            return;
        }

        String type = "Home";
        if (addressTypeGroup.getCheckedRadioButtonId() == R.id.add_delivery_address_office) {
            type = "Office";
        }

        loadingDialog.show();

        final String finalFullName = fullName.getText().toString();
        final String finalMobile = "+91" + phoneNumber.getText().toString();
        final String finalPinCode = pinCode.getText().toString();
        final String finalState = state.getText().toString();
        final String finalCity = city.getText().toString();
        final String finalHouse = house.getText().toString();
        final String finalArea = area.getText().toString();
        final String finalType = type;

        Map<String, Object> addressData = new HashMap<>();
        addressData.put("fullName", finalFullName);
        addressData.put("mobile", finalMobile);
        addressData.put("pinCode", finalPinCode);
        addressData.put("state", finalState);
        addressData.put("city", finalCity);
        addressData.put("house", finalHouse);
        addressData.put("area", finalArea);
        addressData.put("addressType", finalType);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String uid = FirebaseAuth.getInstance().getUid();
        
        if (uid == null) {
            loadingDialog.dismiss();
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        DocumentReference addressRef;
        if (isUpdate && modelToUpdate != null) {
            addressRef = db.collection("USER").document(uid).collection("MY_ADDRESSES").document(modelToUpdate.getAddressID());
            addressData.put("selected", modelToUpdate.isSelected());
        } else {
            addressRef = db.collection("USER").document(uid).collection("MY_ADDRESSES").document();
            // If it's the first address, mark it as selected
            addressData.put("selected", DbLoadData.addressesSelectModelList.isEmpty());
        }

        addressRef.set(addressData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                loadingDialog.dismiss();
                if (task.isSuccessful()) {
                    AddressesSelectModel newModel = new AddressesSelectModel(
                            finalFullName, finalMobile, finalPinCode, finalState, finalCity,
                            finalHouse, finalArea, (boolean) addressData.get("selected"), addressRef.getId()
                    );
                    newModel.setAddressType(finalType);

                    if (isUpdate) {
                        DbLoadData.addressesSelectModelList.set(position, newModel);
                    } else {
                        if (newModel.isSelected()) {
                            DbLoadData.selectedAddresses = DbLoadData.addressesSelectModelList.size();
                        }
                        DbLoadData.addressesSelectModelList.add(newModel);
                    }

                    handleNavigation();
                } else {
                    String error = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                    Toast.makeText(Add_delivery_address_Activity3.this, error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void handleNavigation() {
        String intentType = getIntent().getStringExtra("INTENT");
        if ("deliveryIntent".equals(intentType)) {
            Intent deliveryIntent = new Intent(this, PLaceActivity3.class);
            startActivity(deliveryIntent);
        } else if ("select_address".equals(intentType)) {
            // Already handled by finishing if calling activity refreshes
        }
        finish();
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
