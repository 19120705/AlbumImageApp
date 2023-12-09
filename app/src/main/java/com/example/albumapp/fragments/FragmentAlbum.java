package com.example.albumapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.albumapp.activities.CreateAlbumActivity;
import com.example.albumapp.activities.PrivateAlbumActivity;
import com.example.albumapp.adapters.AlbumAdapter;
import com.example.albumapp.adapters.MenuAlbumUtiAdapter;
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
    private RecyclerView recyclerViewForOtherAlbum;
    private LinearLayout linearLayoutBtnPrivate;
    private LinearLayout linearLayoutBtnTrash;
    private List<MyImage> listImage;
    private View view;
    private androidx.appcompat.widget.Toolbar toolbar_album;
    private List<MyAlbum> listAlbum;
    private AlbumAdapter albumAdapter;
    private MenuAlbumUtiAdapter menuAlbumUtiAdapter;
    private ViewPager2 viewPager2;
    public FragmentAlbum(ViewPager2 viewPager2) {
        this.viewPager2 = viewPager2;
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_album, container, false);
        listImage = GetAllPhotoFromDisk.getImages(view.getContext());
        listAlbum = getListAlbum(listImage);
        toolbar_album = view.findViewById(R.id.toolbar_album);
        recyclerView = view.findViewById(R.id.recyclerViewAlbum);
        linearLayoutBtnPrivate = view.findViewById(R.id.linearLayoutBtnPrivate);
        linearLayoutBtnTrash = view.findViewById(R.id.linearLayoutBtnTrash);
        linearLayoutBtnPrivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPrivateAlbumClicked(v);
            }
        });
        setViewRyc();
        createToolBar();
        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        listAlbum = getListAlbum(listImage);
        albumAdapter.setData(listAlbum);
    }
    private void setViewRyc() {
        recyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), 2, GridLayoutManager.HORIZONTAL, false));
        albumAdapter = new AlbumAdapter(listAlbum, getContext());
        recyclerView.setAdapter(albumAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            int lastX = 0;

            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                switch (e.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lastX = (int) e.getX();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        boolean isScrollingRight = e.getX() < lastX;
                        if ((isScrollingRight && ((GridLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition()
                                == recyclerView.getAdapter().getItemCount() - 1) ||
                                (!isScrollingRight && ((GridLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition() == 0)) {
                            viewPager2.setUserInputEnabled(true);
                        } else {
                            viewPager2.setUserInputEnabled(false);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        lastX = 0;
                        viewPager2.setUserInputEnabled(true);
                        break;
                }
                return false;
            }
            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });

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
        List<String> listAlbumName = DataLocalManager.getInstance().getAllAlbum();
        List<MyAlbum> allAlbum = new ArrayList<>();

        if(!listImage.isEmpty()) {
            MyAlbum token = new MyAlbum(listImage, "Pictures");
            allAlbum.add(token);
        }

        for (int i = 0; i < listAlbumName.size(); i++) {
            List<String> listImgPath = DataLocalManager.getInstance().getAlbumImages(listAlbumName.get(i));
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

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                List<MyAlbum> lisAlbumSearch = new ArrayList<>();

                for (MyAlbum album : listAlbum) {
                    if (album.getName().toLowerCase().contains(s)) {
                        lisAlbumSearch.add(album);
                    }
                }
                albumAdapter.setData(lisAlbumSearch);
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
        view.getContext().startActivity(_intent);
    }

    public void onPrivateAlbumClicked(View view) {
        Intent _intent = new Intent(view.getContext(), PrivateAlbumActivity.class);
        _intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        view.getContext().startActivity(_intent);
    }
}
