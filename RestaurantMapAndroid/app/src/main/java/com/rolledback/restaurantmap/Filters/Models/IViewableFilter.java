package com.rolledback.restaurantmap.Filters.Models;

import android.content.Context;
import android.os.Parcelable;
import android.view.View;

public interface IViewableFilter extends Parcelable {
    View getView(Context context);
}
