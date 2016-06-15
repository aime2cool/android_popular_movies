package com.example.yamgao.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.yamgao.popularmovies.data.MovieContract;
import com.squareup.picasso.Picasso;

/**
 * Created by yamgao on 6/14/2016.
 */
public class FavoriteAdapter extends CursorAdapter {
    public FavoriteAdapter(Context context, Cursor c) {
        super(context, c);
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        ImageView imageView = (ImageView) LayoutInflater.from(mContext).inflate(R.layout.image_view_movie, parent, false);
        return imageView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageView imageView = (ImageView)view;
        String POSTER_PATH = cursor.getString(MoviePosterFragment.COL_POSTER_PATH);
        loadImage(POSTER_PATH, imageView);
    }

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
