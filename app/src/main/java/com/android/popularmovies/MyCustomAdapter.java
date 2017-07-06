package com.android.popularmovies;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by ART_F on 2017-06-29.
 */

public class MyCustomAdapter {
    @BindingAdapter("bind:imageUrl")
    public static void setImageUrl(ImageView imageView, String url) {
        String posterURL = "http://image.tmdb.org/t/p/w500/" + url;
        Picasso.with(imageView.getContext()).load(posterURL).into(imageView);
    }
}
