package com.example.albumapp.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.albumapp.R;
import com.example.albumapp.utility.DataLocalManager;
import com.example.albumapp.adapters.ImageSelectAdapter;
import com.example.albumapp.models.MyImage;
import com.example.albumapp.utility.GetAllPhotoFromDisk;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CreateAlbumActivity extends AppCompatActivity{
    private ImageView imgBackCreateAlbum;
    private ImageView btnTick;
    private EditText edtTitleAlbum;
    private RecyclerView recyclerView;
    private List<MyImage> listImage;
    private List<MyImage> listImageSelected;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_album);
        settingData();
        mappingControls();
        event();
    }
    private void settingData() {
        listImageSelected = new ArrayList<>();
    }
    private void event() {
        imgBackCreateAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        setViewRyc();

        btnTick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(edtTitleAlbum.getText())) {
                    String albumName = edtTitleAlbum.getText().toString();
                    List<String> allAlbumName = DataLocalManager.getInstance().getAllAlbum();
                    if(albumName.contains("#")) {
                        Toast.makeText(getApplicationContext(), "Không được chứa kí tự #", Toast.LENGTH_SHORT).show();
                    }
                    else if (allAlbumName.contains(albumName)) {
                        Toast.makeText(getApplicationContext(), "Album này đã tồn tại", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        CreateAlbumActivity.CreateAlbumAsyncTask myAsyncTask = new CreateAlbumActivity.CreateAlbumAsyncTask(albumName);
                        new Thread(myAsyncTask).start();
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(), "Title null", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setViewRyc() {
        listImage = GetAllPhotoFromDisk.getSelectiveImages(getApplicationContext());
        ImageSelectAdapter imageAdapter = new ImageSelectAdapter(this, true);
        imageAdapter.setData(listImage);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        recyclerView.setAdapter(imageAdapter);
    }

    private void mappingControls() {
        imgBackCreateAlbum = findViewById(R.id.img_back_create_album);
        btnTick = findViewById(R.id.btnTick);
        edtTitleAlbum = findViewById(R.id.edtTitleAlbum);
        recyclerView = findViewById(R.id.rycAddAlbum);
    }
    public class CreateAlbumAsyncTask implements Runnable {
        List<MyImage> listImageSelected;
        String name;
        public CreateAlbumAsyncTask(String name) {
            this.name = name;
        }
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
            for (MyImage img :listImageSelected){
                imageListFavor.add(img.getPath());
            }
            DataLocalManager.getInstance().saveAlbum(name, imageListFavor);
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            });
        }
    }
}