package com.example.albumapp;

import android.Manifest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;


import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private ImageView imageView;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView1);
        imageView = (ImageView) findViewById(R.id.imageView1);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Kiểm tra quyền truy cập vào bộ nhớ ngoại vi
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                != PackageManager.PERMISSION_GRANTED) {
            // Nếu chưa có quyền, yêu cầu quyền
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_MEDIA_IMAGES}, 0);
        } else {
            // Nếu đã có quyền, tải hình ảnh từ bộ nhớ ngoại vi
            setupRecyclerView();
        }

    }

    private void setupRecyclerView() {
//        // Thiết lập RecyclerView với Adapter
        List<String> images = getImages();
        ImageAdapter adapter = new ImageAdapter(this, images);
        recyclerView.setAdapter(adapter);

    }

    private List<String> getImages() {
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
        Cursor cursor = getContentResolver().query(collection, projection, null, null, sortOrder);

        // Lấy chỉ số của cột chúng ta quan tâm
        int dataIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        List<String> imagePaths = new ArrayList<>();

        while (cursor.moveToNext()) {
            // Lấy đường dẫn của hình ảnh
            String data = cursor.getString(dataIndex);
            Toast.makeText(this, data, Toast.LENGTH_SHORT).show();
            imagePaths.add(data);
        }

        cursor.close();
        return imagePaths;
    }


}