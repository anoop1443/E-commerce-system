package com.example.homeelecation.ui.address;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.homeelecation.R;
import com.example.homeelecation.ui.DbLoadData;
import com.example.homeelecation.ui.place.PLaceActivity3;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Add_delivery_address_Activity3 extends AppCompatActivity {
    private Toolbar toolbar;
    private EditText addressFullName, addressPhone, addressPinCode, addressState, addressCity, addressHouse, addressRoad;

    private RadioButton radioHome, radioOffice;
    private Button saveAddress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_delivary_addres3);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Address");

        addressFullName = findViewById(R.id.add_delivery_address_fullName);
        addressPhone = findViewById(R.id.add_delivery_address_phone);
        addressPinCode = findViewById(R.id.add_delivery_address_pinCode);
        addressState = findViewById(R.id.add_delivery_address_state);
        addressCity = findViewById(R.id.add_delivery_address_city);
        addressHouse = findViewById(R.id.add_delivery_address_house);
        addressRoad = findViewById(R.id.add_delivery_address_road);


        radioHome = findViewById(R.id.add_delivery_address_home);
        radioOffice = findViewById(R.id.add_delivery_address_office);
        saveAddress = findViewById(R.id.add_delivery_address_save_btn);


        saveAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!TextUtils.isEmpty(addressFullName.getText())) {
                    if (!TextUtils.isEmpty(addressPhone.getText())&& addressPhone.getText().length()>=10) {
                        if (!TextUtils.isEmpty(addressPinCode.getText()) && addressPinCode.getText().length() == 6) {
                            if (!TextUtils.isEmpty(addressHouse.getText())) {
                                if (!TextUtils.isEmpty(addressRoad.getText())) {

                                    String addressesFull = addressHouse.getText().toString() +","+ addressRoad.getText().toString();

                                    Map<String, Object> addressesMap = new HashMap<>();
                                    addressesMap.put("list_size", (long) DbLoadData.addressesSelectModelList.size() + 1);
                                    addressesMap.put("fullName_" + String.valueOf((long)DbLoadData.addressesSelectModelList.size() + 1), addressFullName.getText().toString());
                                    addressesMap.put("addresses_" + String.valueOf((long)DbLoadData.addressesSelectModelList.size() + 1), addressesFull);
                                    addressesMap.put("addresses_phone_" +String.valueOf((long)DbLoadData.addressesSelectModelList.size() + 1),"+91"+addressPhone.getText().toString());
                                    addressesMap.put("selected_" +String.valueOf((long)DbLoadData.addressesSelectModelList.size() + 1), true);

                                    if (DbLoadData.addressesSelectModelList.size() > 0){
                                    addressesMap.put("selected_" +String.valueOf(DbLoadData.selectedAddresses + 1), false);
                                    }


                                    FirebaseFirestore.getInstance().collection("USER").document(FirebaseAuth.getInstance().getUid())
                                            .collection("USER_DATA").document("MY_ADDRESSES")
                                            .update(addressesMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {

                                                        if (DbLoadData.addressesSelectModelList.size() >0){
                                                            DbLoadData.addressesSelectModelList.get(DbLoadData.selectedAddresses).setSelectAddresses(false);
                                                        }
                                                        DbLoadData.addressesSelectModelList.add(new AddressesSelectModel(addressFullName.getText().toString(), addressesFull, addressPhone.getText().toString(), true));



                                                        if (getIntent().getStringExtra("INTENT").equals("deliveryIntent")) {
                                                            Intent deliveryIntent = new Intent(Add_delivery_address_Activity3.this, PLaceActivity3.class);
                                                            startActivity(deliveryIntent);
                                                        }else {
                                                            Select_Address_Activity3.refreshItem(DbLoadData.selectedAddresses,DbLoadData.addressesSelectModelList.size()-1);
                                                        }
                                                        DbLoadData.selectedAddresses = DbLoadData.addressesSelectModelList.size() -1;
                                                        finish();


                                                    } else {
                                                        String error = task.getException().getMessage();
                                                        Toast.makeText(Add_delivery_address_Activity3.this, error, Toast.LENGTH_SHORT).show();

                                                    }

                                                }
                                            });


                                } else {
                                    addressRoad.setError("Empty");
                                    addressRoad.requestFocus();
                                }

                            } else {
                                addressHouse.setError("Empty");
                                addressHouse.requestFocus();
                            }

                        } else {
                            addressPinCode.setError("Enter 6 Digit no");
                            addressPinCode.requestFocus();
                        }

                    } else {
                        addressPhone.setError("please valid no");
                        addressPhone.requestFocus();
                    }

                } else {
                    addressFullName.setError("Empty");
                    addressFullName.requestFocus();
                }
            }
        });
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home)
            finish();

//            if (id==R.id.men_search){
//                // search code w
//                Toast.makeText(this, "Ok", Toast.LENGTH_SHORT).show();
//
//                return true;
//
//
//            }else if (id==R.id.men_cart) {
//                //cart code w
//                Toast.makeText(this, "please wait ", Toast.LENGTH_SHORT).show();
//
//                return true;
//            }else if (id == android.R.id.home){
//                finish();
//                return true;
//
//            }
//
        return super.onOptionsItemSelected(item);


    }

}