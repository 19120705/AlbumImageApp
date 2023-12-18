package com.example.albumapp.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.albumapp.R;
import com.example.albumapp.adapters.ImageSelectAdapter;
import com.example.albumapp.models.MyImage;
import com.example.albumapp.utility.DataLocalManager;
import com.example.albumapp.utility.GetAllPhotoFromDisk;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class MultiSelectPhotoActivity extends AppCompatActivity implements ImageSelectAdapter.OnImageSelectChangeListener {

    private RecyclerView recyclerView;
    private List<MyImage> listImage;
    private ImageSelectAdapter imageAdapter;
    private List<MyImage> listImageSelected;
    private Button btnCancel;
    private TextView textViewNumberSelectImage;
    private ImageView btnShare;
    private ImageView btnDelete;
    private ImageView btnMore;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mutilselect_photo);

        btnCancel = (Button)findViewById(R.id.btnCancel);
        textViewNumberSelectImage = (TextView) findViewById(R.id.textViewNumberImageSelected);
        settingData();
        setViewRyc();
        setEvent();
        setButtonsEnabled(false);
    }
    @Override
    public void onImageSelectChanged(int selectedCount) {
        if(selectedCount < 1)
        {
            textViewNumberSelectImage.setText("Select Image");
            setButtonsEnabled(false);
        }
        else{
            textViewNumberSelectImage.setText("Selected "+String.valueOf(selectedCount) + " image");
            setButtonsEnabled(true);
        }
    }
    private void setButtonsEnabled(boolean enabled) {
        btnShare.setEnabled(enabled);
        btnDelete.setEnabled(enabled);
        btnMore.setEnabled(enabled);

        float alphaValue = enabled ? 1.0f : 0.5f;
        btnShare.setAlpha(alphaValue);
        btnDelete.setAlpha(alphaValue);
        btnMore.setAlpha(alphaValue);

        int colorResId = enabled ? R.color.colorPrimary : R.color.black; // Chọn màu primary hoặc một màu khác khi tắt
        int color = ContextCompat.getColor(this, colorResId);
        btnShare.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        btnDelete.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        btnMore.setColorFilter(color, PorterDuff.Mode.SRC_IN);
    }
    private void settingData() {
        listImageSelected = new ArrayList<>();
    }
    private void setViewRyc() {
        recyclerView = findViewById(R.id.recyclerViewPhoto);
        listImage = GetAllPhotoFromDisk.getSelectiveImages(getApplicationContext());
        imageAdapter = new ImageSelectAdapter(this, true);
        imageAdapter.setData(listImage);

        imageAdapter.setOnImageSelectChangeListener(this);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        recyclerView.setAdapter(imageAdapter);

    }
    private void setEvent(){
        btnShare = (ImageView) findViewById(R.id.btnShare);
        btnDelete = (ImageView) findViewById(R.id.btnDelete);
        btnMore = (ImageView) findViewById(R.id.btnMore);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ImageSelectAdapter adapter = (ImageSelectAdapter) recyclerView.getAdapter();
                AlertDialog.Builder builder = new AlertDialog.Builder(MultiSelectPhotoActivity.this);

                builder.setTitle("Confirm");
                builder.setMessage("Do you want to delete" +adapter.getSelectedItemCount() + "images?");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        listImageSelected = adapter.getListSelectedImage();
                        String currentDateString = getCurrentDateFormatted("yyyy-M-dd");
                        for(int i =0 ;i<listImageSelected.size();i++)
                        {
                            for(int j = 0 ; j <listImage.size(); j++)
                            {
                                if(Objects.equals(listImageSelected.get(i).getPath(),listImage.get(j).getPath()))
                                {
                                    listImage.remove(j);
                                }
                            }
                            DataLocalManager.getInstance().saveTrash(listImageSelected.get(i).getPath(),convertDateStringToLong(currentDateString, "yyyy-M-dd"));


                            imageAdapter.updateData(listImage);
                            imageAdapter.clearSelections();
                        }

                    }
                });

                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
        btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });
    }

    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.menu_more_multiselect, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                // Handle menu item clicks here
                if(itemId==R.id.add_to_album)
                {

                    return true;
                }
                else if(itemId==R.id.album_item_slideshow)
                {
                    Intent intent_show = new Intent(MultiSelectPhotoActivity.this, SlideShowActivity.class);
                    ArrayList<MyImage> listImages = new ArrayList<>(listImageSelected);
                    intent_show.putParcelableArrayListExtra("dataImages", listImages);
                    intent_show.putExtra("name", "Slide show");
                    intent_show.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    MultiSelectPhotoActivity.this.startActivity(intent_show);
                    return true;
                }
                else if(itemId==R.id.add_to_private)
                {
                    Set<String> privateAlbum = new HashSet<>();
                    for(int i =0 ;i<listImageSelected.size();i++)
                    {
                        for(int j = 0 ; j <listImage.size(); j++)
                        {
                            if(Objects.equals(listImageSelected.get(i).getPath(),listImage.get(j).getPath()))
                            {
                                listImage.remove(j);
                            }
                        }
                        privateAlbum.add(listImageSelected.get(i).getPath());
                        DataLocalManager.getInstance().savePrivateAlbum(privateAlbum);

                        imageAdapter.updateData(listImage);
                        imageAdapter.clearSelections();
                    }
                    return true;
                }
                else if(itemId==R.id.add_to_favorite)
                {
                    return true;
                }
                return false;
            }
        });

        popupMenu.show();
    }
    private static String getCurrentDateFormatted(String dateFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        return sdf.format(new Date());
    }

    private static long convertDateStringToLong(String dateString, String dateFormat) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
            Date date = sdf.parse(dateString);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return -1; // Handle the exception appropriately
        }
    }
}
