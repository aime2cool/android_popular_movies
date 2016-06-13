package com.example.yamgao.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by yamgao on 6/12/16.
 */
public class TrailerAdapter extends ArrayAdapter {
//    private Context mContext;
//    private List<Movie> mList;

    public TrailerAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);
//        mContext = context;
//        mList = objects;

    }
    public View getView (int position, View convertView, ViewGroup parent) {
        Trailer trailer = (Trailer) getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_trailer, parent, false);
        }
        ImageView trailerImage = (ImageView) convertView.findViewById(R.id.list_item_trailer_image);
        TextView trailerName = (TextView) convertView.findViewById(R.id.list_item_trailer);

        trailerImage.setImageResource(R.mipmap.ic_launcher);
        trailerName.setText(trailer.getName());


        return convertView;
    }


}
