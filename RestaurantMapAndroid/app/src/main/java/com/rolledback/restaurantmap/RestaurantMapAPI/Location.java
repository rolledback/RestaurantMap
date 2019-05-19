package com.rolledback.restaurantmap.RestaurantMapAPI;

import android.os.Parcel;
import android.os.Parcelable;

public class Location implements Parcelable {
    public String address;
    public double lat;
    public double lng;

    public Location() {
        this.address = "";
        this.lat = 0;
        this.lng = 0;
    }

    /**
     * Parcelable implementation.
     */
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Location createFromParcel(Parcel in) {
            return new Location(in);
        }

        public Location[] newArray(int size) {
            return new Location[size];
        }
    };

    public Location(Parcel in) {
        this.address = in.readString();
        this.lat = in.readDouble();
        this.lng = in.readDouble();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.address);
        dest.writeDouble(this.lat);
        dest.writeDouble(this.lng);
    }
}
