package com.example.homeelecation.ui.slideshow;

public class SliderModel {
    private String banner;
    private String backgruondcolor;

    public SliderModel(String banner, String backgruondcolor) {
        this.banner = banner;
        this.backgruondcolor = backgruondcolor;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public String getBackgruondcolor() {
        return backgruondcolor;
    }

    public void setBackgruondcolor(String backgruondcolor) {
        this.backgruondcolor = backgruondcolor;
    }
}
