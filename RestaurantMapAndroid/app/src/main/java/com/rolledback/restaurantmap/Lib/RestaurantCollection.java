package com.rolledback.restaurantmap.Lib;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rolledback.restaurantmap.RestaurantMapAPI.Restaurant;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

public class RestaurantCollection implements IRestaurantCollection {

    private Context _context;
    private ArrayList<Restaurant> _restaurants;

    public RestaurantCollection(Context context) {
        this._restaurants = new ArrayList<>();
        this._context = context;
    }

    @Override
    public void addItems(List<Restaurant> restaurants) {
        Iterator<Restaurant> rItr = restaurants.iterator();
        while (rItr.hasNext()) {
            Restaurant rCurr = rItr.next();
            this._restaurants.add(rCurr);
        }
    }

    @Override
    public List<Restaurant> getItems() {
        return new ArrayList<>(this._restaurants);
    }

    @Override
    public void clearItems() {
        this._restaurants.clear();
    }

    @Override
    public void saveToCache() {
        SharedPreferences appSharedPref = PreferenceManager.getDefaultSharedPreferences(this._context);
        SharedPreferences.Editor prefsEditor = appSharedPref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(this._restaurants);
        prefsEditor.putString(Codes.RestaurantCacheSharedPref, json);
        prefsEditor.commit();
    }

    @Override
    public void loadFromCache() {
        SharedPreferences appSharedPref = PreferenceManager.getDefaultSharedPreferences(this._context);
        Gson gson = new Gson();
        String json = appSharedPref.getString(Codes.RestaurantCacheSharedPref, "");
        Type type = new TypeToken<List<Restaurant>>(){}.getType();
        List<Restaurant> restaurants = gson.fromJson(json, type);
        this.addItems(restaurants);
    }

    @Override
    public ArrayList<String> availableGenres() {
        return new ArrayList<String>(new LinkedHashSet<String>(this._restaurants
                .stream()
                .map(r -> r.genre)
                .collect(Collectors.<String>toList())));
    }

    @Override
    public ArrayList<String> availableSubGenres() {
        return new ArrayList<String>(new LinkedHashSet<String>(this._restaurants
                .stream()
                .map(r -> r.subGenre)
                .collect(Collectors.<String>toList())));

    }
}
