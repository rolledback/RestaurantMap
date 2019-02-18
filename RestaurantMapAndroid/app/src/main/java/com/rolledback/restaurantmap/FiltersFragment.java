package com.rolledback.restaurantmap;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.rolledback.restaurantmap.Models.CheckFilterItem;
import com.rolledback.restaurantmap.Models.FilterItemList;
import com.rolledback.restaurantmap.Models.IViewableFilter;
import com.rolledback.restaurantmap.Models.ToggleFilterItem;
import com.rolledback.restaurantmap.Views.FilterItemListView;
import com.rolledback.restaurantmap.Views.Separator;

import java.util.Arrays;
import java.util.List;

import androidx.fragment.app.Fragment;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FiltersFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FiltersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FiltersFragment extends Fragment {
    private OnFragmentInteractionListener mListener;
    private LinearLayout linearLayout;

    public FiltersFragment() {
        // Required empty public constructor
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

        List<IViewableFilter> ratingFilterItems = Arrays.asList(
                new CheckFilterItem(getText(R.string.best_rating_title).toString(), getText(R.string.best_rating_description).toString()),
                new CheckFilterItem(getText(R.string.better_rating_title).toString(), getText(R.string.better_rating_description).toString()),
                new CheckFilterItem(getText(R.string.good_rating_title).toString(), getText(R.string.good_rating_description).toString()),
                new CheckFilterItem(getText(R.string.ok_rating_title).toString(), getText(R.string.ok_rating_description).toString()),
                new CheckFilterItem(getText(R.string.meh_rating_title).toString(), getText(R.string.meh_rating_description).toString()),
                new CheckFilterItem(getText(R.string.want_rating_title).toString(), getText(R.string.want_rating_description).toString())
        );
        FilterItemList ratingFilter = new FilterItemList(getText(R.string.rating_filter_title).toString(), ratingFilterItems);

        List<IViewableFilter> otherFilterItems = Arrays.asList(
                new ToggleFilterItem(getText(R.string.visited_title).toString(), getText(R.string.visited_description).toString()),
                new ToggleFilterItem(getText(R.string.single_title).toString(), getText(R.string.single_description).toString())
        );
        FilterItemList otherFilters = new FilterItemList(getText(R.string.other_filters_title).toString(), otherFilterItems);

        this.linearLayout.addView(ratingFilter.getView(getContext()));
        this.linearLayout.addView(new Separator(getContext()));
        this.linearLayout.addView(otherFilters.getView(getContext()));
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
    }
}
