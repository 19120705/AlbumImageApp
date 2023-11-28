package com.example.albumapp.utility;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.example.albumapp.models.MyImage;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class GetAllPhotoFromDisk {
    public static List<MyImage> getImages(Context context) {
        Uri collection;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
        } else {
            collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }

        // Chỉ lấy các cột mà chúng ta quan tâm
        String[] projection = new String[] {
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DATE_TAKEN,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media._ID,
        };

        // Sắp xếp theo ngày thêm vào
        String sortOrder = MediaStore.Images.Media.DATE_ADDED + " DESC";

        // Truy vấn Content Provider
        Cursor cursor = context.getContentResolver().query(collection, projection, null, null, sortOrder);

        // Lấy chỉ số của cột chúng ta quan tâm
        int dataIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        int dateIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN);
        int dateAddIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED);
        int thumb = cursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails.DATA);

        List<MyImage> listImage = new ArrayList<>();
        Calendar myCal = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd-MM-yyyy");

        while (cursor.moveToNext()) {
            // Lấy đường dẫn của hình ảnh
            String data = cursor.getString(dataIndex);
            MyImage image = new MyImage(data);

            Long dateTaken =cursor.getLong(dateIndex);
            if (dateTaken == 0) {
                dateTaken = cursor.getLong(dateAddIndex);
            }
            if (dateTaken < 10000000000L) {
                dateTaken *= 1000;
            }
            myCal.setTimeInMillis(dateTaken);

            String dateText = formatter.format(myCal.getTime());
            String thumbnail = cursor.getString(thumb);
            String make = null;
            try {
                ExifInterface exif = new ExifInterface(data);
                make = exif.getAttribute(ExifInterface.TAG_MAKE);
                if (make == null) {
                    make = "No Information";
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            image.setDateTaken(dateText);
            image.setThumb(thumbnail);
            image.setMake(make);

            listImage.add(image);
        }

        cursor.close();


        return listImage;
    }
}
