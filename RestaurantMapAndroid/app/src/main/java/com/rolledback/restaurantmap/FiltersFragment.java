package com.rolledback.restaurantmap;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.rolledback.restaurantmap.Filters.IFiltersChangedListener;
import com.rolledback.restaurantmap.Filters.Models.IViewableFilter;
import com.rolledback.restaurantmap.Filters.Views.IFilterView;
import com.rolledback.restaurantmap.Filters.Views.Separator;

import java.util.ArrayList;
import java.util.List;

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
    private List<IFilterView> filterViews;

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
        this.filterViews = new ArrayList<>();

        List<IViewableFilter> filters = (List<IViewableFilter>)getArguments().get("filters");

        for (int i = 0; i < filters.size(); i++) {
            IViewableFilter filter = filters.get(i);
            IFilterView filterView = filter.getView(getContext());
            this.linearLayout.addView(filterView.asView());
            this.filterViews.add(filterView);

            filterView.setChangeListener(() -> {
                ArrayList<IViewableFilter> newFilters = new ArrayList<IViewableFilter>();
                for (int j = 0; j < this.filterViews.size(); j++) {
                    newFilters.add(this.filterViews.get(j).asModel());
                }
                filtersChangedListener.onFiltersChanged(newFilters);
                return true;
            });
            if (i != this.filterViews.size() - 1) {
                this.linearLayout.addView(new Separator(getContext()));
            }
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
}
