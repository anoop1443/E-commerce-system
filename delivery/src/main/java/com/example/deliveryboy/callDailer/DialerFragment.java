package com.example.deliveryboy.callDailer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.deliveryboy.R;


public class DialerFragment extends Fragment implements View.OnClickListener {

    private TextView phoneNumberTextView;
    private ImageButton callButton;
    private ImageButton backspaceButton;
    private Button[] numberButtons = new Button[12];
    private static final int PERMISSION_REQUEST_CALL_PHONE = 1;

    public DialerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialer2, container, false);

        // UI तत्वों को उनके IDs से जोड़ना
        phoneNumberTextView = view.findViewById(R.id.phoneNumberTextView);
        callButton = view.findViewById(R.id.callButton);
        backspaceButton = view.findViewById(R.id.backspaceButton);

        // नंबर बटनों को IDs से जोड़ना
        numberButtons[0] = view.findViewById(R.id.button0);
        numberButtons[1] = view.findViewById(R.id.button1);
        numberButtons[2] = view.findViewById(R.id.button2);
        numberButtons[3] = view.findViewById(R.id.button3);
        numberButtons[4] = view.findViewById(R.id.button4);
        numberButtons[5] = view.findViewById(R.id.button5);
        numberButtons[6] = view.findViewById(R.id.button6);
        numberButtons[7] = view.findViewById(R.id.button7);
        numberButtons[8] = view.findViewById(R.id.button8);
        numberButtons[9] = view.findViewById(R.id.button9);
        numberButtons[10] = view.findViewById(R.id.buttonStar);
        numberButtons[11] = view.findViewById(R.id.buttonHash);

        // सभी नंबर बटनों पर क्लिक लिसनर सेट करना
        for (Button button : numberButtons) {
            button.setOnClickListener(this);
        }

        // कॉल और बैकस्पेस बटनों पर क्लिक लिसनर सेट करना
        callButton.setOnClickListener(this);
        backspaceButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.callButton) {
            dialPhoneNumber();
        } else if (id == R.id.backspaceButton) {
            deleteNumber();
        } else {
            // नंबर बटन पर क्लिक होने पर
            Button clickedButton = (Button) v;
            String number = clickedButton.getText().toString();
            phoneNumberTextView.append(number);
        }
    }

    private void dialPhoneNumber() {
        String phoneNumber = phoneNumberTextView.getText().toString().trim();

        if (phoneNumber.isEmpty()) {
            Toast.makeText(getContext(), "कृपया एक फोन नंबर दर्ज करें।", Toast.LENGTH_SHORT).show();
            return;
        }

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, PERMISSION_REQUEST_CALL_PHONE);
            return;
        }

        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(callIntent);
    }

    private void deleteNumber() {
        String currentText = phoneNumberTextView.getText().toString();
        if (currentText.length() > 0) {
            // आखिरी अक्षर को हटा दें
            phoneNumberTextView.setText(currentText.substring(0, currentText.length() - 1));
        }
    }
}
