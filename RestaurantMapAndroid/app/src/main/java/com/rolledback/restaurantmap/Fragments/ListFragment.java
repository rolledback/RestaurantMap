package com.rolledback.restaurantmap.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rolledback.restaurantmap.Filters.Models.IViewableFilter;
import com.rolledback.restaurantmap.FragmentInterfaces.IRestaurantListSelectListener;
import com.rolledback.restaurantmap.Lib.AppState;
import com.rolledback.restaurantmap.Lib.FilterEvaluator;
import com.rolledback.restaurantmap.R;
import com.rolledback.restaurantmap.RestaurantMapAPI.Restaurant;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

public class ListFragment extends MainFragment {

    private ArrayList<Restaurant> _restaurants;
    private IRestaurantListSelectListener _listener;
    private RecyclerView _recyclerView;
    private LinkedHashMap<String, IViewableFilter> _filters;

    public ListFragment() {
        this._restaurants = new ArrayList<>();
        this._filters = new LinkedHashMap<>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_restaurant_list, container, false);
        Context context = view.getContext();
        _recyclerView = (RecyclerView) view;
        _recyclerView.setLayoutManager(new LinearLayoutManager(context));
        _recyclerView.addItemDecoration(new DividerItemDecoration(_recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        setUpAdapter();
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof IRestaurantListSelectListener) {
            _listener = (IRestaurantListSelectListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement IRestaurantListSelectListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        _listener = null;
    }

    public void setRestaurants(List<Restaurant> restaurants) {
        restaurants.sort(new Comparator<Restaurant>() {
            @Override
            public int compare(Restaurant r1, Restaurant r2) {
                return  r1.name.compareTo(r2.name);
            }
        });
        this._restaurants = new ArrayList<>(restaurants);
        setUpAdapter();
    }

    public void setFilters(LinkedHashMap<String, IViewableFilter> filters) {
        this._filters = filters;
        setUpAdapter();
    }

    private void setUpAdapter() {
        ArrayList<Restaurant> restaurantsToView = new ArrayList<>();
        Iterator<Restaurant> iterator = this._restaurants.iterator();
        while(iterator.hasNext()) {
            Restaurant curr = iterator.next();
            if (FilterEvaluator.shouldShow(curr, this._filters)) {
                restaurantsToView.add(curr);
            }
        }
        _recyclerView.setAdapter(new RestaurantRecyclerViewAdapter(restaurantsToView, _listener));
    }

    @Override
    public boolean shouldShowAddButton(AppState currState) {
        return false;
    }

    @Override
    public boolean shouldShowFilterButton(AppState currState) {
        return currState.restaurantsLoaded;
    }
}
