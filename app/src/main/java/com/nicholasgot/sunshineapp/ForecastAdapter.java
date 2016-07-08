package com.nicholasgot.sunshineapp;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nicholasgot.sunshineapp.data.WeatherContract;

/**
 * {@link ForecastAdapter} exposes a list of weather forecasts
 * from a {@link android.database.Cursor} to a {@link android.widget.ListView}.
 */
public class ForecastAdapter extends CursorAdapter {

    private static final int VIEW_TYPE_TODAY = 0;
    private static final int VIEW_TYPE_FUTURE_DAY = 1;

    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    /**
     * Class to holder views so we don't have to call findViewById every time inside
     * bind View; improves performance
     */
    static class ViewHolder {
        ImageView iconView;
        TextView dateView;
        TextView weatherView;
        TextView highView;
        TextView lowView;
    }

    /*
        Remember that these views are reused as needed.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Choose the layout type
        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = (viewType == VIEW_TYPE_TODAY) ?
                R.layout.list_item_forecast_today : R.layout.list_item_forecast;

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        /**
         * Stores resource IDs in View Holder to prevent having to look them up each time
         * bindView is called
         */
        ViewHolder holder = new ViewHolder();
        holder.iconView = (ImageView) view.findViewById(R.id.list_item_icon);
        holder.dateView = (TextView) view.findViewById(R.id.list_item_date_textview);
        holder.weatherView = (TextView) view.findViewById(R.id.list_item_forecast_textview);
        holder.highView = (TextView) view.findViewById(R.id.list_item_high_textview);
        holder.lowView = (TextView) view.findViewById(R.id.list_item_low_textview);
        view.setTag(holder);

        return view;
    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // our view is pretty simple here --- just a text view
        // we'll keep the UI functional with a simple (and slow!) binding.

        ViewHolder holder = (ViewHolder) view.getTag();

        int weatherID = cursor.getInt(ForecastFragment.COL_WEATHER_ID);
        holder.iconView.setImageResource(R.drawable.ic_wb_sunny_blue_a100_24dp);

        // TODO: read date from cursor
        long date = cursor.getLong(ForecastFragment.COL_WEATHER_DATE);
        holder.dateView.setText(Utility.displayFriendlyDayString(context, date));

        // TODO: read weather forecast from cursor
        String weather = cursor.getString(ForecastFragment.COL_WEATHER_DESC);
        holder.weatherView.setText(weather);

        // read user preferences for units
        boolean isMetric = Utility.isMetric(context);

        // Read high temperature from cursor
        double high = cursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP);
        holder.highView.setText(Utility.formatTemperature(context, high, isMetric));

        // Read low temperature from cursor
        double low = cursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP);
        holder.lowView.setText(Utility.formatTemperature(context, low, isMetric));
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0) ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }
}