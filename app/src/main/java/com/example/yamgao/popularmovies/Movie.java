package com.example.yamgao.popularmovies;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by yamgao on 4/23/16.
 */
public class Movie implements Parcelable{
    private int id;
    private String poster_path;
    private String overview;
    private String release_date;
    private String original_title;
    private double vote_average;

    public Movie (int id, String poster_path, String overview, String release_date, String original_title, double vote_average) {
        this.id = id;
        this.poster_path = poster_path;
        this.overview = overview;
        this.release_date = release_date;
        this.original_title = original_title;
        this.vote_average = vote_average;
    }

    protected Movie(Parcel in) {
        id = in.readInt();
        poster_path = in.readString();
        overview = in.readString();
        release_date = in.readString();
        original_title = in.readString();
        vote_average = in.readDouble();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public int getId() {
        return id;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public String getOverview() {
        return overview;
    }

    public String getRelease_date() {
        return release_date;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public double getVote_average() {
        return vote_average;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(poster_path);
        dest.writeString(overview);
        dest.writeString(release_date);
        dest.writeString(original_title);
        dest.writeDouble(vote_average);
    }
}
