package com.example.albumapp.utility;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DataLocalManager {
    private static final String MY_ALBUM = "MY_ALBUM";
    private static final String MY_SHARED_PREFERENCES = "MY_SHARED_PREFERENCES";
    private static DataLocalManager instance;
    private SharedPreferences sharedPreferences;
    private SharedPreferences albumData;

    public static void init(Context context){
        instance = new DataLocalManager();
        instance.sharedPreferences = context.getSharedPreferences(MY_SHARED_PREFERENCES,Context.MODE_PRIVATE);
        instance.albumData = context.getSharedPreferences(MY_ALBUM,Context.MODE_PRIVATE);
    }

    public static DataLocalManager getInstance(){
        if(instance == null){
            instance = new DataLocalManager();
        }
        return instance;
    }

    public void saveAlbum(String albumName, Set<String> images){
        SharedPreferences.Editor editor = instance.albumData.edit();
        editor.putStringSet(albumName, images);
        editor.apply();
    }

    public void removeAlbum(String albumName){
        SharedPreferences.Editor editor = instance.albumData.edit();
        editor.remove(albumName);
        editor.apply();
    }

    public void setListImgByList(String key, List<String> listImg){
        Set<String> setListImg = new HashSet<>();

        setListImg.addAll(listImg);
        saveAlbum(key,setListImg);

    }

    public List<String> getAlbumImages(String albumName){
        Set<String> strJsonArray = instance.albumData.getStringSet(albumName, new HashSet<>());

        List<String> listImg = new ArrayList<>();

        listImg.addAll(strJsonArray);

        return listImg;
    }

    public List<String> getAllAlbum() {
        List<String> allKey = new ArrayList<String>();
        Map<String, ?> allEntries = instance.albumData.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            allKey.add(entry.getKey());
        }
        return allKey;
    }

    public void saveSpanCount(int spanCount){
        SharedPreferences.Editor editor = instance.sharedPreferences.edit();
        editor.putInt("span_count", spanCount);
        editor.apply();
    }

    public int getSpanCount(){
        if(instance.sharedPreferences != null)
            return instance.sharedPreferences.getInt("span_count", 3);
        else return 3;
    }
}