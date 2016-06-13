package com.example.yamgao.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by yamgao on 6/12/16.
 */
public class ReviewAdapter extends ArrayAdapter {
//    private Context mContext;
//    private List<Movie> mList;

    public ReviewAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);
//        mContext = context;
//        mList = objects;

    }
    public View getView (int position, View convertView, ViewGroup parent) {
        Review review = (Review) getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_review, parent, false);
        }
        TextView reviewAuthor = (TextView) convertView.findViewById(R.id.list_item_review_author);
        TextView reviewContent = (TextView) convertView.findViewById(R.id.list_item_review_content);

        reviewAuthor.setText("A review by "+review.getAuthor());
        reviewContent.setText(review.getContent());


        return convertView;
    }
}
