package com.example.deliveryboy.callDailer;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.deliveryboy.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * The main activity for the call dialer feature.
 * This activity hosts a ViewPager with three tabs: Recent Calls, Contacts, and a Dialer.
 * It also handles requesting necessary permissions for making calls, reading call logs, and reading contacts.
 */
public class CallActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FloatingActionButton fab;
    private static final int PERMISSIONS_REQUEST_CODE = 1;

    /**
     * Initializes the activity, sets up the UI, and requests necessary permissions.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);

        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabs);
        fab = findViewById(R.id.fabBtn);

        // Request all necessary permissions
        requestPermissionsIfNecessary();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go to the dialer tab when the FAB is clicked
                viewPager.setCurrentItem(2);
            }
        });
    }

    /**
     * Checks for necessary permissions and requests them if they are not already granted.
     * If permissions are already granted, it sets up the ViewPager.
     */
    private void requestPermissionsIfNecessary() {
        if (checkPermissions()) {
            setupViewPager(viewPager);
            tabLayout.setupWithViewPager(viewPager);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.READ_CALL_LOG, Manifest.permission.READ_CONTACTS},
                    PERMISSIONS_REQUEST_CODE);
        }
    }

    /**
     * Checks if all the required permissions are granted.
     *
     * @return True if all permissions are granted, false otherwise.
     */
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Handles the result of the permission request.
     *
     * @param requestCode  The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either {@link PackageManager#PERMISSION_GRANTED}
     *                     or {@link PackageManager#PERMISSION_DENIED}. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (checkPermissions()) {
                setupViewPager(viewPager);
                tabLayout.setupWithViewPager(viewPager);
            } else {
                Toast.makeText(this, "Permissions are required to use this app.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Sets up the ViewPager with the fragments for each tab.
     *
     * @param viewPager The ViewPager to set up.
     */
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new RecentCallsFragment(), "रीसेंट");
        adapter.addFragment(new ContactsFragment(), "कॉन्टैक्ट्स");
        adapter.addFragment(new DialerFragment(), "डायलर");
        viewPager.setAdapter(adapter);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    static class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    // Previous DialerFragment, RecentCallsFragment, ContactsFragment classes...
    // The previous implementation of DialerFragment, RecentCallsFragment, and ContactsFragment
    // has been moved to separate files for better organization.
}
