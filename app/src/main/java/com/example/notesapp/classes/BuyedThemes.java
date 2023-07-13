package com.example.notesapp.classes;

public class BuyedThemes {

    String themeUrl, themeColor;
    int pos;

    public BuyedThemes() {
    }

    public BuyedThemes(String themeUrl, int pos, String themeColor) {
        this.themeUrl = themeUrl;
        this.pos = pos;
        this.themeColor = themeColor;
    }

    public String getThemeUrl() {
        return themeUrl;
    }

    public void setThemeUrl(String themeUrl) {
        this.themeUrl = themeUrl;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public String getThemeColor() {
        return themeColor;
    }

    public void setThemeColor(String themeColor) {
        this.themeColor = themeColor;
    }
}
