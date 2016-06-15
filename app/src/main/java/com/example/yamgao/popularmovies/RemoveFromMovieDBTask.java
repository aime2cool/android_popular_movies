package com.example.yamgao.popularmovies;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.yamgao.popularmovies.data.MovieContract;

/**
 * Created by yamgao on 6/14/2016.
 */
public class RemoveFromMovieDBTask extends AsyncTask<Integer, Void, Void> {
    private Context mContext;
    private final String LOG_TAG = RemoveFromMovieDBTask.class.getSimpleName();
    public RemoveFromMovieDBTask (Context context){
        mContext = context;
    }

    @Override
    protected Void doInBackground(Integer... params) {
        int mdb_id = params[0];
        // Define 'where' part of query.
        String selection = MovieContract.MovieEntry.COLUMN_ID + " = ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = { String.valueOf(mdb_id) };
        int rowsDeleted = mContext.getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI, selection, selectionArgs);
        Log.d(LOG_TAG, "Delete : " + rowsDeleted + " rows");
        return null;
    }
}
