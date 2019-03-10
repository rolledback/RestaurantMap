package com.rolledback.restaurantmap.Filters;

import android.content.Context;

import com.rolledback.restaurantmap.Filters.Models.CheckFilter;
import com.rolledback.restaurantmap.Filters.Models.FilterList;
import com.rolledback.restaurantmap.Filters.Models.IViewableFilter;
import com.rolledback.restaurantmap.Filters.Models.ToggleFilter;
import com.rolledback.restaurantmap.R;
import com.rolledback.restaurantmap.Restaurant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

public class FilterManager {

    private LinkedHashMap<String, IViewableFilter> _filters;
    private Context _context;

    public FilterManager(Context context) {
        this._context = context;
        this._filters = new LinkedHashMap<>();
    }

    public void initFilters(List<Restaurant> startingRestaurants) {
        List<CheckFilter> ratingFilterItems = Arrays.asList(
                new CheckFilter(this._context.getText(R.string.best_rating_title).toString(), this._context.getText(R.string.best_rating_description).toString(), false, true),
                new CheckFilter(this._context.getText(R.string.better_rating_title).toString(), this._context.getText(R.string.better_rating_description).toString(), false, true),
                new CheckFilter(this._context.getText(R.string.good_rating_title).toString(), this._context.getText(R.string.good_rating_description).toString(), false, true),
                new CheckFilter(this._context.getText(R.string.ok_rating_title).toString(), this._context.getText(R.string.ok_rating_description).toString(), false, false),
                new CheckFilter(this._context.getText(R.string.meh_rating_title).toString(), this._context.getText(R.string.meh_rating_description).toString(), false, false),
                new CheckFilter(this._context.getText(R.string.want_rating_title).toString(), this._context.getText(R.string.want_rating_description).toString(), false, false)
        );
        FilterList ratingFilter = new FilterList(this._context.getText(R.string.rating_filter_title).toString(), ratingFilterItems);

        List<CheckFilter> genreFilterItems = new ArrayList<>();
        Set<String> seenGenres = new HashSet<>();
        for (int i = 0; i < startingRestaurants.size(); i++) {
            String genre = startingRestaurants.get(i).genre;
            if (!seenGenres.contains(genre)) {
                seenGenres.add(genre);
                genreFilterItems.add(new CheckFilter(genre, null, false, this.isAlwaysShowGenre(genre)));
            }
        }
        Collections.sort(genreFilterItems, (a, b) -> a.getTitle().compareTo(b.getTitle()));
        FilterList genreFilter = new FilterList("Genre", genreFilterItems);

        List<IViewableFilter> otherFilterItems = Arrays.asList(
                new ToggleFilter(this._context.getText(R.string.single_title).toString(), this._context.getText(R.string.single_description).toString(), false, true)
        );
        FilterList otherFilters = new FilterList(this._context.getText(R.string.other_filters_title).toString(), otherFilterItems);

        LinkedHashMap<String, IViewableFilter> filters = new LinkedHashMap<>();
        filters.put("rating", ratingFilter);
        filters.put("genre", genreFilter);
        filters.put("other", otherFilters);

        this._filters = filters;
    }

    private boolean isAlwaysShowGenre(String  genre) {
        return genre.equals("American") ||
                genre.equals("Chinese") ||
                genre.equals("Italian");
    }

    public LinkedHashMap<String, IViewableFilter> getCurrentFilters() {
        return this._filters;
    }

    public void setCurrentFilters(LinkedHashMap<String, IViewableFilter> filters) {
        this._filters = filters;
    }
}
