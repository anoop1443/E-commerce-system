package com.example.homeadmin.ui.home2;

import com.example.homeadmin.ui.horizontal.HorizontalProductScrollModel;
import com.example.homeadmin.ui.slideshow.SliderModel;
import com.example.homeadmin.ui.wishList.WishlistModel;

import java.util.List;

public class Home3Model {

    public static final int BANNER_SLIDER = 0;
    public static final int STRIP_AD_BANNER = 1;
    public static final int HORIZONTAL_PRODUCT = 2;
    public static final int GRID_PRODUCT_VIEW = 3;

    private int Type;
    private String homeDocumentId;
    private List<String> contentIds;
    private String adId;

    public List<String> getContentIds() {
        return contentIds;
    }

    public void setContentIds(List<String> contentIds) {
        this.contentIds = contentIds;
    }

    public String getAdId() {
        return adId;
    }

    public void setAdId(String adId) {
        this.adId = adId;
    }

    ///////Banner slider
    private String backgoundcolor;


    //Banner slider
    private List<SliderModel> sliderModelList;
    public Home3Model(int type, String homeDocumentId, List<SliderModel> sliderModelList) {
        this.Type = type;
        this.homeDocumentId = homeDocumentId;
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
    public String getBackgoundcolor() {
        return backgoundcolor;
    }
    public void setBackgoundcolor(String backgoundcolor) {
        this.backgoundcolor = backgoundcolor;
    }
    // banner


    //strip as
    private  String stripDocumentId;
    private String stripImage;
    private String stripBackGroundColor;


    public Home3Model(int type, String homeDocumentId,String stripDocumentId, String stripImage, String stripBackGroundColor) {
        Type = type;
        this.homeDocumentId = homeDocumentId;
        this.stripDocumentId = stripDocumentId;
        this.stripImage = stripImage;
        this.stripBackGroundColor = stripBackGroundColor;
    }

    public String getStripDocumentId() {
        return stripDocumentId;
    }

    public void setStripDocumentId(String stripDocumentId) {
        this.stripDocumentId = stripDocumentId;
    }

    public String getHomeDocumentId() {
        return homeDocumentId;
    }

    public void setHomeDocumentId(String homeDocumentId) {
        this.homeDocumentId = homeDocumentId;
    }

    public String getStripImage() {
        return stripImage;
    }
    public void setStripImage(String stripImage) {
        this.stripImage = stripImage;
    }

    public String getStripBackGroundColor() {
        return stripBackGroundColor;
    }

    public void setStripBackGroundColor(String stripBackGroundColor) {
        this.stripBackGroundColor = stripBackGroundColor;
    }

    //strip as


    //////////////Horizontal product layout
    private  String Titel;
    private List<HorizontalProductScrollModel> horizontalproductscrollModelList;
    private List<WishlistModel> viewAllProductList;

    //// horizontal view
    public Home3Model(int type, String homeDocumentId, String titel, String backgoundcolor, List<HorizontalProductScrollModel> horizontalproductscrollModelList, List<WishlistModel> viewAllProductList) {
        this.Type = type;
        this.homeDocumentId = homeDocumentId;
        this.Titel = titel;
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


    public Home3Model(int type, String homeDocumentId, String titel, String backgoundcolor, List<HorizontalProductScrollModel> horizontalproductscrollModelList) {
        this.Type = type;
        this.homeDocumentId = homeDocumentId;
        this.Titel = titel;
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
