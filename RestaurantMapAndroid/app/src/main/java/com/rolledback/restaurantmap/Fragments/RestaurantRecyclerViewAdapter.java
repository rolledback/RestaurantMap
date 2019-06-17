package com.rolledback.restaurantmap.Fragments;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rolledback.restaurantmap.Fragments.FragmentInterfaces.IRestaurantListSelectListener;
import com.rolledback.restaurantmap.R;
import com.rolledback.restaurantmap.RestaurantMapAPI.Restaurant;

import java.util.List;

public class RestaurantRecyclerViewAdapter extends RecyclerView.Adapter<RestaurantRecyclerViewAdapter.ViewHolder> {

    private final List<Restaurant> _values;
    private final IRestaurantListSelectListener _listener;

    public RestaurantRecyclerViewAdapter(List<Restaurant> items, IRestaurantListSelectListener listener) {
        _values = items;
        _listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_restaurant, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.item = _values.get(position);
        holder.name.setText(_values.get(position).name);
        holder.subtitle.setText(_values.get(position).genre + ", " + _values.get(position).rating);

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != _listener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    _listener.onRestaurantSelected(holder.item);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return _values.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView name;
        public final TextView subtitle;
        public Restaurant item;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            this.name = view.findViewById(R.id.restaurant_item_name);
            this.subtitle = view.findViewById(R.id.restaurant_item_subtitle);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + name.getText() + "'";
        }
    }
}
