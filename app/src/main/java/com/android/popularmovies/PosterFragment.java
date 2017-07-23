package com.android.popularmovies;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by ART_F on 2017-07-20.
 */

public class PosterFragment extends Fragment {

    private String posterURL;

    public PosterFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_poster, container, false);
        ImageView imageView = rootView.findViewById(R.id.posterView);
        Picasso.with(getContext()).load(posterURL).into(imageView);
        return rootView;
    }

    public void setPoster(String url) {
        posterURL = "http://image.tmdb.org/t/p/w500/" + url;
    }

}
