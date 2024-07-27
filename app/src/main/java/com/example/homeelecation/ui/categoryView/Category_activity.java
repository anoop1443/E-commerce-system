package com.example.homeelecation.ui.categoryView;

import static com.example.homeelecation.ui.DbLoadData.lists;
import static com.example.homeelecation.ui.DbLoadData.loadCategoryActivity;
import static com.example.homeelecation.ui.DbLoadData.loadedCategoriesName;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homeelecation.R;
import com.example.homeelecation.ui.home.HomepageAdapter;
import com.example.homeelecation.ui.home.HomepageModel;
import com.example.homeelecation.ui.horizontal.HorizontalProductScrollModel;
import com.example.homeelecation.ui.slideshow.SliderModel;
import com.example.homeelecation.ui.wishList.WishlistModel;

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
      //  List<SliderModel>sliderModelList = new ArrayList<>();

       
      //  sliderModelList = new ArrayList<SliderModel>();
//        sliderModelList.add(new SliderModel(R.drawable.dth,"#CE873D"));
//        sliderModelList.add(new SliderModel(R.drawable.invt,"#CE873D"));
//        sliderModelList.add(new SliderModel(R.drawable.img,"#CE873D"));
//
//
//        sliderModelList.add(new SliderModel(R.drawable.black_swich,"#3f3f41"));
//        sliderModelList.add(new SliderModel(R.drawable.swich,"#202440"));
//        sliderModelList.add(new SliderModel(R.drawable.cabel,"#ffffff"));
//        sliderModelList.add(new SliderModel(R.drawable.wires,"#CE873D"));
//
//        sliderModelList.add(new SliderModel(R.drawable.haeter,"#f17a0a"));
//        sliderModelList.add(new SliderModel(R.drawable.fansli,"#CE873D"));
//        sliderModelList.add(new SliderModel(R.drawable.fant,"#CE873D"));
//        sliderModelList.add(new SliderModel(R.drawable.dth,"#CE873D"));
//
//
//        sliderModelList.add(new SliderModel(R.drawable.invt,"#CE873D"));
//        sliderModelList.add(new SliderModel(R.drawable.img,"#CE873D"));
//        sliderModelList.add(new SliderModel(R.drawable.black_swich,"#CE873D"));
        /////////Banner slider


        ///////horizontal
     //   List<HorizontalProductScrollModel> horizontalproductscrollModelList = new ArrayList<>();
//        horizontalproductscrollModelList.add(new HorizontalProductScrollModel(R.drawable.haeter,"Havells Haeter","400mm wlla fan","Rs.2499"));
//        horizontalproductscrollModelList.add(new HorizontalProductScrollModel(R.drawable.horizontla_fan,"Havells Fan","400mm wlla fan","Rs.2499"));
//        horizontalproductscrollModelList.add(new HorizontalProductScrollModel(R.drawable.fansli,"Havells Fan","400mm wlla fan","Rs.2499"));
//        horizontalproductscrollModelList.add(new HorizontalProductScrollModel(R.drawable.black_swich,"Havells swich","400mm wlla fan","Rs.2499"));
//        horizontalproductscrollModelList.add(new HorizontalProductScrollModel(R.drawable.swich,"Havells Board","400mm wlla fan","Rs.2499"));
//        horizontalproductscrollModelList.add(new HorizontalProductScrollModel(R.drawable.wires,"Havells wires","400mm wlla fan","Rs.2499"));
//        horizontalproductscrollModelList.add(new HorizontalProductScrollModel(R.drawable.fansli,"Havells Fan","400mm wlla fan","Rs.2499"));
//        horizontalproductscrollModelList.add(new HorizontalProductScrollModel(R.drawable.fant,"Havells Fan","400mm wlla fan","Rs.2499"));
//        horizontalproductscrollModelList.add(new HorizontalProductScrollModel(R.drawable.img,"Havells Item","400mm wlla fan","Rs.2499"));
//        horizontalproductscrollModelList.add(new HorizontalProductScrollModel(R.drawable.fansli,"Havells Fan","400mm wlla fan","Rs.2499"));

        ///////horizontal

        LinearLayoutManager testinglayoutManager = new LinearLayoutManager(this);
        testinglayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        category_recyclerview.setLayoutManager(testinglayoutManager);


        //// Home page
        List<SliderModel> sliderModelFakeListC = new ArrayList<>();

        sliderModelFakeListC.add(new SliderModel("null","#ffffff"));
        sliderModelFakeListC.add(new SliderModel("null","#ffffff"));
        sliderModelFakeListC.add(new SliderModel("null","#ffffff"));
        sliderModelFakeListC.add(new SliderModel("null","#ffffff"));
        sliderModelFakeListC.add(new SliderModel("null","#ffffff"));
        sliderModelFakeListC.add(new SliderModel("null","#ffffff"));
        sliderModelFakeListC.add(new SliderModel("null","#ffffff"));


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



        //List<HomepageModel> homepageModelList = new ArrayList<>();

        //homepageModelList.add(new HomepageModel(0,sliderModelList));
       // homepageModelList.add(new HomepageModel(1,R.drawable.strip_ads,"#FA8E1526"));
//        homepageModelList.add(new HomepageModel(2,"Deals of the Day",horizontalproductscrollModelList));
//        homepageModelList.add(new HomepageModel(3,"Deals of the Day",horizontalproductscrollModelList));
//        homepageModelList.add(new HomepageModel(3,"Deals of the Day",horizontalproductscrollModelList));
//        homepageModelList.add(new HomepageModel(3,"Deals of the Day",horizontalproductscrollModelList));
//        homepageModelList.add(new HomepageModel(2,"Deals of the Day",horizontalproductscrollModelList));
//
//        //homepageModelList.add(new HomepageModel(1,R.drawable.strip_ads,"#FA8E1526"));
//        homepageModelList.add(new HomepageModel(2,"Deals of the Day",horizontalproductscrollModelList));
//        homepageModelList.add(new HomepageModel(3,"Deals of the Day",horizontalproductscrollModelList));
//        homepageModelList.add(new HomepageModel(3,"Deals of the Day",horizontalproductscrollModelList));
//        homepageModelList.add(new HomepageModel(3,"Deals of the Day",horizontalproductscrollModelList));
//        homepageModelList.add(new HomepageModel(2,"Deals of the Day",horizontalproductscrollModelList));
//        homepageModelList.add(new HomepageModel(2,"Deals of the Day",horizontalproductscrollModelList));
//        homepageModelList.add(new HomepageModel(3,"Deals of the Day",horizontalproductscrollModelList));
//        homepageModelList.add(new HomepageModel(3,"Deals of the Day",horizontalproductscrollModelList));
//        homepageModelList.add(new HomepageModel(3,"Deals of the Day",horizontalproductscrollModelList));
//        homepageModelList.add(new HomepageModel(2,"Deals of the Day",horizontalproductscrollModelList));

       // HomepageAdapter adapter = new HomepageAdapter(homepageModelList);
        //category_recyclerview.setAdapter(adapter);
        //adapter.notifyDataSetChanged();


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


        }else if (id==R.id.men_cart) {
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

