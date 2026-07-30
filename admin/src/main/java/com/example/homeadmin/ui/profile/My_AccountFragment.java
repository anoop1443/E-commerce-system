package com.example.homeadmin.ui.profile;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.homeadmin.R;
import com.example.homeadmin.ui.DbLoadData;
import com.example.homeadmin.ui.address.Select_Address_Activity3;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class My_AccountFragment extends Fragment {


    public static final int MANAGE_ADDRESS = 1;
    Button viewAllAddress, signOut;
    private FloatingActionButton profileSettingBtn;
    private FirebaseAuth firebaseAuth;
    private ProgressBar progressBar;
    private TextView profileName,profileEmail,profileMobile;
    private ImageView profileImage;

    private TextView addressName,address,addressPinCode;

    private Dialog loadingDialog;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my__account, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        viewAllAddress = view.findViewById(R.id.my_address_viewAll_btn);
        signOut = view.findViewById(R.id.my_account_sign_out_btn);

        profileName = view.findViewById(R.id.account_fragment_fullName);
        profileEmail = view.findViewById(R.id.account_fragment_Email);
        profileImage = view.findViewById(R.id.account_fragment_profile_image);
        profileSettingBtn = view.findViewById(R.id.profile_settings_btn);

        addressName = view.findViewById(R.id.my_name);
        address = view.findViewById(R.id.my_address);
        addressPinCode = view.findViewById(R.id.my_address_pincode);


        //loading dialog
        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(true);

        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        // loadingDialog.show();
        //loading dialog



        setAddress();



        viewAllAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.show();
                Intent intent = new Intent(getContext(), Select_Address_Activity3.class);
                intent.putExtra("MODE",MANAGE_ADDRESS);
                startActivity(intent);
            }
        });


        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseAuth.getInstance().signOut();
            }
        });

        if (DbLoadData.addressesSelectModelList.size()==0){
            addressName.setText("no address");
            addressPinCode.setText("-");
            address.setText("-");

        }else {
            setAddress();
        }


        profileSettingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),UpDateProfileActivity.class);
                intent.putExtra("NAME",profileName.getText().toString());
                intent.putExtra("EMAIL",profileEmail.getText().toString());
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (DbLoadData.addressesSelectModelList.size()==0){
            addressName.setText("no address");
            addressPinCode.setText("-");
            address.setText("-");

        }else {
            setAddress();
        }


        if (firebaseAuth.getCurrentUser()!= null){
            signOut.setClickable(true);
            DbLoadData.clearData();

        }else {
            signOut.setClickable(false);
            signOut.setBackgroundTintList(getContext().getResources().getColorStateList(R.color.gone));

        }

    }
    private void setAddress(){
        loadingDialog.show();
        FirebaseFirestore.getInstance().collection("UsersAdmin").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DbLoadData.fullName = task.getResult().getString("Full Name");
                            DbLoadData.mobile = task.getResult().getString("mobile");
                            DbLoadData.email = task.getResult().getString("email");
                            DbLoadData.profileImage = task.getResult().getString("profile image");

                            profileName.setText(DbLoadData.fullName);
                            profileEmail.setText(DbLoadData.email);

                            Glide.with(getContext()).load(DbLoadData.profileImage).apply(new RequestOptions().placeholder(R.drawable.male_avatar)).into(profileImage);


                        }else {
                            String error = task.getException().getMessage();
                            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                        }
                        loadingDialog.dismiss();
                    }
                });
        if (DbLoadData.addressesSelectModelList.size()==0){
            addressName.setText("no address");
            addressPinCode.setText("-");
            address.setText("-");

            }else {

            addressName.setText(DbLoadData.addressesSelectModelList.get(DbLoadData.selectedAddresses).getFullName());
            addressPinCode.setText(DbLoadData.addressesSelectModelList.get(DbLoadData.selectedAddresses).getMobileNumber());

            String pinCode = DbLoadData.addressesSelectModelList.get(DbLoadData.selectedAddresses).getPinCode();
            String state = DbLoadData.addressesSelectModelList.get(DbLoadData.selectedAddresses).getState();
            String city = DbLoadData.addressesSelectModelList.get(DbLoadData.selectedAddresses).getCity();
            String house = DbLoadData.addressesSelectModelList.get(DbLoadData.selectedAddresses).getHouse();
            String roadAreaColony = DbLoadData.addressesSelectModelList.get(DbLoadData.selectedAddresses).getRoadAreaColony();

            address.setText(house + " " + roadAreaColony + " " + city + " " + state + " " + pinCode);
        }

    }
}