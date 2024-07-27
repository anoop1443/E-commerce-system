package com.example.homeelecation.ui.Cart;

public class CartModel {
    public static final int CART_ITEM_LAYOUT = 0;
    public static final int CART_TOTAL_AMOUNT_LAYOUT = 1;


    private int Type;

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }

    ////cart layout
    private String productID;
    private String productImage;
    private String product_Title;
    private String product_Price;
    private String product_cut_Price;
    private String product_Coupon;
    private String product_workDay;
    private String product_Service_Amount;
    private boolean inStock;

    public CartModel(int type,String productID, String productImage, String product_Title, String product_Price, String product_cut_Price, String product_Coupon, String product_workDay, String product_Service_Amount,boolean inStock) {
        Type = type;
        this.productID = productID;
        this.productImage = productImage;
        this.product_Title = product_Title;
        this.product_Price = product_Price;
        this.product_cut_Price = product_cut_Price;
        this.product_Coupon = product_Coupon;
        this.product_workDay = product_workDay;
        this.product_Service_Amount = product_Service_Amount;
        this.inStock = inStock;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getProduct_Title() {
        return product_Title;
    }

    public void setProduct_Title(String product_Title) {
        this.product_Title = product_Title;
    }

    public String getProduct_Price() {
        return product_Price;
    }

    public void setProduct_Price(String product_Price) {
        this.product_Price = product_Price;
    }

    public String getProduct_cut_Price() {
        return product_cut_Price;
    }

    public void setProduct_cut_Price(String product_cut_Price) {
        this.product_cut_Price = product_cut_Price;
    }

    public String getProduct_Coupon() {
        return product_Coupon;
    }

    public void setProduct_Coupon(String product_Coupon) {
        this.product_Coupon = product_Coupon;
    }


    public String getProduct_workDay() {
        return product_workDay;
    }

    public void setProduct_workDay(String product_workDay) {
        this.product_workDay = product_workDay;
    }

    public String getProduct_Service_Amount() {
        return product_Service_Amount;
    }

    public void setProduct_Service_Amount(String product_Service_Amount) {
        this.product_Service_Amount = product_Service_Amount;
    }

    public boolean isInStock() {
        return inStock;
    }

    public void setInStock(boolean inStock) {
        this.inStock = inStock;
    }

    ////cart layout


    ////cart total amount layout

    public CartModel(int type) {
        Type = type;
    }




    ////cart total amount layout

}
