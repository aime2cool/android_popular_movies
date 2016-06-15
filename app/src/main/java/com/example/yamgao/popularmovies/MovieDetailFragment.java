package com.example.yamgao.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.yamgao.popularmovies.data.MovieContract;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by yamgao on 6/12/16.
 */
public class MovieDetailFragment extends Fragment {
    private int mMovieID;
    private TrailerAdapter mTrailerAdapter;
    private ReviewAdapter mReviewAdapter;
    private ArrayList<Trailer> mTrailerList;
    private ToggleButton mBtnFavorite;
    private Movie movie;
    public MovieDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if(savedInstanceState != null && savedInstanceState.containsKey("trailers")) {
            mTrailerList = savedInstanceState.getParcelableArrayList("trailers");
        }
        else {
            mTrailerList = new ArrayList<Trailer>(0);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
//        new LoadMovieTrailerTask().execute();
//        new LoadMovieReviewTask().execute();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("trailers", mTrailerList);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra("MOVIE_DETAIL")) {
            movie = (Movie) intent.getParcelableExtra("MOVIE_DETAIL");
            String title = movie.getOriginal_title();
            String date = movie.getRelease_date();
            String overview = movie.getOverview();
            mMovieID = movie.getId();
            double rating = movie.getVote_average();
            ((TextView)rootView.findViewById(R.id.movie_title)).setText(title);
            ((TextView)rootView.findViewById(R.id.movie_overview)).setText(overview);
            ((TextView)rootView.findViewById(R.id.movie_release_date)).setText(date);
            ((TextView)rootView.findViewById(R.id.user_rating)).setText(rating + "/10");

            ImageView imageView = (ImageView)rootView.findViewById(R.id.movie_poster);
            String poster_path = movie.getPoster_path();
            final String BASE_URL = "http://image.tmdb.org/t/p/";
            final String IMG_SIZE = "w185";

            Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendPath(IMG_SIZE)
                    .appendPath(poster_path.replace("/",""))
                    .build();
            System.out.println(builtUri.toString());
            Picasso.with(getActivity()).load(builtUri.toString()).into(imageView);

            new LoadMovieTrailerTask().execute();
            new LoadMovieReviewTask().execute();

//            mBtnFavorite = (ToggleButton) rootView.findViewById(R.id.favorite_button);
//            mBtnFavorite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                    if (isChecked) {
//                        // The toggle is enabled
//                        new AddToMovieDBTask(getActivity()).execute(movie);
//                    } else {
//                        // The toggle is disabled
//                        new RemoveFromMovieDBTask(getActivity()).execute(movie.getId());
//                    }
//                }
//            });
            new QueryFavoriteTask().execute();
        }
        return rootView;
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);

        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
//            listItem.measure(0,0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        System.out.println("height is "+ params.height);
        System.out.println("listAdapter size is " + listAdapter.getCount());
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public static boolean setListViewHeightBasedOnItems(ListView listView) {

        ArrayAdapter listAdapter = (ArrayAdapter) listView.getAdapter();
        if (listAdapter != null) {

            int numberOfItems = listAdapter.getCount();

            // Get total height of all items.
            int totalItemsHeight = 0;
            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = listAdapter.getView(itemPos, null, listView);
                item.measure(0, 0);
                totalItemsHeight += item.getMeasuredHeight();
            }

            // Get total height of all item dividers.
            int totalDividersHeight = listView.getDividerHeight() *
                    (numberOfItems - 1);

            // Set list height.
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight;
            listView.setLayoutParams(params);
            listView.requestLayout();

            return true;

        } else {
            return false;
        }

    }

    private class LoadMovieTrailerTask extends AsyncTask<Void, Void, Trailer[]> {
        private final String LOG_TAG = LoadMovieTrailerTask.class.getSimpleName();

        @Override
        protected Trailer[] doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            Trailer[] trailersArray = null;
            // Will contain the raw JSON response as a string.
            String trailerJsonStr = null;


            try {
                final String MOVIE_DB_URL = "http://api.themoviedb.org/3/movie/";
                final String APPID_PARAM = "api_key";

                Uri builtUri = Uri.parse(MOVIE_DB_URL).buildUpon()
                        .appendPath(String.valueOf(mMovieID))
                        .appendPath("videos")
                        .appendQueryParameter(APPID_PARAM, BuildConfig.MOVIEDB_API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());
                Log.v(LOG_TAG, "Built URI " + builtUri.toString());



                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty( "Accept-Encoding", "" );
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                trailerJsonStr = buffer.toString();
                Log.v(LOG_TAG, trailerJsonStr);
                trailersArray = getTrailerDataFromJson(trailerJsonStr);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            return trailersArray;
        }



        @Override
        protected void onPostExecute(Trailer[] result) {
            if (result != null) {
//                mTrailerAdapter.clear();
                mTrailerList = new ArrayList<Trailer>(Arrays.asList(result));
//                mTrailerAdapter.addAll(mTrailerList);
                ListView trailerView = (ListView) getView().findViewById(R.id.list_view_trailer);
                mTrailerAdapter = new TrailerAdapter(getActivity(), R.layout.list_item_trailer, mTrailerList);
                trailerView.setAdapter(mTrailerAdapter);
//            System.out.println("trailer list size is " + mTrailerList.size());
                setListViewHeightBasedOnChildren(trailerView);
                trailerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Trailer trailer = (Trailer) mTrailerAdapter.getItem(i);
                        String id = trailer.getKey();
                        // call detailActivity
                        try {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
                            startActivity(intent);
                        } catch (ActivityNotFoundException ex) {
                            Intent intent = new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("http://www.youtube.com/watch?v=" + id));
                            startActivity(intent);
                        }
                    }
                });
            }
        }

        private Trailer[] getTrailerDataFromJson(String trailerJsonStr) throws JSONException {
            final String TRAILER_LIST = "results";
            final String TRAILER_KEY = "key";
            final String TRAILER_NAME = "name";


            JSONObject trailerJson = new JSONObject(trailerJsonStr);
            JSONArray trailerArray = trailerJson.getJSONArray(TRAILER_LIST);
            Trailer[] resultTrailers = new Trailer[trailerArray.length()];
            for (int i = 0; i < trailerArray.length(); i++) {
                String key;
                String name;

                JSONObject trailerObject = trailerArray.getJSONObject(i);
                key = trailerObject.getString(TRAILER_KEY);
                name = trailerObject.getString(TRAILER_NAME);

                resultTrailers[i] = new Trailer(key, name);
            }
            for (Trailer m : resultTrailers) {
                Log.v(LOG_TAG, "Trailer entry : " + m.getKey());
            }
            return resultTrailers;
        }


    }

    private class LoadMovieReviewTask extends AsyncTask<Void, Void, Review[]> {
        private final String LOG_TAG = LoadMovieReviewTask.class.getSimpleName();

        @Override
        protected Review[] doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            Review[] reviewsArray = null;
            // Will contain the raw JSON response as a string.
            String reviewJsonStr = null;


            try {
                final String MOVIE_DB_URL = "http://api.themoviedb.org/3/movie/";
                final String APPID_PARAM = "api_key";

                Uri builtUri = Uri.parse(MOVIE_DB_URL).buildUpon()
                        .appendPath(String.valueOf(mMovieID))
                        .appendPath("reviews")
                        .appendQueryParameter(APPID_PARAM, BuildConfig.MOVIEDB_API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());
                Log.v(LOG_TAG, "Built URI " + builtUri.toString());



                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty( "Accept-Encoding", "" );
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                reviewJsonStr = buffer.toString();
                Log.v(LOG_TAG, reviewJsonStr);
                reviewsArray = getReviewDataFromJson(reviewJsonStr);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            return reviewsArray;
        }



        @Override
        protected void onPostExecute(Review[] result) {
            if (result != null) {
//                mReviewAdapter.clear();
                List<Review> reviewsList = new ArrayList<Review>(Arrays.asList(result));
//                mReviewAdapter.addAll(reviewsList);
                ListView reviewView = (ListView) getView().findViewById(R.id.list_view_review);
                mReviewAdapter = new ReviewAdapter(getActivity(), R.layout.list_item_review, reviewsList);
                reviewView.setAdapter(mReviewAdapter);
                setListViewHeightBasedOnChildren(reviewView);
            }
        }

        private Review[] getReviewDataFromJson(String reviewJsonStr) throws JSONException {
            final String REVIEW_LIST = "results";
            final String REVIEW_AUTHOR = "author";
            final String REVIEW_CONTENT = "content";
            final String REVIEW_URL = "url";


            JSONObject reviewJson = new JSONObject(reviewJsonStr);
            JSONArray reviewArray = reviewJson.getJSONArray(REVIEW_LIST);
            Review[] resultReviews = new Review[reviewArray.length()];
            for (int i = 0; i < reviewArray.length(); i++) {
                String author;
                String content;
                String url;

                JSONObject reviewObject = reviewArray.getJSONObject(i);
                author = reviewObject.getString(REVIEW_AUTHOR);
                content = reviewObject.getString(REVIEW_CONTENT);
                url = reviewObject.getString(REVIEW_URL);
                resultReviews[i] = new Review(author, content, url);
            }
            for (Review m : resultReviews) {
                Log.v(LOG_TAG, "Review entry : " + m.getAuthor());
            }
            return resultReviews;
        }


    }


    private class QueryFavoriteTask extends AsyncTask<Void, Void, Cursor>{

        @Override
        protected Cursor doInBackground(Void... params) {
            String[] projection = {MovieContract.MovieEntry.COLUMN_ID};
            String selection = MovieContract.MovieEntry.COLUMN_ID + " = ?";
            // Specify arguments in placeholder order.
            String[] selectionArgs = { String.valueOf(mMovieID) };
            Cursor cursor = getActivity().getContentResolver().query(
                    MovieContract.MovieEntry.CONTENT_URI,
                    projection,
                    selection,
                    selectionArgs,
                    null);
            return cursor;
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            mBtnFavorite = (ToggleButton) getView().findViewById(R.id.favorite_button);

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    mBtnFavorite.setChecked(true);
                }
                else {
                    mBtnFavorite.setChecked(false);
                }
            }

            mBtnFavorite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        // The toggle is enabled
                        new AddToMovieDBTask(getActivity()).execute(movie);
                    } else {
                        // The toggle is disabled
                        new RemoveFromMovieDBTask(getActivity()).execute(mMovieID);
                    }
                }
            });
        }
    }
}

