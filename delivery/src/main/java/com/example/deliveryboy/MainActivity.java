package com.example.deliveryboy;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.deliveryboy.callDailer.CallActivity;
import com.example.deliveryboy.order.OrderAdapterD;
import com.example.deliveryboy.orderfech.OrderListFragment;
import com.example.deliveryboy.order.OrderModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private FloatingActionButton floatingActionButton;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    private Toolbar toolbar;
    private RecyclerView ordersRecyclerView;
    private OrderAdapterD orderAdapter;
    private List<OrderModel> orderList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        floatingActionButton = findViewById(R.id.floatingActionBtn);

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

//        toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true); // back button ke liye

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CallActivity.class);
                startActivity(intent);
            }
        });
        // Firebase instances ko initialize karein
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // RecyclerView aur list ko initialize karein
        ordersRecyclerView = findViewById(R.id.orders_recyclerview);
        ordersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        orderList = new ArrayList<>();
        orderAdapter = new OrderAdapterD(orderList);
        ordersRecyclerView.setAdapter(orderAdapter);

        // Orders ko fetch karein
        //fetchOrders();
    }



    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(OrderListFragment.newInstance("Ordered"), "Pending");
        adapter.addFragment(OrderListFragment.newInstance("Out for Service"), "Accepted");
        adapter.addFragment(OrderListFragment.newInstance("Completed"), "Finished");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> fragmentList = new ArrayList<>();
        private final List<String> fragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            fragmentList.add(fragment);
            fragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitleList.get(position);
        }
    }

}