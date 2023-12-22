package com.example.albumapp.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.dsphotoeditor.sdk.activity.DsPhotoEditorActivity;
import com.dsphotoeditor.sdk.utils.DsPhotoEditorConstants;
import com.example.albumapp.R;
import com.example.albumapp.adapters.AlbumSelectAdapter;
import com.example.albumapp.adapters.SlideImageAdapter;
import com.example.albumapp.models.MyAlbum;
import com.example.albumapp.models.MyImage;
import com.example.albumapp.utility.AlbumInterface;
import com.example.albumapp.utility.DataLocalManager;
import com.example.albumapp.utility.PhotoInterface;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PhotoActivity extends AppCompatActivity implements PhotoInterface, AlbumInterface {

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
    private RecyclerView recycleViewListAlbum;
    private BottomSheetDialog bottomSheetDialog;
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
                else if(id==R.id.menuAddToAlbum){
                    openBottomDialogAddImageToAlbum();
                }
                else if(id==R.id.menuSetWallpaper)
                {
                    File file = new File(imgPath);
                    Uri uri = FileProvider.getUriForFile(PhotoActivity.this, getApplicationContext().getPackageName() + ".provider", file);
                    Intent intent = new Intent(Intent.ACTION_ATTACH_DATA);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // Cấp quyền đọc URI cho Intent
                    intent.setDataAndType(uri, "image/*");
                    startActivity(Intent.createChooser(intent, "Set as:"));
                }
                return true;
            }

        });
    }
    private void setDataIntent() {
        intent = getIntent();
        pos = intent.getIntExtra("pos", 0);
        listImages = intent.getParcelableArrayListExtra("dataImages");
        imgPath = listImages.get(pos).getPath();
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
                pos = position;
                thumb = listImages.get(position).getThumb();
                imgPath = listImages.get(position).getPath();
                imageName = thumb.substring(thumb.lastIndexOf('/') + 1);
                toolbar.setTitle(imageName);
                if(!checkImgInFavorite(imgPath)){
                    btnFavorite.setImageResource(R.drawable.ic_favorite);
                }
                else{
                    btnFavorite.setImageResource(R.drawable.ic_favorite_select);
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

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editIntent = new Intent(PhotoActivity.this, DsPhotoEditorActivity.class);

                if(imgPath.contains("gif")){
                    Toast.makeText(PhotoActivity.this,"Cannot edit GIF images",Toast.LENGTH_SHORT).show();
                }
                else{
                    // Set data
                    editIntent.setData(Uri.fromFile(new File(imgPath)));
                    // Set output directory
                    editIntent.putExtra(DsPhotoEditorConstants.DS_PHOTO_EDITOR_OUTPUT_DIRECTORY, "AlbumApp");
                    // Set toolbar color
                    editIntent.putExtra(DsPhotoEditorConstants.DS_TOOL_BAR_BACKGROUND_COLOR, Color.parseColor("#FF000000"));
                    // Set background color
                    editIntent.putExtra(DsPhotoEditorConstants.DS_MAIN_BACKGROUND_COLOR, Color.parseColor("#FF000000"));
                    // Start activity
                    startActivity(editIntent);
                }
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(PhotoActivity.this);

                builder.setTitle("Confirm");
                builder.setMessage("Do you want to delete this image?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        String currentDateString = getCurrentDateFormatted("yyyy-M-dd");
                        DataLocalManager.getInstance().saveTrash(imgPath,convertDateStringToLong(currentDateString, "yyyy-M-dd"));
                        listImages.remove(pos);
                        slideImageAdapter.notifyDataSetChanged();
                        Toast.makeText(PhotoActivity.this, "Delete successfully: " + targetUri.getPath(), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        finish();
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
        btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkImgInFavorite(imgPath)){
                    imageListFavorite.add(imgPath);
                    btnFavorite.setImageResource(R.drawable.ic_favorite_select);
                }
                else{
                    imageListFavorite.remove(imgPath);
                    btnFavorite.setImageResource(R.drawable.ic_favorite);
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
    private void openBottomDialogAddImageToAlbum() {
        View viewDialog = LayoutInflater.from(PhotoActivity.this).inflate(R.layout.layout_button_recyclerview_add_image_to_album, null);
        recycleViewListAlbum = viewDialog.findViewById(R.id.ryc_album);
        recycleViewListAlbum.setLayoutManager(new GridLayoutManager(this, 2));

        bottomSheetDialog = new BottomSheetDialog(PhotoActivity.this);
        bottomSheetDialog.setContentView(viewDialog);
        new Thread(new MyRunnable(this)).start();
//        MyAsyncTask myAsyncTask = new MyAsyncTask();
//        myAsyncTask.execute();

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
    @Override
    public void add(MyAlbum album) {
        new Thread(new AddAlbumRunnable(this, album)).start();
    }
    public class AddAlbumRunnable implements Runnable {
        private MyAlbum album;
        private PhotoActivity photoActivity;

        public AddAlbumRunnable(PhotoActivity photoActivity, MyAlbum album) {
            this.photoActivity = photoActivity;
            this.album = album;
        }

        @Override
        public void run() {
            DataLocalManager.getInstance().saveImageToAlbum(album.getName(), imgPath);
            photoActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    bottomSheetDialog.cancel();
                }
            });
        }
    }
    public class MyRunnable implements Runnable {
        private AlbumSelectAdapter albumSelectAdapter;
        private List<MyAlbum> listAlbum;
        private List<String> listAlbumNames;
        private PhotoActivity photoActivity;

        public MyRunnable(PhotoActivity photoActivity) {
            this.photoActivity = photoActivity;
            listAlbumNames = DataLocalManager.getInstance().getAllAlbum();
            listAlbum = new ArrayList<>();
        }

        @Override
        public void run() {
            for(int i =0 ;i <listAlbumNames.size();i++)
            {
                List<String> listImagePath = DataLocalManager.getInstance().getAlbumImages(listAlbumNames.get(i));

                List<MyImage> listImage = new ArrayList<>();
                for(int j=0; j<listImagePath.size();j++)
                {
                    listImage.add(new MyImage(listImagePath.get(j)));
                }
                listAlbum.add(new MyAlbum(listImage,listAlbumNames.get(i)));
            }
            photoActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    albumSelectAdapter = new AlbumSelectAdapter(listAlbum, photoActivity);
                    albumSelectAdapter.setAlbumInterface(photoActivity);
                    recycleViewListAlbum.setAdapter(albumSelectAdapter);
                    bottomSheetDialog.show();
                }
            });
        }
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
