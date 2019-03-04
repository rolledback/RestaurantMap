package com.rolledback.restaurantmap.Filters.Views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rolledback.restaurantmap.Filters.Models.FilterList;
import com.rolledback.restaurantmap.Filters.Models.IViewableFilter;
import com.rolledback.restaurantmap.R;

import java.util.ArrayList;
import java.util.List;

import kotlin.NotImplementedError;

public class FilterListView<T extends IFilterView> extends LinearLayout implements IFilterView<FilterList> {

    private TextView _title;
    private LinearLayout _linearLayout;
    private TextView _toggleShowMoreTextView;
    private List<IFilterView> _children;
    private boolean _showingMore;
    private IFilterViewChangeListener _listener;


    public FilterListView(Context context, String title, List<IFilterView> children) {
        super(context);

        LayoutInflater.from(getContext()).inflate(R.layout.filter_list, this);
        this._title = findViewById(R.id.title);
        this._title.setText(title);
        this._children = children;

        this._showingMore = false;

        boolean hasHidden = false;
        this._linearLayout = findViewById(R.id.filter_item_list_linear_layout);
        for (int i = 0; i < this._children.size(); i++) {
            IFilterView child = this._children.get(i);
            this._linearLayout.addView(child.asView(), new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            child.asView().requestLayout();
            if (child.shouldAlwaysShow()) {
                child.show();
            } else {
                hasHidden = true;
                child.hide();
            }
            child.setChangeListener(() -> _listener.callback());
        }

        if (hasHidden) {
            _toggleShowMoreTextView = (TextView)LayoutInflater.from(getContext()).inflate(R.layout.show_more_text, null);
            _toggleShowMoreTextView.setText("Show more");
            this._linearLayout.addView(_toggleShowMoreTextView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            _toggleShowMoreTextView.setOnClickListener(v -> _toggleShowMore());
        }
    }

    public View asView() {
        return this;
    }

    public FilterList asModel() {
        List<IViewableFilter> checkFilters = new ArrayList<>();
        for (int i = 0; i < this._children.size(); i++) {
            checkFilters.add(this._children.get(i).asModel());
        }
        return new FilterList(this._title.getText().toString(), checkFilters);
    }

    public boolean shouldAlwaysShow() {
        return true;
    }

    public void show() {
        throw new NotImplementedError();
    }

    public void hide() {
        throw new NotImplementedError();
    }

    public void setChangeListener(IFilterViewChangeListener listener) {
        this._listener = listener;
    }

    private void _toggleShowMore() {
        this._showingMore = !this._showingMore;

        if (this._showingMore) {
            _toggleShowMoreTextView.setText("Show less");
        } else {
            _toggleShowMoreTextView.setText("Show more");
        }

        this._children.forEach(child -> {
            if (this._showingMore) {
                child.show();
            } else if (!this._showingMore && !child.shouldAlwaysShow()) {
                child.hide();
            }
        });
    }
}
