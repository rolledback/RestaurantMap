package com.rolledback.restaurantmap.Activities;

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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link IFiltersChangedListener} interface
 * to handle interaction events.
 * Use the {@link FiltersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FiltersFragment extends Fragment {
    private IFiltersChangedListener filtersChangedListener;
    private LinearLayout linearLayout;
    private LinkedHashMap<String, IFilterView> filterViews;

    public FiltersFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FiltersFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FiltersFragment newInstance() {
        FiltersFragment fragment = new FiltersFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_filters, container, false);
        this.linearLayout = view.findViewById(R.id.linear_layout);
        this.filterViews = new LinkedHashMap<>();

        LinkedHashMap<String, IViewableFilter> filters = (LinkedHashMap<String, IViewableFilter>)getArguments().getSerializable("filters");

        int mapSize = filters.size();
        int i = 0;

        Iterator fIt = filters.entrySet().iterator();
        while (fIt.hasNext()) {
            Map.Entry<String, IViewableFilter> pair = (Map.Entry)fIt.next();
            IViewableFilter filter = pair.getValue();
            IFilterView filterView = filter.getView(getContext());
            this.linearLayout.addView(filterView.asView());
            this.filterViews.put(pair.getKey(), filterView);
            if (i != mapSize - 1) {
                this.linearLayout.addView(new Separator(getContext()));
            }
            i++;
        }

        Iterator fvIt = filterViews.entrySet().iterator();
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
            filtersChangedListener = (IFiltersChangedListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        filtersChangedListener = null;
    }

    private boolean _sendChangedFilters() {
        LinkedHashMap<String, IViewableFilter> newFilters = new LinkedHashMap<String, IViewableFilter>();

        Iterator fvIt = filterViews.entrySet().iterator();
        while (fvIt.hasNext()) {
            Map.Entry<String, IFilterView> pair = (Map.Entry)fvIt.next();
            IFilterView filterView = pair.getValue();
            newFilters.put(pair.getKey(), filterView.asModel());
        }

        filtersChangedListener.onFiltersChanged(newFilters);

        return true;
    }
}
