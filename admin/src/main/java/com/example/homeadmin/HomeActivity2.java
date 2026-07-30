package com.example.homeadmin;


import static com.example.homeadmin.ui.DbLoadData.cartLis;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.homeadmin.ui.Cart.CartActivity;
import com.example.homeadmin.ui.DbLoadData;
import com.example.homeadmin.ui.finance.FinanceDashboardActivity;
import com.example.homeadmin.ui.helpSuppot.SupportActivity;
import com.example.homeadmin.ui.home.edit.HomeEditActivity;
import com.example.homeadmin.ui.notification.NotificationActivity;
import com.example.homeadmin.ui.quickServices.AddQuickServiceActivity;
import com.example.homeadmin.ui.quickServices.ManageQuickServicesActivity;
import com.example.homeadmin.ui.search.SearchActivity;
import com.example.homeadmin.user.UserSearchActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;


import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.homeadmin.databinding.ActivityHome2Binding;
import com.example.homeadmin.util.EdgeToEdgeUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class HomeActivity2 extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;
    public static DrawerLayout drawer;
    private Dialog singInDialog;

    private ImageView profileImage;
    private TextView profileName, profileEmail;

    private NavController navController;
    private NavigationView navigationView;


    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        ActivityHome2Binding binding = ActivityHome2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain2.toolbar);
        
        // Apply Insets
        EdgeToEdgeUtils.applyTopInset(binding.appBarMain2.toolbar);
//        binding.appBarMain2.fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction( "Action",  null).show());


        drawer = binding.drawerLayout;
        navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_dashboard, R.id.nav__home)
                .setOpenableLayout(drawer)
                .build();

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main2);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
            NavigationUI.setupWithNavController(navigationView, navController);
        }

        navigationView.setNavigationItemSelectedListener(this);

        mAuth = FirebaseAuth.getInstance();

        binding.appBarMain2.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity2.this, HomeEditActivity.class);
                startActivity(intent);

            }
        });


        //SingInDialog
        singInDialog = new Dialog(this);
        singInDialog.setContentView(R.layout.sing_in_dialog_layout);
        singInDialog.setCancelable(true);

        Objects.requireNonNull(singInDialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        Button dialogSingInBtn = singInDialog.findViewById(R.id.sing_in_btn);
        Button dialogSingUpBtn = singInDialog.findViewById(R.id.sing_up_btn);
        ImageView imageView = singInDialog.findViewById(R.id.dialog_image);
        imageView.setImageResource(R.drawable.address_icon);

        dialogSingInBtn.setOnClickListener(v -> {
            Intent login = new Intent(singInDialog.getContext(), MainActivity.class);
            startActivity(login);
            Toast.makeText(singInDialog.getContext(), "Activity login available", Toast.LENGTH_SHORT).show();


        });

        dialogSingUpBtn.setOnClickListener(v -> {
//            Intent register = new Intent(singInDialog.getContext(), RegisterActivity2.class);
//            startActivity(register);
            Toast.makeText(singInDialog.getContext(), "Activity Register available", Toast.LENGTH_SHORT).show();


        });

        //SingInDialog


        View headerView = navigationView.getHeaderView(0);
        if (headerView != null) {
            profileImage = headerView.findViewById(R.id.header_profile_image);
            profileName = headerView.findViewById(R.id.header_fullName);
            profileEmail = headerView.findViewById(R.id.header_emailId);
        } else {
            Log.e("HomeActivity2", "Navigation header view is null.");
        }
//        profileImage = navigationView.getHeaderView(0).findViewById(R.id.header_profile_image);
//        profileName = navigationView.getHeaderView(0).findViewById(R.id.header_fullName);
//        profileEmail = navigationView.getHeaderView(0).findViewById(R.id.header_emailId);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.men_search) {
            // search code w
            Toast.makeText(this, "Ok", Toast.LENGTH_SHORT).show();
            Intent searchIntent = new Intent(this, SearchActivity.class);
            startActivity(searchIntent);

            return true;

        } else if (id == R.id.men_bel) {
            if (mAuth.getCurrentUser() != null) {
                //DbLoadData.checkNotification(getApplicationContext(),false);
                Intent bellIntent = new Intent(HomeActivity2.this, NotificationActivity.class);
                startActivity(bellIntent);
            } else {
                singInDialog.show();
            }


            return true;

        } else if (id == R.id.menu_add) {
            //cart code w
            if (mAuth.getCurrentUser() != null) {
                Intent cartIntent = new Intent(HomeActivity2.this, CartActivity.class);
                startActivity(cartIntent);
            } else {
                singInDialog.show();
            }

            return true;

        } else if (id == R.id.men_quickOrders) {
            Intent addQuick = new Intent(this, ManageQuickServicesActivity.class);
            startActivity(addQuick);

            
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();


    }


    @Override
    protected void onStart() {
        super.onStart();
        invalidateOptionsMenu();

        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "Debug: User NOT logged in. Images may fail.", Toast.LENGTH_LONG).show();
            navigationView.getMenu().getItem(navigationView.getMenu().size() - 1).setEnabled(false);
        } else {
            Toast.makeText(this, "Debug: User logged in as " + mAuth.getCurrentUser().getPhoneNumber(), Toast.LENGTH_SHORT).show();
            String uid = mAuth.getUid();
            String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

            FirebaseFirestore.getInstance().collection("UsersAdmin").document(uid)
                    .collection("DEVICES").document(androidId).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (!task.getResult().exists()) {
                                mAuth.signOut();
                                Intent intent = new Intent(HomeActivity2.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                                Toast.makeText(HomeActivity2.this, "Logged out from another device", Toast.LENGTH_SHORT).show();
                            } else {
                                FirebaseFirestore.getInstance().collection("UsersAdmin").document(uid)
                                        .get().addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                DocumentSnapshot document = task1.getResult();
                                                if (document.exists()) {
                                                    DbLoadData.fullName = document.getString("Full Name");
                                                    DbLoadData.mobile = document.getString("mobile");
                                                    DbLoadData.email = document.getString("email");
                                                    DbLoadData.profileImage = document.getString("profile image");
                                                    DbLoadData.gender = document.getString("gender");

                                                    // Add null checks for views before using them
                                                    if (profileName != null) {
                                                        profileName.setText(DbLoadData.fullName);
                                                    }

                                                    if (profileEmail != null) {
                                                        profileEmail.setText(DbLoadData.email);
                                                    }

                                                    if (profileImage != null) {
                                                        if ("Male".equals(DbLoadData.gender)) {
                                                            Glide.with(HomeActivity2.this).load(DbLoadData.profileImage).apply(new RequestOptions().placeholder(R.drawable.male_avatar)).into(profileImage);
                                                        } else {
                                                            Glide.with(HomeActivity2.this).load(DbLoadData.profileImage).apply(new RequestOptions().placeholder(R.drawable.female_avatar)).into(profileImage);
                                                        }
                                                    }
                                                }
                                            } else {
                                                String error = task1.getException().getMessage();
                                                Toast.makeText(HomeActivity2.this, error, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        } else {
                            Log.e("HomeActivity2", "Device validation failed: " + task.getException().getMessage());
                        }
                    });

            navigationView.getMenu().getItem(navigationView.getMenu().size() - 1).setEnabled(true);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuth.getCurrentUser()!= null) {
           // DbLoadData.checkNotification(getApplicationContext(), true, null);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_finance_hub) {
            startActivity(new Intent(this, FinanceDashboardActivity.class));
            drawer.closeDrawers();
            return true;
        } else if (id == R.id.nav_user_search) {
            Intent searchIntent = new Intent(this, UserSearchActivity.class);
            startActivity(searchIntent);
            drawer.closeDrawers();
            return true;
        } else if (id == R.id.nav_management) {
            Intent managementIntent = new Intent(this, com.example.homeadmin.ui.management.MainManagementActivity.class);
            startActivity(managementIntent);
            drawer.closeDrawers();
            return true;
        } else if (id == R.id.nav_help_center) {
            Intent helpCenterIntent = new Intent(this, com.example.homeadmin.ui.helpCenter.HelpCenterActivity.class);
            startActivity(helpCenterIntent);
            drawer.closeDrawers();
            return true;
        } else if (id == R.id.nav_trash) {
            Intent trashIntent = new Intent(this, com.example.homeadmin.ui.trash.TrashActivity.class);
            startActivity(trashIntent);
            drawer.closeDrawers();
            return true;
        }

        // Handle other navigation items
        boolean handled = NavigationUI.onNavDestinationSelected(item, navController);
        if (handled) {
            drawer.closeDrawers();
        }
        return handled;
    }
}
