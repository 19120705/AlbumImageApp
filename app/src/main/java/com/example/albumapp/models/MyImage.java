package com.example.albumapp.models;

public class MyImage {
    private String path;
    private String thumb;
    private String dateTaken;
    private String make; //Trường lưu thông tin máy ảnh
    public String getPath() {
        return path;
    }

    public MyImage() {
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public MyImage(String path) {
        this.path = path;
    }

    public String getDateTaken() {
        return dateTaken;
    }

    public void setDateTaken(String dateTaken) {
        this.dateTaken = dateTaken;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getName() {
        String[] _array = getPath().split("/");
        return _array[_array.length - 1];
    }

}
