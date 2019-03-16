package com.rolledback.restaurantmap.RestaurantMapAPI;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rolledback.restaurantmap.R;

import java.lang.reflect.Type;

public class AccountManager {

    private static AccountManager _instance;
    public static AccountManager getInstance() {
        if (AccountManager._instance == null) {
            AccountManager._instance = new AccountManager();
        }
        return AccountManager._instance;
    }

    private AccountManager() { }

    public Account currentUser(Context context) {
        SharedPreferences appSharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = appSharedPref.getString(context.getString(R.string.AccessTokenSharedPref), "");
        if (json.equals("")) {
            return null;
        }
        Type type = new TypeToken<Account>(){}.getType();
        return gson.fromJson(json, type);
    }

    public void login(Context context, ILoginListener listener, String username, String password) {
        RestaurantMapApiClient apiClient = new RestaurantMapApiClient();
        LoginRequest loginRequest = new LoginRequest(username, password);
        apiClient.login(loginRequest, new IClientResponseHandler<Account>() {
            @Override
            public void onSuccess(Account response) {
                SharedPreferences appSharedPref = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor prefsEditor = appSharedPref.edit();

                Gson gson = new Gson();
                String json = gson.toJson(response);
                prefsEditor.putString(context.getString(R.string.AccessTokenSharedPref), json);
                prefsEditor.commit();

                listener.onLogin(true);
            }

            @Override
            public void onFailure(String error) {
                listener.onLogin(false);
            }
        });
    }

    public void logout(Context context) {
        SharedPreferences appSharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = appSharedPref.edit();
        prefsEditor.remove(context.getString(R.string.AccessTokenSharedPref));
        prefsEditor.commit();
    }

    public interface ILoginListener {
        void onLogin(boolean loginSuccessful);
    }
}
