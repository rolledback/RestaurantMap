package com.rolledback.restaurantmap.Views;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;

import com.rolledback.restaurantmap.R;

public class Separator extends View {
    public Separator(Context context) {
        super(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, (int)getResources().getDimension(R.dimen.separator_height));
        int margins = (int)getResources().getDimension(R.dimen.separator_margin);
        params.setMargins(margins, margins, margins, margins);
        this.setLayoutParams(params);
        this.setBackgroundColor(Color.parseColor("#595959"));
    }
}
