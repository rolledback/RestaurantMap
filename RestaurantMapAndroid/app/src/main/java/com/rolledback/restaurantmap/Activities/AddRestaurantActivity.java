package com.rolledback.restaurantmap.Activities;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.rolledback.restaurantmap.Codes;
import com.rolledback.restaurantmap.R;
import com.rolledback.restaurantmap.RestaurantMapAPI.IClientResponseHandler;
import com.rolledback.restaurantmap.RestaurantMapAPI.Location;
import com.rolledback.restaurantmap.RestaurantMapAPI.Restaurant;
import com.rolledback.restaurantmap.RestaurantMapAPI.RestaurantMapApiClient;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class AddRestaurantActivity extends AppCompatActivity {

    EditText _restaurantName;
    EditText _address;
    AutoCompleteTextView _genre;
    AutoCompleteTextView _subGenre;
    Spinner _rating;
    Switch _moreThanOneLoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_restaurant);

        getSupportActionBar().setTitle("Add Restaurant");

        Spinner spinner = findViewById(R.id.rating_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.ratings, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        this._restaurantName = findViewById(R.id.restaurant_name_input);
        this._address = findViewById(R.id.address_input);
        this._genre = findViewById(R.id.genre_input);
        this._subGenre = findViewById(R.id.sub_genre_input);
        this._rating = findViewById(R.id.rating_spinner);
        this._moreThanOneLoc = findViewById(R.id.multi_loc_switch);

        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                this._handleSendText(intent); // Handle text being sent
            }
        } else {
            ArrayList<String> availableGenres = intent.getStringArrayListExtra(Codes.AvailableGenresExtra);
            ArrayAdapter<String> genresAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, availableGenres);
            this._genre.setAdapter(genresAdapter);

            ArrayList<String> availableSubGenres = intent.getStringArrayListExtra(Codes.AvailableSubGenresExtra);
            ArrayAdapter<String> subGenresAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, availableSubGenres);
            this._subGenre.setAdapter(subGenresAdapter);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case Codes.SaveRestaurantAction:
                this._tryToSaveRestaurant();
                return true;
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        menu.add(0, Codes.SaveRestaurantAction, 0, "Done").setIcon(R.drawable.ic_check_24dp)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        return true;
    }

    private void _handleSendText(Intent intent) {
        String text = intent.getStringExtra(Intent.EXTRA_TEXT);
        String[] parts = text.split("\n");

        String restaurantName = "";
        String address = "";

        boolean parseSuccessful = false;
        int parsedLines = 0;
        int index = 0;

        while (parsedLines < 2 && index < parts.length) {
            String currPart = parts[index];
            if (!currPart.equals("")) {
                if (parsedLines == 0) {
                    restaurantName = currPart;
                }
                if (parsedLines == 1) {
                    address = currPart;
                }
                parsedLines++;
            }
            index++;
        }

        parseSuccessful = !restaurantName.equals("") && !address.equals("");

        if (parseSuccessful) {
            this._restaurantName.setText(restaurantName);
            this._address.setText(address);
        } else {
            this._showParseFailureToast();
        }
    }

    private void _showParseFailureToast() {
        this._showFailureToast("Failed to parse new restaurant.");
    }

    private void _showFailureToast(CharSequence text) {
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(this, text, duration);
        toast.show();
    }

    private void _tryToSaveRestaurant() {
        boolean inputsValid = this._validateInputs();
        if (inputsValid) {
            String searchTerm = "";
            searchTerm += this._restaurantName.getText().toString();
            searchTerm += " ";
            searchTerm += this._address.getText().toString();

            Geocoder geocoder = new Geocoder(this);
            try {
                List<Address> results = geocoder.getFromLocationName(searchTerm, 100);
                if (results.size() < 1) {
                    this._showFailureToast("Unable to geocode the given restaurant. Please ensure the restaurant name and address are accurate.");
                } else if (results.size() > 1) {
                    this._showFailureToast("Too many possible results. Please refine the restaurant name or address.");
                }else {
                    Address result = results.get(0);

                    Restaurant restaurant = new Restaurant();
                    restaurant.name = this._restaurantName.getText().toString();
                    restaurant.genre = this._genre.getText().toString();
                    restaurant.subGenre = this._subGenre.getText().toString();
                    restaurant.rating = (String) this._rating.getSelectedItem();
                    restaurant.hasOtherLocations = this._moreThanOneLoc.isActivated();

                    Location loc = new Location();
                    loc.address = result.getAddressLine(0);
                    loc.lat = result.getLatitude();
                    loc.lng = result.getLongitude();

                    restaurant.location = loc;

                    RestaurantMapApiClient client = new RestaurantMapApiClient(this);
                    client.addRestaurant(restaurant, new IClientResponseHandler<Void>() {
                        @Override
                        public void onSuccess(Void response) {
                            _sendAddedResult(loc);
                        }

                        @Override
                        public void onFailure(String error) {
                            _showFailureToast("Failed to contact server. Please try again later. Error: " + error);
                        }
                    });
                }
            } catch (Exception e) {
                this._showFailureToast("Unable to geocode the given restaurant. Please ensure the restaurant name and address are accurate.");
            }
        }
    }

    private void _sendAddedResult(Location addedLocation) {
        Intent data = new Intent();
        data.putExtra("lat", addedLocation.lat);
        data.putExtra("lng", addedLocation.lng);
        setResult( Codes.ResultRestaurantAdded, data);
        finish();
    }

    private boolean _validateInputs() {
        String restaurantName = this._restaurantName.getText().toString();
        if (restaurantName.isEmpty()) {
            this._restaurantName.setError("Restaurant name is required.");
            return false;
        } else {
            this._restaurantName.setError(null);
        }

        String address = this._address.getText().toString();
        if (address.isEmpty()) {
            this._address.setError("Address is required.");
            return false;
        } else {
            this._address.setError(null);
        }

        String genre = this._genre.getText().toString();
        if (genre.isEmpty()) {
            this._genre.setError("Genre is required.");
            return false;
        } else {
            this._genre.setError(null);
        }

        String subGenre = this._subGenre.getText().toString();
        if (subGenre.isEmpty()) {
            this._subGenre.setError("Sub-genre is required.");
            return false;
        } else {
            this._subGenre.setError(null);
        }

        return true;
    }
}
