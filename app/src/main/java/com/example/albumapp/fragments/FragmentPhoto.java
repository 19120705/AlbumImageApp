package com.example.albumapp.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.albumapp.adapters.CategoryAdapter;
import com.example.albumapp.adapters.ImageAdapter;
import com.example.albumapp.R;
import com.example.albumapp.models.MyCategory;
import com.example.albumapp.models.MyImage;
import com.example.albumapp.utility.GetAllPhotoFromDisk;

import java.util.ArrayList;
import java.util.List;

public class FragmentPhoto extends Fragment {
    private RecyclerView recyclerView;
    private CategoryAdapter categoryAdapter;
    private ImageView imageView;
    private List<MyImage> listImages;
    private androidx.appcompat.widget.Toolbar toolbar_photo;

    private ActivityResultLauncher<Intent> mActivityResultLauncher;
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // Xử lý dữ liệu trả về ở đây
                            try {
                                thumbnail = MediaStore.Images.Media.getBitmap(
                                        getActivity().getApplicationContext().getContentResolver(), imageUri);

                                //imageurl = getRealPathFromURI(imageUri);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

//                            if (requestCode == REQUEST_CODE_MULTI) {
//                                ImageAdapter.MyAsyncTask myAsyncTask = new ImageAdapter.MyAsyncTask();
//                                myAsyncTask.execute();
//                                Toast.makeText(context, "Your image is hidden", Toast.LENGTH_SHORT).show();
//                            }
                        }
                    }
                }
        );
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,@Nullable ViewGroup container,@Nullable Bundle
                             savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_photo, container, false);
        context = view.getContext();
        recyclerView = view.findViewById(R.id.recyclerViewPhoto);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        setupRecyclerView();

        toolbar_photo = view.findViewById(R.id.toolbar_photo);
        setupToolBarPhoto();



        return view;
    }


    private void setupToolBarPhoto(){
        toolbar_photo.inflateMenu(R.menu.menu_top);
        toolbar_photo.setTitle(getContext().getResources().getString(R.string.photo));
        toolbar_photo.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if(id == R.id.menuSearch) {
                    eventSearch(item);
                }
                else if(id == R.id.menuCamera)
                {
                    eventCamera();
                }
//                    case R.id.menuSearch:
//
//                    case R.id.menuCamera:
//                        eventSearch(item);
//                        //takenImg();
//                        break;
//                    case R.id.menuSearch_Advanced:
//                        actionSearchAdvanced();
//                        break;
//                    case R.id.duplicateImages:
//                        actionDuplicateImage();
//                        break;
//                    case R.id.menuFilter:
//                        eventSearch(item);
//                        Intent intent_mul = new Intent(getContext(), MultiSelectImage.class);
//                        startActivityForResult(intent_mul, REQUEST_CODE_MULTI);
//                        break;
//                    case R.id.menuSettings:
//                        eventSearch(item);
//                        Intent intent = new Intent(getContext(), SettingsActivity.class);
//                        startActivity(intent);
//                }
                return true;
            }
        });
    }

    public void eventSearch(@NonNull MenuItem item){}

    //Camera
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static final int PICTURE_RESULT = 1;
    private Uri imageUri;
    private String imageurl;
    private Bitmap thumbnail;
    private void eventCamera(){

        // Kiểm tra quyền CAMERA
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            // Quyền CAMERA đã được cấp, khởi chạy ActivityResultLauncher
            // Đăng ký ActivityResultLauncher cho quyền CAMERA
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "New Picture");
            values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
            imageUri = getActivity().getApplicationContext().getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            mActivityResultLauncher.launch(intent);
        } else {
            // Nếu không, yêu cầu quyền CAMERA
            ActivityResultLauncher<String> requestPermissionLauncher =
                    registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                        if (isGranted) {
                            Toast.makeText(getActivity(), "camera permission granted", Toast.LENGTH_LONG).show();
                            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                            mActivityResultLauncher.launch(cameraIntent);
                        } else {
                            Toast.makeText(getActivity(), "camera permission denied", Toast.LENGTH_LONG).show();
                        }
                    });
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }
    private void setupRecyclerView() {
//        // Thiết lập RecyclerView với Adapter
        categoryAdapter = new CategoryAdapter(getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        categoryAdapter.setListCategories(getListCategory());
        recyclerView.setAdapter(categoryAdapter);
    }

    private List<String> getListImagePaths(List<MyImage> images) {
        List<String> listPath = new ArrayList<>();
        for (int i = 0; i < images.size(); i++) {
            listPath.add(images.get(i).getPath());
        }
        return listPath;
    }

    @NonNull
    private List<MyCategory> getListCategory() {
        List<MyCategory> categoryList = new ArrayList<>();
        int categoryCount = 0;
        listImages = GetAllPhotoFromDisk.getImages(getContext());


        try {
            categoryList.add(new MyCategory(listImages.get(0).getDateTaken(), new ArrayList<>()));
            categoryList.get(categoryCount).addItemToListImages(listImages.get(0));
            for (int i = 1; i < listImages.size(); i++) {
                if (!listImages.get(i).getDateTaken().equals(listImages.get(i - 1).getDateTaken())) {
                    categoryList.add(new MyCategory(listImages.get(i).getDateTaken(), new ArrayList<>()));
                    categoryCount++;
                }
                categoryList.get(categoryCount).addItemToListImages(listImages.get(i));
            }
            return categoryList;
        } catch (Exception e) {
            return null;
        }

    }
}
