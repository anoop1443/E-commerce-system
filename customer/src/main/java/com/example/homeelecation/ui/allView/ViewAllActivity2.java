package com.example.homeelecation.ui.allView;



import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homeelecation.R;
import com.example.homeelecation.ui.gridView.GridAllviewAdapter;
import com.example.homeelecation.ui.horizontal.HorizontalProductScrollModel;
import com.example.homeelecation.ui.wishList.WishlistAdapter;
import com.example.homeelecation.ui.wishList.WishlistModel;

import java.util.List;

public class ViewAllActivity2 extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView recyclerView ,recyclerView2;
    GridView allgridview;
    private Object ListAdapter;

    public static List<HorizontalProductScrollModel> horizontalproductscrollModelList;
    public static List<WishlistModel> viewAllProductList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewall2);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getIntent().getStringExtra("title"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        recyclerView= findViewById(R.id.activity_recycler_view);
        allgridview = findViewById(R.id.all_gridview);

        int layout_code = getIntent().getIntExtra("layout_code",-1);

        if (layout_code == 0) {

            recyclerView.setVisibility(View.VISIBLE);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            layoutManager.setOrientation(layoutManager.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);




            WishlistAdapter adapter = new WishlistAdapter(viewAllProductList, false);

            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();

        }else if (layout_code == 1) {

            allgridview.setVisibility(View.VISIBLE);
            ///////horizontal

            GridAllviewAdapter adapter = new GridAllviewAdapter(horizontalproductscrollModelList);
            allgridview.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }

        ///////horizontal








    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_cart_icon,menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id==R.id.men_search){
            // search code w
            Toast.makeText(this, "Ok", Toast.LENGTH_SHORT).show();

            return true;


        }else if (id==R.id.men_cart) {
            //cart code w
            Toast.makeText(this, "please wait ", Toast.LENGTH_SHORT).show();

            return true;
        }else if (id == android.R.id.home){
            finish();
            return true;

        }

        return super.onOptionsItemSelected(item);
    }
}