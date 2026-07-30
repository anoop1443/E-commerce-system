package com.example.homeadmin.ui.Cart;

import static com.example.homeadmin.ui.DbLoadData.cartLis;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homeadmin.MainActivity;
import com.example.homeadmin.R;
import com.example.homeadmin.ui.DbLoadData;
import com.example.homeadmin.ui.place.PLaceActivity3;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;


public class CartFragment extends Fragment {



    private RecyclerView recyclerView;
    public static Dialog loadingDialog;
    private Dialog singInDialog;
    public static CartAdapter cartAdapter;
    public static  boolean currentFragmentC;
    private  TextView totalAmount;
    private Button buyNow;





    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        recyclerView = view.findViewById(R.id.cart_RecyclerView);
        totalAmount = view.findViewById(R.id.total_cart_AmountF);
        buyNow = view.findViewById(R.id.place_order_bt);





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
            singInDialog.dismiss();
            LinearLayout parent = (LinearLayout) totalAmount.getParent();

            if (DbLoadData.cartItemModelList.isEmpty()) {
                cartLis.clear();
               // DbLoadData.loadCartList(getContext(), loadingDialog, true,new TextView(getContext()),totalAmount);
                //parent.setVisibility(View.VISIBLE);

            } else {
                if (DbLoadData.cartItemModelList.get(DbLoadData.cartItemModelList.size()-1).getType()== CartItemModel.CART_TOTAL_AMOUNT_LAYOUT){
                    parent.setVisibility(View.VISIBLE);

                }
                loadingDialog.dismiss();
            }

            cartAdapter = new CartAdapter(DbLoadData.cartItemModelList,totalAmount,true);
            recyclerView.setAdapter(cartAdapter);
            //cartAdapter.notifyDataSetChanged();
            cartAdapter.notifyDataSetChanged();

        } else {
            loadingDialog.dismiss();
            singInDialog.show();
        }



        buyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.show();
                if (FirebaseAuth.getInstance().getCurrentUser()  == null) {
                    singInDialog.show();

                } else {
                    loadingDialog.show();

                    PLaceActivity3.cartItemModelList = new ArrayList<>();
                    for (int x = 0;x<DbLoadData.cartItemModelList.size();x++){
                        CartItemModel cartItemModel = DbLoadData.cartItemModelList.get(x);
                        if (cartItemModel.isInStock()){
                            PLaceActivity3.cartItemModelList.add(cartItemModel);
                        }
                    }
                    PLaceActivity3.cartItemModelList.add(new CartItemModel(CartItemModel.CART_TOTAL_AMOUNT_LAYOUT));

                    if (DbLoadData.addressesSelectModelList.size()==0) {
                      //  DbLoadData.loadAddresses(getContext(), loadingDialog,true);
                    }else {
                        loadingDialog.dismiss();
                        Intent intent = new Intent(getContext(),PLaceActivity3.class);
                        startActivity(intent);
                    }

                }
            }
        });





        return view;
    }



    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.menu_add).setVisible(false);
        menu.findItem(R.id.men_bel).setVisible(false);
        menu.findItem(R.id.men_search).setVisible(false);
    }

    @Override
    public void onStart() {
        cartAdapter = new CartAdapter(DbLoadData.cartItemModelList,totalAmount,true);
        recyclerView.setAdapter(cartAdapter);
        //cartAdapter.notifyDataSetChanged();
        cartAdapter.notifyDataSetChanged();

        super.onStart();
    }
}