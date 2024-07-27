package com.example.homeelecation.ui.home;

import com.example.homeelecation.ui.horizontal.HorizontalProductScrollModel;
import com.example.homeelecation.ui.slideshow.SliderModel;
import com.example.homeelecation.ui.wishList.WishlistModel;

import java.util.List;

public class HomepageModel {
    public static final int BANNER_SLIDER = 0;
    public static final int STRIP_AD_BANNER = 1;
    public static final int HORIZONTAL_PRODUCT = 2;
    public static final int GRID_PRODUCT_VIEW = 3;

    private int Type;
    private String backgoundcolor;


    ///////Banner slider
   private List<SliderModel> sliderModelList;
    public HomepageModel(int type, List<SliderModel> sliderModelList) {
        Type = type;
        this.sliderModelList = sliderModelList;
    }
    public int getType() {
        return Type;
    }
    public void setType(int type) {
        Type = type;
    }
    public List<SliderModel> getSliderModelList() {
        return sliderModelList;
    }
    public void setSliderModelList(List<SliderModel> sliderModelList) {
        this.sliderModelList = sliderModelList;
    }
    ///////Banner slider



    //////strip as
    private String resouce;

    public HomepageModel(int type, String resouce, String backgoundcolor) {
        Type = type;
        this.resouce = resouce;
        this.backgoundcolor = backgoundcolor;
    }
    public String getResouce() {
        return resouce;
    }
    public void setResouce(String resouce) {
        this.resouce = resouce;
    }
    public String getBackgoundcolor() {
        return backgoundcolor;
    }
    public void setBackgoundcolor(String backgoundcolor) {
        this.backgoundcolor = backgoundcolor;
    }
    //////strip as


    //////////////Horizontal product layout
    private  String Titel;
    private List<HorizontalProductScrollModel> horizontalproductscrollModelList;
    private List<WishlistModel> viewAllProductList;

    //// horizontal view
    public HomepageModel(int type, String titel, String backgoundcolor, List<HorizontalProductScrollModel> horizontalproductscrollModelList, List<WishlistModel> viewAllProductList) {
        Type = type;
        Titel = titel;
        this.backgoundcolor =backgoundcolor;
        this.horizontalproductscrollModelList = horizontalproductscrollModelList;
        this.viewAllProductList = viewAllProductList;
    }

    public List<WishlistModel> getViewAllProductList() {
        return viewAllProductList;
    }

    public void setViewAllProductList(List<WishlistModel> viewAllProductList) {
        this.viewAllProductList = viewAllProductList;
    }

    //// horizontal view


    public HomepageModel(int type, String titel, String backgoundcolor, List<HorizontalProductScrollModel> horizontalproductscrollModelList) {
        Type = type;
        Titel = titel;
        this.backgoundcolor =backgoundcolor;
        this.horizontalproductscrollModelList = horizontalproductscrollModelList;
    }
    public String getTitel() {
        return Titel;
    }
    public void setTitel(String titel) {
        Titel = titel;
    }
    public List<HorizontalProductScrollModel> getHorizontalproductscrollModelList() {
        return horizontalproductscrollModelList;
    }
    public void setHorizontalproductscrollModelList(List<HorizontalProductScrollModel> horizontalproductscrollModelList) {
        this.horizontalproductscrollModelList = horizontalproductscrollModelList;
    }
    //////////////Horizontal product layout




    ///////////// Grid layout



    ///////////// Grid layout
}
