package com.rolledback.restaurantmap.Filters.Models;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Pair;

import com.rolledback.restaurantmap.Filters.Views.ToggleFilterView;

public class ToggleFilter implements IViewableFilter<Pair<String, Boolean>> {
    private String _title;
    private String _description;
    private boolean _checked;
    private boolean _alwaysShow;

    public ToggleFilter(String title, String description, boolean checked, boolean alwaysShow) {
        this._title = title;
        this._description = description;
        this._checked = checked;
        this._alwaysShow = alwaysShow;
    }

    public ToggleFilterView getView(Context context) {
        return new ToggleFilterView(context, this._title, this._description, this._checked, this._alwaysShow);
    }

    @Override
    public Pair<String, Boolean> getValue() {
        return new Pair<String, Boolean>(this._title, this._checked);
    }

    /**
     * Parcelable implementation.
     */
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public ToggleFilter createFromParcel(Parcel in) {
            return new ToggleFilter(in);
        }

        public ToggleFilter[] newArray(int size) {
            return new ToggleFilter[size];
        }
    };

    public ToggleFilter(Parcel in) {
        this._title = in.readString();
        this._description = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this._title);
        dest.writeString(this._description);
    }

    public String getTitle() {
        return this._title;
    }
}
