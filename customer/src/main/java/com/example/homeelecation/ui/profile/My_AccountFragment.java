package com.example.homeelecation.ui.profile;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.homeelecation.LoginActivity;
import com.example.homeelecation.R;
import com.example.homeelecation.ui.DbLoadData;
import com.example.homeelecation.ui.address.Add_delivery_address_Activity3;
import com.example.homeelecation.ui.address.AddressViewModel;
import com.example.homeelecation.ui.address.AddressesSelectModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class My_AccountFragment extends Fragment {

    public static final int MANAGE_ADDRESS = 1;
    private AddressViewModel addressViewModel;
    private Button viewAllAddress;
    private View signOutContainer;
    private FloatingActionButton profileSettingBtn;
    private FirebaseAuth firebaseAuth;
    private ProgressBar progressBar;
    private TextView profileName, profileEmail, profileMobile, needHelp;
    private ImageView profileImage;

    private TextView addressName, addressDetails, addressPinCode;
    private Dialog loadingDialog;
    private View myBillsContainer, helpCenterContainer, termsContainer, bankDetailsContainer;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my__account, container, false);

        initViews(view);
        setupClickListeners();

        firebaseAuth = FirebaseAuth.getInstance();
        addressViewModel = new ViewModelProvider(this).get(AddressViewModel.class);

        loadingDialog = new Dialog(requireContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        return view;
    }

    private void initViews(View view) {
        // Profile Details
        profileName = view.findViewById(R.id.account_fragment_fullName);
        profileEmail = view.findViewById(R.id.account_fragment_Email);
        profileImage = view.findViewById(R.id.account_fragment_profile_image);
        profileSettingBtn = view.findViewById(R.id.profile_settings_btn);
        needHelp = view.findViewById(R.id.needHelp_profile);

        // Address Details
        addressName = view.findViewById(R.id.my_name);
        addressDetails = view.findViewById(R.id.my_address);
        addressPinCode = view.findViewById(R.id.my_address_pincode);
        viewAllAddress = view.findViewById(R.id.my_address_viewAll_btn);

        // My Bills & Help
        myBillsContainer = view.findViewById(R.id.my_bills_btn_container);
        helpCenterContainer = view.findViewById(R.id.help_center_btn_container);
        bankDetailsContainer = view.findViewById(R.id.bank_details_btn_container);
        termsContainer = view.findViewById(R.id.terms_btn_container);

        // Sign Out
        signOutContainer = view.findViewById(R.id.my_account_sign_out_btn_container);
    }

    private void setupClickListeners() {
        myBillsContainer.setOnClickListener(v -> startActivity(new Intent(getContext(), BillingActivity.class)));

        helpCenterContainer.setOnClickListener(v -> startActivity(new Intent(getContext(), HelpCenterActivity.class)));

        bankDetailsContainer.setOnClickListener(v -> startActivity(new Intent(getContext(), BankAccountActivity.class)));

        termsContainer.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Terms & Conditions coming soon", Toast.LENGTH_SHORT).show();
        });

        signOutContainer.setOnClickListener(v -> showLogoutBottomSheet());

        viewAllAddress.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), com.example.homeelecation.ui.address.Select_Address_Activity3.class);
            intent.putExtra("MODE", MANAGE_ADDRESS);
            startActivity(intent);
        });

        profileSettingBtn.setOnClickListener(v -> {
            Intent updateIntent = new Intent(getContext(), UpDateProfileActivity.class);
            updateIntent.putExtra("NAME", DbLoadData.fullName);
            updateIntent.putExtra("EMAIL", DbLoadData.email);
            startActivity(updateIntent);
        });

        needHelp.setOnClickListener(v -> {
            // Future help center logic
            Toast.makeText(getContext(), "Help Center coming soon", Toast.LENGTH_SHORT).show();
        });
    }

    private void showLogoutBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View view = getLayoutInflater().inflate(R.layout.layout_logout_bottom_sheet, null);
        bottomSheetDialog.setContentView(view);

        view.findViewById(R.id.logout_current_device).setOnClickListener(v -> {
            logoutCurrentDevice();
            bottomSheetDialog.dismiss();
        });

        view.findViewById(R.id.logout_all_devices).setOnClickListener(v -> {
            logoutFromAllDevices();
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();
    }

    private void logoutCurrentDevice() {
        loadingDialog.show();
        String uid = firebaseAuth.getUid();
        String androidId = android.provider.Settings.Secure.getString(requireContext().getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        FirebaseFirestore.getInstance().collection("USER").document(uid)
                .collection("DEVICES").document(androidId).delete()
                .addOnCompleteListener(task -> {
                    firebaseAuth.signOut();
                    DbLoadData.clearData();
                    loadingDialog.dismiss();
                    redirectToLogin();
                });
    }

    private void logoutFromAllDevices() {
        loadingDialog.show();
        String uid = firebaseAuth.getUid();

        FirebaseFirestore.getInstance().collection("USER").document(uid)
                .collection("DEVICES").get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (com.google.firebase.firestore.DocumentSnapshot doc : task.getResult()) {
                            doc.getReference().delete();
                        }
                    }
                    firebaseAuth.signOut();
                    DbLoadData.clearData();
                    loadingDialog.dismiss();
                    redirectToLogin();
                });
    }

    private void redirectToLogin() {
        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (firebaseAuth.getCurrentUser() != null) {
            profileName.setText(DbLoadData.fullName);
            profileEmail.setText(DbLoadData.email);

            Glide.with(this).load(DbLoadData.profileImage)
                    .apply(new RequestOptions().placeholder(R.drawable.ic_person))
                    .into(profileImage);

            setupObservers();
            addressViewModel.loadAddresses(false, false);
        }
    }

    private void setupObservers() {
        addressViewModel.addresses.observe(getViewLifecycleOwner(), list -> {
            if (list != null && !list.isEmpty()) {
                updateAddressUI(list);
            } else {
                addressName.setText("No Address Found");
                addressDetails.setText("Please add a shipping address");
                addressPinCode.setText("");
            }
        });
    }

    private void updateAddressUI(List<AddressesSelectModel> list) {
        int index = DbLoadData.selectedAddresses;
        if (index < 0 || index >= list.size()) index = 0;

        AddressesSelectModel model = list.get(index);
        addressName.setText(model.getFullName());
        addressDetails.setText(model.getHouse() + ", " + model.getRoadAreaColony() + ", " + model.getCity());
        addressPinCode.setText(model.getPinCode() + " (" + model.getState() + ")");
    }
}
