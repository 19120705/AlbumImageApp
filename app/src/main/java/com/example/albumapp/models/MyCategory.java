package com.example.albumapp.models;

import java.util.List;

public class MyCategory {

    private String name;
    private List<MyImage> listImage;

    public MyCategory(String nameCategory, List<MyImage> listImage) {
        this.name = nameCategory;
        this.listImage = listImage;
    }

    public MyCategory(List<MyImage> listImage) {
        this.listImage = listImage;
    }

    public String getNameCategory() {
        return name;
    }

    public void setNameCategory(String nameCategory) {
        this.name = nameCategory;
    }

    public List<MyImage> getListImages() {
        return listImage;
    }

    public void setListImages(List<MyImage> listImage) {
        this.listImage = listImage;
    }

    public void addItemToListImages(MyImage img){this.listImage.add(img);}


}
