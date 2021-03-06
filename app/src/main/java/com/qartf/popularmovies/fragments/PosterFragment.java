package com.qartf.popularmovies.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;


public class PosterFragment extends Fragment {

    private String posterURL;
    private ImageView imageView;

    public PosterFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(com.qartf.popularmovies.R.layout.fragment_poster, container, false);
        imageView = rootView.findViewById(com.qartf.popularmovies.R.id.posterView);
        Picasso.with(getContext()).load(posterURL).into(imageView);
        return rootView;
    }

    public void setPoster(String url) {
        posterURL = "http://image.tmdb.org/t/p/w185/" + url;
    }

    public void loadNewPoster(String url) {
        posterURL = "http://image.tmdb.org/t/p/w185/" + url;
        if(imageView != null) {
            Picasso.with(getContext()).load(posterURL).into(imageView);
        }
    }
}
