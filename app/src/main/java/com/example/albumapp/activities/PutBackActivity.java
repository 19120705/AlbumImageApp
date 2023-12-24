package com.example.albumapp.activities;

import android.content.Intent;
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
import com.example.albumapp.adapters.ImageSelectAdapter;
import com.example.albumapp.models.MyImage;
import com.example.albumapp.utility.DataLocalManager;
import com.example.albumapp.utility.GetAllPhotoFromDisk;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PutBackActivity extends AppCompatActivity{
    private ImageView imgBackCreateAlbum;
    private ImageView btnTick;
    private RecyclerView recyclerView;
    private List<MyImage> listImage;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_put_back_trash);
        mappingControls();
        event();
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
                PutBackActivity.PutBackAsyncTask myAsyncTask = new PutBackActivity.PutBackAsyncTask();
                new Thread(myAsyncTask).start();
            }
        });
    }

    private void setViewRyc() {
        Intent intent = getIntent();
        listImage = intent.getParcelableArrayListExtra("dataImages");
        ImageSelectAdapter imageAdapter = new ImageSelectAdapter(this, true);
        imageAdapter.setData(listImage);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setAdapter(imageAdapter);
    }

    private void mappingControls() {
        imgBackCreateAlbum = findViewById(R.id.img_back_create_album);
        btnTick = findViewById(R.id.btnTick);
        recyclerView = findViewById(R.id.rycAddAlbum);
    }
    public class PutBackAsyncTask implements Runnable {
        List<MyImage> listImageSelected;
        @Override
        public void run() {
            ImageSelectAdapter adapter = (ImageSelectAdapter) recyclerView.getAdapter();
            if (adapter!=null) {
                listImageSelected = adapter.getListSelectedImage();
            }
            for (MyImage img :listImageSelected){
                DataLocalManager.getInstance().removeImageTrash(img.getPath());
            }
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Intent resultIntent = new Intent();
                    if (listImageSelected.isEmpty()) {
                        setResult(RESULT_CANCELED, resultIntent);
                    }
                    else {
                        resultIntent.putParcelableArrayListExtra("list_result", new ArrayList<>(listImageSelected));
                        resultIntent.putExtra("REQUEST_CODE", "PUTBACK");
                        setResult(RESULT_OK, resultIntent);
                    }
                    finish();
                }
            });
        }
    }
}