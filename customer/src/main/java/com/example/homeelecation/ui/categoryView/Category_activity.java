package com.example.homeelecation.ui.categoryView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homeelecation.R;
import com.example.homeelecation.ui.home.HomepageAdapter;
import com.example.homeelecation.ui.home.HomepageAdapter2;
import com.example.homeelecation.ui.home.HomepageModel;
import com.example.homeelecation.ui.horizontal.HorizontalProductScrollModel;
import com.example.homeelecation.ui.slideshow.SliderModel;
import com.example.homeelecation.util.EdgeToEdgeUtils;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint

public class Category_activity extends AppCompatActivity {
    private HomepageAdapter2 adapter;
    private final List<HomepageModel> homepageModelList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        EdgeToEdge.enable(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // Apply Insets
        EdgeToEdgeUtils.applyTopInset(findViewById(R.id.appbar));


        String categoryId = getIntent().getStringExtra("CategoryId");
        String categoryName = getIntent().getStringExtra("CategoryName");
        
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(categoryName);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        RecyclerView categoryRecyclerview = findViewById(R.id.category_recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        categoryRecyclerview.setLayoutManager(layoutManager);

        adapter = new HomepageAdapter2(homepageModelList);
        categoryRecyclerview.setAdapter(adapter);

        CategoryViewModel categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);

        categoryViewModel.categoryPageItems.observe(this, homepageModels -> {
            if (homepageModels != null) {
                adapter.updateList(homepageModels);
            }
        });

        categoryViewModel.uiState.observe(this, uiStatus -> {
            switch (uiStatus) {
                case LOADING:
                    setupFakeLists();
                    adapter.notifyDataSetChanged();
                    break;
                case SUCCESS:
                    // Data is handled by categoryPageItems.observe
                    break;
                case ERROR:
                    Toast.makeText(this, "Error loading data.", Toast.LENGTH_SHORT).show();
                    break;
                case NO_INTERNET:
                    Toast.makeText(this, "No internet connection.", Toast.LENGTH_SHORT).show();
                    break;
            }
        });

        if (categoryId != null) {
            categoryViewModel.loadCategoryPage(categoryId);
        }
    }

    private void setupFakeLists() {
        homepageModelList.clear();
        List<SliderModel> sliderModelFakeList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            sliderModelFakeList.add(new SliderModel("", "null", "#FFFFF0"));
        }

        List<HorizontalProductScrollModel> horizontalModelFakeList = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            horizontalModelFakeList.add(new HorizontalProductScrollModel("", "", "", "", ""));
        }

        homepageModelList.add(new HomepageModel(0, "", sliderModelFakeList));
        homepageModelList.add(new HomepageModel(1, "", "", "", "#FFFFF0"));
        homepageModelList.add(new HomepageModel(2, "", "", "#FFFFF0", horizontalModelFakeList, new ArrayList<>()));
        homepageModelList.add(new HomepageModel(3, "", "", "#FFFFF0", horizontalModelFakeList));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_cart_icon, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.men_search) {
            Toast.makeText(this, "Ok", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.men_cart) {
            Toast.makeText(this, "please wait ", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
