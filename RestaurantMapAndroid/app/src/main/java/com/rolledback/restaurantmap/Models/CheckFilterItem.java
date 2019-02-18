package com.rolledback.restaurantmap.Models;

import android.content.Context;
import com.rolledback.restaurantmap.Views.CheckFilterItemView;

public class CheckFilterItem implements IViewableFilter {

    private String _title;
    private String _description;
    private boolean _alwaysShow;

    public CheckFilterItem(String title, String description, boolean alwaysShow) {
        this._title = title;
        this._description = description;
        this._alwaysShow = alwaysShow;
    }

    public CheckFilterItemView getView(Context context) {
        return new CheckFilterItemView(context, this._title, this._description, this._alwaysShow);
    }
}
