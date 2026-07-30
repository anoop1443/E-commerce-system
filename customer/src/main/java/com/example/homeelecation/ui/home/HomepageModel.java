package com.example.homeelecation.ui.home;

import com.example.homeelecation.ui.horizontal.HorizontalProductScrollModel;
import com.example.homeelecation.ui.slideshow.SliderModel;
import com.example.homeelecation.ui.wishList.WishlistModel;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.PropertyName;

import java.util.List;

public class HomepageModel {
    public static final int BANNER_SLIDER = 0;
    public static final int STRIP_AD_BANNER = 1;
    public static final int HORIZONTAL_PRODUCT = 2;
    public static final int GRID_PRODUCT_VIEW = 3;

    private int type;
    private String documentID;
    private String backgroundColor;

    // global fields
    public int getType() { return type; }
    public void setType(int type) { this.type = type; }
    
    @PropertyName("documentID")
    public String getDocumentID() { return documentID; }
    @PropertyName("documentID")
    public void setDocumentID(String documentID) { this.documentID = documentID; }
    
    public String getBackgroundColor() { return backgroundColor; }
    public void setBackgroundColor(String backgroundColor) { this.backgroundColor = backgroundColor; }

    /////// Banner slider
    private List<SliderModel> sliderModelList;

    public HomepageModel(int type, List<SliderModel> sliderModelList) {
        this.type = type;
        this.sliderModelList = sliderModelList;
    }

    public HomepageModel(int type, String documentID, List<SliderModel> sliderModelList) {
        this.type = type;
        this.documentID = documentID;
        this.sliderModelList = sliderModelList;
    }

    public List<SliderModel> getSliderModelList() { return sliderModelList; }
    public void setSliderModelList(List<SliderModel> sliderModelList) { this.sliderModelList = sliderModelList; }

    /////// Strip Ad
    private String stripDocumentID;
    private String stripImage;
    private String stripBackgroundColor;

    public HomepageModel(int type, String stripImage, String stripBackgroundColor) {
        this.type = type;
        this.stripImage = stripImage;
        this.stripBackgroundColor = stripBackgroundColor;
    }

    public HomepageModel(int type, String documentID, String stripDocumentID, String stripImage, String stripBackgroundColor) {
        this.type = type;
        this.documentID = documentID;
        this.stripDocumentID = stripDocumentID;
        this.stripImage = stripImage;
        this.stripBackgroundColor = stripBackgroundColor;
    }

    @PropertyName("stripDocumentID")
    public String getStripDocumentID() { return stripDocumentID; }
    @PropertyName("stripDocumentID")
    public void setStripDocumentID(String stripDocumentID) { this.stripDocumentID = stripDocumentID; }
    
    public String getStripImage() { return stripImage; }
    public void setStripImage(String stripImage) { this.stripImage = stripImage; }
    public String getStripBackgroundColor() { return stripBackgroundColor; }
    public void setStripBackgroundColor(String stripBackgroundColor) { this.stripBackgroundColor = stripBackgroundColor; }

    /////// Horizontal product layout
    private String title;
    private List<HorizontalProductScrollModel> horizontalproductscrollModelList;
    private List<WishlistModel> viewAllProductList;

    public HomepageModel(int type, String title, String backgroundColor, List<HorizontalProductScrollModel> horizontalproductscrollModelList, List<WishlistModel> viewAllProductList) {
        this.type = type;
        this.title = title;
        this.backgroundColor = backgroundColor;
        this.horizontalproductscrollModelList = horizontalproductscrollModelList;
        this.viewAllProductList = viewAllProductList;
    }

    public HomepageModel(int type, String documentID, String title, String backgroundColor, List<HorizontalProductScrollModel> horizontalproductscrollModelList, List<WishlistModel> viewAllProductList) {
        this.type = type;
        this.documentID = documentID;
        this.title = title;
        this.backgroundColor = backgroundColor;
        this.horizontalproductscrollModelList = horizontalproductscrollModelList;
        this.viewAllProductList = viewAllProductList;
    }

    public HomepageModel(int type, String title, String backgroundColor, List<HorizontalProductScrollModel> horizontalproductscrollModelList) {
        this.type = type;
        this.title = title;
        this.backgroundColor = backgroundColor;
        this.horizontalproductscrollModelList = horizontalproductscrollModelList;
    }

    public HomepageModel(int type, String documentID, String title, String backgroundColor, List<HorizontalProductScrollModel> horizontalproductscrollModelList) {
        this.type = type;
        this.documentID = documentID;
        this.title = title;
        this.backgroundColor = backgroundColor;
        this.horizontalproductscrollModelList = horizontalproductscrollModelList;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public List<HorizontalProductScrollModel> getHorizontalproductscrollModelList() { return horizontalproductscrollModelList; }
    public void setHorizontalproductscrollModelList(List<HorizontalProductScrollModel> horizontalproductscrollModelList) { this.horizontalproductscrollModelList = horizontalproductscrollModelList; }
    public List<WishlistModel> getViewAllProductList() { return viewAllProductList; }
    public void setViewAllProductList(List<WishlistModel> viewAllProductList) { this.viewAllProductList = viewAllProductList; }

    // Legacy support for any discrepancies
    @Exclude
    public String getDocumentId() { return getDocumentID(); }
    @Exclude
    public void setDocumentId(String id) { setDocumentID(id); }
    
    @Exclude
    public String getBackGroundColor() { return getBackgroundColor(); }
    @Exclude
    public void setBackGroundColor(String color) { setBackgroundColor(color); }

    @Exclude
    public String getBackgoundcolor() { return getBackgroundColor(); }
    @Exclude
    public void setBackgoundcolor(String color) { setBackgroundColor(color); }
    
    @Exclude
    public String getStripDocumentId() { return getStripDocumentID(); }
    @Exclude
    public void setStripDocumentId(String id) { setStripDocumentID(id); }

    @Exclude
    public String getStripBackGroundColor() { return getStripBackgroundColor(); }
    @Exclude
    public void setStripBackGroundColor(String color) { setStripBackgroundColor(color); }

    @Exclude
    public String getResouce() { return getStripImage(); }
    @Exclude
    public void setResouce(String res) { setStripImage(res); }
    @Exclude
    public String getTitel() { return getTitle(); }
    @Exclude
    public void setTitel(String t) { setTitle(t); }
}
