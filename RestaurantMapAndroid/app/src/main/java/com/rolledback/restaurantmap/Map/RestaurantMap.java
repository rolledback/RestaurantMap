package com.rolledback.restaurantmap.Map;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.LocationManager;
import android.preference.PreferenceManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rolledback.restaurantmap.Codes;
import com.rolledback.restaurantmap.Filters.FilterManager;
import com.rolledback.restaurantmap.Filters.IFilterable;
import com.rolledback.restaurantmap.Filters.Models.IViewableFilter;
import com.rolledback.restaurantmap.RestaurantMapAPI.Location;
import com.rolledback.restaurantmap.RestaurantMapAPI.Restaurant;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

import androidx.core.content.ContextCompat;

public class RestaurantMap implements IFilterable {
    private GoogleMap _map;
    private ArrayList<Restaurant> _restaurants;
    private ArrayList<RestaurantMarker> _restaurantMarkers;

    public RestaurantMap() {
    }

    public void setMap(GoogleMap map) {
        this._map = map;
    }

    public void initMarkers() {
        if (this._map != null && this._restaurants != null) {
            this._restaurantMarkers = new ArrayList<>();
            Iterator<Restaurant> rItr = this._restaurants.iterator();
            while (rItr.hasNext()) {
                Restaurant rCurr = rItr.next();
                Location location = rCurr.location;
                MarkerOptions mOp = new MarkerOptions()
                        .position(new LatLng(location.lat, location.lng))
                        .title(rCurr.name)
                        .icon(BitmapDescriptorFactory.defaultMarker(RestaurantMarker.getGenreMarkerColor(rCurr)));
                Marker marker = this._map.addMarker(mOp);
                this._restaurantMarkers.add(new RestaurantMarker(marker, rCurr));
            }
        }
    }

    public void setItems(List<Restaurant> restaurants) {
        this._restaurants = new ArrayList<>(restaurants);
    }

    public void moveToLocation(Location loc) {
        RestaurantMarker match = null;
        for (int i = 0; i < this._restaurantMarkers.size(); i++) {
            if (this._restaurantMarkers.get(i).isLocation(loc)) {
                match = this._restaurantMarkers.get(i);
                break;
            }
        }

        if (match != null) {
            this._moveToMarker(match);
        }
    }

    public void applyFilters(LinkedHashMap<String, IViewableFilter> filters) {
        Iterator<RestaurantMarker> mItr = this._restaurantMarkers.iterator();
        while (mItr.hasNext()) {
            RestaurantMarker mCurr = mItr.next();
            if (mCurr.shouldShow(filters)) {
                mCurr.show();
            } else {
                mCurr.hide();
            }
        }
    }

    public Restaurant getRestaurantFromMarker(Marker m) {
        Iterator<RestaurantMarker> iter  = this._restaurantMarkers.iterator();
        while (iter.hasNext()) {
            RestaurantMarker restaurantMarker = iter.next();
            if (restaurantMarker.getMarker().equals((m))) {
                return restaurantMarker.getRestaurant();
            }
        }
        return null;
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
