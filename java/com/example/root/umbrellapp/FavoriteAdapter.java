package com.example.root.umbrellapp;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class FavoriteAdapter extends BaseAdapter {
    public final ArrayList<String> mItems = new ArrayList<String>();
    private final Context mContext;
    private Favorite undoFav;
    private DBHandler db;
    private CoordinatorLayout mCoord;

    public FavoriteAdapter(Context context, CoordinatorLayout coord) {
        mContext = context;
        mCoord = coord;
        db = new DBHandler(context);
    }

    public void add(String item) {
        mItems.add(item);
        notifyDataSetChanged();
    }

    public void remove(int pos) {
        mItems.remove(pos);
        notifyDataSetChanged();
    }

    public void clear() {
        mItems.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int pos) {
        return mItems.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    private static class ViewHolder {
        LinearLayout linear;
        TextView location;
        ImageView delete;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final String locationText = (String) getItem(position);
        int deleteIcon, textColor, linearColor;
        View vi = convertView;
        final ViewHolder holder;

        if (convertView == null) {
            vi = LayoutInflater.from(mContext).inflate(R.layout.favorite, parent, false);

            holder = new ViewHolder();
            holder.linear = (LinearLayout) vi.findViewById(R.id.favorite);
            holder.location  = (TextView) vi.findViewById(R.id.locationView);
            holder.delete  = (ImageView) vi.findViewById(R.id.deleteView);

            vi.setTag(holder);
        } else
            holder = (ViewHolder) vi.getTag();

        if (position % 2 == 0) {
            linearColor = R.drawable.ic_favorite_white_button;
            textColor = R.color.holyPurple;
            deleteIcon = R.drawable.ic_vector_x_gray;
        } else {
            linearColor = R.drawable.ic_favorite_purple_button;
            textColor = R.color.holyGreen;
            deleteIcon = R.drawable.ic_vector_x_purple_dark;
        }

        holder.linear.setBackgroundResource(linearColor);

        holder.location.setText(locationText);
        Typeface font = Typeface.createFromAsset(mContext.getAssets(), "Biko_Regular.otf");
        holder.location.setTypeface(font);
        holder.location.setTextColor(mContext.getResources().getColor(textColor));

        holder.delete.setImageResource(deleteIcon);
        holder.delete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                undoFav = db.getFavorite(locationText);
                db.deleteFavorite(locationText);
                remove(position);

                Snackbar sna = Snackbar.make(mCoord, "", Snackbar.LENGTH_INDEFINITE);
                sna.getView().setBackgroundColor(mContext.getResources().getColor(R.color.holySomon));
                ((TextView) sna.getView().findViewById(android.support.design.R.id.snackbar_action)).setTextColor(Color.WHITE);
                sna.setAction(R.string.fav_undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        db.addFavorite(undoFav.getName(), "", undoFav.getDate(), undoFav.getMax(), undoFav.getMin(), undoFav.getIcon());
                        add(undoFav.getName());
                    }
                }).show();
            }
        });

        return vi;
    }
}