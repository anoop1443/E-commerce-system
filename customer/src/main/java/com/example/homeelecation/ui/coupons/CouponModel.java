package com.example.homeelecation.ui.coupons;

public class CouponModel {
    private String CouponTitle;
    private String CouponValid;
    private String CouponBody;

    public CouponModel(String couponTitle, String couponValid, String couponBody) {
        CouponTitle = couponTitle;
        CouponValid = couponValid;
        CouponBody = couponBody;
    }

    public String getCouponTitle() {
        return CouponTitle;
    }

    public void setCouponTitle(String couponTitle) {
        CouponTitle = couponTitle;
    }

    public String getCouponValid() {
        return CouponValid;
    }

    public void setCouponValid(String couponValid) {
        CouponValid = couponValid;
    }

    public String getCouponBody() {
        return CouponBody;
    }

    public void setCouponBody(String couponBody) {
        CouponBody = couponBody;
    }
}
