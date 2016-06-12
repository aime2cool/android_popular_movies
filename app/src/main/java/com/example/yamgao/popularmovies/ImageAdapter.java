package com.example.yamgao.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by yamgao on 4/23/16.
 */
public class ImageAdapter extends ArrayAdapter {
    private Context mContext;
    private List<Movie> mList;
    private int mResource;
    public ImageAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);
        mContext = context;
        mList = objects;
        mResource = resource;
    }

    public View getView (int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = (ImageView) LayoutInflater.from(mContext).inflate(R.layout.image_view_movie, parent, false);
//            LayoutInflater vi = LayoutInflater.from(mContext);
//            imageView = (ImageView) vi.inflate(mResource, parent);
//            imageView = new ImageView(mContext);
//            imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
//            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//            imageView.setPadding(8, 8, 8, 8);
        }
        else {
            imageView = (ImageView) convertView;
        }
        String POSTER_PATH = mList.get(position).getPoster_path();
        loadImage(POSTER_PATH, imageView);
        return imageView;
    }

//    @Override
//    public View newView(Context context, Cursor cursor, ViewGroup parent) {
//        View view = LayoutInflater.from(context).inflate(R.layout.image_view_movie, parent, false);
//        return view;
//    }
//
//    @Override
//    public void bindView(View view, Context context, Cursor cursor) {
//        String POSTER_PATH = mList.get(position).getPoster_path();
//        loadImage(POSTER_PATH, (ImageView) view);
//
//
//        ViewHolder viewHolder = (ViewHolder) view.getTag();
//
//
//
//        // Read date from cursor
//        long dateInMillis = cursor.getLong(ForecastFragment.COL_WEATHER_DATE);
//        // Find TextView and set formatted date on it
//        viewHolder.dateView.setText(Utility.getFriendlyDayString(context, dateInMillis));
//
//        // Read weather forecast from cursor
//        String description = cursor.getString(ForecastFragment.COL_WEATHER_DESC);
//        // Find TextView and set weather forecast on it
//        viewHolder.descriptionView.setText(description);
//
//        // For accessibility, add a content description to the icon field
//        viewHolder.iconView.setContentDescription(description);
//
//        // Read user preference for metric or imperial temperature units
//        boolean isMetric = Utility.isMetric(context);
//
//        // Read high temperature from cursor
//        double high = cursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP);
//        viewHolder.highTempView.setText(Utility.formatTemperature(context, high));
//
//        // Read low temperature from cursor
//        double low = cursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP);
//        viewHolder.lowTempView.setText(Utility.formatTemperature(context, low));
//    }

    public void loadImage(String POSTER_PATH, ImageView imageView) {
        final String BASE_URL = "http://image.tmdb.org/t/p/";
        final String IMG_SIZE = "w185";

        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(IMG_SIZE)
                .appendPath(POSTER_PATH.replace("/",""))
                .build();
        System.out.println(builtUri.toString());
        Picasso.with(mContext).load(builtUri.toString()).into(imageView);
    }
}
