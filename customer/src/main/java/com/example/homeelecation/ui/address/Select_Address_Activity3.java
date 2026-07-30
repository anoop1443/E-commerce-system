package com.example.homeelecation.ui.address;

import static com.example.homeelecation.ui.place.PLaceActivity3.SELECT_ADDRESS;
import static com.example.homeelecation.ui.profile.My_AccountFragment.MANAGE_ADDRESS;

import android.annotation.SuppressLint;
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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.example.homeelecation.R;
import com.example.homeelecation.ui.DbLoadData;

import java.util.ArrayList;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class Select_Address_Activity3 extends AppCompatActivity {

    private int previsesAddress;
    private int mode;
    private RecyclerView recyclerView;
    private Button addNewAddress, addressesHare;
    private Dialog loadingDialog;
    private AddressViewModel addressViewModel;

    public static AddressesSelectAdapter addressAdapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_addres3);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Select Address");

        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        previsesAddress = DbLoadData.selectedAddresses;

        recyclerView = findViewById(R.id.select_recycler);
        addNewAddress = findViewById(R.id.select_addres_btn);
        addressesHare = findViewById(R.id.select_addresses_hare_btn);

        if (recyclerView.getItemAnimator() != null) {
            ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        }

        mode = getIntent().getIntExtra("MODE", -1);
        if (mode == SELECT_ADDRESS) {
            addressesHare.setVisibility(View.VISIBLE);
        } else if (mode == MANAGE_ADDRESS) {
            addressesHare.setVisibility(View.GONE);
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        addressAdapter = new AddressesSelectAdapter(DbLoadData.addressesSelectModelList, mode, loadingDialog);
        recyclerView.setAdapter(addressAdapter);

        // ViewModel Setup
        addressViewModel = new ViewModelProvider(this).get(AddressViewModel.class);
        setupViewModelObservers();

        addNewAddress.setOnClickListener(v -> {
            Intent intentAddress = new Intent(Select_Address_Activity3.this, Add_delivery_address_Activity3.class);
            intentAddress.putExtra("INTENT", (mode != SELECT_ADDRESS) ? "select_address" : "null");
            startActivity(intentAddress);
        });

        addressesHare.setOnClickListener(v -> {
            if (DbLoadData.selectedAddresses != previsesAddress) {
                String oldAddressID = DbLoadData.addressesSelectModelList.get(previsesAddress).getAddressID();
                String newAddressID = DbLoadData.addressesSelectModelList.get(DbLoadData.selectedAddresses).getAddressID();
                addressViewModel.updateSelectedAddress(oldAddressID, newAddressID);
            } else {
                finish();
            }
        });
    }

    private void setupViewModelObservers() {
        addressViewModel.isLoading.observe(this, isLoading -> {
            if (isLoading != null && isLoading) loadingDialog.show();
            else loadingDialog.dismiss();
        });

        addressViewModel.selectionUpdated.observe(this, updated -> {
            if (updated != null && updated) {
                addressViewModel.onNavigationComplete();
                finish();
            }
        });

        addressViewModel.error.observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
                // Rollback if needed
                DbLoadData.selectedAddresses = previsesAddress;
            }
        });
    }

    public static void refreshItem(int deSelect, int select) {
        if (addressAdapter != null) {
            addressAdapter.notifyItemChanged(deSelect);
            addressAdapter.notifyItemChanged(select);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            handleBackAction();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        handleBackAction();
        super.onBackPressed();
    }

    private void handleBackAction() {
        if (mode == SELECT_ADDRESS) {
            if (DbLoadData.selectedAddresses != previsesAddress) {
                DbLoadData.addressesSelectModelList.get(DbLoadData.selectedAddresses).setSelectAddresses(false);
                DbLoadData.addressesSelectModelList.get(previsesAddress).setSelectAddresses(true);
                DbLoadData.selectedAddresses = previsesAddress;
            }
        }
        finish();
    }
}
