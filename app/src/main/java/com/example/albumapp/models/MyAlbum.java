package com.example.albumapp.models;

import java.util.List;

public class MyAlbum {
    private MyImage image;
    private String name;
    private List<MyImage> listImage;
    public MyAlbum(List<MyImage> listImg, String name) {
        this.name = name;
        listImage = listImg;
        if (!listImage.isEmpty()) {
            int random = (int) (Math.random() * listImage.size());
            this.image = listImg.get(random);
        }
    }

    public MyImage getImage() {
        return image;
    }
    public String getName() {
        return name;
    }
    public List<MyImage> getListImage() {
        return listImage;
    }
    public void addItem(MyImage img) {
        listImage.add(img);
    }
}
