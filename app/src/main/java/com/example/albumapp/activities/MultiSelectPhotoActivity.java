package com.example.albumapp.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.albumapp.R;
import com.example.albumapp.adapters.ImageSelectAdapter;
import com.example.albumapp.models.MyImage;
import com.example.albumapp.utility.GetAllPhotoFromDisk;

import java.util.ArrayList;
import java.util.List;

public class MultiSelectPhotoActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<MyImage> listImage;
    private List<MyImage> listImageSelected;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mutilselect_photo);
        settingData();
        setViewRyc();
    }
    private void settingData() {
        listImageSelected = new ArrayList<>();
    }
    private void setViewRyc() {
        recyclerView = findViewById(R.id.recyclerViewPhoto);
        listImage = GetAllPhotoFromDisk.getImages(getApplicationContext());
        ImageSelectAdapter imageAdapter = new ImageSelectAdapter(this, true);
        imageAdapter.setData(listImage);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        recyclerView.setAdapter(imageAdapter);
    }
}
