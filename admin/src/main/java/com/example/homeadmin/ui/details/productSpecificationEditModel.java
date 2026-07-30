package com.example.homeadmin.ui.details;

public class productSpecificationEditModel{

    public static final int SPECIFICATION_DETAILS = 1;


    private int viewType;

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

     String featureName;
     String featureValue;

    public productSpecificationEditModel(int viewType, String featureName, String featureValue) {
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
}
