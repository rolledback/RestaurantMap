package com.rolledback.restaurantmap.Filters.Models;

import android.content.Context;
import android.os.Parcelable;
import android.view.View;

import com.rolledback.restaurantmap.Filters.Views.IFilterView;

public interface IViewableFilter<T> extends Parcelable {
    IFilterView getView(Context context);
    T getValue();
}
