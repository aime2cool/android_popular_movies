package com.example.yamgao.popularmovies;


import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

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
 * Created by yamgao on 4/23/16.
 */
public class MoviePosterFragment extends Fragment {
    private ImageAdapter mMovieAdapter;
    private ArrayList<Movie> mMovieList;

    public MoviePosterFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovieList();
    }



    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("movies", mMovieList);

    }


    public void updateMovieList() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sort_order = sharedPref.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_default));
        new LoadMoviePosterTask().execute(sort_order);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        GridView gridview = (GridView) rootView.findViewById(R.id.gridview_movie_poster);
        mMovieAdapter = new ImageAdapter(getActivity(), R.layout.image_view_movie, mMovieList);
        gridview.setAdapter(mMovieAdapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Movie movie = (Movie) mMovieAdapter.getItem(i);
                // call detailActivity
                Intent detailIntent = new Intent(getActivity(), DetailActivity.class);
                detailIntent.putExtra("MOVIE_DETAIL", (Parcelable)movie);
                startActivity(detailIntent);
            }
        });

        return rootView;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if(savedInstanceState != null && savedInstanceState.containsKey("movies")) {
            mMovieList = savedInstanceState.getParcelableArrayList("movies");
        }
        else {
            mMovieList = new ArrayList<Movie>(0);
        }
    }


    private class LoadMoviePosterTask extends AsyncTask<String, Void, Movie[]> {
        private final String LOG_TAG = LoadMoviePosterTask.class.getSimpleName();

        @Override
        protected Movie[] doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            Movie[] moviesArray = null;
            // Will contain the raw JSON response as a string.
            String movieJsonStr = null;

            String sortBy = params[0];
            try {
                final String MOVIE_DB_URL = "http://api.themoviedb.org/3/movie/";
                final String APPID_PARAM = "api_key";

                Uri builtUri = Uri.parse(MOVIE_DB_URL).buildUpon()
                        .appendPath(sortBy)
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
                movieJsonStr = buffer.toString();
                Log.v(LOG_TAG, movieJsonStr);
                moviesArray = getMovieDataFromJson(movieJsonStr);
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
            return moviesArray;
        }



        @Override
        protected void onPostExecute(Movie[] result) {
            if (result != null) {
                mMovieAdapter.clear();
                List<Movie> movieList = new ArrayList<Movie>(Arrays.asList(result));
                mMovieAdapter.addAll(movieList);
            }
        }

        private Movie[] getMovieDataFromJson(String movieJsonStr) throws JSONException {
            final String MDB_LIST = "results";
            final String MDB_PATH = "poster_path";
            final String MDB_OVERVIEW = "overview";
            final String MDB_DATE = "release_date";
            final String MDB_ID = "id";
            final String MDB_ORI_TITLE = "original_title";
            final String MDB_VOTE_AVG = "vote_average";
            JSONObject forecastJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = forecastJson.getJSONArray(MDB_LIST);
            Movie[] resultMovies = new Movie[movieArray.length()];
            for (int i = 0; i < movieArray.length(); i++) {
                String poster_path;
                String overview;
                String release_date;
                int id;
                String original_title;
                double vote_average;
                JSONObject movieObject = movieArray.getJSONObject(i);
                poster_path = movieObject.getString(MDB_PATH);
                overview = movieObject.getString(MDB_OVERVIEW);
                release_date = movieObject.getString(MDB_DATE);
                id = movieObject.getInt(MDB_ID);
                original_title = movieObject.getString(MDB_ORI_TITLE);
                vote_average = movieObject.getDouble(MDB_VOTE_AVG);
                resultMovies[i] = new Movie(id, poster_path, overview, release_date, original_title, vote_average);
            }
            for (Movie m : resultMovies) {
                Log.v(LOG_TAG, "Movie entry : " + m.getOriginal_title());
            }
            return resultMovies;
        }
    }

}
