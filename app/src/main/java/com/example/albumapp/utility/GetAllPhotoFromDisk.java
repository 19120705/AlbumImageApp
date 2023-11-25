package com.example.albumapp.utility;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.example.albumapp.models.MyImage;

import java.util.ArrayList;
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
        };

        // Sắp xếp theo ngày thêm vào
        String sortOrder = MediaStore.Images.Media.DATE_ADDED + " DESC";

        // Truy vấn Content Provider
        Cursor cursor = context.getContentResolver().query(collection, projection, null, null, sortOrder);

        // Lấy chỉ số của cột chúng ta quan tâm
        int dataIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        List<MyImage> listImage = new ArrayList<>();

        while (cursor.moveToNext()) {
            // Lấy đường dẫn của hình ảnh
            String data = cursor.getString(dataIndex);
            listImage.add(new MyImage(data));
        }

        cursor.close();

        return listImage;
    }
}
