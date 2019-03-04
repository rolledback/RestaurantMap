package com.rolledback.restaurantmap.Filters;

import com.rolledback.restaurantmap.Filters.Models.IViewableFilter;

import java.util.ArrayList;

public interface IFilterable {
    void applyFilters(ArrayList<IViewableFilter> filters);
    ArrayList<IViewableFilter> getCurrentFilters();
}
