package com.rolledback.restaurantmap.Filters.Models;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.rolledback.restaurantmap.Filters.Views.FilterListView;
import com.rolledback.restaurantmap.Filters.Views.IFilterView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FilterList<U, T extends IViewableFilter<U>> implements IViewableFilter<ArrayList<U>> {

    private String _title;
    private List<T> _children;

    public FilterList(String title, List<T> items) {
        this._title = title;
        this._children = items;
    }

    @Override
    public FilterListView getView(Context context) {
        List<IFilterView> itemViews = this._children.stream().map(i -> i.getView(context)).collect(Collectors.toList());
        return new FilterListView(context, this._title, itemViews);
    }

    @Override
    public ArrayList<U> getValue() {
        ArrayList<U> retValue = new ArrayList<>();
        for (int i = 0; i < this._children.size(); i++) {
            retValue.add(this._children.get(i).getValue());
        }
        return retValue;
    }

    /**
     * Parcelable implementation.
     */
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public FilterList createFromParcel(Parcel in) {
            return new FilterList(in);
        }

        public FilterList[] newArray(int size) {
            return new FilterList[size];
        }
    };

    public FilterList(Parcel in) {
        this._title = in.readString();
        in.readList(this._children, List.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this._title);
        dest.writeList(this._children);
    }
}
