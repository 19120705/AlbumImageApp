package com.example.albumapp.fragments;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.albumapp.ImageAdapter;
import com.example.albumapp.MainActivity;
import com.example.albumapp.R;

import java.util.ArrayList;
import java.util.List;

public class FragmentPhoto extends Fragment {
    private RecyclerView recyclerView;
    private androidx.appcompat.widget.Toolbar toolbar_photo;

    private Context context;
    private MainActivity main;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{
            context = getActivity();
            main = (MainActivity) getActivity();
        }catch (IllegalStateException e){
            throw new IllegalStateException("MainActivity must implement callbacks");
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
                             savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_photo, null);
        context = view.getContext();
        recyclerView = view.findViewById(R.id.recyclerViewPhoto);
        toolbar_photo = view.findViewById(R.id.toolbar_photo);
        toolbar_photo.inflateMenu(R.menu.menu_top);
        setupRecyclerView();
        return view;
    }


    private void setupRecyclerView() {
//        // Thiết lập RecyclerView với Adapter
        List<String> images = getImages();
        ImageAdapter adapter = new ImageAdapter(context, images);
        recyclerView.setAdapter(adapter);

    }

    private List<String> getImages() {
        Uri collection;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
        } else {
            collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }

        // Chỉ lấy các cột mà chúng ta quan tâm
        String[] projection = new String[] {
                MediaStore.Images.Media.DATA,
        };

        // Sắp xếp theo ngày thêm vào
        String sortOrder = MediaStore.Images.Media.DATE_ADDED + " DESC";

        // Truy vấn Content Provider
        Cursor cursor = getContext().getContentResolver().query(collection, projection, null, null, sortOrder);

        // Lấy chỉ số của cột chúng ta quan tâm
        int dataIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        List<String> imagePaths = new ArrayList<>();

        while (cursor.moveToNext()) {
            // Lấy đường dẫn của hình ảnh
            String data = cursor.getString(dataIndex);
            imagePaths.add(data);
        }

        cursor.close();
        return imagePaths;
    }
}
