package com.rolledback.restaurantmap.Map;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rolledback.restaurantmap.Filters.FilterManager;
import com.rolledback.restaurantmap.Filters.IFilterable;
import com.rolledback.restaurantmap.Filters.Models.CheckFilter;
import com.rolledback.restaurantmap.Filters.Models.FilterList;
import com.rolledback.restaurantmap.Filters.Models.IViewableFilter;
import com.rolledback.restaurantmap.Filters.Models.ToggleFilter;
import com.rolledback.restaurantmap.Location;
import com.rolledback.restaurantmap.R;
import com.rolledback.restaurantmap.Restaurant;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Filter;

import androidx.core.content.ContextCompat;

public class RestaurantMap implements IFilterable {
    private Context _context;
    private GoogleMap _map;
    private ArrayList<RestaurantMarker> _markers;
    private FilterManager filterManager;

    public RestaurantMap(Context context, GoogleMap map) {
        this._context = context;
        this._map = map;
        this._markers = new ArrayList<>();
        this.filterManager = new FilterManager(context);
    }

    public boolean moveToStartingLocation() {
        if (ContextCompat.checkSelfPermission(this._context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            this._map.setMyLocationEnabled(true);
            try {
                LocationManager manager = (LocationManager) this._context.getSystemService(Context.LOCATION_SERVICE);
                Criteria mCriteria = new Criteria();
                String bestProvider = String.valueOf(manager.getBestProvider(mCriteria, true));
                android.location.Location mLocation = manager.getLastKnownLocation(bestProvider);
                if (mLocation != null) {
                    Log.e("TAG", "GPS is on");
                    final double currentLatitude = mLocation.getLatitude();
                    final double currentLongitude = mLocation.getLongitude();
                    this._map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLatitude, currentLongitude), 15));
                    this._map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
                }
                return true;
            } catch (SecurityException e) {
                // Shouldn't happen?
                return false;
            }
        } else {
            LatLng seattle = new LatLng(47.609722, -122.333056 );
            this._map.moveCamera(CameraUpdateFactory.zoomTo(8));
            this._map.moveCamera(CameraUpdateFactory.newLatLng(seattle));
            return false;
        }
    }

    public void addItems(List<Restaurant> restaurants) {
        Iterator<Restaurant> rItr = restaurants.iterator();
        while (rItr.hasNext()) {
            Restaurant rCurr = rItr.next();
            Location location = rCurr.location;
            MarkerOptions mOp = new MarkerOptions()
                    .position(new LatLng(location.lat, location.lng))
                    .title(rCurr.name)
                    .icon(BitmapDescriptorFactory.defaultMarker(this._getMarkerColor(rCurr)));
            Marker marker = this._map.addMarker(mOp);
            this._markers.add(new RestaurantMarker(marker, rCurr));
        }
        this.filterManager.initFilters(restaurants);;
    }

    public void saveToCache(List<Restaurant> restaurants) {
        SharedPreferences appSharedPref = PreferenceManager.getDefaultSharedPreferences(this._context);
        SharedPreferences.Editor prefsEditor = appSharedPref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(restaurants);
        prefsEditor.putString("RESTAURANT_CACHE", json);
        prefsEditor.commit();
    }

    public void loadFromCache() {
        SharedPreferences appSharedPref = PreferenceManager.getDefaultSharedPreferences(this._context);
        Gson gson = new Gson();
        String json = appSharedPref.getString("RESTAURANT_CACHE", "");
        Type type = new TypeToken<List<Restaurant>>(){}.getType();
        List<Restaurant> restaurants = gson.fromJson(json, type);
        this.addItems(restaurants);
    }

    public void applyFilters(LinkedHashMap<String, IViewableFilter> filters) {
        Iterator<RestaurantMarker> mItr = this._markers.iterator();
        while (mItr.hasNext()) {
            RestaurantMarker mCurr = mItr.next();
            if (mCurr.shouldShow(filters)) {
                mCurr.show();
            } else {
                mCurr.hide();
            }
        }
        this.filterManager.setCurrentFilters(filters);
    }

    public LinkedHashMap<String, IViewableFilter> getCurrentFilters() {
        return this.filterManager.getCurrentFilters();
    }

    private float _getMarkerColor(Restaurant restaurant) {
        switch (restaurant.rating) {
            case "Ok":
                return BitmapDescriptorFactory.HUE_ORANGE;
            case "Good":
                return BitmapDescriptorFactory.HUE_AZURE;
            case "Better":
                return BitmapDescriptorFactory.HUE_YELLOW;
            case "Best":
                return BitmapDescriptorFactory.HUE_GREEN;
            default:
                return BitmapDescriptorFactory.HUE_RED;
        }
    }
}