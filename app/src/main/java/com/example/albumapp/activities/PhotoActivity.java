package com.example.albumapp.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.albumapp.R;
import com.example.albumapp.adapters.SlideImageAdapter;
import com.example.albumapp.models.MyImage;
import com.example.albumapp.utility.DataLocalManager;
import com.example.albumapp.utility.PhotoInterface;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PhotoActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;
    private FrameLayout frameLayout;
    private ArrayList<MyImage> listImages;
    private Intent intent;
    private String thumb;
    private String imgPath;
    private String imageName;

    private SlideImageAdapter slideImageAdapter;
    private PhotoInterface activityPhoto;

    private int pos;
    public static List<String> imageListFavorite = DataLocalManager.getInstance().getAlbumImages("Favorite");
    @Override
    protected void onResume() {
        super.onResume();
        imageListFavorite = DataLocalManager.getInstance().getAlbumImages("Favorite");
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);


//        //Fix Uri file SDK link: https://stackoverflow.com/questions/48117511/exposed-beyond-app-through-clipdata-item-geturi?answertab=oldest#tab-top
//        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
//        StrictMode.setVmPolicy(builder.build());
//
//
//        mappingControls();
//
//        events();

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation_photo);

        frameLayout = (FrameLayout) findViewById(R.id.frameViewPager_photo);


        setToolbar();
        setDataIntent();
        setUpViewPaper();

    }

    private void setToolbar(){
        toolbar = findViewById(R.id.toolbar_photo);
        toolbar.inflateMenu(R.menu.menu_top_photo);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        // Show info
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.menuInfomation) {
                    Uri targetUri = Uri.parse("file://" + thumb);
                    if (targetUri != null) {
                        showInformation(targetUri);
                    }
                }
                return true;
            }

        });
    }
    private void setDataIntent() {
        intent = getIntent();
        listImages = intent.getParcelableArrayListExtra("dataImages");
        pos = intent.getIntExtra("pos", 0);
        //activityPicture = this;

    }
    private void setUpViewPaper(){
        viewPager = (ViewPager) findViewById(R.id.viewPager_photo);
        slideImageAdapter = new SlideImageAdapter();
        slideImageAdapter.setData(listImages);
        slideImageAdapter.setContext(getApplicationContext());
        slideImageAdapter.setPictureInterface(activityPhoto);
        viewPager.setAdapter(slideImageAdapter);
        viewPager.setCurrentItem(pos);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                thumb = listImages.get(position).getThumb();
                imgPath = listImages.get(position).getPath();
                imageName = thumb.substring(thumb.lastIndexOf('/') + 1);
                toolbar.setTitle(imageName);
                if(!checkImgInFavorite(imgPath)){
                    bottomNavigationView.getMenu().getItem(2).setIcon(R.drawable.ic_favorite);
                }
                else{
                    bottomNavigationView.getMenu().getItem(2).setIcon(R.drawable.ic_favorite_select);
                }
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
    public Boolean checkImgInFavorite(String  Path){
        for (String img: imageListFavorite) {
            if(img.equals(Path)){
                return true;
            }
        }
        return false;
    }

    private void showInformation(Uri photoUri) {
        if (photoUri != null) {

            ParcelFileDescriptor parcelFileDescriptor = null;

            try {
                parcelFileDescriptor = getContentResolver().openFileDescriptor(photoUri, "r");
                FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();

                ExifInterface exifInterface = new ExifInterface(fileDescriptor);

                BottomSheetDialog infoDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
                View infoDialogView = LayoutInflater.from(getApplicationContext())
                        .inflate(
                                R.layout.layout_information_photo,
                                (LinearLayout) findViewById(R.id.layoutInformationPhoto),
                                false
                        );
                TextView txtInfoProducer = (TextView) infoDialogView.findViewById(R.id.txtInfoProducer);
                TextView txtInfoSize = (TextView) infoDialogView.findViewById(R.id.txtInfoSize);
                TextView txtInfoModel = (TextView) infoDialogView.findViewById(R.id.txtInfoModel);
                TextView txtInfoModelMore = (TextView) infoDialogView.findViewById(R.id.txtInfoModelMore);
                TextView txtInfoMP = (TextView) infoDialogView.findViewById(R.id.txtInfoMP);
                TextView txtInfoAuthor = (TextView) infoDialogView.findViewById(R.id.txtInfoAuthor);
                TextView txtInfoTime = (TextView) infoDialogView.findViewById(R.id.txtInfoTime);
                TextView txtInfoName = (TextView) infoDialogView.findViewById(R.id.txtInfoName);

                File file = new File(imgPath);
                long fileSizeInBytes = file.length();
                txtInfoSize.setText( (String.format("%.2f", (fileSizeInBytes/1024.0)/1024.0)) + "MB" );
                int width = exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH,-1);
                int height = exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH,-1);
                if (width != -1 && height != -1) {
                    double megapixels = (width * height) / 1_000_000.0;
                    txtInfoMP.setText(String.format("%.2f",megapixels) + "MP " + height+ "x" +width);
                }
                txtInfoName.setText(imageName);
                txtInfoProducer.setText(exifInterface.getAttribute(ExifInterface.TAG_MAKE));
                txtInfoModel.setText(exifInterface.getAttribute(ExifInterface.TAG_MODEL));

                String modelMore = exifInterface.getAttribute(ExifInterface.TAG_FOCAL_LENGTH)+ "mm f/"+
                        exifInterface.getAttribute(ExifInterface.TAG_APERTURE_VALUE)+ " "+
                        exifInterface.getAttribute(ExifInterface.TAG_EXPOSURE_TIME)+ "s ISO"+
                        exifInterface.getAttribute(ExifInterface.TAG_ISO_SPEED_RATINGS);
                txtInfoModelMore.setText(modelMore);
                txtInfoAuthor.setText(exifInterface.getAttribute(ExifInterface.TAG_ARTIST));
                txtInfoTime.setText(exifInterface.getAttribute(ExifInterface.TAG_DATETIME));

                infoDialog.setContentView(infoDialogView);
                infoDialog.show();


                parcelFileDescriptor.close();


            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),
                        "Something wrong:\n" + e.toString(),
                        Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),
                        "Something wrong:\n" + e.toString(),
                        Toast.LENGTH_LONG).show();
            }


        } else {
            Toast.makeText(getApplicationContext(),
                    "photoUri == null",
                    Toast.LENGTH_LONG).show();
        }
    }
//    private void setUpToolBar() {
//        // Toolbar events
//        toolbar.inflateMenu(R.menu.menu_top_);
//        setTitleToolbar("abc");
//
//        // Show back button
//        toolbar_picture.setNavigationIcon(R.drawable.abc_ic_ab_back_material);
//        toolbar_picture.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//            }
//        });
//    }
}
