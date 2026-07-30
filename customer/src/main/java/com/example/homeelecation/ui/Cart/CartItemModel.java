package com.example.homeelecation.ui.Cart;

public class CartItemModel {
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
    private String productTitle;
    private String productPrice;
    private String productCutPrice;
    private String productCoupon;
    private String productWorkDay;
    private String productServiceAmount;
    private long   productQty;
    private boolean inStock;

    public CartItemModel(int type, String productID, String productImage, String productTitle, String productPrice, String productCutPrice, String productCoupon, String productWorkDay, String productServiceAmount, Long productQty, boolean inStock) {
        Type = type;
        this.productID = productID;
        this.productImage = productImage;
        this.productTitle = productTitle;
        this.productPrice = productPrice;
        this.productCutPrice = productCutPrice;
        this.productCoupon = productCoupon;
        this.productWorkDay = productWorkDay;
        this.productServiceAmount = productServiceAmount;
        this.productQty = productQty;
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

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductCutPrice() {
        return productCutPrice;
    }

    public void setProductCutPrice(String productCutPrice) {
        this.productCutPrice = productCutPrice;
    }

    public String getProductCoupon() {
        return productCoupon;
    }

    public void setProductCoupon(String productCoupon) {
        this.productCoupon = productCoupon;
    }


    public String getProductWorkDay() {
        return productWorkDay;
    }

    public void setProductWorkDay(String productWorkDay) {
        this.productWorkDay = productWorkDay;
    }

    public String getProductServiceAmount() {
        return productServiceAmount;
    }

    public void setProductServiceAmount(String productServiceAmount) {
        this.productServiceAmount = productServiceAmount;
    }

    public Long getProductQty() {
        return productQty;
    }

    public void setProductQty(Long productQty) {
        this.productQty = productQty;
    }

    public boolean isInStock() {
        return inStock;
    }

    public void setInStock(boolean inStock) {
        this.inStock = inStock;
    }

    ////cart layout


    ////cart total amount layout
    private long totalItem ;
    private long totalItemPrise;
    private long totalItemDiscount;
    private String deliveryCharges;
    private long totalAmount;

    public CartItemModel(int type) {
        Type = type;
    }

    public long getTotalItem() {
        return totalItem;
    }

    public void setTotalItem(long totalItem) {
        this.totalItem = totalItem;
    }

    public long getTotalItemPrise() {
        return totalItemPrise;
    }

    public void setTotalItemPrise(long totalItemPrise) {
        this.totalItemPrise = totalItemPrise;
    }

    public long getTotalItemDiscount() {
        return totalItemDiscount;
    }

    public void setTotalItemDiscount(long totalItemDiscount) {
        this.totalItemDiscount = totalItemDiscount;
    }

    public String getDeliveryCharges() {
        return deliveryCharges;
    }

    public void setDeliveryCharges(String deliveryCharges) {
        this.deliveryCharges = deliveryCharges;
    }

    public long getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(long totalAmount) {
        this.totalAmount = totalAmount;
    }

////cart total amount layout

}
