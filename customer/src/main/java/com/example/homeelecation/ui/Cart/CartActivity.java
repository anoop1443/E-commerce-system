package com.example.homeelecation.ui.Cart;

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

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homeelecation.LoginActivity;
import com.example.homeelecation.MainActivity;
import com.example.homeelecation.R;
import com.example.homeelecation.ui.DbLoadData;
import com.example.homeelecation.ui.address.Add_delivery_address_Activity3;
import com.example.homeelecation.ui.address.AddressViewModel;
import com.example.homeelecation.ui.place.PLaceActivity3;
import com.example.homeelecation.util.EdgeToEdgeUtils;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CartActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private Dialog singInDialog;
    private Dialog loadingDialog;
    private CartAdapter cartAdapter;
    private Button buyNow;
    private CartViewModel cartViewModel;
    private AddressViewModel addressViewModel;
    private TextView totalAmountTextView;
    private LinearLayout bottomContainer;
    
    // Empty State Views
    private View emptyStateLayout;
    private Button startShoppingBtn;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        EdgeToEdge.enable(this);

        // Apply Insets
        EdgeToEdgeUtils.applyTopInset(findViewById(R.id.appbar));
        // EdgeToEdgeUtils.applyBottomInset(findViewById(R.id.bottom_action_container));
        EdgeToEdgeUtils.applyBottomInset(findViewById(R.id.cart_activity_constraint));


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("My Cart");

        recyclerView = findViewById(R.id.activity_cart_RecyclerView);
        buyNow = findViewById(R.id.place_order_bt);
        totalAmountTextView = findViewById(R.id.total_place_Amount);
        bottomContainer = findViewById(R.id.linearLayout7);
        
        emptyStateLayout = findViewById(R.id.empty_cart_layout);
        startShoppingBtn = emptyStateLayout.findViewById(R.id.empty_state_button);
        
        TextView emptyTitle = emptyStateLayout.findViewById(R.id.empty_state_title);
        TextView emptyDesc = emptyStateLayout.findViewById(R.id.empty_state_desc);
        emptyTitle.setText("Your Cart is Empty");
        emptyDesc.setText("Add some products to your cart to start shopping!");
        
        startShoppingBtn.setOnClickListener(v -> finish());

        // --- Dialogs Setup ---
        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        singInDialog = new Dialog(this);
        singInDialog.setContentView(R.layout.sing_in_dialog_layout);
        singInDialog.setCancelable(true);
        singInDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        Button dialogSingInBtn = singInDialog.findViewById(R.id.sing_in_btn);
        Button dialogSingUpBtn = singInDialog.findViewById(R.id.sing_up_btn);
        ImageView imageView = singInDialog.findViewById(R.id.dialog_image);
        imageView.setImageResource(R.drawable.address_icon);

        dialogSingInBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            singInDialog.dismiss();
        });

        dialogSingUpBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            singInDialog.dismiss();
        });

        // --- ViewModel & Adapter Setup ---
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);
        addressViewModel = new ViewModelProvider(this).get(AddressViewModel.class);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter = new CartAdapter(new ArrayList<>(), totalAmountTextView, true, cartViewModel);
        recyclerView.setAdapter(cartAdapter);

        setupObservers();

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            cartViewModel.loadCart();
        } else {
            singInDialog.show();
        }

        buyNow.setOnClickListener(v -> {
            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                singInDialog.show();
            } else {
                List<CartItemModel> currentItems = cartViewModel.getCartItems().getValue();
                if (currentItems != null && !currentItems.isEmpty()) {
                    PLaceActivity3.cartItemModelList = new ArrayList<>();
                    for (CartItemModel model : currentItems) {
                        if (model.getType() == CartItemModel.CART_ITEM_LAYOUT && model.isInStock()) {
                            PLaceActivity3.cartItemModelList.add(model);
                        }
                    }
                    PLaceActivity3.cartItemModelList.add(new CartItemModel(CartItemModel.CART_TOTAL_AMOUNT_LAYOUT));
                    
                    // एड्रेस चेक करें और लोड करें
                    addressViewModel.loadAddresses(true, true);
                } else {
                    Toast.makeText(this, "Cart is empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setupObservers() {
        // 1. कार्ट आइटम्स ऑब्जर्वर
        cartViewModel.getCartItems().observe(this, cartItems -> {
            if (cartItems != null) {
                cartAdapter.updateList(cartItems);
                
                // Toggle Empty State
                if (cartItems.isEmpty()) {
                    emptyStateLayout.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    bottomContainer.setVisibility(View.GONE);
                } else {
                    emptyStateLayout.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    bottomContainer.setVisibility(View.VISIBLE);
                }
            }
        });

        // 2. टोटल अमाउंट ऑब्जर्वर
        cartViewModel.getTotalAmount().observe(this, amount -> {
            if (totalAmountTextView != null) {
                totalAmountTextView.setText("Rs. " + amount + "/-");
                ((View) totalAmountTextView.getParent()).setVisibility(amount > 0 ? View.VISIBLE : View.GONE);
            }
        });

        // 3. लोडिंग स्टेट ऑब्जर्वर
        cartViewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading) loadingDialog.show();
            else loadingDialog.dismiss();
        });

        // 4. एरर मैसेज ऑब्जर्वर
        cartViewModel.getError().observe(this, error -> {
            if (error != null) Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
        });

        // 5. एड्रेस नेविगेशन ऑब्जर्वर
        addressViewModel.navigateToAddAddress.observe(this, shouldNavigate -> {
            if (shouldNavigate != null && shouldNavigate) {
                Intent intent = new Intent(this, Add_delivery_address_Activity3.class);
                intent.putExtra("INTENT", "deliveryIntent");
                startActivity(intent);
                addressViewModel.onNavigationComplete();
            }
        });

        addressViewModel.navigateToPayment.observe(this, shouldNavigate -> {
            if (shouldNavigate != null && shouldNavigate) {
                startActivity(new Intent(this, PLaceActivity3.class));
                addressViewModel.onNavigationComplete();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            cartViewModel.loadCart();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_icon, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
