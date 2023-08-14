package com.mmf.plantpal.models;

public class HelpItem {
    private String Title;
    private int helpImg;


    public HelpItem(String title, int helpImg) {
        Title = title;
        this.helpImg = helpImg;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public int getHelpImg() {
        return helpImg;
    }

    public void setHelpImg(int helpImg) {
        this.helpImg = helpImg;
    }
}
