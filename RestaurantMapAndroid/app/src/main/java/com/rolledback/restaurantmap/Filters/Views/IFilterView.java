package com.rolledback.restaurantmap.Filters.Views;

import android.view.View;

import com.rolledback.restaurantmap.Filters.Models.IViewableFilter;

public interface IFilterView<T extends IViewableFilter> {
    T asModel();
    View asView();
    boolean shouldAlwaysShow();
    void show();
    void hide();
    void setChangeListener(IFilterViewChangeListener listener);
}
