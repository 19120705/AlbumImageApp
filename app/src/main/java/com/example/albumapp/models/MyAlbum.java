package com.example.albumapp.models;

import java.util.ArrayList;
import java.util.List;

public class MyAlbum {
    private String pathFolder;
    private MyImage img;
    private String name;
    private List<MyImage> listImage;
    public MyAlbum(List<MyImage> listImg, String name) {
        this.name = name;
        listImage = listImg;
        if (!listImage.isEmpty()) {
            int random = (int) (Math.random() * listImage.size());
            this.img = listImg.get(random);
        }
    }

    public MyImage getImg() {
        return img;
    }
    public String getName() {
        return name;
    }
    public List<MyImage> getList() {
        return listImage;
    }
    public void setItem(MyImage img) {
        listImage.add(img);
    }
}
