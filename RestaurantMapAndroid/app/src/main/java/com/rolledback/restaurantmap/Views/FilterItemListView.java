package com.rolledback.restaurantmap.Views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rolledback.restaurantmap.R;

import java.util.List;

public class FilterItemListView extends LinearLayout {

    private TextView title;
    private LinearLayout linearLayout;

    public FilterItemListView(Context context, String title, List<View> itemViews) {
        super(context);

        LayoutInflater.from(getContext()).inflate(R.layout.filter_item_list, this);
        this.title = findViewById(R.id.title);
        this.title.setText(title);

        this.linearLayout = findViewById(R.id.filter_item_list_linear_layout);
        itemViews.forEach(i -> {
            this.linearLayout.addView(i, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            i.requestLayout();
        });
    }
}
