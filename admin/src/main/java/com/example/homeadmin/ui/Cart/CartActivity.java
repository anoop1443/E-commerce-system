package com.example.homeadmin.ui.Cart;


import static com.example.homeadmin.ui.Cart.CartFragment.cartAdapter;
import static com.example.homeadmin.ui.DbLoadData.cartLis;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homeadmin.MainActivity;
import com.example.homeadmin.R;
import com.example.homeadmin.ui.DbLoadData;
import com.example.homeadmin.ui.place.PLaceActivity3;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView;

    //public static Dialog loadingDialog;
    private Dialog singInDialog;
    //public static CartAdapter cartAdapter;

    private  TextView totalAmount;
    private Button buyNow;
    private  Dialog loadingDialog;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("My Cart");


        recyclerView = findViewById(R.id.activity_cart_RecyclerView);
        totalAmount = findViewById(R.id.total_place_Amount);
        buyNow = findViewById(R.id.place_order_bt);

        //loading dialog
        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(true);

        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();
        //loading dialog

        // dialog
        singInDialog = new Dialog(this);
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


        LinearLayoutManager layoutManager = new LinearLayoutManager(CartActivity.this);
        layoutManager.setOrientation(layoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);


        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            singInDialog.dismiss();
            LinearLayout parent = (LinearLayout) totalAmount.getParent();

            if (DbLoadData.cartItemModelList.isEmpty()) {
                cartLis.clear();
                //DbLoadData.loadCartList(CartActivity.this, loadingDialog, true,new TextView(CartActivity.this),totalAmount);
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
                       // DbLoadData.loadAddresses(CartActivity.this, loadingDialog,true);
                    }else {
                        loadingDialog.dismiss();
                        Intent intent = new Intent(CartActivity.this,PLaceActivity3.class);
                        startActivity(intent);
                    }

                }
            }
        });


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_icon,menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id==R.id.men_search){
            // search code w
            Toast.makeText(this, "Ok", Toast.LENGTH_SHORT).show();

            return true;


        }else if (id==R.id.menu_add) {
            //cart code w
            Toast.makeText(this, "please wait ", Toast.LENGTH_SHORT).show();

            return true;
        }else if (id == android.R.id.home){
            finish();
            return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

            cartAdapter = new CartAdapter(DbLoadData.cartItemModelList,totalAmount,true);
            recyclerView.setAdapter(cartAdapter);
            //cartAdapter.notifyDataSetChanged();
            cartAdapter.notifyDataSetChanged();




    }
}