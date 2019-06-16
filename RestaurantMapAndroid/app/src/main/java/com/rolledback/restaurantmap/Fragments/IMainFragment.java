package com.rolledback.restaurantmap.Fragments;

import com.rolledback.restaurantmap.Lib.AppState;

public interface IMainFragment {
    boolean shouldShowAddButton(AppState currState);
    boolean shouldShowFilterButton(AppState currState);
}
