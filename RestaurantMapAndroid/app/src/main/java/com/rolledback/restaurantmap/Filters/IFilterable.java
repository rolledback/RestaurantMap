package com.rolledback.restaurantmap.Filters;

import com.rolledback.restaurantmap.Filters.Models.IViewableFilter;

import java.util.LinkedHashMap;

public interface IFilterable {
    void applyFilters(LinkedHashMap<String, IViewableFilter> filters);
}
