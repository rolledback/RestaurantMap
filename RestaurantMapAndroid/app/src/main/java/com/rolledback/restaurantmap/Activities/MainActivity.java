package com.rolledback.restaurantmap.Activities;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.rolledback.restaurantmap.FragmentInterfaces.IRestaurantListSelectListener;
import com.rolledback.restaurantmap.Fragments.ListFragment;
import com.rolledback.restaurantmap.Lib.Codes;
import com.rolledback.restaurantmap.Filters.FilterManager;
import com.rolledback.restaurantmap.Filters.IFiltersChangedListener;
import com.rolledback.restaurantmap.Filters.Models.IViewableFilter;
import com.rolledback.restaurantmap.FragmentInterfaces.IAppLoginListener;
import com.rolledback.restaurantmap.Fragments.AccountFragment;
import com.rolledback.restaurantmap.Fragments.FiltersFragment;
import com.rolledback.restaurantmap.Fragments.LoginFragment;
import com.rolledback.restaurantmap.Fragments.MainFragment;
import com.rolledback.restaurantmap.Fragments.MapFragment;
import com.rolledback.restaurantmap.Lib.AppState;
import com.rolledback.restaurantmap.Lib.IRestaurantCollection;
import com.rolledback.restaurantmap.Lib.RestaurantCollection;
import com.rolledback.restaurantmap.R;
import com.rolledback.restaurantmap.RestaurantMapAPI.AccountManager;
import com.rolledback.restaurantmap.RestaurantMapAPI.IClientResponseHandler;
import com.rolledback.restaurantmap.RestaurantMapAPI.Location;
import com.rolledback.restaurantmap.RestaurantMapAPI.Restaurant;
import com.rolledback.restaurantmap.RestaurantMapAPI.RestaurantMapApiClient;

import java.util.LinkedHashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements IFiltersChangedListener, IAppLoginListener, IRestaurantListSelectListener {
    private IRestaurantCollection _restaurantCollection;
    private RestaurantMapApiClient _apiClient;
    private FilterManager _filterManager;
    private AppState _currState;

    private FrameLayout filtersFragmentContainer;
    private ExtendedFloatingActionButton _filterButton;
    private FloatingActionButton _addButton;
    private BottomNavigationView _bottomNavBar;

    private MapFragment _mapFragment;
    private ListFragment _listFragment;
    private LoginFragment _loginFragment;
    private AccountFragment _acctFragment;
    private MainFragment _activeFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _currState = new AppState();
        _currState.loggedIn = AccountManager.getInstance().currentUser(this) != null;

        _mapFragment = new MapFragment();
        _listFragment = new ListFragment();
        _loginFragment = new LoginFragment();
        _acctFragment = new AccountFragment();

        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, _acctFragment).hide(_acctFragment).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, _loginFragment).hide(_loginFragment).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, _listFragment).hide(_listFragment).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, _mapFragment).commit();
        _activeFragment = _mapFragment;

        filtersFragmentContainer = findViewById(R.id.filters_fragment_container);

        _bottomNavBar = findViewById(R.id.bottom_nav_bar);
        _bottomNavBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                MainFragment selectedFragment = null;
                switch (menuItem.getItemId()) {
                    case R.id.nav_map:
                        selectedFragment = _mapFragment;
                        break;
                    case R.id.nav_list:
                        selectedFragment = _listFragment;
                        break;
                    case R.id.nav_account:
                        if (_currState.loggedIn) {
                            selectedFragment = _acctFragment;
                        } else {
                            selectedFragment = _loginFragment;
                        }
                        break;
                }
                _changeFragment(selectedFragment);

                return true;
            }
        });

        this._addButton = findViewById(R.id.add_button);
        this._addButton.setOnClickListener(v -> {
            _openAddActivity();
        });

        this._filterButton = findViewById(R.id.filter_button);
        this._filterButton.setOnClickListener(view -> {
            _showFilters();
        });
        this._updateButtonsState();

        this._restaurantCollection = new RestaurantCollection(this);
        this._apiClient = new RestaurantMapApiClient(this);
        this._filterManager = new FilterManager(this);

        this._refresh(null);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Codes.LocationPermissionsRequest);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        this._mapFragment.moveToInitialLocation();
    }

    public void _showFilters() {
        FiltersFragment filtersFragment = new FiltersFragment();

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
        this._listFragment.setFilters(this._filterManager.getCurrentFilters());
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        menu.add(0, Codes.RefreshButtonAction, 0, "Refresh").setIcon(R.drawable.ic_refresh_24dp).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case Codes.RefreshButtonAction:
                this._refresh(null);
                return true;
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Codes.AddActivityRequest) {
            if (resultCode == Codes.ResultRestaurantAdded) {
                this._refresh(new IRefreshCallback() {
                    @Override
                    public void callback() {
                        if (_activeFragment.equals(_mapFragment)) {
                            Location loc = new Location();
                            loc.lat = data.getDoubleExtra("lat", Integer.MIN_VALUE);
                            loc.lng = data.getDoubleExtra("lng", Integer.MIN_VALUE);
                            _mapFragment.moveToLocation(loc);
                        }
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
        Intent intent = new Intent(this, AddRestaurantActivity.class);
        intent.putStringArrayListExtra(Codes.AvailableGenresExtra, this._restaurantCollection.availableGenres());
        intent.putStringArrayListExtra(Codes.AvailableSubGenresExtra, this._restaurantCollection.availableSubGenres());
        startActivityForResult(intent, Codes.AddActivityRequest);
    }

    private void _refresh(IRefreshCallback callback) {
        _apiClient.getRestaurants(new IClientResponseHandler<List<Restaurant>>() {
            @Override
            public void onSuccess(List<Restaurant> response) {
                _restaurantCollection.addItems(response);
                _restaurantCollection.saveToCache();
                _filterManager.initFilters(_restaurantCollection.getItems());

                _listFragment.setRestaurants(_restaurantCollection.getItems());
                _mapFragment.setRestaurants(_restaurantCollection.getItems());

                if (callback != null) {
                    callback.callback();
                }

                _currState.restaurantsLoaded = true;
                _updateButtonsState();
            }

            @Override
            public void onFailure(String error) {
                _showFailureToast();
                _restaurantCollection.loadFromCache();
                _mapFragment.setRestaurants(_restaurantCollection.getItems());
                _filterManager.initFilters(_restaurantCollection.getItems());

                _currState.restaurantsLoaded = true;
                _updateButtonsState();
            }
        });
    }

    @Override
    public void onLoginEvent() {
        _currState.loggedIn = true;
        _changeFragment(_acctFragment);
    }

    @Override
    public void onRestaurantSelected(Restaurant restaurant) {
    }

    private interface IRefreshCallback {
        void callback();
    }

    private void _changeFragment(MainFragment newFragment) {
        getSupportFragmentManager().beginTransaction().hide(_activeFragment).show(newFragment).commit();
        _activeFragment = newFragment;
        _updateButtonsState();
    }

    private void _updateButtonsState() {
        if (_activeFragment != null && _activeFragment.shouldShowAddButton(_currState)) {
            _addButton.show();
        } else {
            _addButton.hide();
        }

        if (_activeFragment != null && _activeFragment.shouldShowFilterButton(_currState)) {
            _filterButton.show();
        } else {
            _filterButton.hide();
        }
    }
}
