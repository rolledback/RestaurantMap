package com.rolledback.restaurantmap.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.rolledback.restaurantmap.Lib.AppState;
import com.rolledback.restaurantmap.R;
import com.rolledback.restaurantmap.RestaurantMapAPI.AccountManager;
import com.rolledback.restaurantmap.RestaurantMapAPI.User;

public class AccountFragment extends MainFragment {

    private ImageView _profilePicView;

    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        this._profilePicView = view.findViewById(R.id.profilePic);
        User currentUser = AccountManager.getInstance().currentUser(this.getContext());

        String firstChar = currentUser.username.substring(0, 1).toUpperCase();
        TextDrawable drawable = TextDrawable.builder().buildRound(firstChar, getResources().getColor(R.color.colorAccent));

        this._profilePicView.setImageDrawable(drawable);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public boolean shouldShowAddButton(AppState currState) {
        return false;
    }

    @Override
    public boolean shouldShowFilterButton(AppState currState) {
        return false;
    }
}
