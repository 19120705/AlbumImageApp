package com.example.albumapp.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
import com.example.albumapp.utility.GetAllPhotoFromDisk;
import com.example.albumapp.utility.PhotoInterface;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationBarView;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PhotoActivity extends AppCompatActivity implements PhotoInterface{

    private ViewPager viewPager;
    private Toolbar toolbar;
    private FrameLayout frameLayout;
    private List<MyImage> listImages;
    private Intent intent;
    private String thumb;
    private String imgPath;
    private String imageName;
    private LinearLayout layoutButton;
    private ImageView btnShare, btnEdit, btnFavorite, btnDelete;

    private SlideImageAdapter slideImageAdapter;
    private PhotoInterface activityPhoto;

    private Uri imageUri;
    private ActivityResultLauncher<Intent> shareLauncher;

    private int pos;
    public static List<String> imageListFavorite = DataLocalManager.getInstance()
            .getAlbumImages("Favorite");
    @Override
    protected void onResume() {
        super.onResume();
        imageListFavorite = DataLocalManager.getInstance().getAlbumImages("Favorite");
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        shareLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_CANCELED) {
                            // Người dùng đã hủy việc chia sẻ, xóa hình ảnh
                            getContentResolver().delete(imageUri, null, null);
                        }
                    }
                });

//        //Fix Uri file SDK link: https://stackoverflow.com/questions/48117511/exposed-beyond-app-through-clipdata-item-geturi?answertab=oldest#tab-top
//        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
//        StrictMode.setVmPolicy(builder.build());
//
//
//        mappingControls();
//
//        events();


        frameLayout = (FrameLayout) findViewById(R.id.frameViewPager_photo);

        layoutButton = (LinearLayout) findViewById(R.id.layoutButton);
        setToolbar();
        setDataIntent();
        setUpViewPaper();
        setBottomNavigationView();

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
        pos = intent.getIntExtra("pos", 0);
        listImages = intent.getParcelableArrayListExtra("dataImages");
        activityPhoto = this;

    }
    private void setUpViewPaper(){
        viewPager = (ViewPager) findViewById(R.id.viewPager_photo);
        slideImageAdapter = new SlideImageAdapter();
        slideImageAdapter.setData(new ArrayList<>(listImages));
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
                    Log.e("checkFavorite", "onNavigationItemSelected: No favorite");
                    btnFavorite.setImageResource(R.drawable.ic_favorite);
                    //bottomNavigationView.getMenu().getItem(2).setIcon(R.drawable.ic_favorite);
                }
                else{
                    Log.e("checkFavorite", "onNavigationItemSelected: favorite");
                    btnFavorite.setImageResource(R.drawable.ic_favorite_select);
                    //bottomNavigationView.getMenu().getItem(2).setIcon(R.drawable.ic_favorite_select);
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
    private void setBottomNavigationView(){
        btnShare = (ImageView) findViewById(R.id.btnShare);
        btnEdit = (ImageView) findViewById(R.id.btnEdit);
        btnFavorite = (ImageView) findViewById(R.id.btnFavorite);
        btnDelete = (ImageView) findViewById(R.id.btnDelete);

        Uri targetUri =Uri.parse("file://" + thumb);
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(thumb.contains("gif")){
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("image/*");
                    share.putExtra(Intent.EXTRA_STREAM, targetUri);
                    startActivity( Intent.createChooser(share, "Share this image to your friends!") );
                }
                else {
                    Drawable mDrawable = Drawable.createFromPath(imgPath);
                    Bitmap mBitmap = ((BitmapDrawable) mDrawable).getBitmap();
                    String path = MediaStore.Images.Media.insertImage(getContentResolver(), mBitmap, "Image Description", null);
                    thumb = thumb.replaceAll(" ", "");

                    imageUri = Uri.parse(path);
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("image/*");
                    shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                    shareLauncher.launch(Intent.createChooser(shareIntent, "Share Image"));
                }
            }
        });

        btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkImgInFavorite(imgPath)){
                    Log.e("checkFavorite", "onNavigationItemSelected: No favorite");

                    imageListFavorite.add(imgPath);
                    btnFavorite.setImageResource(R.drawable.ic_favorite_select);
                    //bottomNavigationView.getMenu().getItem(2).setIcon(R.drawable.ic_favorite);
                }
                else{
                    Log.e("checkFavorite", "onNavigationItemSelected: favorite");
                    imageListFavorite.remove(imgPath);
                    btnFavorite.setImageResource(R.drawable.ic_favorite);
                    //bottomNavigationView.getMenu().getItem(2).setIcon(R.drawable.ic_favorite_select);
                }
                Set<String> setListImgFavorite = new HashSet<>();
                for (String i: imageListFavorite) {
                    setListImgFavorite.add(i);
                }
                DataLocalManager.getInstance().saveAlbum("Favorite",setListImgFavorite);
                Toast.makeText(PhotoActivity.this, imageListFavorite.size()+"", Toast.LENGTH_SHORT).show();
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
                TextView txtLocation = (TextView) infoDialogView.findViewById(R.id.txtLocation);

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

                String latitude = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
                String latitudeRef = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
                String longitude = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
                String longitudeRef = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
                String location = latitude + " " + latitudeRef + ", " + longitude + " " + longitudeRef;
                txtLocation.setText(location);

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

    private void showNavigation(boolean flag) {
        if (!flag) {
            layoutButton.setVisibility(View.GONE);
            //bottomNavigationView.setVisibility(View.INVISIBLE);
            toolbar.setVisibility(View.INVISIBLE);
        } else {
            layoutButton.setVisibility(View.VISIBLE);
            //bottomNavigationView.setVisibility(View.VISIBLE);
            toolbar.setVisibility(View.VISIBLE);
        }
    }
    @Override
    public void actionShow(boolean flag) {
        showNavigation(flag);
    }
}
