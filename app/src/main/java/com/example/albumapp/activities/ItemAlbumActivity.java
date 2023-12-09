package com.example.albumapp.activities;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.albumapp.R;
import com.example.albumapp.adapters.ImageSelectAdapter;
import com.example.albumapp.adapters.ItemAlbumAdapter;
import com.example.albumapp.models.MyImage;
import com.example.albumapp.utility.DataLocalManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_album);

        recyclerView = findViewById(R.id.ryc_list_image);
        toolbarItemAlbum = findViewById(R.id.toolbar_item_album);
        intent = getIntent();
        setData();
        spanCount = DataLocalManager.getInstance().getSpanCount();
        setRyc();
        events();
    }

    private ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() != RESULT_OK) {
                        return;
                    }
                    Intent data = result.getData();
                    assert data != null;
                    String requestCode = data.getStringExtra("REQUEST_CODE");

                    if (Objects.equals(requestCode, "ADD")) {
                        List<MyImage> resultList = data.getParcelableArrayListExtra("list_result");
                        if(!resultList.isEmpty()) {
                            dataImages.addAll(resultList);
                            recyclerView.setAdapter(new ItemAlbumAdapter(dataImages, spanCount));
                        }
                    }
//                    if (result.getResultCode() == RESULT_OK && requestCode == REQUEST_CODE_CHOOSE) {
//                        int isMoved = data.getIntExtra("move", 0);
//                        if (isMoved == 1) {
//                            ArrayList<String> resultList = data.getStringArrayListExtra("list_result");
//                            if (resultList != null) {
//                                dataImages.remove(resultList);
//                                recyclerView.setAdapter(new ItemAlbumAdapter(dataImages, spanCount));
//                            }
//                        }
//                    }
//                    else if (Objects.equals(requestCode, "PRIVATE")) {
//                        List<MyImage> resultList = data.getParcelableArrayListExtra("dataImages");
//            myAsyncTask.execute();
//                    }
//                    if (Objects.equals(requestCode, "PHOTO")) {
//                        String path_img = data.getStringExtra("path_img");
//                        if(isSecret == 1) {
//                            dataImages.remove(path_img);
//                        }else if (duplicateImg == 2){
//                            dataImages.remove(path_img);
//                        }
//                        recyclerView.setAdapter(new ItemAlbumAdapter(dataImages, spanCount));
//                    }
                }
            });

    private void setRyc() {
        albumName = intent.getStringExtra("name");
        recyclerView.setLayoutManager(new GridLayoutManager(this, spanCount));
        if (dataImages.isEmpty()) {
            return;
        }
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
                }
                else if (id == R.id.album_item_slideshow) {
                    slideShowEvents();
                }
               else if (id == R.id.menu_add_image) {
                    Intent intent_add = new Intent(ItemAlbumActivity.this, AddImageActivity.class);
                    intent_add.putParcelableArrayListExtra("dataImages", new ArrayList<>(dataImages));
                    intent_add.putExtra("name", albumName);
                    someActivityResultLauncher.launch(intent_add);
                }
                else if (id == R.id.menu_remove_album) {
                    deleteAlbum();
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
        if (itemAlbumAdapter == null) {
            return;
        }
        recyclerView.setAdapter(itemAlbumAdapter);
        itemAlbumAdapter.setSpanCount(spanCount);

        animationRyc();

        DataLocalManager.getInstance().saveSpanCount(spanCount);
    }

    private void slideShowEvents() {
        Intent intent_show = new Intent(ItemAlbumActivity.this, SlideShowActivity.class);
        ArrayList<MyImage> listImages = new ArrayList<>(dataImages);
        intent_show.putParcelableArrayListExtra("dataImages", listImages);
        intent_show.putExtra("name", albumName);
        intent_show.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ItemAlbumActivity.this.startActivity(intent_show);
    }
    private void deleteAlbum() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.dialog_confirm_delete)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Perform delete operation here
                        ItemAlbumActivity.RemoveAlbumAsyncTask myAsyncTask = new ItemAlbumActivity.RemoveAlbumAsyncTask(albumName);
                        new Thread(myAsyncTask).start();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void setData() {
        dataImages = intent.getParcelableArrayListExtra("dataImages");
        isSecret = intent.getIntExtra("isSecret", 0);
        duplicateImg = intent.getIntExtra("duplicateImg",0);
//        isAlbum = intent.getIntExtra("ok",0);

    }

//    @Override
//    public boolean onSupportNavigateUp() {
//        onBackPressed();
//        return true;
//    }
//

    public class RemoveAlbumAsyncTask implements Runnable {
        String name;
        public RemoveAlbumAsyncTask(String name) {
            this.name = name;
        }
        @Override
        public void run() {
            DataLocalManager.getInstance().removeAlbum(name);
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            });
        }
    }

}