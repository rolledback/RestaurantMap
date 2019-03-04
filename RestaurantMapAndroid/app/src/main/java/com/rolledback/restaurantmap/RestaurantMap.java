package com.rolledback.restaurantmap;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.LocationManager;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.rolledback.restaurantmap.Filters.IFilterable;
import com.rolledback.restaurantmap.Filters.Models.CheckFilter;
import com.rolledback.restaurantmap.Filters.Models.FilterList;
import com.rolledback.restaurantmap.Filters.Models.IViewableFilter;
import com.rolledback.restaurantmap.Filters.Models.ToggleFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import androidx.core.content.ContextCompat;

public class RestaurantMap implements IFilterable {
    private Context _context;
    private GoogleMap _map;

    public RestaurantMap(Context context, GoogleMap map) {
        this._context = context;
        this._map = map;
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
            Iterator<Location> lItr = rCurr.locations.iterator();
            while (lItr.hasNext()) {
                Location lCurr = lItr.next();
                this._map.addMarker(new MarkerOptions()
                        .position(new LatLng(lCurr.lat, lCurr.lng))
                        .title(rCurr.name)
                        .icon(BitmapDescriptorFactory.defaultMarker(this._getMarkerColor(rCurr))));
            }
        }
    }

    public void loadFromCache() {

    }

    public void applyFilters(ArrayList<IViewableFilter> filters) {

    }

    public ArrayList<IViewableFilter> getCurrentFilters() {
        List<CheckFilter> ratingFilterItems = Arrays.asList(
                new CheckFilter(this._context.getText(R.string.best_rating_title).toString(), this._context.getText(R.string.best_rating_description).toString(), false, true),
                new CheckFilter(this._context.getText(R.string.better_rating_title).toString(), this._context.getText(R.string.better_rating_description).toString(), false, true),
                new CheckFilter(this._context.getText(R.string.good_rating_title).toString(), this._context.getText(R.string.good_rating_description).toString(), false, true),
                new CheckFilter(this._context.getText(R.string.ok_rating_title).toString(), this._context.getText(R.string.ok_rating_description).toString(), false, false),
                new CheckFilter(this._context.getText(R.string.meh_rating_title).toString(), this._context.getText(R.string.meh_rating_description).toString(), false, false),
                new CheckFilter(this._context.getText(R.string.want_rating_title).toString(), this._context.getText(R.string.want_rating_description).toString(), false, false)
        );
        FilterList ratingFilter = new FilterList(this._context.getText(R.string.rating_filter_title).toString(), ratingFilterItems);

        List<CheckFilter> genreFilterItems = Arrays.asList(
                new CheckFilter("American", null, false, true),
                new CheckFilter("Cajun", null, false, false),
                new CheckFilter("Chinese", null, false, true),
                new CheckFilter("French", null, false, false),
                new CheckFilter("German", null, false, false),
                new CheckFilter("Global", null, false, false),
                new CheckFilter("Italian", null, false, true),
                new CheckFilter("Indian", null, false, false),
                new CheckFilter("Korean", null, false, false),
                new CheckFilter("Japanese", null, false, false),
                new CheckFilter("Mexican", null, false, false)
        );
        FilterList genreFilter = new FilterList("Genre", genreFilterItems);

        List<IViewableFilter> otherFilterItems = Arrays.asList(
                new ToggleFilter(this._context.getText(R.string.visited_title).toString(), this._context.getText(R.string.visited_description).toString(), false, true),
                new ToggleFilter(this._context.getText(R.string.single_title).toString(), this._context.getText(R.string.single_description).toString(), false, true)
        );
        FilterList otherFilters = new FilterList(this._context.getText(R.string.other_filters_title).toString(), otherFilterItems);

        ArrayList<IViewableFilter> filters = new ArrayList<IViewableFilter>();
        filters.add(ratingFilter);
        filters.add(genreFilter);
        filters.add(otherFilters);

        return filters;
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
