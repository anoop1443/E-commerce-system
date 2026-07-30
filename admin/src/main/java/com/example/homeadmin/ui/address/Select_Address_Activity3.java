package com.example.homeadmin.ui.address;

import static com.example.homeadmin.ui.place.PLaceActivity3.SELECT_ADDRESS;
import static com.example.homeadmin.ui.profile.My_AccountFragment.MANAGE_ADDRESS;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homeadmin.R;
import com.example.homeadmin.ui.DbLoadData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

public class Select_Address_Activity3 extends AppCompatActivity {

    private int previousAddressIndex;
    private int mode;

    Toolbar toolbar;
    RecyclerView recyclerView;
    Button addNewAddress, selectThisAddressBtn;

    private Dialog loadingDialog;
    public static AddressesSelectAdapter addressAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_addres3);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle("Select Address");
        }

        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        previousAddressIndex = DbLoadData.selectedAddresses;
        recyclerView = findViewById(R.id.select_recycler);
        addNewAddress = findViewById(R.id.select_addres_btn);
        selectThisAddressBtn = findViewById(R.id.select_addresses_hare_btn);

        mode = getIntent().getIntExtra("MODE", -1);
        
        if (mode == SELECT_ADDRESS) {
            selectThisAddressBtn.setVisibility(View.VISIBLE);
        } else {
            selectThisAddressBtn.setVisibility(View.GONE);
            if (getSupportActionBar() != null) getSupportActionBar().setTitle("Manage Addresses");
        }

        addNewAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentAddress = new Intent(Select_Address_Activity3.this, Add_delivery_address_Activity3.class);
                intentAddress.putExtra("INTENT", "null");
                startActivity(intentAddress);
            }
        });

        selectThisAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DbLoadData.selectedAddresses != previousAddressIndex) {
                    saveSelectionToFirestore();
                } else {
                    finish();
                }
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        addressAdapter = new AddressesSelectAdapter(DbLoadData.addressesSelectModelList, mode, loadingDialog);
        recyclerView.setAdapter(addressAdapter);
    }

    private void saveSelectionToFirestore() {
        loadingDialog.show();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String uid = FirebaseAuth.getInstance().getUid();
        
        if (uid == null) {
            loadingDialog.dismiss();
            return;
        }

        WriteBatch batch = db.batch();
        
        // Deselect previous
        if (previousAddressIndex != -1 && previousAddressIndex < DbLoadData.addressesSelectModelList.size()) {
            String prevID = DbLoadData.addressesSelectModelList.get(previousAddressIndex).getAddressID();
            if (prevID != null) {
                batch.update(db.collection("USER").document(uid).collection("MY_ADDRESSES").document(prevID), "selected", false);
            }
        }
        
        // Select new
        String newID = DbLoadData.addressesSelectModelList.get(DbLoadData.selectedAddresses).getAddressID();
        if (newID != null) {
            batch.update(db.collection("USER").document(uid).collection("MY_ADDRESSES").document(newID), "selected", true);
        }

        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                loadingDialog.dismiss();
                if (task.isSuccessful()) {
                    finish();
                } else {
                    String error = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                    Toast.makeText(Select_Address_Activity3.this, error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static void refreshItem(int deSelect, int select) {
        addressAdapter.notifyItemChanged(deSelect);
        addressAdapter.notifyItemChanged(select);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (mode == SELECT_ADDRESS && DbLoadData.selectedAddresses != previousAddressIndex) {
                // Revert selection if user exits without saving
                if (DbLoadData.selectedAddresses != -1) {
                    DbLoadData.addressesSelectModelList.get(DbLoadData.selectedAddresses).setSelected(false);
                }
                if (previousAddressIndex != -1) {
                    DbLoadData.addressesSelectModelList.get(previousAddressIndex).setSelected(true);
                }
                DbLoadData.selectedAddresses = previousAddressIndex;
            }
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Always reload addresses to ensure latest data
        DbLoadData.loadAddresses(this, loadingDialog, false);
        addressAdapter.notifyDataSetChanged();
    }
}
