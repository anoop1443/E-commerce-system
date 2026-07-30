package com.example.homeelecation.ui.profile;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.homeelecation.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class BankAccountActivity extends AppCompatActivity {

    private TextInputEditText holderName, bankName, accNumber, confirmAccNumber, ifscCode, upiId;
    private Button saveBtn;
    private Dialog loadingDialog;
    private boolean isEditMode = false;
    private Menu menu;

    @Inject
    FirebaseFirestore db;

    @Inject
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_details);

        Toolbar toolbar = findViewById(R.id.toolbar_bank);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize Views
        holderName = findViewById(R.id.bank_acc_holder_name);
        bankName = findViewById(R.id.bank_name_input);
        accNumber = findViewById(R.id.bank_acc_number);
        confirmAccNumber = findViewById(R.id.bank_acc_number_confirm);
        ifscCode = findViewById(R.id.bank_ifsc_code);
        upiId = findViewById(R.id.bank_upi_id);
        saveBtn = findViewById(R.id.btn_save_bank);

        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        if (loadingDialog.getWindow() != null) {
            loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        loadBankDetails();

        saveBtn.setOnClickListener(v -> {
            if (validateInputs()) {
                saveBankDetails();
            }
        });

        // Set initial state to non-editable until data loads
        setFieldsEditable(true); // Allow editing if empty
    }

    private void setFieldsEditable(boolean editable) {
        isEditMode = editable;
        holderName.setEnabled(editable);
        bankName.setEnabled(editable);
        accNumber.setEnabled(editable);
        confirmAccNumber.setEnabled(editable);
        ifscCode.setEnabled(editable);
        upiId.setEnabled(editable);

        saveBtn.setVisibility(editable ? View.VISIBLE : View.GONE);
        
        if (menu != null) {
            MenuItem editItem = menu.findItem(R.id.action_edit_bank);
            if (editItem != null) {
                editItem.setVisible(!editable);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bank_details_menu, menu);
        this.menu = menu;
        // Initially hide edit icon if we haven't loaded data yet, or logic inside loadBankDetails will handle it
        return true;
    }

    private boolean validateInputs() {
        if (TextUtils.isEmpty(holderName.getText())) {
            holderName.setError("Required!");
            return false;
        }
        if (TextUtils.isEmpty(bankName.getText())) {
            bankName.setError("Required!");
            return false;
        }
        if (TextUtils.isEmpty(accNumber.getText()) || accNumber.getText().length() < 9) {
            accNumber.setError("Invalid Account Number!");
            return false;
        }
        if (!accNumber.getText().toString().equals(confirmAccNumber.getText().toString())) {
            confirmAccNumber.setError("Account numbers do not match!");
            return false;
        }
        if (TextUtils.isEmpty(ifscCode.getText()) || ifscCode.getText().length() != 11) {
            ifscCode.setError("Invalid IFSC Code (11 characters required)!");
            return false;
        }
        return true;
    }

    private void loadBankDetails() {
        String uid = auth.getUid();
        if (uid == null) return;

        loadingDialog.show();
        db.collection("USER").document(uid).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null && task.getResult().contains("BANK_DETAILS")) {
                Map<String, Object> details = (Map<String, Object>) task.getResult().get("BANK_DETAILS");
                if (details != null) {
                    holderName.setText(String.valueOf(details.get("holderName")));
                    bankName.setText(String.valueOf(details.get("bankName")));
                    accNumber.setText(String.valueOf(details.get("accNumber")));
                    confirmAccNumber.setText(String.valueOf(details.get("accNumber")));
                    ifscCode.setText(String.valueOf(details.get("ifscCode")));
                    upiId.setText(String.valueOf(details.get("upiId")));
                    
                    // Check if Admin has hard-locked the details
                    Boolean adminLocked = (Boolean) details.get("isLocked");
                    if (adminLocked != null && !adminLocked) {
                        // If false, it means Admin has locked it permanently
                        setFieldsEditable(false);
                        if (menu != null) {
                            MenuItem editItem = menu.findItem(R.id.action_edit_bank);
                            if (editItem != null) editItem.setVisible(false);
                        }
                        Toast.makeText(this, "Details are verified and locked by Admin", Toast.LENGTH_LONG).show();
                    } else {
                        // Normal lock (user can still click edit)
                        setFieldsEditable(false);
                    }
                }
            } else {
                // If no details, stay in edit mode
                setFieldsEditable(true);
            }
            loadingDialog.dismiss();
        });
    }

    private void saveBankDetails() {
        String uid = auth.getUid();
        if (uid == null) return;

        loadingDialog.show();

        Map<String, Object> bankMap = new HashMap<>();
        bankMap.put("holderName", holderName.getText().toString().trim());
        bankMap.put("bankName", bankName.getText().toString().trim());
        bankMap.put("accNumber", accNumber.getText().toString().trim());
        bankMap.put("ifscCode", ifscCode.getText().toString().trim().toUpperCase());
        bankMap.put("upiId", upiId.getText().toString().trim());
        bankMap.put("isLocked", true); // Boolean flag to indicate details are saved and locked

        Map<String, Object> userUpdate = new HashMap<>();
        userUpdate.put("BANK_DETAILS", bankMap);

        db.collection("USER").document(uid).update(userUpdate).addOnCompleteListener(task -> {
            loadingDialog.dismiss();
            if (task.isSuccessful()) {
                Toast.makeText(this, "Bank details saved successfully!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.action_edit_bank) {
            setFieldsEditable(true);
            Toast.makeText(this, "Editing Enabled", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
