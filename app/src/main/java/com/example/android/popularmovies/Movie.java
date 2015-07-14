package com.example.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Biswajit Ghosh on 7/13/2015.
 */
public class Movie implements Parcelable {

    public static final String TAG = Movie.class.getSimpleName();
    public static final String TMDB_RESULTS_ID = "id";
    public static final String TMDB_RESULTS_OVERVIEW = "overview";
    public static final String TMDB_RESULTS_RELEASE_DATE = "release_date";
    public static final String TMDB_RESULTS_POSTER_PATH = "poster_path";
    public static final String TMDB_RESULTS_TITLE = "title";
    public static final String TMDB_RESULTS_VOTE_AVERAGE = "vote_average";

    private String id;
    private String overview;
    private String release_date;
    private String poster_path;
    private String title;
    private String vote_average;

    public Movie(JSONObject movie) throws JSONException {

        this.id = movie.getString(TMDB_RESULTS_ID);
        this.overview = movie.getString(TMDB_RESULTS_OVERVIEW);
        this.release_date = movie.getString(TMDB_RESULTS_RELEASE_DATE);
        this.poster_path = movie.getString(TMDB_RESULTS_POSTER_PATH);
        this.title = movie.getString(TMDB_RESULTS_TITLE);
        this.vote_average = movie.getString(TMDB_RESULTS_VOTE_AVERAGE);

    }

    public String getId() { return id; }
    public String getOverview() { return overview; }
    public String getRelease_date() { return release_date; }
    public String getPoster_path() { return poster_path; }
    public String getTitle() { return title; }
    public String getVote_average() { return vote_average; }

    protected Movie(Parcel in) {
        this.id = in.readString();
        this.overview = in.readString();
        this.release_date = in.readString();
        this.poster_path = in.readString();
        this.title = in.readString();
        this.vote_average = in.readString();

    }

    @Override
    public int describeContents() {return 0;}

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.overview);
        dest.writeString(this.release_date);
        dest.writeString(this.poster_path);
        dest.writeString(this.title);
        dest.writeString(this.vote_average);
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {

        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

}
