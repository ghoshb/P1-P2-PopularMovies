package com.example.android.popularmovies;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Biswajit Ghosh on 7/13/2015.
 */
public class MovieGridViewAdapter extends ArrayAdapter<Movie> {

    public MovieGridViewAdapter(Activity context, List<Movie> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent) {

        final String URL_BASE = "http://image.tmdb.org/t/p/w185";
        Movie movie = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.grid_item_movie, parent, false);
        }

        ImageView imageView = (ImageView) convertView.findViewById(R.id.grid_item_imageView);
        Picasso.with(getContext())
                .load(URL_BASE + movie.getPoster_path())
                .into(imageView);

        return convertView;

    }
}
