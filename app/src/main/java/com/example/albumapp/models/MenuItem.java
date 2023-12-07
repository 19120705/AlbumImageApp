package com.example.albumapp.models;

public class MenuItem {
    private int iconResource;
    private String title;
    private int color; // Thêm thuộc tính màu

    public MenuItem(int iconResource, String title, int color) {
        this.iconResource = iconResource;
        this.title = title;
        this.color = color;
    }

    public int getIconResource() {
        return iconResource;
    }

    public String getTitle() {
        return title;
    }

    public int getColor() {
        return color;
    }
}
