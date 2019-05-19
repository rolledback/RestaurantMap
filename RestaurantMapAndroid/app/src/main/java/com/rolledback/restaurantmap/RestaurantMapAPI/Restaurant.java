package com.rolledback.restaurantmap.RestaurantMapAPI;

import android.os.Parcel;
import android.os.Parcelable;

import com.rolledback.restaurantmap.Filters.Models.CheckFilter;

import java.util.ArrayList;
import java.util.List;

public class Restaurant implements Parcelable {
    public String name;
    public String genre;
    public String subGenre;
    public String description;
    public String rating;
    public Location location;
    public List<String> reviewSites;
    public boolean hasOtherLocations;

    public Restaurant() {
        this.name = "";
        this.genre = "";
        this.subGenre = "";
        this.description = "";
        this.rating = "";
        this.location = new Location();
        this.reviewSites = new ArrayList<>();
        this.hasOtherLocations = false;
    }

    /**
     * Parcelable implementation.
     */
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Restaurant createFromParcel(Parcel in) {
            return new Restaurant(in);
        }

        public Restaurant[] newArray(int size) {
            return new Restaurant[size];
        }
    };

    public Restaurant(Parcel in) {
        this.name = in.readString();
        this.genre = in.readString();
        this.subGenre = in.readString();
        this.description = in.readString();
        this.rating = in.readString();
        this.location = in.readParcelable(Location.class.getClassLoader());
        if (this.reviewSites == null) {
            this.reviewSites = new ArrayList<>();
        }
        in.readList(this.reviewSites, List.class.getClassLoader());
        this.hasOtherLocations = in.readByte() == (byte)1;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.genre);
        dest.writeString(this.subGenre);
        dest.writeString(this.description);
        dest.writeString(this.rating);
        dest.writeParcelable(this.location, 0);
        dest.writeList(this.reviewSites);
        dest.writeByte(this.hasOtherLocations ? (byte)1 : (byte)0);
    }
}
