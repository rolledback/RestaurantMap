package com.rolledback.restaurantmap;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.rolledback.restaurantmap.Filters.IFiltersChangedListener;
import com.rolledback.restaurantmap.Filters.Models.IViewableFilter;
import com.rolledback.restaurantmap.Map.RestaurantMap;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, IFiltersChangedListener {
    private static int LocPermissionReqCode = 1994;
    private GoogleMap mMap;
    private RestaurantMap restaurantMap;
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
        this.restaurantMap = new RestaurantMap(this, googleMap);
        boolean moveSuccessful = restaurantMap.moveToStartingLocation();
        if (!moveSuccessful) {
            ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.ACCESS_FINE_LOCATION }, LocPermissionReqCode);
        }

        RestaurantMapApiClient client = new RestaurantMapApiClient();
        client.getRestaurants(new IClientResponseHandler<List<Restaurant>>() {
            @Override
            public void onSuccess(List<Restaurant> response) {
                restaurantMap.addItems(response);
                restaurantMap.saveToCache(response);
            }

            @Override
            public void onFailure(String error) {
                _showFailureToast();
                restaurantMap.loadFromCache();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == LocPermissionReqCode) {
            if (permissions.length == 1 && permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                this.restaurantMap.moveToStartingLocation();
            }
        }
    }

    public void showFilters() {
        FiltersFragment filtersFragment = FiltersFragment.newInstance();

        Bundle args = new Bundle();
        args.putSerializable("filters", this.restaurantMap.getCurrentFilters());
        filtersFragment.setArguments(args);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down, R.anim.slide_up, R.anim.slide_down);
        transaction.addToBackStack(null);
        transaction.add(R.id.filters_fragment_container, filtersFragment, "FILTERS_FRAGMENT").commit();
    }

    @Override
    public void onFiltersChanged(LinkedHashMap<String, IViewableFilter> filters) {
        this.restaurantMap.applyFilters(filters);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        menu.add(0, 0, 0, "History").setIcon(R.drawable.account_circle)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        Drawable drawable = menu.getItem(0).getIcon();
        if(drawable != null) {
            drawable.mutate();
            drawable.setColorFilter(0xffffff, PorterDuff.Mode.SRC_ATOP);
        }

        return true;
    }

    private void _showFailureToast() {
        CharSequence text = "Failed to reach server. Loading restaurants from cache.";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(this, text, duration);
        toast.show();
    }
}
