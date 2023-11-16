package com.example.albumapp;

import android.Manifest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;


import com.example.albumapp.fragments.FragmentPhoto;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private FragmentTransaction ft;
    private RecyclerView recyclerView;

    private BottomNavigationView btnNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView1);
        btnNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frameLayoutActivityMain,new FragmentPhoto());
        ft.commit();

        btnNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                if(item.getItemId()==R.id.photo)
                {
                    selectedFragment = new FragmentPhoto();
                }
//              switch (item.getItemId()) {
//                  case R.id.photo:
//                      selectedFragment = new FramentPhoto();
//                      break;
//                  case R.id.album:
//                        selectedFragment = new FramentPhoto();
//                        break;
//                  case R.id.secret:
//                      selectedFragment = new FramentPhoto();
//                      break;
//                   case R.id.favorite:
//                       selectedFragment = new FramentPhoto();
//                       break;
//                  default:
//              }
                if(selectedFragment!=null) {
                    ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.frameLayoutActivityMain, selectedFragment);
                    ft.commit();
                }
                return true;
            }
        });






//        // Kiểm tra quyền truy cập vào bộ nhớ ngoại vi
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
//                != PackageManager.PERMISSION_GRANTED) {
//            // Nếu chưa có quyền, yêu cầu quyền
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.READ_MEDIA_IMAGES}, 0);
//        } else {
//            // Nếu đã có quyền, tải hình ảnh từ bộ nhớ ngoại vi
//            setupRecyclerView();
//        }

    }




}