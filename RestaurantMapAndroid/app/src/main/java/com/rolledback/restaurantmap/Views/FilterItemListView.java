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
    TextView toggleShowMoreTextView;
    private List<View> _itemViews;

    private boolean _containsHideable;
    private boolean _showingMore;

    public FilterItemListView(Context context, String title, List<View> itemViews) {
        super(context);

        LayoutInflater.from(getContext()).inflate(R.layout.filter_item_list, this);
        this.title = findViewById(R.id.title);
        this.title.setText(title);
        this._itemViews = itemViews;

        this._containsHideable = false;
        this._showingMore = false;

        this.linearLayout = findViewById(R.id.filter_item_list_linear_layout);
        this._itemViews.forEach(iV -> {
            this.linearLayout.addView(iV, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            iV.requestLayout();
            if (iV instanceof IHideable) {
                this._containsHideable = true;
                if (((IHideable) iV).shouldAlwaysShow()) {
                    ((IHideable) iV).show();
                } else {
                    ((IHideable) iV).hide();
                }
            }
        });

        if (this._containsHideable) {
            toggleShowMoreTextView = (TextView)LayoutInflater.from(getContext()).inflate(R.layout.show_more_text, null);
            toggleShowMoreTextView.setText("Show more");
            this.linearLayout.addView(toggleShowMoreTextView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            toggleShowMoreTextView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    _toggleShowMore();
                }
            });
        }
    }

    private void _toggleShowMore() {
        this._showingMore = !this._showingMore;

        if (this._showingMore) {
            toggleShowMoreTextView.setText("Show less");
        } else {
            toggleShowMoreTextView.setText("Show more");
        }

        this._itemViews.forEach(iV -> {
            if (iV instanceof IHideable) {
                if (this._showingMore) {
                    ((IHideable) iV).show();
                } else if (!this._showingMore && !((IHideable) iV).shouldAlwaysShow()) {
                    ((IHideable) iV).hide();
                }
            }
        });
    }
}
