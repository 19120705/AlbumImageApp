package com.example.albumapp.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.albumapp.activities.CreateAlbumActivity;
import com.example.albumapp.adapters.AlbumAdapter;
import com.example.albumapp.models.MyAlbum;
import com.example.albumapp.models.MyImage;
import com.example.albumapp.R;
import com.example.albumapp.utility.DataLocalManager;
import com.example.albumapp.utility.GetAllPhotoFromDisk;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FragmentAlbum extends Fragment {
    private static final int REQUEST_CODE_CREATE = 100;
    private RecyclerView recyclerView;
    private List<MyImage> listImage;
    private View view;
    private androidx.appcompat.widget.Toolbar toolbar_album;
    private List<MyAlbum> listAlbum;
    private AlbumAdapter albumAdapter;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_album, container, false);
        listImage = GetAllPhotoFromDisk.getImages(view.getContext());
        listAlbum = getListAlbum(listImage);
        toolbar_album = view.findViewById(R.id.toolbar_album);
        recyclerView = view.findViewById(R.id.recyclerViewAlbum);
        setViewRyc();
        createToolBar();
        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        albumAdapter.setData(listAlbum);
    }
    private void setViewRyc() {
        recyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), 2));
        albumAdapter = new AlbumAdapter(listAlbum, getContext());
        recyclerView.setAdapter(albumAdapter);
    }
    private void createToolBar() {
        toolbar_album.inflateMenu(R.menu.menu_top_album);
        toolbar_album.setTitle(getContext().getResources().getString(R.string.album));
        toolbar_album.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.menuSearch) {
                    eventSearch(item);
                }
                else if (id == R.id.menuAdd) {
                    openCreateAlbumActivity();
                }
                return true;
            }
        });
    }
    private List<MyAlbum> getListAlbum(List<MyImage> listImage) {
        List<String> listAlbumName = DataLocalManager.getAllKey();
        List<MyAlbum> allAlbum = new ArrayList<>();

        if(!listImage.isEmpty()) {
            MyAlbum token = new MyAlbum(listImage, "Pictures");
            allAlbum.add(token);
        }

        for (int i = 0; i < listAlbumName.size(); i++) {
            List<String> listImgPath = DataLocalManager.getListImg(listAlbumName.get(i));
            List<MyImage> listImgAlbum = new ArrayList<>();
            for (int j = 0; j < listImgPath.size(); j++) {
                for (int k = 0; k < listImage.size(); k++) {
                    if (Objects.equals(listImage.get(k).getPath(), listImgPath.get(j))) {
                        listImgAlbum.add(listImage.get(k));
                    }
                }
            }

            MyAlbum token = new MyAlbum(listImgAlbum, listAlbumName.get(i));
            allAlbum.add(token);
        }
        return allAlbum;
    }
    private void eventSearch(@NonNull MenuItem item) {
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setQueryHint("Type to search");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                List<MyAlbum> lisAlbumSearch = new ArrayList<>();

                for (MyAlbum album : listAlbum) {
                    if (album.getName().toLowerCase().contains(s)) {
                        lisAlbumSearch.add(album);
                    }
                }

                if (lisAlbumSearch.size() != 0) {
                    albumAdapter.setData(lisAlbumSearch);
                    synchronized (FragmentAlbum.this) {
                        FragmentAlbum.this.notifyAll();
                    }
                } else {
                    Toast.makeText(getContext(), "Searched album not found", Toast.LENGTH_LONG).show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        item.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                albumAdapter.setData(listAlbum);
                synchronized (FragmentAlbum.this) {
                    FragmentAlbum.this.notifyAll();
                }
                return true;
            }
        });

    }

    private void openCreateAlbumActivity() {
        Intent _intent = new Intent(view.getContext(), CreateAlbumActivity.class);
        _intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ((Activity) view.getContext()).startActivityForResult(_intent, REQUEST_CODE_CREATE);
    }
}
