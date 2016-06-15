package com.example.yamgao.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.yamgao.popularmovies.data.MovieContract;

/**
 * Created by yamgao on 6/14/2016.
 */
public class AddToMovieDBTask extends AsyncTask<Movie, Void, Void> {
    private Context mContext;
    private final String LOG_TAG = AddToMovieDBTask.class.getSimpleName();
    public AddToMovieDBTask (Context context){
        mContext = context;
    }

    @Override
    protected Void doInBackground(Movie... params) {
        Movie movie = params[0];
        String poster_path = movie.getPoster_path();
        String overview = movie.getOverview();
        String release_date = movie.getRelease_date();
        int id = movie.getId();
        String original_title = movie.getOriginal_title();
        double vote_average = movie.getVote_average();

        ContentValues movieValues = new ContentValues();
        movieValues.put(MovieContract.MovieEntry.COLUMN_ID, id);
        movieValues.put(MovieContract.MovieEntry.COLUMN_ORI_TITLE, original_title);
        movieValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, overview);
        movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, poster_path);
        movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, release_date);
        movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVG, vote_average);
        Uri uri = mContext.getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, movieValues);
        Log.d(LOG_TAG, "insert uri : " + uri);
        return null;
    }
}
