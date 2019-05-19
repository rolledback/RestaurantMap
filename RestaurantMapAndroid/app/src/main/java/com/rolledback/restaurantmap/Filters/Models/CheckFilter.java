package com.rolledback.restaurantmap.Filters.Models;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Pair;

import com.rolledback.restaurantmap.Filters.Views.CheckFilterView;

public class CheckFilter implements IViewableFilter<Pair<String, Boolean>> {

    private String _title;
    private String _description;
    private boolean _checked;
    private boolean _alwaysShow;

    public CheckFilter(String title, String description, boolean checked, boolean alwaysShow) {
        this._title = title;
        this._description = description;
        this._checked = checked;
        this._alwaysShow = alwaysShow;
    }

    public CheckFilterView getView(Context context) {
        return new CheckFilterView(context, this._title, this._description, this._checked, this._alwaysShow);
    }

    public String getTitle() {
        return this._title;
    }

    @Override
    public Pair<String, Boolean> getValue() {
        return new Pair<String, Boolean>(this._title, this._checked);
    }

    /**
     * Parcelable implementation.
     */
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public CheckFilter createFromParcel(Parcel in) {
            return new CheckFilter(in);
        }

        public CheckFilter[] newArray(int size) {
            return new CheckFilter[size];
        }
    };

    public CheckFilter(Parcel in) {
        this._title = in.readString();
        this._description = in.readString();
        this._alwaysShow = in.readByte() == (byte)1;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this._title);
        dest.writeString(this._description);
        dest.writeByte(this._alwaysShow ? (byte)1 : (byte)0);
    }
}
