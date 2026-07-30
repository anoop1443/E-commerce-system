package com.example.homeelecation.ui.details;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.example.homeelecation.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

public class FullScreenImageActivity extends AppCompatActivity {

    private TabLayout viewpagerIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_full_screen_image);
        viewpagerIndicator = findViewById(R.id.full_viewpager_indicator);


        ViewPager2 viewPager = findViewById(R.id.full_screen_viewpager);
        findViewById(R.id.close_button).setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed() );

        // Intent से डेटा प्राप्त करें
        ArrayList<String> imageUrls = getIntent().getStringArrayListExtra("image_urls");
        int currentPosition = getIntent().getIntExtra("position", 0);

        // अडैप्टर सेट करें
        FullScreenImageAdapter adapter = new FullScreenImageAdapter(imageUrls);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(currentPosition, false);



        //viewpagerIndicator.setupWithViewPager(viewPager, true);

        new TabLayoutMediator(viewpagerIndicator, viewPager, (tab, position) -> {
            // Tab customization logic if needed
        }).attach();


    }
}