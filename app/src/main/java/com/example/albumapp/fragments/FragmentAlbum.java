package com.example.albumapp.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
import androidx.recyclerview.widget.RecyclerView;

import com.example.albumapp.activities.CreateAlbumActivity;
import com.example.albumapp.adapters.AlbumAdapter;
import com.example.albumapp.models.MyAlbum;
import com.example.albumapp.models.MyImage;
import com.example.albumapp.R;

import java.util.ArrayList;
import java.util.List;

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

        toolbar_album = view.findViewById(R.id.toolbar_album);
        createToolBar();
        return view;
    }
    private void createToolBar() {
        toolbar_album.inflateMenu(R.menu.menu_top_album);
        toolbar_album.setTitle(getContext().getResources().getString(R.string.album));
        toolbar_album.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.menuSearch) {
                    //eventSearch(item);
                }
                else if (id == R.id.menuAdd) {
                    openCreateAlbumActivity();
                }
                return true;
            }
        });
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
