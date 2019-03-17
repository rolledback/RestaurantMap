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
import com.rolledback.restaurantmap.Filters.Models.IViewableFilter;
import com.rolledback.restaurantmap.R;
import com.rolledback.restaurantmap.RestaurantMapAPI.Location;
import com.rolledback.restaurantmap.RestaurantMapAPI.Restaurant;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

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
                    final double currentLatitude = mLocation.getLatitude();
                    final double currentLongitude = mLocation.getLongitude();
                    this._map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLatitude, currentLongitude), 13));
                    this._map.animateCamera(CameraUpdateFactory.zoomTo(13), 2000, null);
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
        this.filterManager.initFilters(restaurants);
        this.applyFilters(this.filterManager.getCurrentFilters());
    }

    public void moveToRestaurant(Location loc) {
        RestaurantMarker match = null;
        for (int i = 0; i < this._markers.size(); i++) {
            if (this._markers.get(i).isLocation(loc)) {
                match = this._markers.get(i);
                break;
            }
        }

        if (match != null) {
            this._moveToMarker(match);
        }
    }

    public void clearItems() {
        for (int i = 0; i < this._markers.size(); i++) {
            this._markers.get(i).remove();
        }
        this._markers.clear();
    }

    public void saveToCache(List<Restaurant> restaurants) {
        SharedPreferences appSharedPref = PreferenceManager.getDefaultSharedPreferences(this._context);
        SharedPreferences.Editor prefsEditor = appSharedPref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(restaurants);
        prefsEditor.putString(this._context.getString(R.string.RestaurantCacheSharedPref), json);
        prefsEditor.commit();
    }

    public void loadFromCache() {
        SharedPreferences appSharedPref = PreferenceManager.getDefaultSharedPreferences(this._context);
        Gson gson = new Gson();
        String json = appSharedPref.getString(this._context.getString(R.string.RestaurantCacheSharedPref), "");
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

    public ArrayList<String> getAvailableGenres() {
        return new ArrayList<>(new LinkedHashSet<>(this._markers.stream().map(m -> m.getGenre()).collect(Collectors.<String>toList())));
    }

    public ArrayList<String> getAvailableSubGenres() {
        return new ArrayList<>(new LinkedHashSet<>(this._markers.stream().map(m -> m.getSubGenre()).collect(Collectors.<String>toList())));
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
            case "Meh":
                return BitmapDescriptorFactory.HUE_RED;
            case "Want to Go":
                return BitmapDescriptorFactory.HUE_VIOLET;
            default:
                return BitmapDescriptorFactory.HUE_RED;
        }
    }

    private void _moveToMarker(RestaurantMarker marker) {
        Location markerLocation = marker.getLocation();
        final double lat = markerLocation.lat;
        final double lng = markerLocation.lng;
        this._map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 16));
        this._map.animateCamera(CameraUpdateFactory.zoomTo(16), 2000, null);
        marker.show();
        marker.select();
    }
}
