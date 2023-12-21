package com.example.albumapp.activities;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
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
import com.example.albumapp.adapters.ItemAlbumAdapter;
import com.example.albumapp.models.MyImage;
import com.example.albumapp.utility.DataLocalManager;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class ItemAlbumActivity extends AppCompatActivity {
    private List<MyImage> dataImages;
    private RecyclerView recyclerView;
    private Intent intent;
    private String albumName;
    Toolbar toolbarItemAlbum;
    private ItemAlbumAdapter itemAlbumAdapter;
    private int spanCount;
    private int isPrivate;
    private int isTrash;
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
                    if (Objects.equals(requestCode, "PHOTO")) {
//                        String path_img = data.getStringExtra("path_img");
//                        if(isPrivate == 1) {
//                            dataImages.remove(path_img);
//                        }else if (duplicateImg == 2){
//                            dataImages.remove(path_img);
//                        }
                        recyclerView.setAdapter(new ItemAlbumAdapter(dataImages, spanCount));
                    }
                }
            });

    private void setRyc() {
        albumName = intent.getStringExtra("name");
        recyclerView.setLayoutManager(new GridLayoutManager(this, spanCount));
        if (dataImages.isEmpty()) {
            return;
        }
        itemAlbumAdapter = new ItemAlbumAdapter(dataImages,  spanCount);
        if (isTrash == 1) {
            itemAlbumAdapter.turnOffClickable();
        }
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
        if (isPrivate == 1) {
            toolbarItemAlbum.getMenu().findItem(R.id.menu_remove_album).setVisible(false);
        }
        else if (isPrivate == 0){
            toolbarItemAlbum.getMenu().findItem(R.id.menu_change_password).setVisible(false);
        }
        if (isTrash == 1) {
            toolbarItemAlbum.getMenu().findItem(R.id.menu_remove_album).setVisible(false);
            toolbarItemAlbum.getMenu().findItem(R.id.album_item_slideshow).setVisible(false);
            toolbarItemAlbum.getMenu().findItem(R.id.menu_add_image).setVisible(false);
        }
        else {
            toolbarItemAlbum.getMenu().findItem(R.id.menu_put_back).setVisible(false);
        }
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
                    intent_add.putExtra("isPrivate", isPrivate);
                    someActivityResultLauncher.launch(intent_add);
                }
                else if (id == R.id.menu_remove_album) {
                    deleteAlbum();
                }
                else if (id == R.id.menu_change_password) {
                    showEnterPasswordDialog();
                }
                return true;
            }
        });
        if(isPrivate == 1)
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
    private void showEnterPasswordDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_enter_password, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setPositiveButton("Enter", null)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = DataLocalManager.getInstance().getPassword();
                EditText editTextPassword = dialogView.findViewById(R.id.enterPassword);
                TextInputLayout enterPassField = dialogView.findViewById(R.id.enterPasswordField);
                String enterPassword = editTextPassword.getText().toString().trim();

                if (enterPassword.equals("")) {
                    enterPassField.setError("Empty Input");
                    return;
                }
                if (checkBcrypt(enterPassword, password)) {
                    showChangePasswordDialog();
                    dialog.dismiss();
                }
                else {
                    enterPassField.setError("Incorrect password");
                }
            }
        });
    }

    private void showChangePasswordDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_change_password, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setTitle("Change Password")
                .setPositiveButton("Change Password", null)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();

        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editNewPassword = dialogView.findViewById(R.id.newPassword);
                EditText editConfirmPassword = dialogView.findViewById(R.id.confirmNewPassword);

                TextInputLayout newPassField = dialogView.findViewById(R.id.newPasswordField);
                TextInputLayout confirmNewPassField = dialogView.findViewById(R.id.confirmPasswordField);
                String newPassword = editNewPassword.getText().toString().trim();
                String confirmPassword = editConfirmPassword.getText().toString().trim();
                if (newPassword.equals("")) {
                    newPassField.setError("Empty Input");
                }
                else if (confirmPassword.equals("")) {
                    confirmNewPassField.setError("Empty Input");
                }
                if (newPassword.equals("") || confirmPassword.equals("")) {
                    return;
                }

                if (newPassword.equals(confirmPassword)) {
                    DataLocalManager.getInstance().savePassword(hashBcrypt(newPassword));
                    Toast.makeText(getApplicationContext(),"Password changed successfully!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    confirmNewPassField.setError("Passwords do not match.");
                    Toast.makeText(getApplicationContext(),"Passwords do not match. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    private String hashBcrypt(String password) {
        int costFactor = 12;

        return BCrypt.withDefaults().hashToString(costFactor, password.toCharArray());
    }

    public boolean checkBcrypt(String plainTextPassword, String hashedPassword) {
        return BCrypt.verifyer().verify(plainTextPassword.toCharArray(), hashedPassword).verified;
    }

    private void setData() {
        dataImages = intent.getParcelableArrayListExtra("dataImages");
        isPrivate = intent.getIntExtra("isPrivate", 0);
        isTrash = intent.getIntExtra("isTrash", 0);
        duplicateImg = intent.getIntExtra("duplicateImg",0);
//        isAlbum = intent.getIntExtra("ok",0);

    }

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