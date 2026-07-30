package com.example.homeelecation.ui.details;

public class productSpecificationModel {

    public static final int GENERAL_TEXT_VIEW = 0;
    public static final int SPECIFICATION_DETAILS = 1;
    public static final int MORE_INFO_DETAILS = 2;


    ////type
     int viewType;

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }
    ////type


    ////specification layout
    String featureName;
    String featureValue;

    public productSpecificationModel(int viewType, String featureName, String featureValue) {
        this.viewType = viewType;
        this.featureName = featureName;
        this.featureValue = featureValue;
    }

    public String getFeatureName() {
        return featureName;
    }

    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

    public String getFeatureValue() {
        return featureValue;
    }

    public void setFeatureValue(String featureValue) {
        this.featureValue = featureValue;
    }
    ////specification layout

    ////more info layout
    String manufactureText;
    String manufactureAdd;
    int image;

    public productSpecificationModel(int viewType,String manufactureText, String manufactureAdd,int image) {
        this.viewType = viewType;
        this.manufactureText = manufactureText;
        this.manufactureAdd = manufactureAdd;
    }

    public String getManufactureText() {
        return manufactureText;
    }

    public void setManufactureText(String manufactureText) {
        this.manufactureText = manufactureText;
    }

    public String getManufactureAdd() {
        return manufactureAdd;
    }

    public void setManufactureAdd(String manufactureAdd) {
        this.manufactureAdd = manufactureAdd;
    }
    ////more info layout


    ////only text
    String generaltext;

    public productSpecificationModel(int viewType, String generaltext) {
        this.viewType = viewType;
        this.generaltext = generaltext;
    }

    public String getGeneraltext() {
        return generaltext;
    }

    public void setGeneraltext(String generaltext) {
        this.generaltext = generaltext;
    }

    ////only text


}
