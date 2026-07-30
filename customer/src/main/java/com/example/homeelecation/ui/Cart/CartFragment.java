package com.example.homeelecation.ui.Cart;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
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
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint

public class CartFragment extends Fragment {



    private RecyclerView recyclerView;
    public static Dialog loadingDialog;
    private Dialog singInDialog;
    public  CartAdapter cartAdapter;
    private  TextView totalAmount;

    public  CartViewModel cartViewModel;
    private AddressViewModel addressViewModel;

    private TextView totalAmountTextView;
    private TextView badgeCountTextView;







    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        // Apply Bottom Inset to the actual bottom bar layout
        //EdgeToEdgeUtils.applyInsets(view.findViewById(R.id.cart_fragment_constraint));



        recyclerView = view.findViewById(R.id.cart_RecyclerView);
        totalAmount = view.findViewById(R.id.total_cart_AmountF);
        Button buyNow = view.findViewById(R.id.place_order_bt);





        // dialog
        singInDialog = new Dialog(view.getContext());
        singInDialog.setContentView(R.layout.sing_in_dialog_layout);
        singInDialog.setCancelable(true);

        singInDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

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
            Intent register = new Intent(singInDialog.getContext(), LoginActivity.class);
            startActivity(register);
            Toast.makeText(singInDialog.getContext(), "Activity Register available", Toast.LENGTH_SHORT).show();


        });
        // dialog


        //loading dialog
        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(true);

        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
       // loadingDialog.show();
        //loading dialog


        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);
        addressViewModel = new ViewModelProvider(this).get(AddressViewModel.class);




        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);


        setupObservers();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            singInDialog.dismiss();
            LinearLayout parent = (LinearLayout) totalAmount.getParent();
            cartViewModel.loadCart();

            cartAdapter = new CartAdapter(new ArrayList<>(),totalAmountTextView,true,cartViewModel);
            recyclerView.setAdapter(cartAdapter);
        }else {
            singInDialog.show();
        }


        buyNow.setOnClickListener(v -> {
            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                singInDialog.show();
            } else {
                // चेकआउट के लिए लिस्ट तैयार करें
                PLaceActivity3.cartItemModelList = new ArrayList<>();

                // ViewModel से वर्तमान कार्ट आइटम्स लें
                List<CartItemModel> currentItems = cartViewModel.getCartItems().getValue();

                if (currentItems != null) {
                    for (CartItemModel model : currentItems) {
                        if (model.getType() == CartItemModel.CART_ITEM_LAYOUT && model.isInStock()) {
                            PLaceActivity3.cartItemModelList.add(model);
                        }
                    }
                    PLaceActivity3.cartItemModelList.add(new CartItemModel(CartItemModel.CART_TOTAL_AMOUNT_LAYOUT));
                }

                // अगर एड्रेस लिस्ट खाली है, तो ViewModel के ज़रिये लोड करें
                if (DbLoadData.addressesSelectModelList.isEmpty()) {
                    addressViewModel.loadAddresses(true, true);
                } else {
                    loadingDialog.dismiss();
                    Intent intent = new Intent(getContext(), PLaceActivity3.class);
                    startActivity(intent);
                }
            }
        });





        return view;
    }



    @Override
    public void onStart() {
        cartAdapter = new CartAdapter(DbLoadData.cartItemModelList,totalAmount,true,cartViewModel);
        recyclerView.setAdapter(cartAdapter);
        cartAdapter.notifyDataSetChanged();

        super.onStart();
    }
    private void setupObservers() {
        // 1. कार्ट आइटम्स (Products + Total View)
        cartViewModel.getCartItems().observe(getViewLifecycleOwner(), cartItems -> {
            if (cartItems != null) {
                cartAdapter.updateList(cartItems); // एडाप्टर में एक मेथड बनाएं जो लिस्ट अपडेट करे
            }
        });

        // 2. कुल कीमत
        cartViewModel.getTotalAmount().observe(getViewLifecycleOwner(), amount -> {
            if (totalAmountTextView != null) {
                if (amount > 0) {
                    String totalAmount = "Rs. " + amount + "/-";
                    totalAmountTextView.setText(totalAmount);
                    // Layout को विजिबल करें
                    ((View) totalAmountTextView.getParent()).setVisibility(View.VISIBLE);
                } else {
                    // Layout को छिपाएं
                    ((View) totalAmountTextView.getParent()).setVisibility(View.GONE);
                }
            }
        });

        // 3. बैज काउंट
        cartViewModel.getBadgeCount().observe(getViewLifecycleOwner(), count -> {
            if (badgeCountTextView != null) {
                if (count > 0) {
                    badgeCountTextView.setVisibility(View.VISIBLE);
                    badgeCountTextView.setText(String.valueOf(count));
                } else {
                    badgeCountTextView.setVisibility(View.INVISIBLE);
                }
            }
        });

        // 4. लोडिंग स्टेट
        cartViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                loadingDialog.show();
            } else {
                loadingDialog.dismiss();
            }
        });

        // 5. एरर मैसेज
        cartViewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        // ... आपके मौजूदा कार्ट ऑब्जर्वर्स ...// 6. एड्रेस नेविगेशन के लिए ऑब्जर्वर्स
        addressViewModel.navigateToAddAddress.observe(getViewLifecycleOwner(), shouldNavigate -> {
            if (shouldNavigate != null && shouldNavigate) {
                Intent intent = new Intent(getContext(), Add_delivery_address_Activity3.class);
                intent.putExtra("INTENT", "deliveryIntent");
                startActivity(intent);
                addressViewModel.onNavigationComplete(); // इवेंट रीसेट करें
            }
        });

        addressViewModel.navigateToPayment.observe(getViewLifecycleOwner(), shouldNavigate -> {
            if (shouldNavigate != null && shouldNavigate) {
                Intent intent = new Intent(getContext(), PLaceActivity3.class);
                startActivity(intent);
                addressViewModel.onNavigationComplete();
            }
        });

    }

}