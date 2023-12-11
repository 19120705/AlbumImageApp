package com.example.albumapp.activities;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.albumapp.R;
import com.example.albumapp.adapters.ImageSelectAdapter;
import com.example.albumapp.models.MyImage;
import com.example.albumapp.utility.GetAllPhotoFromDisk;
import com.example.albumapp.utility.DataLocalManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class AddImageActivity extends AppCompatActivity{
    private ImageView img_back_create_album;
    private ImageView btnTick;
    private RecyclerView recyclerView;
    private List<MyImage> listImages;
    private ArrayList<MyImage> listImageSelected;
    private Intent intent;
    private ArrayList<MyImage> dataImages;
    private String albumName;
    private int isPrivate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_image_album);

        intent = getIntent();
        settingData();
        mappingControls();
        event();
    }
    private void settingData() {
        listImageSelected = new ArrayList<>();
        albumName = intent.getStringExtra("name");
        dataImages = intent.getParcelableArrayListExtra("dataImages");
        isPrivate = intent.getIntExtra("isPrivate", 0);
    }
    private void event() {
        img_back_create_album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        setViewRyc();

        btnTick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddImageAsyncTask myAsyncTask = new AddImageAsyncTask();
                new Thread(myAsyncTask).start();
            }
        });
    }


    private void setViewRyc() {
        listImages = GetAllPhotoFromDisk.getSelectiveImages(this);
        ImageSelectAdapter imageAdapter = new ImageSelectAdapter(this, true);
        imageAdapter.setData(listImages);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        recyclerView.setAdapter(imageAdapter);
    }

    private void mappingControls() {
        img_back_create_album = findViewById(R.id.img_back_create_album);
        btnTick = findViewById(R.id.btnTick);
        recyclerView = findViewById(R.id.rycAddAlbum);
    }

    public class AddImageAsyncTask implements Runnable {
        List<MyImage> listImageSelected;

        @Override
        public void run() {
            ImageSelectAdapter adapter = (ImageSelectAdapter) recyclerView.getAdapter();
            if (adapter!=null) {
                listImageSelected = adapter.getListSelectedImage();
            }
            if (listImageSelected.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Chọn một bức ảnh", Toast.LENGTH_SHORT).show();
                return;
            }


            Set<String> imageListFavor = new HashSet<>();

            for (MyImage img: dataImages) {
                imageListFavor.add(img.getPath());
            }
            for (MyImage img: listImageSelected){
                for (MyImage dataImg: dataImages) {
                    if (Objects.equals(img.getPath(), dataImg.getPath())) {
                        listImageSelected.remove(img);
                        break;
                    }
                }
            }
            if (!listImageSelected.isEmpty()) {
                for (MyImage img: listImageSelected) {
                    imageListFavor.add(img.getPath());
                }
            }

            if (isPrivate == 1) {
                DataLocalManager.getInstance().savePrivateAlbum(imageListFavor);
            }
            else {
                DataLocalManager.getInstance().saveAlbum(albumName, imageListFavor);
            }

             // Update UI on the main thread
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Intent resultIntent = new Intent();
                    if (listImageSelected.isEmpty()) {
                        setResult(RESULT_CANCELED, resultIntent);
                    }
                    else {
                        resultIntent.putParcelableArrayListExtra("list_result", new ArrayList<>(listImageSelected));
                        resultIntent.putExtra("REQUEST_CODE", "ADD");
                        setResult(RESULT_OK, resultIntent);
                    }
                    finish();
                }
            });
        }
    }
}
