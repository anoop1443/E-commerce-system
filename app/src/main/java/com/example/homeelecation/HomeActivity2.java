package com.example.homeelecation;

import static android.text.TextUtils.replace;
import static com.example.homeelecation.ui.DbLoadData.cartLis;
import static com.example.homeelecation.ui.DbLoadData.loadCartList;
import static com.example.homeelecation.ui.details.ProductDeteilsActivity.ALREADY_ADDED_TO_CART;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.homeelecation.ui.Cart.CartActivity;
import com.example.homeelecation.ui.Cart.CartFragment;
import com.example.homeelecation.ui.DbLoadData;
import com.example.homeelecation.ui.address.Select_Address_Activity3;
import com.example.homeelecation.ui.details.ProductDeteilsActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;



import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.homeelecation.databinding.ActivityHome2Binding;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class HomeActivity2 extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityHome2Binding binding;
    public static DrawerLayout drawer;
    private Dialog singInDialog;
    private Dialog loadingDialog;

    public static MenuItem cartItem;
    private   TextView badgeCount;


    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHome2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarHome2.toolbar);
        binding.appBarHome2.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav__home)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home2);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


        mAuth = FirebaseAuth.getInstance();


     //SingInDialog
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
                Intent register = new Intent(singInDialog.getContext(), RegisterActivity2.class);
                startActivity(register);
                Toast.makeText(singInDialog.getContext(), "Activity Register available", Toast.LENGTH_SHORT).show();


            }

        });

        //SingInDialog



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_activity2, menu);
           MenuItem cartItem = menu.findItem(R.id.men_cart);


            cartItem.setActionView(R.layout.badge_layout);
            ImageView badgeIcon = Objects.requireNonNull(cartItem.getActionView()).findViewById(R.id.badge_icon);
            badgeIcon.setImageResource(R.drawable.ic_cart);
            badgeCount = cartItem.getActionView().findViewById(R.id.badge_count);

            if (mAuth.getCurrentUser()!=null){
                if (cartLis.size()==0){
                    DbLoadData.loadCartList(this,new Dialog(HomeActivity2.this),false,badgeCount,new TextView(this));
                }else {
                    badgeCount.setVisibility(View.VISIBLE);
                    if (cartLis.size() < 99) {
                        badgeCount.setText(String.valueOf(cartLis.size()));
                    } else {
                        badgeCount.setText("99");
                    }
                }
            }

            cartItem.getActionView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mAuth.getCurrentUser() != null) {
                        //if (cartLis.size()!=0) {
                            Intent cartIntent = new Intent(HomeActivity2.this, CartActivity.class);
                            startActivity(cartIntent);
                       // }
                        Toast.makeText(HomeActivity2.this, "Please shopping", Toast.LENGTH_SHORT).show();
                    } else {
                        singInDialog.show();
                    }
                }
            });



        return true;

    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.men_search) {
            // search code w
            Toast.makeText(this, "Ok", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, Select_Address_Activity3.class);
            startActivity(intent);

            return true;

        } else if (id == R.id.men_bel) {


            return true;

        } else if (id == R.id.men_cart) {
            //cart code w
            if (mAuth.getCurrentUser() != null) {
                if (cartLis.size()!=0) {
                    Intent cartIntent = new Intent(HomeActivity2.this, CartActivity.class);
                    startActivity(cartIntent);
                }
                Toast.makeText(HomeActivity2.this, "Please shopping", Toast.LENGTH_SHORT).show();
            } else {
                singInDialog.show();
            }

            return true;

        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home2);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();



    }



    @Override
    protected void onStart() {

        invalidateOptionsMenu();

        super.onStart();
    }
}