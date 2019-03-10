package com.rolledback.restaurantmap.Filters.Views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridLayout;
import android.widget.TextView;

import com.rolledback.restaurantmap.Filters.Models.CheckFilter;
import com.rolledback.restaurantmap.R;

public class CheckFilterView extends GridLayout implements IFilterView<CheckFilter> {

    private TextView _title;
    private TextView _description;
    private CheckBox _checkBox;
    private boolean _alwaysShow;
    private IFilterViewChangeListener _listener;

    public CheckFilterView(Context context) {
        super(context);
        this._init();
    }

    public CheckFilterView(Context context, AttributeSet attrs) {
        super(context);
        this._init();
        TypedArray attrsAsArr = context.obtainStyledAttributes(attrs, R.styleable.CheckFilterView);
        this._readAttrs(attrsAsArr);
    }

    public CheckFilterView(Context context, String title, String description, boolean checked, boolean alwaysShow) {
        super(context);
        this._init();
        this._alwaysShow = alwaysShow;
        this._setTextFields(title, description);
        this._setCheckBox(checked);
    }

    public View asView() {
        return this;
    }

    public CheckFilter asModel() {
        return new CheckFilter(this._title.getText().toString(), this._description.getText().toString(), this._checkBox.isChecked(), this._alwaysShow);
    }

    public boolean shouldAlwaysShow() {
        return this._alwaysShow || this._checkBox.isChecked();
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
        this._checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> _listener.callback());
    }

    private void _init() {
        LayoutInflater.from(getContext()).inflate(R.layout.check_filter, this);
        this._title = findViewById(R.id.title);
        this._description = findViewById(R.id.description);
        this._checkBox = findViewById(R.id.check_box);
        findViewById(R.id.grid_layout).setOnClickListener(v -> _checkBox.setChecked(!_checkBox.isChecked()));
    }

    private void _readAttrs(TypedArray attrs) {
        try {
            String title = attrs.getString(R.styleable.CheckFilterView_title);
            String description= attrs.getString(R.styleable.CheckFilterView_description);
            this._setTextFields(title, description);
        } finally {
            attrs.recycle();
        }
    }

    private void _setTextFields(String title, String description) {
        this._title.setText(title);
        if (description != null && description.length() > 0) {
            this._description.setText(description);
        } else {
            this._description.setVisibility(GONE);
        }
    }

    private void _setCheckBox(boolean checked) {
        this._checkBox.setChecked(checked);
    }
}
