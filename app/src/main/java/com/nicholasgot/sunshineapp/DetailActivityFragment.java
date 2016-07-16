package com.nicholasgot.sunshineapp;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nicholasgot.sunshineapp.data.WeatherContract;

import org.w3c.dom.Text;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment
    implements LoaderManager.LoaderCallbacks<Cursor> {

    // Integer ID for cursor loader (unique)
    private static final int DETAIL_LOADER = 2;

    // Projection for columns returned from query
    private static final String[] DETAIL_COLUMNS = {
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID
    };

    // Constant values tied to FORECAST_COLUMNS (so we don't have to ask for col indices from cursor: performance)
    private static final int COL_WEATHER_ID = 0;
    private static final int COL_WEATHER_DATE = 1;
    private static final int COL_WEATHER_DESC = 2;
    private static final int COL_WEATHER_MAX_TEMP = 3;
    private static final int COL_WEATHER_MIN_TEMP = 4;
    private static final int COL_WEATHER_HUMIDITY = 5;
    private static final int COL_WEATHER_WIND_SPEED = 6;
    private static final int COL_WEATHER_DEGREES = 7;
    private static final int COL_WEATHER_PRESSURE = 8;
    private static final int COL_WEATHER_CONDITION_ID = 9;

    private ShareActionProvider mShareActionProvider;
    private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";
    private static final String LOG_TAG = DetailActivity.class.getSimpleName();
    private String mForecast;

    private TextView mDateTextView;
    private TextView mHighTextView;
    private TextView mLowTextView ;
    private TextView mDescView;
    private TextView mHumidityTextView;
    private TextView mWindTextView;
    private TextView mPressureTextView;
    private TextView mDayTextView;
    private ImageView mIconView;

    public DetailActivityFragment() {
    }

    public static final String DETAIL_URI = "detailUri";

    public static DetailActivityFragment newInstance(Uri uri) {
        DetailActivityFragment fragment = new DetailActivityFragment();

        // Supply Uri input as argument
        Bundle args = new Bundle();
        args.putParcelable(DETAIL_URI, uri);
        fragment.setArguments(args);

        return fragment;
    }

    public static Uri mUri;

    public Uri getDetailUri() {
        return getArguments().getParcelable(DETAIL_URI);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detail_fragment, menu);

        MenuItem item = menu.findItem(R.id.menu_item_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareIntent());
        }
        else {
            Log.d(LOG_TAG, "Share action provider is null?");
        }
    }

    private Intent createShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        shareIntent.putExtra(Intent.EXTRA_TEXT, mForecast + FORECAST_SHARE_HASHTAG);

        return shareIntent;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle args = getArguments();
        if (args != null) {
            mUri = args.getParcelable(DETAIL_URI);
        }

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        mIconView = (ImageView) rootView.findViewById(R.id.detail_icon);
        mDayTextView = (TextView) rootView.findViewById(R.id.detail_today_textview);
        mDateTextView = (TextView) rootView.findViewById(R.id.detail_date_textview);
        mHighTextView = (TextView) rootView.findViewById(R.id.detail_high_textview);
        mLowTextView = (TextView) rootView.findViewById(R.id.detail_low_textview);
        mDescView = (TextView) rootView.findViewById(R.id.detail_description_textview);
        mHumidityTextView = (TextView) rootView.findViewById(R.id.detail_humidity_textview);
        mWindTextView = (TextView) rootView.findViewById(R.id.detail_wind_textview);
        mPressureTextView = (TextView) rootView.findViewById(R.id.detail_pressure_textview);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Init cursor loader
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderID, Bundle args) {
        Log.v(LOG_TAG, "In onCreateLoader");

        if (mUri != null) {
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    DETAIL_COLUMNS, // Projection
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update display since we're not using an adapter (TODO: change to use adapter?)
        Log.v(LOG_TAG, "Inside onLoadFinished");
        if (!data.moveToFirst()) {
            return;
        }

        // dynamically set image to display depending on weather condition ID
        int weatherConditionID = data.getInt(COL_WEATHER_CONDITION_ID);
        int weatherRes = Utility.getArtResourceForWeatherCondition(weatherConditionID);
        if (weatherRes != -1) {
            mIconView.setImageResource(weatherRes);
        } else {
            mIconView.setImageResource(R.drawable.ic_wb_sunny_blue_a100_24dp);
        }

        boolean isMetric = Utility.isMetric(getActivity());
        String dateString = Utility.formatDate(data.getLong(COL_WEATHER_DATE));
        String day = Utility.getDayName(getContext(), data.getLong(COL_WEATHER_DATE));
        mDayTextView.setText(day);
        mDateTextView.setText(dateString);

        double highTemp = data.getDouble(COL_WEATHER_MAX_TEMP);
        mHighTextView.setText(
                Utility.formatTemperature(getContext(), highTemp, isMetric));

        double lowTemp = data.getDouble(COL_WEATHER_MIN_TEMP);
        mLowTextView.setText(
                Utility.formatTemperature(getContext(), lowTemp, isMetric));

        String desc = data.getString(COL_WEATHER_DESC);
        mDescView.setText(desc);

        double humidity = data.getDouble(COL_WEATHER_HUMIDITY);
        int formatHumidity = R.string.format_humidity;
        mHumidityTextView.setText(getContext()
                .getString(formatHumidity, humidity));

        float windSpeed = data.getFloat(COL_WEATHER_WIND_SPEED);
        float windDirection = data.getFloat(COL_WEATHER_DEGREES);
        mWindTextView.setText(Utility.getFormattedWind(getContext(),
                windSpeed, windDirection));

        double pressure = data.getDouble(COL_WEATHER_PRESSURE);
        int formatPressure = R.string.format_pressure;
        mPressureTextView.setText(getContext()
                .getString(formatPressure, pressure));

        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                mForecast + FORECAST_SHARE_HASHTAG);
        return shareIntent;
    }

    public void onLocationChanged(String newLocation) {
        // Replace the uri since the loader has changed
        Uri uri = mUri;

        if (uri != null) {
            long date = WeatherContract.WeatherEntry.getDateFromUri(uri);
            mUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(newLocation, date);
            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
        }
    }
}
