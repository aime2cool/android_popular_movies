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
        }
        else {
            imageView = (ImageView) convertView;
        }
        String POSTER_PATH = mList.get(position).getPoster_path();
        loadImage(POSTER_PATH, imageView);
        return imageView;
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
