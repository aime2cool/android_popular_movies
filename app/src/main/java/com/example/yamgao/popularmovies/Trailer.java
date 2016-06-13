package com.example.yamgao.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by zhucai on 6/12/16.
 */
public class Trailer implements Parcelable {
    private String key;
    private String name;
    public Trailer (String k, String n) {
        key = k;
        name = n;
    }
    public String getKey() {
        return key;
    }
    public String getName() {
        return name;
    }

    protected Trailer(Parcel in) {

        key = in.readString();
        name = in.readString();

    }

    public static final Creator<Trailer> CREATOR = new Creator<Trailer>() {
        @Override
        public Trailer createFromParcel(Parcel in) {
            return new Trailer(in);
        }

        @Override
        public Trailer[] newArray(int size) {
            return new Trailer[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(key);
        dest.writeString(name);

    }
}
