package com.rolledback.restaurantmap.Views;

import android.content.Context;
import android.widget.LinearLayout;

import com.rolledback.restaurantmap.Models.IViewableFilter;

import java.util.ArrayList;

public class FilterListView extends LinearLayout {
    public FilterListView(Context context, ArrayList<IViewableFilter> filters) {
        super(context);
    }
}
