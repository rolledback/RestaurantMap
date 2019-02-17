package com.rolledback.restaurantmap;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Filter;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Iterator;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, FiltersFragment.OnFragmentInteractionListener {
    private static int LocPermissionReqCode = 1994;
    private GoogleMap mMap;
    private FrameLayout filtersFragmentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        filtersFragmentContainer = findViewById(R.id.filters_fragment_container);

        Button button = findViewById(R.id.filter_button);
        button.setOnClickListener(view -> {
            showFilters();
        });
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
        mMap = googleMap;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.i("com.rolledback.restaurantmap.INFO", "Permission already granted");
            mMap.setMyLocationEnabled(true);
            moveMapToCurrentLocation();
        } else {
            Log.i("com.rolledback.restaurantmap.INFO", "Requesting permission");
            LatLng seattle = new LatLng(47.609722, -122.333056 );
            mMap.moveCamera(CameraUpdateFactory.zoomTo(8));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(seattle));
            ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.ACCESS_FINE_LOCATION }, LocPermissionReqCode);
        }

        RestaurantMapApiClient client = new RestaurantMapApiClient();
        client.getRestaurants(new IClientResponseHandler<List<Restaurant>>() {
            @Override
            public void onSuccess(List<Restaurant> response) {
                Iterator<Restaurant> rItr = response.iterator();
                while (rItr.hasNext()) {
                    Restaurant rCurr = rItr.next();
                    Iterator<Location> lItr = rCurr.locations.iterator();
                    while (lItr.hasNext()) {
                        Location lCurr = lItr.next();
                        mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(lCurr.lat, lCurr.lng))
                                .title(rCurr.name)
                                .icon(BitmapDescriptorFactory.defaultMarker(getMarkerColor(rCurr))));
                    }
                }
            }

            @Override
            public void onFailure(String error) {
//                Context context = getApplicationContext();
//                CharSequence text = "Unable to load data: " + error.toString();
//                int duration = Toast.LENGTH_SHORT;
//
//                Toast toast = Toast.makeText(context, text, duration);
//                toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL, 0, 0);
//                toast.show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == LocPermissionReqCode) {
            if (permissions.length == 1 && permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                try {
                    mMap.setMyLocationEnabled(true);
                    moveMapToCurrentLocation();
                } catch (SecurityException e) {
                    this.finish();
                }
            } else {
                this.finish();
            }
        }
    }

    public void moveMapToCurrentLocation() {
        try {
            LocationManager manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            Criteria mCriteria = new Criteria();
            String bestProvider = String.valueOf(manager.getBestProvider(mCriteria, true));
            android.location.Location mLocation = manager.getLastKnownLocation(bestProvider);
            if (mLocation != null) {
                Log.e("TAG", "GPS is on");
                final double currentLatitude = mLocation.getLatitude();
                final double currentLongitude = mLocation.getLongitude();
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLatitude, currentLongitude), 15));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
            }
        } catch (SecurityException e) {
            this.finish();
        }
    }

    public float getMarkerColor(Restaurant restaurant) {
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

    public void showFilters() {
        FiltersFragment filtersFragment = FiltersFragment.newInstance();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down, R.anim.slide_up, R.anim.slide_down);
        transaction.addToBackStack(null);
        transaction.add(R.id.filters_fragment_container, filtersFragment, "FILTERS_FRAGMENT").commit();
    }
}
