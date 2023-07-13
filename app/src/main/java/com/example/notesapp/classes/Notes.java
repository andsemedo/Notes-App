package com.example.notesapp.classes;

import java.util.Date;

public class Notes {
    String id, title, content, date, imageURL, theme, themeColor;


    public Notes() {}

    public Notes(String id,String title, String content, String date, String theme, String themeColor, String imageURL) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.date = date;
        this.theme = theme;
        this.themeColor = themeColor;
        this.imageURL = imageURL;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getThemeColor() {
        return themeColor;
    }

    public void setThemeColor(String themeColor) {
        this.themeColor = themeColor;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}
