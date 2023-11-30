package com.example.albumapp.utility;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DataLocalManager {
    private static final String MY_SHARED_PREFERENCES="MY_SHARED_PREFERENCES";
    private static DataLocalManager instance;
    private SharedPreferences sharedPreferences;

    public static void init(Context context){
        instance = new DataLocalManager();
        instance.sharedPreferences = context.getSharedPreferences(MY_SHARED_PREFERENCES,Context.MODE_PRIVATE);
    }

    public static DataLocalManager getInstance(){
        if(instance == null){
            instance = new DataLocalManager();
        }
        return instance;
    }

    public void setListImg(String key, Set<String> listImg){
        SharedPreferences.Editor editor = instance.sharedPreferences.edit();
        editor.remove(key).apply();
        editor.putStringSet(key,listImg);
        editor.apply();
    }

    public void setListImgByList(String key, List<String> listImg){
        Set<String> setListImg = new HashSet<>();

        setListImg.addAll(listImg);
        setListImg(key,setListImg);

    }

    public List<String> getListImg(String key){
        Set<String> strJsonArray = getListSet(key);

        List<String> listImg = new ArrayList<>();

        listImg.addAll(strJsonArray);

        return listImg;
    }

    public Set<String> getListSet(String key){
        return instance.sharedPreferences.getStringSet(key, new HashSet<>());
    }

    public static List<String> getAllKey() {
        List<String> allKey = new ArrayList<String>();
        Map<String, ?> allEntries = instance.sharedPreferences.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            allKey.add(entry.getKey());
        }
        return allKey;
    }
}