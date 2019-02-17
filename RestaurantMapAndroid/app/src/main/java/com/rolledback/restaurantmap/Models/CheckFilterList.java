package com.rolledback.restaurantmap.Models;

import android.content.Context;
import android.view.View;

import com.rolledback.restaurantmap.Views.CheckFilterItemView;
import com.rolledback.restaurantmap.Views.CheckFilterListView;

import java.util.List;
import java.util.stream.Collectors;

public class CheckFilterList implements IViewable {

    private String title;
    private List<CheckFilterItem> items;

    public CheckFilterList(String title, List<CheckFilterItem> items) {
        this.title = title;
        this.items = items;
    }

    @Override
    public View getView(Context context) {
        List<CheckFilterItemView> itemViews = this.items.stream().map(i -> i.getView(context)).collect(Collectors.toList());
        return new CheckFilterListView(context, this.title, itemViews);
    }
}
