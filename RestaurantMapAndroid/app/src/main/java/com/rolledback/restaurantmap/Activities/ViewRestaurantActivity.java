package com.rolledback.restaurantmap.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.rolledback.restaurantmap.Map.RestaurantMarker;
import com.rolledback.restaurantmap.R;
import com.rolledback.restaurantmap.RestaurantMapAPI.Location;
import com.rolledback.restaurantmap.RestaurantMapAPI.Restaurant;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class ViewRestaurantActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap _mMap;
    private Restaurant _model;
    private TextView _restaurantName;
    private TextView _restaurantGenre;
    private TextView _restaurantRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_restaurant);

        Intent intent = this.getIntent();
        Bundle data = intent.getExtras();
        this._model = (Restaurant) data.getParcelable("restaurant");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        this._restaurantName = this.findViewById(R.id.restaurant_name);
        this._restaurantName.setText(this._model.name);
        this._restaurantGenre = this.findViewById(R.id.restaurant_genre);
        this._restaurantGenre.setText(this._model.genre + ", " + this._model.subGenre);
        this._restaurantRating = this.findViewById(R.id.restaurant_rating);
        this._restaurantRating.setText(this._model.rating);
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
        this._mMap = googleMap;
        LatLng restaurantLoc = new LatLng(_model.location.lat, _model.location.lng );
        this._mMap.moveCamera(CameraUpdateFactory.zoomTo(12));
        this._mMap.moveCamera(CameraUpdateFactory.newLatLng(restaurantLoc));

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            this._mMap.setMyLocationEnabled(true);
        }

        Location location = _model.location;
        MarkerOptions mOp = new MarkerOptions()
                .position(new LatLng(location.lat, location.lng))
                .icon(BitmapDescriptorFactory.defaultMarker(RestaurantMarker.getGenreMarkerColor(_model)));
        Marker marker = this._mMap.addMarker(mOp);
    }
}
