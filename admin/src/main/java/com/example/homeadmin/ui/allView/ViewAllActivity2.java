package com.example.homeadmin.ui.allView;



import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homeadmin.R;
import com.example.homeadmin.ui.gridView.GridAllViewAdapter;
import com.example.homeadmin.ui.horizontal.HorizontalProductScrollModel;
import com.example.homeadmin.ui.wishList.WishlistAdapter;
import com.example.homeadmin.ui.wishList.WishlistModel;

import java.util.List;

public class ViewAllActivity2 extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView recyclerView;
    GridView allgridview;

    public static List<HorizontalProductScrollModel> horizontalproductscrollModelList;
    public static List<WishlistModel> viewAllProductList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Niche diye gaye code ko setContentView se pehle add karein
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Window window = getWindow();
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
//        }
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




//            wishlistModelList.add(new WishlistModel(R.drawable.tebal_fan, 2, 3, 3432, "Tabla fan ", 750, 900, "cash on delivery available"));
//            wishlistModelList.add(new WishlistModel(R.drawable.fansli, 2, 3, 3482, " Fan ", 1450, 1650, "cash on delivery available"));
//            wishlistModelList.add(new WishlistModel(R.drawable.haeter, 2, 3, 3432, "hater fan ", 750, 900, "cash on delivery available"));
//            wishlistModelList.add(new WishlistModel(R.drawable.horizontla_fan, 2, 3, 3432, "Tabla fan ", 750, 900, "cash on delivery available"));
//            wishlistModelList.add(new WishlistModel(R.drawable.tebal_fan, 2, 3, 3432, "Tabla fan ", 750, 900, "cash on delivery available"));
//            wishlistModelList.add(new WishlistModel(R.drawable.swich, 6, 3, 3432, "Tabla fan ", 750, 900, "cash on delivery available"));
//            wishlistModelList.add(new WishlistModel(R.drawable.tebal_fan, 2, 3, 3432, "Tabla fan ", 750, 900, "cash on delivery available"));
//            wishlistModelList.add(new WishlistModel(R.drawable.wires, 0, 3, 3432, "Tabla fan ", 750, 900, "cash on delivery available"));
//            wishlistModelList.add(new WishlistModel(R.drawable.tebal_fan, 2, 3, 3432, "Tabla fan ", 750, 900, "cash on delivery available"));
//            wishlistModelList.add(new WishlistModel(R.drawable.dth, 1, 3, 3432, "Free DTH  ", 1499, 1750, "cash on delivery available"));
//            wishlistModelList.add(new WishlistModel(R.drawable.tebal_fan, 2, 3, 3432, "Tabla fan ", 750, 900, "cash on delivery available"));
//            wishlistModelList.add(new WishlistModel(R.drawable.tebal_fan, 2, 3, 3432, "Tabla fan ", 750, 900, "cash on delivery available"));
//            wishlistModelList.add(new WishlistModel(R.drawable.tebal_fan, 2, 3, 3432, "Tabla fan ", 750, 900, "cash on delivery available"));
//            wishlistModelList.add(new WishlistModel(R.drawable.cabel, 2, 3, 3432, "Cable 6mm 440V/hs ", 1200, 1500, "cash on delivery available"));
//            wishlistModelList.add(new WishlistModel(R.drawable.tebal_fan, 2, 3, 3432, "Tabla fan ", 750, 900, "cash on delivery available"));
//            wishlistModelList.add(new WishlistModel(R.drawable.tebal_fan, 2, 3, 3432, "Tabla fan ", 750, 900, "cash on delivery available"));
//            wishlistModelList.add(new WishlistModel(R.drawable.black_swich, 2, 3, 3432, "Black switch ", 750, 900, "cash on delivery available"));
//            wishlistModelList.add(new WishlistModel(R.drawable.fansli, 2, 3, 3482, " Fan ", 1450, 1650, "cash on delivery available"));
//            wishlistModelList.add(new WishlistModel(R.drawable.haeter, 2, 3, 3432, "hater fan ", 750, 900, "cash on delivery available"));
//            wishlistModelList.add(new WishlistModel(R.drawable.horizontla_fan, 2, 3, 3432, "Tabla fan ", 750, 900, "cash on delivery available"));
//            wishlistModelList.add(new WishlistModel(R.drawable.tebal_fan, 2, 3, 3432, "Tabla fan ", 750, 900, "cash on delivery available"));
//            wishlistModelList.add(new WishlistModel(R.drawable.swich, 2, 3, 3432, "Tabla fan ", 750, 900, "cash on delivery available"));
//            wishlistModelList.add(new WishlistModel(R.drawable.tebal_fan, 2, 3, 3432, "Tabla fan ", 750, 900, "cash on delivery available"));
//            wishlistModelList.add(new WishlistModel(R.drawable.wires, 0, 3, 3432, "Tabla fan ", 750, 900, "cash on delivery available"));

            ///////horizontal

            WishlistAdapter adapter = new WishlistAdapter(viewAllProductList, false);

            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            ///////horizontal


        }else if (layout_code == 1) {

            allgridview.setVisibility(View.VISIBLE);
            ///////gritView
            GridAllViewAdapter adapter = new GridAllViewAdapter(horizontalproductscrollModelList);
            allgridview.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            ///////gritView
        }









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


        }else if (id==R.id.menu_add) {
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