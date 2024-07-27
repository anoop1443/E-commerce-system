package com.example.homeelecation.ui.address;

import static com.example.homeelecation.ui.address.Select_Address_Activity3.refreshItem;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homeelecation.R;
import com.example.homeelecation.ui.DbLoadData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Select_Address_Activity3 extends AppCompatActivity {

    private int previsesAddress;

    Toolbar toolbar;
    RecyclerView recyclerView;
    Button radioGroup, addressesHare;

    private Dialog loadingDialog;

    public static AddressesSelectAdapter addressAdapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_addres3);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Address");



        //loading dialog
        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(true);

        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        // loadingDialog.show();
        //loading dialog


        previsesAddress = DbLoadData.selectedAddresses;

        recyclerView = findViewById(R.id.select_recycler);
        radioGroup = findViewById(R.id.select_addres_btn);
        addressesHare = findViewById(R.id.select_addresses_hare_btn);

        radioGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentAddress = new Intent(Select_Address_Activity3.this, Add_delivery_address_Activity3.class);
                intentAddress.putExtra("INTENT", "null");
                startActivity(intentAddress);
                finish();
            }
        });

        int mode = getIntent().getIntExtra("MODE", -1);

        if (mode == SELECT_ADDRESS){
            addressesHare.setVisibility(View.VISIBLE);

        }else if (mode == MANAGE_ADDRESS){
            addressesHare.setVisibility(View.INVISIBLE);


        }




            addressesHare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DbLoadData.selectedAddresses != previsesAddress) {
                    loadingDialog.show();
                    final int previsesAddressIndex = previsesAddress;

                    Map<String, Object> selectionAddresses = new HashMap<>();
                    selectionAddresses.put("selected_" + String.valueOf(previsesAddress+1), false);
                    selectionAddresses.put("selected_" + String.valueOf(DbLoadData.selectedAddresses+1), true);

                    previsesAddress = DbLoadData.selectedAddresses;

                    FirebaseFirestore.getInstance().collection("USER").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_ADDRESSES")
                            .update(selectionAddresses).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        finish();
                                    } else {
                                        previsesAddress = previsesAddressIndex;
                                        String error = task.getException().getMessage();
                                        Toast.makeText(Select_Address_Activity3.this, error, Toast.LENGTH_SHORT).show();
                                    }
                                    loadingDialog.dismiss();
                                }
                            });
                }else {
                    finish();
                }
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


//        List<AddressesSelectModel> addressesSelectModelList = new ArrayList<AddressesSelectModel>();
//
//        addressesSelectModelList.add(new AddressesSelectModel("Akash kumar","#13 gangan (Avatar election) bachauli auras unnao,Hasanganj,avatar election,hyderabad,uttar pradesh - 209870","8288943143",true));
//        addressesSelectModelList.add(new AddressesSelectModel("Archana ","#13 gangan (Avatar election) bachauli auras unnao,Hasanganj,avatar election,hyderabad,uttar pradesh - 209870","8288943143",false));
//        addressesSelectModelList.add(new AddressesSelectModel("Anchal ","#13 gangan (Avatar election) bachauli auras unnao,Hasanganj,avatar election,hyderabad,uttar pradesh - 209870","9651659191",false));
//        addressesSelectModelList.add(new AddressesSelectModel("Muskan Rathaur","#13 gangan (Avatar election) bachauli auras unnao,Hasanganj,avatar election,hyderabad,uttar pradesh - 209870","9198800581",false));
//        addressesSelectModelList.add(new AddressesSelectModel("Anjli Rawat","#13 gangan (Avatar election) bachauli auras unnao,Hasanganj,avatar election,hyderabad,uttar pradesh - 209870","9664262729",false));
//        addressesSelectModelList.add(new AddressesSelectModel("Alka verma","#13 gangan (Avatar election) bachauli auras unnao,Hasanganj,avatar election,hyderabad,uttar pradesh - 209870","9170095051",false));
//        addressesSelectModelList.add(new AddressesSelectModel("Anshika Sharma","#13 gangan (Avatar election) bachauli auras unnao,Hasanganj,avatar election,hyderabad,uttar pradesh - 209870","738089865",false));
//        addressesSelectModelList.add(new AddressesSelectModel("Madhu","#13 gangan (Avatar election) bachauli auras unnao,Hasanganj,avatar election,hyderabad,uttar pradesh - 209870","7355575503",false));
//        addressesSelectModelList.add(new AddressesSelectModel("Madhu Gupta","#13 gangan (Avatar election) bachauli auras unnao,Hasanganj,avatar election,hyderabad,uttar pradesh - 209870","7985743876",false));
//        addressesSelectModelList.add(new AddressesSelectModel("Chadni","#13 gangan (Avatar election) bachauli auras unnao,Hasanganj,avatar election,hyderabad,uttar pradesh - 209870","9125218445",false));
//        addressesSelectModelList.add(new AddressesSelectModel("Kanti ","#13 gangan (Avatar election) bachauli auras unnao,Hasanganj,avatar election,hyderabad,uttar pradesh - 209870","9888345623",false));
//        addressesSelectModelList.add(new AddressesSelectModel("Khushbu maurya","#13 gangan (Avatar election) bachauli auras unnao,Hasanganj,avatar election,hyderabad,uttar pradesh - 209870","8887877682",false));



        addressAdapter = new AddressesSelectAdapter(DbLoadData.addressesSelectModelList, mode);
        recyclerView.setAdapter(addressAdapter);
        addressAdapter.notifyDataSetChanged();


    }

    public static void refreshItem(int deSelect, int select) {
        addressAdapter.notifyItemChanged(deSelect);
        addressAdapter.notifyItemChanged(select);

    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home)
            if (DbLoadData.selectedAddresses != previsesAddress) {

                DbLoadData.addressesSelectModelList.get(DbLoadData.selectedAddresses).setSelectAddresses(false);
                DbLoadData.addressesSelectModelList.get(previsesAddress).setSelectAddresses(true);
                DbLoadData.selectedAddresses = previsesAddress;
            }

        finish();


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if (DbLoadData.selectedAddresses != previsesAddress) {

            DbLoadData.addressesSelectModelList.get(DbLoadData.selectedAddresses).setSelectAddresses(false);
            DbLoadData.addressesSelectModelList.get(previsesAddress).setSelectAddresses(true);
            DbLoadData.selectedAddresses = previsesAddress;
        }

        super.onBackPressed();
    }
}