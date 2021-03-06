package com.example.android.popularmovies;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


public class DetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container_detail, new DetailFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    public static class DetailFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            final String URL_BASE = "http://image.tmdb.org/t/p/w342";

            View view = inflater.inflate(R.layout.fragment_detail, container, false);

            Intent intent = getActivity().getIntent();
            Movie movie = intent.getParcelableExtra(Movie.TAG);

            TextView textView = (TextView) view.findViewById(R.id.detail_title);
            textView.setText(movie.getTitle());

            textView = (TextView) view.findViewById(R.id.detail_releaseDate);
            textView.setText(movie.getRelease_date().substring(0, 4));

            textView = (TextView) view.findViewById(R.id.detail_voteAverage);
            textView.setText(movie.getVote_average() + "/10");

            textView = (TextView) view.findViewById(R.id.detail_overview);
            textView.setText(movie.getOverview());

            ImageView imageView = (ImageView) view.findViewById(R.id.detail_posterPath);
            Picasso.with(getActivity())
                    .load(URL_BASE + movie.getPoster_path())
                    .into(imageView);

            return view;

        }

    }
}
