package com.example.albumapp.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.albumapp.R;
import com.example.albumapp.adapters.ImageSelectAdapter;
import com.example.albumapp.models.MyImage;
import com.example.albumapp.utility.GetAllPhotoFromDisk;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class MultiSelectPhotoActivity extends AppCompatActivity implements ImageSelectAdapter.OnImageSelectChangeListener {

    private RecyclerView recyclerView;
    private List<MyImage> listImage;
    private List<MyImage> listImageSelected;
    private Button btnCancel;
    private TextView textViewNumberSelectImage;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mutilselect_photo);

        btnCancel = (Button)findViewById(R.id.btnCancel);
        textViewNumberSelectImage = (TextView) findViewById(R.id.textViewNumberImageSelected);
        settingData();
        setViewRyc();
        setEvent();
    }
    @Override
    public void onImageSelectChanged(int selectedCount) {
        if(selectedCount < 1)
        {
            textViewNumberSelectImage.setText("Select Image");
        }
        else{
            textViewNumberSelectImage.setText("Selected "+String.valueOf(selectedCount) + " image");
        }
    }
    private void settingData() {
        listImageSelected = new ArrayList<>();
    }
    private void setViewRyc() {
        recyclerView = findViewById(R.id.recyclerViewPhoto);
        listImage = GetAllPhotoFromDisk.getSelectiveImages(getApplicationContext());
        ImageSelectAdapter imageAdapter = new ImageSelectAdapter(this, true);
        imageAdapter.setData(listImage);

        imageAdapter.setOnImageSelectChangeListener(this);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        recyclerView.setAdapter(imageAdapter);


    }
    private void setEvent(){
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
