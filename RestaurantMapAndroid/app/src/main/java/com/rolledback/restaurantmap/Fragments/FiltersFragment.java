package com.rolledback.restaurantmap.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.rolledback.restaurantmap.Filters.IFiltersChangedListener;
import com.rolledback.restaurantmap.Filters.Models.IViewableFilter;
import com.rolledback.restaurantmap.Filters.Views.IFilterView;
import com.rolledback.restaurantmap.Filters.Views.Separator;
import com.rolledback.restaurantmap.R;

import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.Map;

import androidx.fragment.app.Fragment;

public class FiltersFragment extends Fragment {
    private IFiltersChangedListener _filtersChangedListener;
    private LinearLayout _linearLayout;
    private LinkedHashMap<String, IFilterView> _filterViews;

    public FiltersFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_filters, container, false);
        this._linearLayout = view.findViewById(R.id.linear_layout);
        this._filterViews = new LinkedHashMap<>();

        LinkedHashMap<String, IViewableFilter> filters = (LinkedHashMap<String, IViewableFilter>)getArguments().getSerializable("filters");

        int mapSize = filters.size();
        int i = 0;

        Iterator fIt = filters.entrySet().iterator();
        while (fIt.hasNext()) {
            Map.Entry<String, IViewableFilter> pair = (Map.Entry)fIt.next();
            IViewableFilter filter = pair.getValue();
            IFilterView filterView = filter.getView(getContext());
            this._linearLayout.addView(filterView.asView());
            this._filterViews.put(pair.getKey(), filterView);
            if (i != mapSize - 1) {
                this._linearLayout.addView(new Separator(getContext()));
            }
            i++;
        }

        Iterator fvIt = _filterViews.entrySet().iterator();
        while (fvIt.hasNext()) {
            Map.Entry<String, IFilterView> pair = (Map.Entry)fvIt.next();
            IFilterView filterView = pair.getValue();
            filterView.setChangeListener(() -> this._sendChangedFilters());
        }

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof IFiltersChangedListener) {
            _filtersChangedListener = (IFiltersChangedListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        _filtersChangedListener = null;
    }

    private boolean _sendChangedFilters() {
        LinkedHashMap<String, IViewableFilter> newFilters = new LinkedHashMap<String, IViewableFilter>();

        Iterator fvIt = _filterViews.entrySet().iterator();
        while (fvIt.hasNext()) {
            Map.Entry<String, IFilterView> pair = (Map.Entry)fvIt.next();
            IFilterView filterView = pair.getValue();
            newFilters.put(pair.getKey(), filterView.asModel());
        }

        _filtersChangedListener.onFiltersChanged(newFilters);

        return true;
    }
}
