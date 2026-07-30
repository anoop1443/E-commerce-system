package com.example.homeadmin.ui.profile;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.homeadmin.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UpDateProfileActivity extends AppCompatActivity {



    private RadioGroup genderRadioGroup;
    private RadioButton maleRadioButton;
    private RadioButton femaleRadioButton;
    private ImageView genderImageView;
    private EditText fullNameEditText,emailEditText;
    private Button updateButton;
    
    private  Dialog loadingDialog;
    private String gender = "Male";
    private FirebaseFirestore firebaseFirestore;

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+(?:\\.[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+)*@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_up_date_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        firebaseFirestore = FirebaseFirestore.getInstance();

        // Initialize UI elements
        genderRadioGroup = findViewById(R.id.genderRadioGroup);
        maleRadioButton = findViewById(R.id.maleRadioButton);
        femaleRadioButton = findViewById(R.id.femaleRadioButton);
        genderImageView = findViewById(R.id.upDateImage);

        fullNameEditText = findViewById(R.id.upDate_fullName);
        emailEditText = findViewById(R.id.upDate_email);
        updateButton = findViewById(R.id.update_btn);

        String name = getIntent().getStringExtra("NAME");
        String email = getIntent().getStringExtra("EMAIL");

        fullNameEditText.setText(name);
        emailEditText.setText(email);


        //loading dialog
        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(true);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //loading dialog



        // Set a listener for the RadioGroup to detect changes in selection
        genderRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.maleRadioButton) {
                    // User selected Male
                    gender = "Male";
                    genderImageView.setImageResource(R.drawable.male_avatar); // Assuming male_avatar.png in drawable
                } else if (checkedId == R.id.femaleRadioButton) {
                    // User selected Female
                    gender = "Female";

                    genderImageView.setImageResource(R.drawable.female_avatar); // Assuming female_avatar.png in drawable
                }
            }
        });

        // Optionally, set a default selection and image when the activity starts
        maleRadioButton.setChecked(true); // Default to male
        genderImageView.setImageResource(R.drawable.male_avatar); // Display male image by default


        updateButton.setClickable(!fullNameEditText.getText().toString().isEmpty() && emailEditText.getText().toString().isEmpty());

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.show();
                if (!TextUtils.isEmpty(fullNameEditText.getText())){

                    if (!TextUtils.isEmpty(emailEditText.getText().toString().trim().toLowerCase())){
                        if ( emailEditText.getText().toString().trim().toLowerCase().matches(EMAIL_REGEX)) {

                            Map<String, Object> updatee = new HashMap<>();
                            updatee.put("Full Name", fullNameEditText.getText().toString());
                            updatee.put("email", emailEditText.getText().toString());
                            updatee.put("gender",gender);

                            firebaseFirestore.collection("UsersAdmin").document(FirebaseAuth.getInstance().getUid()).
                                    update(updatee).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) {
                                                Toast.makeText(UpDateProfileActivity.this, "successFully update!", Toast.LENGTH_SHORT).show();
                                                finish();
                                            } else {
                                                String error = task.getException().getMessage();
                                                Toast.makeText(UpDateProfileActivity.this, error, Toast.LENGTH_SHORT).show();
                                            }

                                            loadingDialog.dismiss();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    });

                        }else {
                            emailEditText.setError("Invalid Email, lowerCase");
                            emailEditText.requestFocus();
                            loadingDialog.dismiss();

                        }

                    }else {
                        emailEditText.setError("Empty");
                        emailEditText.requestFocus();
                        loadingDialog.dismiss();
                    }

                }else {
                    fullNameEditText.setError("Empty");
                    fullNameEditText.requestFocus();
                    loadingDialog.dismiss();
                }
            }
        });

    }
}