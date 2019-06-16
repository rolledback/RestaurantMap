package com.rolledback.restaurantmap.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.rolledback.restaurantmap.Codes;
import com.rolledback.restaurantmap.Filters.FilterManager;
import com.rolledback.restaurantmap.Filters.IFiltersChangedListener;
import com.rolledback.restaurantmap.Filters.Models.IViewableFilter;
import com.rolledback.restaurantmap.Fragments.FiltersFragment;
import com.rolledback.restaurantmap.Fragments.MapFragment;
import com.rolledback.restaurantmap.Map.IRestaurantCollection;
import com.rolledback.restaurantmap.Map.RestaurantCollection;
import com.rolledback.restaurantmap.R;
import com.rolledback.restaurantmap.RestaurantMapAPI.IClientResponseHandler;
import com.rolledback.restaurantmap.RestaurantMapAPI.Location;
import com.rolledback.restaurantmap.RestaurantMapAPI.Restaurant;
import com.rolledback.restaurantmap.RestaurantMapAPI.RestaurantMapApiClient;

import java.util.LinkedHashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements IFiltersChangedListener {
    private IRestaurantCollection _restaurantCollection;
    private RestaurantMapApiClient _apiClient;
    private FilterManager _filterManager;

    private FrameLayout filtersFragmentContainer;
    private MaterialButton _filterButton;
    private BottomNavigationView _bottomNavBar;

    private MapFragment _mapFragment;
    private MapFragment _listFragment;
    private MapFragment _acctFragment;
    private Fragment _activeFragment;

    FloatingActionButton _addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _mapFragment = new MapFragment();
        _listFragment = new MapFragment();
        _acctFragment = new MapFragment();

        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, _acctFragment).hide(_acctFragment).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, _listFragment).hide(_listFragment).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, _mapFragment).commit();
        _activeFragment = _mapFragment;

        filtersFragmentContainer = findViewById(R.id.filters_fragment_container);

        _bottomNavBar = findViewById(R.id.bottom_nav_bar);
        _bottomNavBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment selectedFragment = null;
                switch (menuItem.getItemId()) {
                    case R.id.nav_map:
                        selectedFragment = _mapFragment;
                        break;
                    case R.id.nav_list:
                        selectedFragment = _listFragment;
                        break;
                    case R.id.nav_account:
                        selectedFragment = _acctFragment;
                        break;
                }

                getSupportFragmentManager().beginTransaction().hide(_activeFragment).show(selectedFragment).commit();
                _activeFragment = selectedFragment;

                return true;
            }
        });

        Button filterButton = findViewById(R.id.filter_button);
        filterButton.setOnClickListener(view -> {
            showFilters();
        });

        this._addButton = findViewById(R.id.add_button);
        this._addButton.setOnClickListener(v -> {
            _openAddActivity();
        });
        this._addButton.hide();

        this._filterButton = findViewById(R.id.filter_button);
        this._filterButton.setVisibility(View.INVISIBLE);

        this._restaurantCollection = new RestaurantCollection(this);
        this._apiClient = new RestaurantMapApiClient(this);
        this._filterManager = new FilterManager(this);

        _apiClient.getRestaurants(new IClientResponseHandler<List<Restaurant>>() {
            @Override
            public void onSuccess(List<Restaurant> response) {
                _restaurantCollection.addItems(response);
                _restaurantCollection.saveToCache();
                _addButton.show();
                _filterButton.setVisibility(View.VISIBLE);
                _mapFragment.setRestaurants(_restaurantCollection.getItems());
                _filterManager.initFilters(_restaurantCollection.getItems());
            }

            @Override
            public void onFailure(String error) {
                _showFailureToast();
                _restaurantCollection.loadFromCache();
                _addButton.show();
                _filterButton.setVisibility(View.VISIBLE);
                _mapFragment.setRestaurants(_restaurantCollection.getItems());
                _filterManager.initFilters(_restaurantCollection.getItems());
            }
        });

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Codes.LocationPermissionsRequest);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        this._mapFragment.moveToInitialLocation();
    }

    public void showFilters() {
        FiltersFragment filtersFragment = FiltersFragment.newInstance();

        Bundle args = new Bundle();
        args.putSerializable("filters", this._filterManager.getCurrentFilters());
        filtersFragment.setArguments(args);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down, R.anim.slide_up, R.anim.slide_down);
        transaction.addToBackStack(null);
        transaction.add(R.id.filters_fragment_container, filtersFragment, "FILTERS_FRAGMENT").commit();
    }

    @Override
    public void onFiltersChanged(LinkedHashMap<String, IViewableFilter> filters) {
        this._filterManager.setCurrentFilters(filters);
        this._mapFragment.setFilters(this._filterManager.getCurrentFilters());
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        menu.add(0, Codes.RefreshButtonAction, 0, "Refresh").setIcon(R.drawable.ic_refresh_24dp).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case Codes.LoginButtonAction:
                intent = new Intent(this, LoginActivity.class);
                startActivityForResult(intent, Codes.LoginActivityRequest);
                return true;
            case Codes.ShowProfileAction:
                intent = new Intent(this, ProfileActivity.class);
                startActivityForResult(intent, Codes.ProfileActivityRequest);
                return true;
            case Codes.RefreshButtonAction:
                this._refreshMap(null);
                return true;
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Codes.LoginActivityRequest) {
            if (resultCode == Codes.ResultLogin) {
                invalidateOptionsMenu();
                this._checkIfAccountExists();
            }
        }
        if (requestCode == Codes.ProfileActivityRequest) {
            if (resultCode == Codes.ResultLogout) {
                invalidateOptionsMenu();
                this._checkIfAccountExists();
            }
        }
        if (requestCode == Codes.AddActivityRequest) {
            if (resultCode == Codes.ResultRestaurantAdded) {
                this._refreshMap(new IRefreshCallback() {
                    @Override
                    public void callback() {
                        Location loc = new Location();
                        loc.lat = data.getDoubleExtra("lat", Integer.MIN_VALUE);
                        loc.lng = data.getDoubleExtra("lng", Integer.MIN_VALUE);
                        _scrollMapToRestaurant(loc);
                    }
                });
            }
        }
    }

    private void _showFailureToast() {
        CharSequence text = "Failed to reach server. Loading restaurants from cache.";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(this, text, duration);
        toast.show();
    }

    private void _openAddActivity() {
//        Intent intent = new Intent(this, AddRestaurantActivity.class);
//        intent.putStringArrayListExtra(Codes.AvailableGenresExtra, this.restaurantMap.getAvailableGenres());
//        intent.putStringArrayListExtra(Codes.AvailableSubGenresExtra, this.restaurantMap.getAvailableSubGenres());
//        startActivityForResult(intent, Codes.AddActivityRequest);
    }

    private void _checkIfAccountExists() {
//        User currUser = AccountManager.getInstance().currentUser(this);
//        if (currUser == null) {
//            this._addButton.hide();
//        } else {
//            this._addButton.show();
//        }
    }

    private void _refreshMap(IRefreshCallback callback) {
//        RestaurantMapApiClient client = new RestaurantMapApiClient(this);
//        client.getRestaurants(new IClientResponseHandler<List<Restaurant>>() {
//            @Override
//            public void onSuccess(List<Restaurant> response) {
//                restaurantMap.clearItems();
//                restaurantMap.addItems(response);
//                restaurantMap.saveToCache(response);
//                if (callback != null) {
//                    callback.callback();;
//                }
//            }
//
//            @Override
//            public void onFailure(String error) {
//                _showFailureToast();
//                restaurantMap.clearItems();
//                restaurantMap.loadFromCache();
//            }
//        });
    }

    private void _scrollMapToRestaurant(Location loc) {
//        this.restaurantMap.moveToRestaurant(loc);
    }

    private interface IRefreshCallback {
        void callback();
    }
}
