package com.rolledback.restaurantmap.Models;

import android.content.Context;
import android.view.View;

import com.rolledback.restaurantmap.Views.FilterItemListView;

import java.util.List;
import java.util.stream.Collectors;

public class FilterItemList implements IViewableFilter {

    private String title;
    private List<IViewableFilter> items;

    public FilterItemList(String title, List<IViewableFilter> items) {
        this.title = title;
        this.items = items;
    }

    @Override
    public View getView(Context context) {
        List<View> itemViews = this.items.stream().map(i -> i.getView(context)).collect(Collectors.toList());
        return new FilterItemListView(context, this.title, itemViews);
    }
}
