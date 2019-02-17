package com.rolledback.restaurantmap.Views;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rolledback.restaurantmap.R;

import java.util.List;

public class CheckFilterListView extends LinearLayout {

    private TextView title;

    public CheckFilterListView(Context context, String title, List<CheckFilterItemView> itemViews) {
        super(context);

        LayoutInflater.from(getContext()).inflate(R.layout.check_filter_list, this);
        this.title = findViewById(R.id.title);
        this.title.setText(title);
        itemViews.forEach(i -> this.addView(i, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)));
    }
}
