package com.example.homeadmin.ui.wishList;



import static com.example.homeadmin.ui.DbLoadData.wishLisT;
import static com.example.homeadmin.ui.DbLoadData.wishlistModelList;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homeadmin.MainActivity;
import com.example.homeadmin.R;
import com.google.firebase.auth.FirebaseAuth;


public class Wishlist_Fragment extends Fragment {
    RecyclerView recyclerView;

    public static Dialog loadingDialog;
    private Dialog singInDialog;
    public static WishlistAdapter wishlistAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_wishlist_, container, false);

        recyclerView = view.findViewById(R.id.wishlist_recyclervieww);

        // dialog
        singInDialog = new Dialog(view.getContext());
        singInDialog.setContentView(R.layout.sing_in_dialog_layout);
        singInDialog.setCancelable(true);

        singInDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        Button dialogSingInBtn = singInDialog.findViewById(R.id.sing_in_btn);
        Button dialogSingUpBtn = singInDialog.findViewById(R.id.sing_up_btn);
        ImageView imageView = singInDialog.findViewById(R.id.dialog_image);
        imageView.setImageResource(R.drawable.address_icon);

        dialogSingInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent login = new Intent(singInDialog.getContext(), MainActivity.class);
                startActivity(login);
                Toast.makeText(singInDialog.getContext(), "Activity login available", Toast.LENGTH_SHORT).show();


            }
        });

        dialogSingUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent register = new Intent(singInDialog.getContext(), RegisterActivity2.class);
//                startActivity(register);
                Toast.makeText(singInDialog.getContext(), "Activity Register available", Toast.LENGTH_SHORT).show();


            }

        });
        // dialog


        //loading dialog
        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(true);

        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();
        //loading dialog

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(layoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            if (wishlistModelList.size() == 0) {
                wishLisT.clear();
              //  loadWishList(getContext(), loadingDialog, true);

            } else {

                //singInDialog.show();
                loadingDialog.dismiss();
            }

            wishlistAdapter = new WishlistAdapter(wishlistModelList, true);
            recyclerView.setAdapter(wishlistAdapter);
            wishlistAdapter.notifyDataSetChanged();

        } else {
            loadingDialog.dismiss();
            singInDialog.show();
        }

        return view;
    }
}