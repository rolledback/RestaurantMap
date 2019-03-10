package com.rolledback.restaurantmap;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rolledback.restaurantmap.Filters.IFiltersChangedListener;
import com.rolledback.restaurantmap.Filters.Models.IViewableFilter;
import com.rolledback.restaurantmap.Map.RestaurantMap;
import com.rolledback.restaurantmap.RestaurantMapAPI.IClientResponseHandler;
import com.rolledback.restaurantmap.RestaurantMapAPI.LoginRequest;
import com.rolledback.restaurantmap.RestaurantMapAPI.LoginResult;
import com.rolledback.restaurantmap.RestaurantMapAPI.Restaurant;
import com.rolledback.restaurantmap.RestaurantMapAPI.RestaurantMapApiClient;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, IFiltersChangedListener {
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
            ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.ACCESS_FINE_LOCATION }, Codes.LocationPermissionsRequest);
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
        if (requestCode == Codes.LocationPermissionsRequest) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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
    public boolean onPrepareOptionsMenu(Menu menu) {
        LoginResult currentLogin = this._getCurrentLogin();
        if (currentLogin != null) {
            int size = (int) (24 * Resources.getSystem().getDisplayMetrics().density);
            String firstChar = currentLogin.username.substring(0, 1).toUpperCase();
            TextDrawable drawable = TextDrawable.builder().beginConfig().width(size).height(size).endConfig().buildRound(firstChar, getResources().getColor(R.color.colorAccent));
            menu.add(0, 99, 0, "My Account").setIcon(drawable)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        } else {
            menu.add(0, Codes.LoginButtonAction, 0, "Login").setIcon(R.drawable.account_circle)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case Codes.LoginButtonAction:
                Intent intent = new Intent(this, LoginActivity.class);
                startActivityForResult(intent, Codes.LoginActivityRequest);
                return true;
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Codes.LoginActivityRequest) {
            if (resultCode == RESULT_OK) {
                invalidateOptionsMenu();
            }
        }
    }

    private void _showFailureToast() {
        CharSequence text = "Failed to reach server. Loading restaurants from cache.";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(this, text, duration);
        toast.show();
    }

    private LoginResult _getCurrentLogin() {
        SharedPreferences appSharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        Gson gson = new Gson();
        String json = appSharedPref.getString(this.getString(R.string.AccessTokenSharedPref), "");
        if (json.equals("")) {
            return null;
        }
        Type type = new TypeToken<LoginResult>(){}.getType();
        return gson.fromJson(json, type);
    }
}
