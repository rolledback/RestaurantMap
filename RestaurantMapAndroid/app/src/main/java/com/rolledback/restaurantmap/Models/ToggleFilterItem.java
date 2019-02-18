package com.rolledback.restaurantmap.Models;

import android.content.Context;

import com.rolledback.restaurantmap.Views.ToggleFilterItemView;

public class ToggleFilterItem implements IViewableFilter {

    private String _title;
    private String _description;

    public ToggleFilterItem(String title, String description) {
        this._title = title;
        this._description = description;
    }

    public ToggleFilterItemView getView(Context context) {
        return new ToggleFilterItemView(context, this._title, this._description);
    }
}
