package com.example.homeelecation;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.TooltipCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.homeelecation.databinding.ActivityHome2Binding;
import com.example.homeelecation.ui.Cart.CartViewModel;
import com.example.homeelecation.ui.notification.NotificationViewModel;
import com.example.homeelecation.ui.profile.UserViewModel;
import com.example.homeelecation.ui.quickOrder.QuickOrderActivity;
import com.example.homeelecation.ui.quickOrder.QuickServiceAdapter;
import com.example.homeelecation.ui.quickOrder.QuickServiceModel;
import com.example.homeelecation.ui.search.SearchActivity;
import com.example.homeelecation.util.EdgeToEdgeUtils;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HomeActivity2 extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    public static DrawerLayout drawer;
    private Dialog singInDialog;

    private ImageView profileImage;
    private TextView profileName, profileEmail;

    private NavController navController;
    private NavigationView navigationView;
    private CartViewModel cartViewModel;
    private UserViewModel userViewModel;
    private NotificationViewModel notificationViewModel;

    private FirebaseAuth mAuth;
    private TextView badgeCountTextView;
    private TextView notifyCountTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        ActivityHome2Binding binding = ActivityHome2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarHome2.toolbar);
        
        // Apply Insets to root layout
        EdgeToEdgeUtils.applyTopInset(binding.appBarHome2.appBarLayout);
        EdgeToEdgeUtils.applyBottomInset(binding.appBarHome2.fab);

        binding.appBarHome2.fab.setOnClickListener(view -> {
            if (mAuth.getCurrentUser() != null) {
                showQuickBookingSheet();
            } else {
                singInDialog.show();
            }
        });

        TooltipCompat.setTooltipText(binding.appBarHome2.fab, "Quick Emergency Booking");


        drawer = binding.drawerLayout;
        navigationView = binding.navView;
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav__home)
                .setOpenableLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home2);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


        mAuth = FirebaseAuth.getInstance();
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        notificationViewModel = new ViewModelProvider(this).get(NotificationViewModel.class);

        setupViewModelObservers();


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
        });

        dialogSingUpBtn.setOnClickListener(v -> {
            Intent register = new Intent(singInDialog.getContext(), LoginActivity.class);
            startActivity(register);
        });

        profileImage = navigationView.getHeaderView(0).findViewById(R.id.header_profile_image);
        profileName = navigationView.getHeaderView(0).findViewById(R.id.header_fullName);
        profileEmail = navigationView.getHeaderView(0).findViewById(R.id.header_emailId);

        if (mAuth.getCurrentUser() != null) {
            cartViewModel.loadCart();
            userViewModel.loadUserData();
            notificationViewModel.startNotificationListener();
        } else {
            singInDialog.show();
        }
    }

    private void setupViewModelObservers() {
        userViewModel.userData.observe(this, user -> {
            if (user != null) {
                profileName.setText(user.fullName);
                profileEmail.setText(user.email);
                Glide.with(this).load(user.profileImage)
                        .apply(new RequestOptions().placeholder(R.drawable.ic_person))
                        .into(profileImage);
            }
        });

        cartViewModel.getError().observe(this, error -> {
            if (error != null) Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.men_search) {
            startActivity(new Intent(this, SearchActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }


    @SuppressLint("HardwareIds")
    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() == null) {
            disableNavigationMenu();
        } else {
            checkSession();
        }
    }

    private void disableNavigationMenu() {
        Menu menu = navigationView.getMenu();
        menu.findItem(R.id.nav_profile).setEnabled(false);
        menu.findItem(R.id.nav_cart).setEnabled(false);
        menu.findItem(R.id.nav_orders).setEnabled(false);
        menu.findItem(R.id.nav_wishlist).setEnabled(false);
        menu.findItem(R.id.nav_coupon).setEnabled(false);
    }

    private void checkSession() {
        String uid = mAuth.getUid();
        String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        FirebaseFirestore.getInstance().collection("USER").document(uid)
                .collection("DEVICES").document(androidId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().exists()) {
                            mAuth.signOut();
                            Intent intent = new Intent(this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                            Toast.makeText(this, "Logged out from another device", Toast.LENGTH_SHORT).show();
                        } else {
                            cartViewModel.loadCart();
                            userViewModel.loadUserData();
                        }
                    }
                });
    }

    private void showQuickBookingSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View v = getLayoutInflater().inflate(R.layout.layout_quick_booking, null);
        RecyclerView recyclerView = v.findViewById(R.id.quick_booking_recycler_view);
        ProgressBar progressBar = v.findViewById(R.id.quick_booking_progress);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<QuickServiceModel> serviceList = new ArrayList<>();
        QuickServiceAdapter adapter = new QuickServiceAdapter(serviceList, model -> {
            openDirectDelivery(model.getDocumentId(), model.getName(), model.getPrice(), model.getRules(), model.getIcon());
            bottomSheetDialog.dismiss();
        });
        recyclerView.setAdapter(adapter);

        progressBar.setVisibility(View.VISIBLE);
        FirebaseFirestore.getInstance().collection("QUICK_SERVICES")
                .orderBy("index", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            QuickServiceModel model = document.toObject(QuickServiceModel.class);
                            model.setDocumentId(document.getId());
                            serviceList.add(model);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });

        bottomSheetDialog.setContentView(v);
        bottomSheetDialog.show();
    }

    private void openDirectDelivery(String documentID, String serviceName, String price, String rules, String image) {
        Intent deliveryIntent = new Intent(this, QuickOrderActivity.class);
        deliveryIntent.putExtra("SERVICE_ID", documentID);
        deliveryIntent.putExtra("SERVICE_NAME", serviceName);
        deliveryIntent.putExtra("SERVICE_PRICE", price);
        deliveryIntent.putExtra("SERVICE_RULES", rules);
        deliveryIntent.putExtra("SERVICE_IMAGE", image);
        deliveryIntent.putExtra("IS_DIRECT_BOOKING", true);
        startActivity(deliveryIntent);
    }
}
