package com.rolledback.restaurantmap.Filters.Views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.GridLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.rolledback.restaurantmap.Filters.Models.ToggleFilter;
import com.rolledback.restaurantmap.R;

public class ToggleFilterView extends GridLayout implements IFilterView<ToggleFilter> {

    private TextView _title;
    private TextView _description;
    private Switch _toggle;
    private boolean _alwaysShow;
    private IFilterViewChangeListener _listener;

    public ToggleFilterView(Context context) {
        super(context);
        this._init();
    }

    public ToggleFilterView(Context context, AttributeSet attrs) {
        super(context);
        this._init();
        TypedArray attrsAsArr = context.obtainStyledAttributes(attrs, R.styleable.ToggleFilterView);
        this._readAttrs(attrsAsArr);
    }

    public ToggleFilterView(Context context, String title, String description, boolean checked, boolean alwaysShow) {
        super(context);
        this._init();
        this._setTextFields(title, description);
        this._setToggle(checked);
        this._alwaysShow = alwaysShow;
    }

    public View asView() {
        return this;
    }

    public ToggleFilter asModel() {
        return new ToggleFilter(this._title.getText().toString(), this._description.getText().toString(), this._toggle.isChecked(), this._alwaysShow);
    }

    public boolean shouldAlwaysShow() {
        return this._alwaysShow || this._toggle.isChecked();
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

    public void setChangeListener(IFilterViewChangeListener listener) {
        this._listener = listener;
        this._toggle.setOnCheckedChangeListener((buttonView, isChecked) -> _listener.callback());
    }

    private void _init() {
        LayoutInflater.from(getContext()).inflate(R.layout.toggle_filter, this);
        this._title = findViewById(R.id.title);
        this._description =findViewById(R.id.description);
        this._toggle = findViewById(R.id.toggle);
        findViewById(R.id.grid_layout).setOnClickListener(v -> _toggle.setChecked(!_toggle.isChecked()));
    }

    private void _readAttrs(TypedArray attrs) {
        try {
            String title = attrs.getString(R.styleable.ToggleFilterView_title);
            String description = attrs.getString(R.styleable.ToggleFilterView_description);
            this._setTextFields(title, description);
        } finally {
            attrs.recycle();
        }
    }

    private void _setTextFields(String title, String description) {
        this._title.setText(title);
        this._description.setText(description);
    }

    private void _setToggle(boolean checked) {
        this._toggle.setChecked(checked);
    }
}
