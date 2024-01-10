package com.example.albumapp.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.albumapp.activities.ItemAlbumActivity;
import com.example.albumapp.activities.MultiSelectPhotoActivity;
import com.example.albumapp.activities.SettingsActivity;
import com.example.albumapp.adapters.CategoryAdapter;
import com.example.albumapp.R;
import com.example.albumapp.models.MyCategory;
import com.example.albumapp.models.MyImage;
import com.example.albumapp.utility.GetAllPhotoFromDisk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FragmentPhoto extends Fragment {
    private RecyclerView recyclerView;
    private CategoryAdapter categoryAdapter;
    private ImageView imageView;
    private List<MyImage> listImages;
    private androidx.appcompat.widget.Toolbar toolbar_photo;

    private ActivityResultLauncher<Intent> mActivityResultLauncher;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private Context context;

    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static final int PICTURE_RESULT = 1;
    private Uri imageUri;
    private String imageurl;
    private Bitmap thumbnail;
    private int spanCount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        spanCount = 3;
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

                                imageurl = getRealPathFromURI(imageUri);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            new Thread(new MyTask(categoryAdapter)).start();
                            Toast.makeText(context, "Your image is hidden", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
        requestPermissionLauncher =
                registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                    if (isGranted) {
                        Toast.makeText(getActivity(), "camera permission granted", Toast.LENGTH_LONG).show();
                        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        mActivityResultLauncher.launch(cameraIntent);
                    } else {
                        Toast.makeText(getActivity(), "camera permission denied", Toast.LENGTH_LONG).show();
                    }
                });
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

    @Override
    public void onResume() {
        super.onResume();
        new Thread(new MyTask(categoryAdapter)).start();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
    private void setupRecyclerView() {
        // Thiết lập RecyclerView với Adapter
        categoryAdapter = new CategoryAdapter(getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        categoryAdapter.setListCategories(getListCategory());
//        categoryAdapter.setListCategories(getListCategoryTitleMake());
        categoryAdapter.setSpanCount(spanCount);
        recyclerView.setAdapter(categoryAdapter);
    }
    private void setupToolBarPhoto(){
        toolbar_photo.inflateMenu(R.menu.menu_top);
        toolbar_photo.setTitle(getContext().getResources().getString(R.string.photo));
        toolbar_photo.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if(id == R.id.menuSpanCount) {
                    changeSpanCount();
                }
                else if(id == R.id.menuCamera)
                {
                    eventCamera();
                }
                else if(id == R.id.menuMultiSelectPhoto)
                {
                    Intent intent = new Intent(context, MultiSelectPhotoActivity.class);
                    startActivity(intent);
                }
                else if(id == R.id.menuDuplicate)
                {
                    actionDuplicateImage();
                }
                else if(id == R.id.menuSettings)
                {
                    Intent intent = new Intent(getContext(), SettingsActivity.class);
                    startActivity(intent);
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

    private void changeSpanCount() {
        if(spanCount >= 4) {
            spanCount = 1;
        }
        else {
            spanCount++;
        }
        //recyclerView.setLayoutManager(new GridLayoutManager(getContext(), spanCount));
        if (categoryAdapter == null) {
            return;
        }
        categoryAdapter.setSpanCount(spanCount);
        recyclerView.setAdapter(categoryAdapter);

        animationRyc();
    }
    private void animationRyc() {
        switch(spanCount) {
        case 1:
            Animation animation1 = AnimationUtils.loadAnimation(getContext().getApplicationContext(),R.anim.anim_layout_ryc_1);
            recyclerView.setAnimation(animation1);
        case 2:
            Animation animation2 = AnimationUtils.loadAnimation(getContext().getApplicationContext(), R.anim.anim_layout_ryc_1);
            recyclerView.setAnimation(animation2);
            break;
        case 3:
            Animation animation3 = AnimationUtils.loadAnimation(getContext().getApplicationContext(), R.anim.anim_layout_ryc_2);
            recyclerView.setAnimation(animation3);
            break;
        case 4:
            Animation animation4 = AnimationUtils.loadAnimation(getContext().getApplicationContext(), R.anim.anim_layout_ryc_3);
            recyclerView.setAnimation(animation4);
            break;
        }
    }

    private void actionDuplicateImage(){
        new DupRunnable(getContext()).execute();
    }

    public class DupRunnable implements Runnable {
        private ProgressDialog mProgressDialog;
        private ArrayList<MyImage> list;
        private Context context;

        public DupRunnable(Context context) {
            this.context = context;
        }

        @Override
        public void run() {
            list = new ArrayList<>();
            List<String> listImage = getListImgDuplicate();
            for(int i = 0; i< listImage.size();i++)
            {
                list.add(new MyImage(listImage.get(i)));
            }

            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Intent intent_duplicate = new Intent(context, ItemAlbumActivity.class);
                    intent_duplicate.putParcelableArrayListExtra("dataImages", list);
                    intent_duplicate.putExtra("name", "Duplicate Image");
                    intent_duplicate.putExtra("duplicateImg", 2);
                    intent_duplicate.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent_duplicate);
                    mProgressDialog.cancel();
                }
            });
        }

        public void execute() {
            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setMessage("Loading, please wait...");
            mProgressDialog.show();

            new Thread(this).start();
        }
    }

    public ArrayList<String> getListImgDuplicate(){
        List<MyImage> imageList = GetAllPhotoFromDisk.getImages(getContext());
        long hash = 0;
        Map<Long,ArrayList<String>> map = new HashMap<Long,ArrayList<String>>();
        for (MyImage img: imageList) {
            Bitmap bitmap = BitmapFactory.decodeFile(img.getPath());
            hash = hashBitmap(bitmap);
            if(map.containsKey(hash)){
                map.get(hash).add(img.getPath());
            }else{
                ArrayList<String> list = new ArrayList<>();
                list.add(img.getPath());
                map.put(hash,list);
            }
        }
        ArrayList<String> result = new ArrayList<>();
        Set set = map.keySet();
        for (Object key: set) {
            if(map.get(key).size() >=2){

                result.addAll(map.get(key));
            }
        }
        return result;
    }
    public long hashBitmap(Bitmap bmp){
        long hash = 31;
        for(int x = 1; x <  bmp.getWidth(); x=x*2){
            for (int y = 1; y < bmp.getHeight(); y=y*2){
                hash *= (bmp.getPixel(x,y) + 31);
                hash = hash%1111122233;
            }
        }
        return hash;
    }
    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
    public class MyTask implements Runnable {
        private List<MyCategory> listCategory;
        private CategoryAdapter categoryAdapter;
        public MyTask(CategoryAdapter categoryAdapter) {
            this.categoryAdapter = categoryAdapter;
        }
        @Override
        public void run() {
            //doInBackground
            listCategory = getListCategory();
            // Update UI on the main thread
            //onPostExecute
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    categoryAdapter.setListCategories(listCategory);
                }
            });
        }
    }

    //Camera
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
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
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
        listImages = GetAllPhotoFromDisk.getSelectiveImages(getContext());

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
    @NonNull
    private List<MyCategory> getListCategoryTitleMake() {
        List<MyCategory> categoryList = new ArrayList<>();
        int categoryCount = 0;
        listImages = GetAllPhotoFromDisk.getSelectiveImages(getContext());

        try {
            categoryList.add(new MyCategory(listImages.get(0).getMake(), new ArrayList<>()));
            categoryList.get(categoryCount).addItemToListImages(listImages.get(0));
            for (int i = 1; i < listImages.size(); i++) {
                if (!listImages.get(i).getMake().equals(listImages.get(i - 1).getMake())) {
                    categoryList.add(new MyCategory(listImages.get(i).getMake(), new ArrayList<>()));
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
