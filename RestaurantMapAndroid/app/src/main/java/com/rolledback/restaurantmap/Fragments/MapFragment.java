package com.rolledback.restaurantmap.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.rolledback.restaurantmap.Filters.Models.IViewableFilter;
import com.rolledback.restaurantmap.Lib.AppState;
import com.rolledback.restaurantmap.Lib.RestaurantMap;
import com.rolledback.restaurantmap.R;
import com.rolledback.restaurantmap.RestaurantMapAPI.Location;
import com.rolledback.restaurantmap.RestaurantMapAPI.Restaurant;

import java.util.LinkedHashMap;
import java.util.List;

public class MapFragment extends MainFragment implements OnMapReadyCallback {
    private GoogleMap _map;
    private RestaurantMap _restaurantMap;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this._restaurantMap = new RestaurantMap();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        return view;
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this._map = googleMap;
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        this._restaurantMap.setMap(this._map);
        this._restaurantMap.initMarkers();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void setRestaurants(List<Restaurant> restaurants) {
        this._restaurantMap.setItems(restaurants);
        this._restaurantMap.initMarkers();
    }

    public void setFilters(LinkedHashMap<String, IViewableFilter> filters) {
        this._restaurantMap.applyFilters(filters);
    }

    public void moveToInitialLocation() {
        if (ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            this._map.setMyLocationEnabled(true);
            try {
                LocationManager manager = (LocationManager) this.getContext().getSystemService(Context.LOCATION_SERVICE);
                Criteria mCriteria = new Criteria();
                String bestProvider = String.valueOf(manager.getBestProvider(mCriteria, true));
                android.location.Location mLocation = manager.getLastKnownLocation(bestProvider);
                if (mLocation != null) {
                    final double currentLatitude = mLocation.getLatitude();
                    final double currentLongitude = mLocation.getLongitude();
                    this._map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLatitude, currentLongitude), 13));
                    this._map.animateCamera(CameraUpdateFactory.zoomTo(13), 2000, null);
                }
            } catch (SecurityException e) {
                // Shouldn't happen?
            }
        } else {
            LatLng seattle = new LatLng(47.609722, -122.333056 );
            this._map.moveCamera(CameraUpdateFactory.zoomTo(8));
            this._map.moveCamera(CameraUpdateFactory.newLatLng(seattle));
        }
    }

    public void moveToLocation(Location loc) {
        this._restaurantMap.moveToLocation(loc);
    }

    @Override
    public boolean shouldShowAddButton(AppState currState) {
        return currState.restaurantsLoaded && currState.loggedIn;
    }

    @Override
    public boolean shouldShowFilterButton(AppState currState) {
        return currState.restaurantsLoaded;
    }
}
