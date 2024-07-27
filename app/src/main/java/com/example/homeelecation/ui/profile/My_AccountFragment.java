package com.example.homeelecation.ui.profile;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.example.homeelecation.R;
import com.example.homeelecation.ui.DbLoadData;
import com.example.homeelecation.ui.address.Select_Address_Activity3;
import com.google.firebase.auth.FirebaseAuth;


public class My_AccountFragment extends Fragment {


    public static final int MANAGE_ADDRESS = 1;
    Button viewAllAddress, signOut;
    private FirebaseAuth firebaseAuth;
    private ProgressBar progressBar;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my__account, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        viewAllAddress = view.findViewById(R.id.my_address_viewAll_btn);
        signOut = view.findViewById(R.id.my_account_sign_out_btn);



        viewAllAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (firebaseAuth.getCurrentUser()!= null){
            signOut.setClickable(true);
            DbLoadData.clearData();

        }else {
            signOut.setClickable(false);
            signOut.setBackgroundTintList(getContext().getResources().getColorStateList(R.color.gone));



        }

    }
}