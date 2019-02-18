package com.rolledback.restaurantmap.Models;

import android.content.Context;
import com.rolledback.restaurantmap.Views.CheckFilterItemView;

public class CheckFilterItem implements IViewableFilter {

    private String _title;
    private String _description;

    public CheckFilterItem(String title, String description) {
        this._title = title;
        this._description = description;
    }

    public CheckFilterItemView getView(Context context) {
        return new CheckFilterItemView(context, this._title, this._description);
    }
}
