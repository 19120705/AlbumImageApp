package com.example.albumapp;

import android.Manifest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.albumapp.R;
import com.example.albumapp.adapters.ViewPagerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPager2;

    private BottomNavigationView btnNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager2 = (ViewPager2) findViewById(R.id.viewPaper2);
        btnNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);


        // Kiểm tra quyền truy cập vào bộ nhớ ngoại vi
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                != PackageManager.PERMISSION_GRANTED) {
            // Nếu chưa có quyền, yêu cầu quyền
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_MEDIA_IMAGES}, 0);
        } else {
            // Nếu đã có quyền, tải hình ảnh từ bộ nhớ ngoại vi
            setUpViewPager();
        }


        btnNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if(item.getItemId()==R.id.photo)
                {
                    viewPager2.setCurrentItem(0);
                }
                if(item.getItemId()==R.id.album)
                {
                    viewPager2.setCurrentItem(1);
                }
                if(item.getItemId()==R.id.trash)
                {
                    viewPager2.setCurrentItem(2);
                }
                if(item.getItemId()==R.id.favorite)
                {
                    viewPager2.setCurrentItem(3);
                }
//                switch (item.getItemId()) {
//                    case R.id.photo:
//
//                        viewPager2.setCurrentItem(0);
//                        break;
//
//                    case R.id.album:
//
//                        viewPager2.setCurrentItem(1);
//                        break;
//
//                    case R.id.secret:
//
//                        viewPager2.setCurrentItem(2);
//                        break;
//
//                    case R.id.favorite:
//
//                        viewPager2.setCurrentItem(3);
//                        break;

//                }
                return true;
            }
        });







    }
    private void setUpViewPager() {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), getLifecycle());
        viewPagerAdapter.setContext(getApplicationContext());
        viewPager2.setAdapter(viewPagerAdapter);

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);

            }
            @Override
            public void onPageSelected(int position) {

                switch (position){
                    case 0:
                        btnNavigationView.getMenu().findItem(R.id.photo).setChecked(true);
                        break;
                    case 1:

                        btnNavigationView.getMenu().findItem(R.id.album).setChecked(true);
                        break;
                    case 2:

                        btnNavigationView.getMenu().findItem(R.id.trash).setChecked(true);
                        break;
                    case 3:

                        btnNavigationView.getMenu().findItem(R.id.favorite).setChecked(true);
                        break;
                }
            }
        });
    }




}