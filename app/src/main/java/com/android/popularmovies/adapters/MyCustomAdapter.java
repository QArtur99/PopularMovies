package com.android.popularmovies.adapters;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.android.popularmovies.R;
import com.squareup.picasso.Picasso;


public class MyCustomAdapter {
    @BindingAdapter("bind:imageUrl")
    public static void setImageUrl(ImageView imageView, String url) {
        String posterURL = "http://image.tmdb.org/t/p/w185/" + url;
        Picasso.with(imageView.getContext()).load(posterURL).error(R.drawable.ic_ondemand_video).noFade().placeholder(R.drawable.ic_ondemand_video).into(imageView);
    }
}
