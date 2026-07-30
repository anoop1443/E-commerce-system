package com.example.homeadmin.ui.place;

public class PlaceModel {
    private int Image;
    private String Title;
    private String TitleBody;
    private int TotalRating;
    private int CatPrise;
    private int PercentOff;
    private  String OffersApplied;
    private String OffersAvailable;
    private String DeliveryDate;
    private String Charges;


    public PlaceModel(int image, String title, String titleBody, int totalRating, int catPrise, int percentOff, String offersApplied, String offersAvailable, String deliveryDate, String charges) {
        Image = image;
        Title = title;
        TitleBody = titleBody;
        TotalRating = totalRating;
        CatPrise = catPrise;
        PercentOff = percentOff;
        OffersApplied = offersApplied;
        OffersAvailable = offersAvailable;
        DeliveryDate = deliveryDate;
        Charges = charges;
    }

    public int getImage() {
        return Image;
    }

    public void setImage(int image) {
        Image = image;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getTitleBody() {
        return TitleBody;
    }

    public void setTitleBody(String titleBody) {
        TitleBody = titleBody;
    }

    public int getTotalRating() {
        return TotalRating;
    }

    public void setTotalRating(int totalRating) {
        TotalRating = totalRating;
    }

    public int getCatPrise() {
        return CatPrise;
    }

    public void setCatPrise(int catPrise) {
        CatPrise = catPrise;
    }

    public int getPercentOff() {
        return PercentOff;
    }

    public void setPercentOff(int percentOff) {
        PercentOff = percentOff;
    }

    public String getOffersApplied() {
        return OffersApplied;
    }

    public void setOffersApplied(String offersApplied) {
        OffersApplied = offersApplied;
    }

    public String getOffersAvailable() {
        return OffersAvailable;
    }

    public void setOffersAvailable(String offersAvailable) {
        OffersAvailable = offersAvailable;
    }

    public String getDeliveryDate() {
        return DeliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        DeliveryDate = deliveryDate;
    }

    public String getCharges() {
        return Charges;
    }

    public void setCharges(String charges) {
        Charges = charges;
    }
}
