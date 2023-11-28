package com.example.albumapp.activities;

import android.os.Bundle;
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
    private ImageView img_back_create_album;
    private ImageView btnTick;
    private EditText edtTitleAlbum;
    private RecyclerView rycAddAlbum;
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
                if(!TextUtils.isEmpty(edtTitleAlbum.getText())) {
                    String albumName = edtTitleAlbum.getText().toString();
                    List<String> allAlbumName = DataLocalManager.getAllKey();
                    if(albumName.contains("#")) {
                        Toast.makeText(getApplicationContext(), "Không được chứa kí tự #", Toast.LENGTH_SHORT).show();
                    }
                    else if (allAlbumName.contains(albumName)) {
                        Toast.makeText(getApplicationContext(), "Album này đã tồn tại", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        createAlbum(albumName);
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(), "Title null", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setViewRyc() {
        listImage = GetAllPhotoFromDisk.getImages(getApplicationContext());
        ImageSelectAdapter imageAdapter = new ImageSelectAdapter(this);
        imageAdapter.setData(listImage);
        rycAddAlbum.setLayoutManager(new GridLayoutManager(this, 4));
        rycAddAlbum.setAdapter(imageAdapter);
    }

    private void mappingControls() {
        img_back_create_album = findViewById(R.id.img_back_create_album);
        btnTick = findViewById(R.id.btnTick);
        edtTitleAlbum = findViewById(R.id.edtTitleAlbum);
        rycAddAlbum = findViewById(R.id.rycAddAlbum);
    }

    private void createAlbum(String name) {
        ImageSelectAdapter adapter = (ImageSelectAdapter) rycAddAlbum.getAdapter();
        if (adapter!=null) {
            listImageSelected = adapter.getListSelectedImage();
        }
        Set<String> imageListFavor = new HashSet<>();
        for (MyImage img :listImageSelected){
            imageListFavor.add(img.getPath());
        }
        DataLocalManager.setListImg(name, imageListFavor);
    }
}
