package com.example.albumapp.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class MyImage implements Parcelable {
    private String path;
    private String thumb;
    private String dateTaken;
    private String make; //Trường lưu thông tin máy ảnh

    public MyImage() {
    }
    public MyImage(String path) {
        this.path = path;
    }
    protected MyImage(Parcel in) {
        path = in.readString();
        thumb = in.readString();
        dateTaken = in.readString();
        make = in.readString();
    }

    public static final Creator<MyImage> CREATOR = new Creator<MyImage>() {
        @Override
        public MyImage createFromParcel(Parcel in) {
            return new MyImage(in);
        }

        @Override
        public MyImage[] newArray(int size) {
            return new MyImage[size];
        }
    };

    public String getPath() {
        return path;
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


    public String getDateTaken() {
        return dateTaken;
    }

    public void setDateTaken(String dateTaken) {
        this.dateTaken = dateTaken;
    }

    public String getMake() {
        if (make == null) {
            return "No Webcam";
        } else {
            return make;
        }
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getName() {
        String[] _array = getPath().split("/");
        return _array[_array.length - 1];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {

        dest.writeString(path);
        dest.writeString(thumb);
        dest.writeString(dateTaken);
        dest.writeString(make);
    }
}
