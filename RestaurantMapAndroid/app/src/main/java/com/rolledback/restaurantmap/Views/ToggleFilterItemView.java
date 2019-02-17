package com.rolledback.restaurantmap.Views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.GridLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.rolledback.restaurantmap.R;

public class ToggleFilterItemView extends GridLayout {

    private TextView title;
    private TextView description;
    private Switch toggle;

    public ToggleFilterItemView(Context context) {
        super(context);
        this._init();
    }

    public ToggleFilterItemView(Context context, AttributeSet attrs) {
        super(context);
        this._init();
        TypedArray attrsAsArr = context.obtainStyledAttributes(attrs, R.styleable.ToggleFilterItemView);
        this._readAttrs(attrsAsArr);
    }

    public ToggleFilterItemView(Context context, String title, String description) {
        super(context);
        this._init();
        this._setTextFields(title, description);
    }

    private void _init() {
        LayoutInflater.from(getContext()).inflate(R.layout.toggle_filter_item, this);
        this.title = findViewById(R.id.title);
        this.description =findViewById(R.id.description);
        this.toggle = findViewById(R.id.toggle);
        findViewById(R.id.grid_layout).setOnClickListener(v -> toggle.setChecked(!toggle.isChecked()));
    }

    private void _readAttrs(TypedArray attrs) {
        try {
            String title = attrs.getString(R.styleable.ToggleFilterItemView_title);
            String description = attrs.getString(R.styleable.ToggleFilterItemView_description);
            this._setTextFields(title, description);
        } finally {
            attrs.recycle();
        }
    }

    private void _setTextFields(String title, String description) {
        this.title.setText(title);
        this.description.setText(description);
    }
}
