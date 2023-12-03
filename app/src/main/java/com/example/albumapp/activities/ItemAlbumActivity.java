package com.example.albumapp.activities;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.albumapp.R;
import com.example.albumapp.adapters.ItemAlbumAdapter;
import com.example.albumapp.models.MyImage;

import java.util.ArrayList;
import java.util.List;

public class ItemAlbumActivity extends AppCompatActivity {
    private List<MyImage> dataImages;
    private RecyclerView recyclerView;
    private Intent intent;
    private String albumName;
    Toolbar toolbarItemAlbum;
    private ItemAlbumAdapter itemAlbumAdapter;
    private int spanCount;
    private int isSecret;
    private int duplicateImg;
    private int isAlbum;
    private static final int REQUEST_CODE_PIC = 10;
    private static final int REQUEST_CODE_CHOOSE = 55;
    private static final int REQUEST_CODE_ADD = 56;
    private static final int REQUEST_CODE_SECRET = 57;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_album);

        recyclerView = findViewById(R.id.ryc_list_image);
        toolbarItemAlbum = findViewById(R.id.toolbar_item_album);
        intent = getIntent();
        setUpSpanCount();
        setData();
        setRyc();
        events();
    }

    private void setUpSpanCount() {
        SharedPreferences sharedPref = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        spanCount = sharedPref.getInt("span_count", 3);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_ADD) {
            List<MyImage> resultList = data.getParcelableArrayListExtra("list_result");
            if(resultList !=null) {
                dataImages.addAll(resultList);
                recyclerView.setAdapter(new ItemAlbumAdapter(dataImages, spanCount));
            }
        }
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_CHOOSE) {
            if(data != null) {
                int isMoved = data.getIntExtra("move", 0);
                if (isMoved == 1) {
                    ArrayList<String> resultList = data.getStringArrayListExtra("list_result");
                    if (resultList != null) {
                        dataImages.remove(resultList);
                        recyclerView.setAdapter(new ItemAlbumAdapter(dataImages, spanCount));
                    }
                }
            }
        }
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_SECRET) {
//            MyAsyncTask myAsyncTask = new MyAsyncTask();
//            myAsyncTask.execute();
        }
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_PIC) {
            String path_img = data.getStringExtra("path_img");
            if(isSecret == 1) {
                dataImages.remove(path_img);
            }else if (duplicateImg == 2){
                dataImages.remove(path_img);
            }
            recyclerView.setAdapter(new ItemAlbumAdapter(dataImages, spanCount));
        }
    }

    private void setRyc() {
        albumName = intent.getStringExtra("name");
        recyclerView.setLayoutManager(new GridLayoutManager(this, spanCount));
        itemAlbumAdapter = new ItemAlbumAdapter(dataImages,  spanCount);
        recyclerView.setAdapter(new ItemAlbumAdapter(dataImages, spanCount));
    }

    private void animationRyc() {
        switch(spanCount) {
            case 1:
                Animation animation1 = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.anim_layout_ryc_1);
                recyclerView.setAnimation(animation1);
            case 2:
                Animation animation2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_layout_ryc_1);
                recyclerView.setAnimation(animation2);
                break;
            case 3:
                Animation animation3 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_layout_ryc_2);
                recyclerView.setAnimation(animation3);
                break;
            case 4:
                Animation animation4 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_layout_ryc_3);
                recyclerView.setAnimation(animation4);
                break;
        }
    }


    private void events() {
        // Toolbar events
        toolbarItemAlbum.inflateMenu(R.menu.menu_top_item_album);
        toolbarItemAlbum.setTitle(albumName);
//        if(isAlbum == 0) {
//            toolbar_item_album.getMenu().findItem(R.id.menu_add_image).setVisible(false);
//        } else
//            toolbar_item_album.getMenu().findItem(R.id.menu_add_image).setVisible(true);
        // Show back button
        toolbarItemAlbum.setNavigationIcon(R.drawable.ic_back);
        toolbarItemAlbum.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Toolbar options
        toolbarItemAlbum.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.change_span_count) {
                    spanCountEvent();
//                    case R.id.album_item_slideshow:
//                        slideShowEvents();
//                        break;
//                    case R.id.menu_add_image:
//
//                            Intent intent_add = new Intent(ItemAlbumActivity.this, AddImageToAlbumActivity.class);
//                            intent_add.putStringArrayListExtra("list_image", myAlbum);
//                            intent_add.putExtra("path_folder", path_folder);
//                            intent_add.putExtra("name_folder", album_name);
//                            startActivityForResult(intent_add, REQUEST_CODE_ADD);
//
//                        break;
                }

                return true;
            }
        });
        if(isSecret == 1)
            hideMenu();
    }

    private void hideMenu() {
        toolbarItemAlbum.getMenu().findItem(R.id.menu_add_image).setVisible(false);
    }

    private void spanCountEvent() {
        if(spanCount >= 4) {
            spanCount = 1;
        }
        else {
            spanCount++;
        }
        recyclerView.setLayoutManager(new GridLayoutManager(this, spanCount));
        recyclerView.setAdapter(itemAlbumAdapter);
        itemAlbumAdapter.setSpanCount(spanCount);

        animationRyc();
        SharedPreferences sharedPref = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("span_count", spanCount);
        editor.commit();
    }

//    private void slideShowEvents() {
//        Intent intent = new Intent(ItemAlbumActivity.this, SlideShowActivity.class);
//        intent.putStringArrayListExtra("data_slide", myAlbum);
//        intent.putExtra("name", album_name);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        ItemAlbumActivity.this.startActivity(intent);
//    }

    private void setData() {
        dataImages = intent.getParcelableArrayListExtra("data");
        isSecret = intent.getIntExtra("isSecret", 0);
        duplicateImg = intent.getIntExtra("duplicateImg",0);
//        isAlbum = intent.getIntExtra("ok",0);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
//        MyAsyncTask myAsyncTask = new MyAsyncTask();
//        myAsyncTask.execute();
    }


//    public class MyAsyncTask extends AsyncTaskExecutorService<Void, Integer, Void> {
//
//        @Override
//        protected Void doInBackground(Void voids) {
//            for(int i=0;i<myAlbum.size();i++) {
//                File file = new File(myAlbum.get(i));
//                if(!file.exists()) {
//                    myAlbum.remove(i);
//                }
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void unused) {
//            spanAction();
//        }
//    }

}