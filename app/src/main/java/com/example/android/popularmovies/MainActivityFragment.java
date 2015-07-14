package com.example.android.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
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
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private MovieGridViewAdapter movieGridViewAdapter = null;
    private static int page = 1;
    private boolean fetchMoviesTaskRunning = false;
    private String lastSortOrder = null;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        List<Movie> movies = new ArrayList<>();
        movieGridViewAdapter = new MovieGridViewAdapter(getActivity(), movies);
        GridView gridView = (GridView) view.findViewById(R.id.gridMovie);
        gridView.setAdapter(movieGridViewAdapter);

        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0) {
                    return;
                } // Seeing some jitter - don't know why
                if ((firstVisibleItem + visibleItemCount) == totalItemCount) {
                    populateGrid();
                }
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Movie movie = movieGridViewAdapter.getItem(position);

                Intent intent = new Intent(getActivity(), DetailActivity.class);

                intent.putExtra(Movie.TMDB_RESULTS_OVERVIEW, movie.getOverview());
                intent.putExtra(Movie.TMDB_RESULTS_RELEASE_DATE, movie.getRelease_date());
                intent.putExtra(Movie.TMDB_RESULTS_POSTER_PATH, movie.getPoster_path());
                intent.putExtra(Movie.TMDB_RESULTS_TITLE, movie.getTitle());
                intent.putExtra(Movie.TMDB_RESULTS_VOTE_AVERAGE, movie.getVote_average());

                startActivity(intent);

            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        populateGrid();
    }


    private void populateGrid() {

        if (movieGridViewAdapter == null ) { return; }
        if (fetchMoviesTaskRunning) { return; }

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getActivity().getApplicationContext());
        String sortOrder = sharedPreferences
                .getString(getResources().getString(R.string.pref_key), getResources().getString(R.string.pref_keyPopularity));

        if (lastSortOrder == null) {
            lastSortOrder = new String(sortOrder);
        } else if (!(lastSortOrder.contentEquals(sortOrder))) {
            movieGridViewAdapter.clear();
            lastSortOrder = sortOrder;
        }

        if (movieGridViewAdapter.getCount() == 0) { page = 1; }

        String[] taskParams = new String[] { sortOrder, Integer.toString(page++) };
        fetchMoviesTaskRunning = true;
        new FetchMoviesTask().execute(taskParams);

    }

    private class FetchMoviesTask extends AsyncTask<String, Void, String> {

        private final String URL_BASE = "http://api.themoviedb.org/3/discover/movie?";
        private final String ACCEPT_KEY = "Accept";
        private final String ACCEPT_VALUE = "application/json";
        private final String SORT_KEY = "sort_by";
        private final String PAGE_KEY = "page";
        private final String APIKEY_KEY = "api_key";
        private final String APIKEY_VALUE = getResources().getString(R.string.tmdb_apikeyValue);
        private final String REQUEST_METHOD = "GET";
        private final String RESULT_KEY = "results";
        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        protected String doInBackground(String... params) {

            HttpURLConnection httpURLConnection = null;
            BufferedReader bufferedReader = null;
            String moviesJSONResponse = null;

            Uri queryUri = Uri.parse(URL_BASE)
                    .buildUpon()
                    .appendQueryParameter(ACCEPT_KEY, ACCEPT_VALUE)
                    .appendQueryParameter(SORT_KEY, params[0])
                    .appendQueryParameter(PAGE_KEY, params[1])
                    .appendQueryParameter(APIKEY_KEY, APIKEY_VALUE)
                    .build();

            try {

                URL url = new URL(queryUri.toString());
                Log.v(LOG_TAG, url.toString());

                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod(REQUEST_METHOD);
                httpURLConnection.connect();

                InputStream inputStream = httpURLConnection.getInputStream();
                if (inputStream == null) { return null; }

                StringBuffer stringBuffer = new StringBuffer();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                for (String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine()) {
                    stringBuffer.append(line + "\n");
                }
                if (stringBuffer.length() == 0) { return null; }

                moviesJSONResponse = stringBuffer.toString();


            } catch (IOException e) {

                Log.e(LOG_TAG, "Error ", e);

            } finally {

                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }

                if (bufferedReader != null) {

                    try {
                        bufferedReader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream ", e);
                    }

                }

            }

            return moviesJSONResponse;

        }

        protected void onPostExecute(String jsonResponse) {

            fetchMoviesTaskRunning = false;
            if (jsonResponse.length() == 0) { return; }

            try {
                movieGridViewAdapter.addAll(parseJSON(jsonResponse));
            } catch (JSONException e) {
                Log.e(LOG_TAG, "JSON Error :", e);
            }

        }

        private List<Movie> parseJSON(String jsonString) throws JSONException {

            List<Movie> movieList = new ArrayList<>();

            JSONObject jsonObjectResponse = new JSONObject(jsonString);
            JSONArray jsonArrayMovies = jsonObjectResponse.getJSONArray(RESULT_KEY);

            for (int index = 0; index < jsonArrayMovies.length(); index++)
            {
                JSONObject jsonObjectMovie = jsonArrayMovies.getJSONObject(index);
                Movie movie = new Movie(jsonObjectMovie);
                movieList.add(movie);
            }

            return movieList;

        }

    }
}
