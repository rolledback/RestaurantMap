package com.rolledback.restaurantmap.Views;

public interface IHideable {
    boolean shouldAlwaysShow();
    void show();
    void hide();
}
