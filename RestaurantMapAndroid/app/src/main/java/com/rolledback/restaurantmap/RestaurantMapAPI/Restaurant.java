package com.rolledback.restaurantmap.RestaurantMapAPI;

import java.util.ArrayList;
import java.util.List;

public class Restaurant {
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
}
