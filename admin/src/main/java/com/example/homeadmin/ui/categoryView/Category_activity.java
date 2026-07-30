package com.example.homeadmin.ui.categoryView;

//import static com.example.homeelecation.ui.DbLoadData.lists;
//import static com.example.homeelecation.ui.DbLoadData.loadCategoryActivity;
//import static com.example.homeelecation.ui.DbLoadData.loadedCategoriesName;

import static com.example.homeadmin.ui.DbLoadData.lists;
import static com.example.homeadmin.ui.DbLoadData.loadCategoryActivity;
import static com.example.homeadmin.ui.DbLoadData.loadedCategoriesName;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

//import com.example.homeelecation.R;
//import com.example.homeelecation.ui.home.HomepageAdapter;
//import com.example.homeelecation.ui.home.HomepageModel;
//import com.example.homeelecation.ui.horizontal.HorizontalProductScrollModel;
//import com.example.homeelecation.ui.slideshow.SliderModel;
//import com.example.homeelecation.ui.wishList.WishlistModel;

import com.example.homeadmin.R;
import com.example.homeadmin.ui.home.HomepageAdapter;
import com.example.homeadmin.ui.home.HomepageModel;
import com.example.homeadmin.ui.horizontal.HorizontalProductScrollModel;
import com.example.homeadmin.ui.slideshow.SliderModel;
import com.example.homeadmin.ui.wishList.WishlistModel;

import java.util.ArrayList;
import java.util.List;


public class Category_activity extends AppCompatActivity  {
    Toolbar toolbar;
    RecyclerView category_recyclerview;
    private HomepageAdapter adapter;
    private List<HomepageModel>homepageModelFakeList = new ArrayList<>();






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        toolbar = findViewById(R.id.toolbar);
        category_recyclerview = findViewById(R.id.category_recyclerview);

        setSupportActionBar(toolbar);
        String title = getIntent().getStringExtra("CategoryName");
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /////////Banner slider

        LinearLayoutManager testinglayoutManager = new LinearLayoutManager(this);
        testinglayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        category_recyclerview.setLayoutManager(testinglayoutManager);


        //// Home page
        List<SliderModel> sliderModelFakeListC = new ArrayList<>();

        sliderModelFakeListC.add(new SliderModel("","null","#ffffff"));
        sliderModelFakeListC.add(new SliderModel("","null","#ffffff"));
        sliderModelFakeListC.add(new SliderModel("","null","#ffffff"));
        sliderModelFakeListC.add(new SliderModel("","null","#ffffff"));
        sliderModelFakeListC.add(new SliderModel("","null","#ffffff"));
        sliderModelFakeListC.add(new SliderModel("","null","#ffffff"));
        sliderModelFakeListC.add(new SliderModel("","null","#ffffff"));


        List<HorizontalProductScrollModel> horizontalModelFakeList = new ArrayList<>();

        horizontalModelFakeList.add(new HorizontalProductScrollModel("","","","",""));
        horizontalModelFakeList.add(new HorizontalProductScrollModel("","","","",""));
        horizontalModelFakeList.add(new HorizontalProductScrollModel("","","","",""));
        horizontalModelFakeList.add(new HorizontalProductScrollModel("","","","",""));
        horizontalModelFakeList.add(new HorizontalProductScrollModel("","","","",""));
        horizontalModelFakeList.add(new HorizontalProductScrollModel("","","","",""));
        horizontalModelFakeList.add(new HorizontalProductScrollModel("","","","",""));

        homepageModelFakeList.add(new HomepageModel(0,sliderModelFakeListC));
        homepageModelFakeList.add(new HomepageModel(1,"","#ffffff"));
        homepageModelFakeList.add(new HomepageModel(2,"","#ffffff",horizontalModelFakeList,new ArrayList<WishlistModel>()));
        homepageModelFakeList.add(new HomepageModel(3,"","#ffffff",horizontalModelFakeList));

        adapter = new HomepageAdapter(homepageModelFakeList);
        category_recyclerview.setAdapter(adapter);
        adapter.notifyDataSetChanged();





        int listPosition = 0;

        for (int x = 0; x<loadedCategoriesName.size();x++){

            if (loadedCategoriesName.get(x).equals(title.toUpperCase())){
                listPosition = x;
            }
        }
        if (listPosition == 0){
            loadedCategoriesName.add(title.toUpperCase());
            lists.add(new ArrayList<HomepageModel>());
            adapter = new HomepageAdapter(lists.get(loadedCategoriesName.size() -1));
            loadCategoryActivity(category_recyclerview,this,loadedCategoriesName.size() -1,title);



        }else {

            adapter = new HomepageAdapter(lists.get(listPosition));
            category_recyclerview.setAdapter(adapter);
            homepageModelFakeList.clear();
            adapter.notifyDataSetChanged();

        }


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.search_cart_icon,menu);

        return super.onCreateOptionsMenu(menu);

    }
    @Override
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
            homepageModelFakeList.clear();
            finish();
            return true;

        }

        return super.onOptionsItemSelected(item);
    }
//



}

