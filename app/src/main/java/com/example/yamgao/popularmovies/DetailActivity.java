package com.example.yamgao.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by yamgao on 4/24/16.
 */
public class DetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            Intent intent = getActivity().getIntent();
            if (intent != null && intent.hasExtra("MOVIE_DETAIL")) {
                Movie movie = (Movie) intent.getParcelableExtra("MOVIE_DETAIL");
                String title = movie.getOriginal_title();
                String date = movie.getRelease_date();
                String overview = movie.getOverview();
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
            }
            return rootView;
        }
    }

}
