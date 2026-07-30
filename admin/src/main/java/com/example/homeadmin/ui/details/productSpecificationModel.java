package com.example.homeadmin.ui.details;

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
    String moreInfoTitle;
    String moreInfoBody;
    String imageLogo;

    public productSpecificationModel(int viewType, String moreInfoTitle, String moreInfoBody, String imageLogo) {
        this.viewType = viewType;
        this.moreInfoTitle = moreInfoTitle;
        this.moreInfoBody = moreInfoBody;
        this.imageLogo = imageLogo;
    }

    public String getMoreInfoTitle() {
        return moreInfoTitle;
    }

    public void setMoreInfoTitle(String moreInfoTitle) {
        this.moreInfoTitle = moreInfoTitle;
    }

    public String getMoreInfoBody() {
        return moreInfoBody;
    }

    public void setMoreInfoBody(String moreInfoBody) {
        this.moreInfoBody = moreInfoBody;
    }

    public String getImageLogo() {
        return imageLogo;
    }

    public void setImageLogo(String imageLogo) {
        this.imageLogo = imageLogo;
    }

    ////more info layout


    ////only text
    String generateText;

    public productSpecificationModel(int viewType, String generaltext) {
        this.viewType = viewType;
        this.generateText = generaltext;
    }

    public String getGenerateText() {
        return generateText;
    }

    public void setGenerateText(String generateText) {
        this.generateText = generateText;
    }

    ////only text


}
