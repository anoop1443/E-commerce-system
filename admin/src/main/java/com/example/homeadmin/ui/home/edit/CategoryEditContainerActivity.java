package com.example.homeadmin.ui.home.edit;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.homeadmin.R;

public class CategoryEditContainerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_edit_container);

        Toolbar toolbar = findViewById(R.id.toolbar_container);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        int editType = getIntent().getIntExtra("EDIT_TYPE", 0);
        String target = getIntent().getStringExtra("TARGET");
        String categoryId = getIntent().getStringExtra("CATEGORY_ID");

        Fragment fragment;
        String title;

        switch (editType) {
            case 0:
                fragment = new Type0Fragment();
                title = "Manage Banners";
                break;
            case 1:
                fragment = new Type1Fragment();
                title = "Manage Ads";
                break;
            case 2:
                fragment = new Type2Fragment();
                title = "Horizontal View";
                break;
            case 3:
                fragment = new Type3Fragment();
                title = "Grid View";
                break;
            default:
                fragment = new Type0Fragment();
                title = "Edit";
                break;
        }

        getSupportActionBar().setTitle(title);

        Bundle args = new Bundle();
        args.putString("TARGET", target);
        args.putString("CATEGORY_ID", categoryId);
        fragment.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
