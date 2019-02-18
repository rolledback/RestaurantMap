package com.rolledback.restaurantmap.Views;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.GridLayout;
import android.widget.TextView;

import com.rolledback.restaurantmap.R;

import androidx.recyclerview.widget.GridLayoutManager;

public class CheckFilterItemView extends GridLayout implements IHideable {

    private TextView title;
    private TextView description;
    private CheckBox checkBox;
    private boolean _alwaysShow;

    public CheckFilterItemView(Context context) {
        super(context);
        this._init();
    }

    public CheckFilterItemView(Context context, AttributeSet attrs) {
        super(context);
        this._init();
        TypedArray attrsAsArr = context.obtainStyledAttributes(attrs, R.styleable.CheckFilterItemView);
        this._readAttrs(attrsAsArr);
    }

    public CheckFilterItemView(Context context, String title, String description, boolean alwaysShow) {
        super(context);
        this._init();
        this._alwaysShow = alwaysShow;
        this._setTextFields(title, description);
    }

    public boolean shouldAlwaysShow() {
        return this._alwaysShow || this.checkBox.isChecked();
    }

    public void show() {
        if (this.getVisibility() != VISIBLE) {
            this.setVisibility(VISIBLE);
            this.requestLayout();
        }
    }

    public void hide() {
        if (this.getVisibility() != GONE) {
            this.setVisibility(GONE);
            this.requestLayout();
        }
    }

    private void _init() {
        LayoutInflater.from(getContext()).inflate(R.layout.check_filter_item, this);
        this.title = findViewById(R.id.title);
        this.description = findViewById(R.id.description);
        this.checkBox = findViewById(R.id.check_box);
        findViewById(R.id.grid_layout).setOnClickListener(v -> checkBox.setChecked(!checkBox.isChecked()));
    }

    private void _readAttrs(TypedArray attrs) {
        try {
            String title = attrs.getString(R.styleable.CheckFilterItemView_title);
            String description= attrs.getString(R.styleable.CheckFilterItemView_description);
            this._setTextFields(title, description);
        } finally {
            attrs.recycle();
        }
    }

    private void _setTextFields(String title, String description) {
        this.title.setText(title);
        if (description != null && description.length() > 0) {
            this.description.setText(description);
        } else {
            this.description.setVisibility(GONE);
        }
    }
}
