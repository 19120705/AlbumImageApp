package com.example.albumapp.fragments;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

    private Context context;

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//    }
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
        toolbar_photo.inflateMenu(R.menu.menu_top);

        return view;
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
            for(int i = 0; i< categoryCount; i++)
            {
                Log.e("Size list image", "categoriesCount "+i+":"+categoryList.get(i).getListImages().size() );
            }
            Log.e("Size list image", "categoriesList: "+categoryList.size() );
            return categoryList;
        } catch (Exception e) {
            return null;
        }

    }
}
