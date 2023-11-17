package com.example.albumapp.adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.albumapp.fragments.FragmentAlbum;
import com.example.albumapp.fragments.FragmentAlbumFavorite;
import com.example.albumapp.fragments.FragmentAlbumPrivate;
import com.example.albumapp.fragments.FragmentPhoto;

public class ViewPagerAdapter extends FragmentStateAdapter {

    private Context context;

    public void setContext(Context context) {
        this.context = context;
//        data = GetAllPhotoFromGallery.getAllImageFromGallery(context);
    }

    public ViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);

    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new FragmentPhoto();
            case 1:
                return new FragmentAlbum();
            case 2:
                return new FragmentAlbumPrivate();
            case 3:
                return new FragmentAlbumFavorite();
            default:
                return new FragmentPhoto();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
