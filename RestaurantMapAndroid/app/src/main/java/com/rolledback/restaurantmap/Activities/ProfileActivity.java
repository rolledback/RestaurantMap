package com.rolledback.restaurantmap.Activities;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.amulyakhare.textdrawable.TextDrawable;
import com.rolledback.restaurantmap.Codes;
import com.rolledback.restaurantmap.R;
import com.rolledback.restaurantmap.RestaurantMapAPI.Account;
import com.rolledback.restaurantmap.RestaurantMapAPI.AccountManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

public class ProfileActivity extends AppCompatActivity {

    private ImageView _profilePicView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        this._profilePicView = findViewById(R.id.profilePic);

        Account currentLogin = AccountManager.getInstance().currentUser(this);

        String firstChar = currentLogin.username.substring(0, 1).toUpperCase();
        TextDrawable drawable = TextDrawable.builder().buildRound(firstChar, getResources().getColor(R.color.colorAccent));

        this._profilePicView.setImageDrawable(drawable);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        menu.add(0, Codes.LogoutMenuAction, 0, "Logout").setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case Codes.LogoutMenuAction:
                AccountManager.getInstance().logout(this);
                Intent data = new Intent();
                setResult(Codes.ResultLogout, data);
                finish();
                return true;
            case android.R.id.home:
                finish();
                return true;
        }
        return true;
    }
}
