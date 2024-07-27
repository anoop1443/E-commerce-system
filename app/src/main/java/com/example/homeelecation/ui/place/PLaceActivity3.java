package com.example.homeelecation.ui.place;

import static com.google.firebase.appcheck.internal.util.Logger.TAG;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homeelecation.HomeActivity2;
import com.example.homeelecation.R;
import com.example.homeelecation.ui.Cart.CartAdapter;
import com.example.homeelecation.ui.Cart.CartModel;
import com.example.homeelecation.ui.DbLoadData;
import com.example.homeelecation.ui.address.Select_Address_Activity3;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PLaceActivity3 extends AppCompatActivity implements PaymentResultListener {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
   private Button changeAddresses,continueBe;
   private TextView fullName,addressesFull, UserPinCode,mobile,totalAmount;
    public static final int SELECT_ADDRESS = 0;
    public static  List<CartModel> cartModelList = new ArrayList<>();

    private  Dialog paymentDialog;

    // order confirmation layout
    private  TextView confirmationText,orderIdText,expectedDeliveryText;
    private Button continueBtn;
    private ImageView imageChek;
    // order confirmation layout




    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place3);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Place order");



        recyclerView = findViewById(R.id.place_recyclerview);
        changeAddresses = findViewById(R.id.place_deliver_addres_btn);

        fullName = findViewById(R.id.place_deliver_addres_userName);
        addressesFull = findViewById(R.id.place_deliver_addres_userAddresses);
        UserPinCode = findViewById(R.id.place_deliver_addres_picode);
        mobile = findViewById(R.id.place_deliver_addres_user_mobile);
        totalAmount = findViewById(R.id.total_place_Amount);
        continueBe = findViewById(R.id.place_order_bt);

        confirmationText = findViewById(R.id.order_confirmed_layout_confirmed_textview);
        orderIdText = findViewById(R.id.order_confirmed_layout_order_id_textview);
        expectedDeliveryText = findViewById(R.id.order_confirmed_layout_expected_delivery_textview);
        continueBtn =findViewById(R.id.order_confirmed_layout_shop_button);
        imageChek =findViewById(R.id.order_confirmed_layout_chek_image);



        fullName.setText(DbLoadData.addressesSelectModelList.get(DbLoadData.selectedAddresses).getFullName());
        addressesFull.setText(DbLoadData.addressesSelectModelList.get(DbLoadData.selectedAddresses).getFullAddress());
        mobile.setText(DbLoadData.addressesSelectModelList.get(DbLoadData.selectedAddresses).getPhone());

        /// payment dialog
        paymentDialog = new Dialog(PLaceActivity3.this);
        paymentDialog.setContentView(R.layout.paymentdailog);
        paymentDialog.setCancelable(true);

        paymentDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        ImageButton dialogCashBtn = paymentDialog.findViewById(R.id.cashPayment);
        ImageButton dialogCartBtn = paymentDialog.findViewById(R.id.cartPayment);

        dialogCashBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(PLaceActivity3.this, "Please use Cash on delivery", Toast.LENGTH_SHORT).show();

            }
        });




        /// payment dialog

        LinearLayoutManager layoutManager = new LinearLayoutManager(PLaceActivity3.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);


        changeAddresses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PLaceActivity3.this, Select_Address_Activity3.class);
                intent.putExtra("MODE",SELECT_ADDRESS);
                startActivity(intent);
            }
        });



//        List<PlaceModel>placeModelList = new ArrayList<>();
//
//        placeModelList.add(new PlaceModel(R.drawable.tebal_fan,"Canon EOS 3000D","black",89,599,66,"2 Offers applied","2 Offers available","delivered 12th jan ","Rs 50"));
//        placeModelList.add(new PlaceModel(R.drawable.ic__reward,"Canon EOS 3000D","black",87,599,63,"2 Offers applied","2 Offers available","delivered 12th jan ","Rs 50"));
//        placeModelList.add(new PlaceModel(R.drawable.ic_home,"Canon EOS 3000D","black",87,599,63,"2 Offers applied","2 Offers available","delivered 12th jan ","Rs 50"));
//        placeModelList.add(new PlaceModel(R.drawable.img,"Canon EOS 3000D","black",87,599,68,"2 Offers applied","2 Offers available","delivered 12th jan ","Rs 50"));
//        placeModelList.add(new PlaceModel(R.drawable.tebal_fan,"Canon EOS 3000D","black",87,599,63,"2 Offers applied","2 Offers available","delivered 12th jan ","Rs 50"));
//        placeModelList.add(new PlaceModel(R.drawable.ic_baseline_settings_24,"Canon EOS 3000D","black",887,599,63,"2 Offers applied","2 Offers available","delivered 12th jan ","Rs 50"));
//        placeModelList.add(new PlaceModel(R.drawable.discount_coupan24,"Canon EOS 3000D","black",87,599,3,"2 Offers applied","2 Offers available","delivered 12th jan ","Rs 50"));
//        placeModelList.add(new PlaceModel(R.drawable.horizontla_fan,"Canon EOS 3000D","black",8987,599,63,"2 Offers applied","2 Offers available","delivered 12th jan ","Rs 50"));
//        placeModelList.add(new PlaceModel(R.drawable.tebal_fan,"Canon EOS 3000D","black",887,599,63,"2 Offers applied","2 Offers available","delivered 12th jan ","Rs 50"));
//        placeModelList.add(new PlaceModel(R.drawable.tebal_fan,"Canon EOS 3000D","black",8987,599,63,"2 Offers applied","2 Offers available","delivered 12th jan ","Rs 50"));
//        placeModelList.add(new PlaceModel(R.drawable.ic__reward,"Canon EOS 3000D","black",8987,599,63,"2 Offers applied","2 Offers available","delivered 12th jan ","Rs 50"));
//
//        PlaceAdapter adapter = new PlaceAdapter(placeModelList);
//        recyclerView.setAdapter(adapter);
//        adapter.notifyDataSetChanged();



        CartAdapter adapter = new CartAdapter(cartModelList,totalAmount,false);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();


       // String amount =  (String) totalAmount.getText().toString().substring(3,totalAmount);
        String phone = DbLoadData.addressesSelectModelList.get(DbLoadData.selectedAddresses).getPhone();
        continueBe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                paymentDialog.show();

                Toast.makeText(PLaceActivity3.this, phone, Toast.LENGTH_LONG).show();
            }
        });


        dialogCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Checkout.preload(getApplicationContext());
                // Checkout checkout = null;
                Checkout checkout = new Checkout();

                checkout.setKeyID("rzp_test_ZvAWfcnowk6XRW");
                /**
                 * Instantiate Checkout
                 */
                //checkout = new Checkout();

                /**
                 * Set your logo here
                 */
                //checkout.setImage(R.drawable.logo);

                /**
                 * Reference to current activity
                 */
                // final Activity activity = PLaceActivity3.this;

                /**
                 * Pass your payment options to the Razorpay Checkout as a JSONObject
                 */
                JSONObject options = new JSONObject();
                try {

                    options.put("name", "Merchant Name");
                    options.put("description", "Reference No. #123456");
                    options.put("image", "");
                    // options.put("order_id", "order_DBJOWzybf0sJbb");//from response of step 3.
                    options.put("theme.color", "");
                    options.put("currency", "INR");
                    options.put("amount", totalAmount.getText().toString().substring(3)+"00");//pass amount in currency subunits
                    options.put("prefill.email", "anoop.kumar121443@gmail.com");
                    options.put("prefill.contact",phone.substring(3));
//                        JSONObject retryObj = new JSONObject();
//                        retryObj.put("enabled", true);
//                        retryObj.put("max_count", 4);
//                        options.put("retry", retryObj);

                    checkout.open(PLaceActivity3.this, options);

                } catch (Exception e) {
                    Log.e(TAG, "Error in starting Razorpay Checkout", e);
                }


                Toast.makeText(PLaceActivity3.this, "Please use Card", Toast.LENGTH_SHORT).show();



            }
        });

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id==R.id.men_search){
            // search code w
            item.setVisible(false);

            Toast.makeText(this, "Ok", Toast.LENGTH_SHORT).show();

            return true;


        }else if (id==R.id.men_cart) {
            //cart code w
            Toast.makeText(this, "please wait ", Toast.LENGTH_SHORT).show();
            item.setVisible(false);

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
        fullName.setText(DbLoadData.addressesSelectModelList.get(DbLoadData.selectedAddresses).getFullName());
        addressesFull.setText(DbLoadData.addressesSelectModelList.get(DbLoadData.selectedAddresses).getFullAddress());
        mobile.setText(DbLoadData.addressesSelectModelList.get(DbLoadData.selectedAddresses).getPhone());

    }

    @Override
    public void onPaymentSuccess(String s) {

        paymentDialog.dismiss();
        Toast.makeText(this, " payment success", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(PLaceActivity3.this, HomeActivity2.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();

    }
}