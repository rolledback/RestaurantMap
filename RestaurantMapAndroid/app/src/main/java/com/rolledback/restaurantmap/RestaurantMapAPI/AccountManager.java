package com.rolledback.restaurantmap.RestaurantMapAPI;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rolledback.restaurantmap.Codes;

import java.io.IOException;
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

    public User currentUser(Context context) {
        AuthResult currAuthInfo = this._getCurrentAuthInfo(context);
        if (currAuthInfo == null) {
            return null;
        }
        return currAuthInfo.user;
    }

    public String currentAuthToken(Context context) {
        AuthResult currAuthInfo = this._getCurrentAuthInfo(context);
        if (currAuthInfo == null) {
            return null;
        }
        return currAuthInfo.accessToken;
    }

    private AuthResult _getCurrentAuthInfo(Context context) {
        SharedPreferences appSharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = appSharedPref.getString(Codes.ApiAuthInfoSharedPref, "");
        if (json.equals("")) {
            return null;
        }
        Type type = new TypeToken<AuthResult>(){}.getType();
        return gson.fromJson(json, type);
    }

    private void _updateCurrentUser(Context context, AuthResult authResult) {
        SharedPreferences appSharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = appSharedPref.edit();

        Gson gson = new Gson();
        String json = gson.toJson(authResult);
        prefsEditor.putString(Codes.ApiAuthInfoSharedPref, json);
        prefsEditor.commit();
    }

    public void login(Context context, ILoginListener listener, String username, String password) {
        RestaurantMapApiClient apiClient = new RestaurantMapApiClient(context);
        LoginRequest loginRequest = new LoginRequest(username, password);
        apiClient.login(loginRequest, new IClientResponseHandler<AuthResult>() {
            @Override
            public void onSuccess(AuthResult response) {
                _updateCurrentUser(context, response);
                listener.onLogin(true);
            }

            @Override
            public void onFailure(String error) {
                listener.onLogin(false);
            }
        });
    }

    public void reauth(Context context, RestaurantMapApiClient apiClient, ILoginListener listener) {
        AuthResult currentAuthInfo = this._getCurrentAuthInfo(context);
        ReauthRequest reauthRequest = new ReauthRequest(currentAuthInfo.user.username, currentAuthInfo.accessToken, currentAuthInfo.refreshToken);
        apiClient.reauth(reauthRequest, new IClientResponseHandler<AuthResult>() {
            @Override
            public void onSuccess(AuthResult response) {
                _updateCurrentUser(context, response);
                listener.onLogin(true);
            }

            @Override
            public void onFailure(String error) {
                listener.onLogin(false);
            }
        });
    }

    public AuthResult reauthSync(Context context, RestaurantMapApiClient apiClient) throws IOException, ApiException {
        if (apiClient == null) {
            apiClient = new RestaurantMapApiClient(context);
        }

        AuthResult currentAuthInfo = this._getCurrentAuthInfo(context);
        ReauthRequest reauthRequest = new ReauthRequest(currentAuthInfo.user.username, currentAuthInfo.accessToken, currentAuthInfo.refreshToken);
        AuthResult result = apiClient.reauthSync(reauthRequest);
        _updateCurrentUser(context, result);
        return result;
    }

    public void logout(Context context) {
        SharedPreferences appSharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = appSharedPref.edit();
        prefsEditor.remove(Codes.ApiAuthInfoSharedPref);
        prefsEditor.commit();
    }

    public interface ILoginListener {
        void onLogin(boolean loginSuccessful);
    }
}
