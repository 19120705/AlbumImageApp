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
    private static final String MY_PRIVATE_ALBUM = "MY_PRIVATE_ALBUM";
    private static final String MY_TRASH_ALBUM = "MY_TRASH_ALBUM";
    private static DataLocalManager instance;
    private SharedPreferences sharedPreferences;
    private SharedPreferences albumData;
    private SharedPreferences secretInfo;
    private SharedPreferences albumTrash;

    public static void init(Context context){
        instance = new DataLocalManager();
        instance.sharedPreferences = context.getSharedPreferences(MY_SHARED_PREFERENCES,Context.MODE_PRIVATE);
        instance.albumData = context.getSharedPreferences(MY_ALBUM,Context.MODE_PRIVATE);
        instance.secretInfo = context.getSharedPreferences(MY_PRIVATE_ALBUM,Context.MODE_PRIVATE);
        instance.albumTrash = context.getSharedPreferences(MY_TRASH_ALBUM,Context.MODE_PRIVATE);
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

    public void savePassword(String password){
        SharedPreferences.Editor editor = instance.secretInfo.edit();
        editor.putString("password", password);
        editor.apply();
    }
    public void saveQuestion(String password){
        SharedPreferences.Editor editor = instance.secretInfo.edit();
        editor.putString("question", password);
        editor.apply();
    }
    public void saveAnswer(String password){
        SharedPreferences.Editor editor = instance.secretInfo.edit();
        editor.putString("answer", password);
        editor.apply();
    }

    public String getPassword(){
        return instance.secretInfo.getString("password", "");
    }
    public String getQuestion(){
        return instance.secretInfo.getString("question", "");
    }
    public String getAnswer(){
        return instance.secretInfo.getString("answer", "");
    }

    public void savePrivateAlbum(Set<String> image){
        SharedPreferences.Editor editor = instance.secretInfo.edit();
        Set<String> secretAlbum = getPrivateAlbum();
        secretAlbum.addAll(image);
        editor.putStringSet("Secret Album", secretAlbum);
        editor.apply();
    }
    public Set<String> getPrivateAlbum(){
        return instance.secretInfo.getStringSet("Secret Album", new HashSet<>());
    }
    //thoi gian dua anh vao thung rac theo format ""yyyy-M-dd" va chuyen thanh kieu long
    public void saveTrash(String imagePath, Long dateDelete){
        SharedPreferences.Editor editor = instance.albumTrash.edit();
        editor.putLong(imagePath, dateDelete);
        editor.apply();
    }
    public Map<String, ?> getTrash(){
        return instance.albumTrash.getAll();
    }
    public void removeImageTrash(String imagePath){
        SharedPreferences.Editor editor = instance.albumTrash.edit();
        editor.remove(imagePath);
        editor.apply();
    }
}