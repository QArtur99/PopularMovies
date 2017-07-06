package com.android.popularmovies;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by ART_F on 2017-06-29.
 */

public class Movie extends BaseObservable {
    String vote_count;
    String id;
    String video;
    String vote_average;
    String popularity;
    public String poster_path;
    String original_language;
    String original_title;
    String genre_ids;
    String backdrop_path;
    String adult;
    String overview;
    String release_date;


    Movie(JSONObject jsonObject) throws JSONException {
        if (jsonObject.has("vote_count")) {
            vote_count = jsonObject.getString("vote_count");
        }

        if (jsonObject.has("id")) {
            id = jsonObject.getString("id");
        }


        if (jsonObject.has("video")) {
            video = jsonObject.getString("video");
        }

        if (jsonObject.has("vote_average")) {
            vote_average = jsonObject.getString("vote_average");
        }

        if (jsonObject.has("popularity")) {
            popularity = jsonObject.getString("popularity");
        }

        if (jsonObject.has("poster_path")) {
            poster_path = jsonObject.getString("poster_path");
        }

        if (jsonObject.has("original_language")) {
            original_language = jsonObject.getString("original_language");
        }

        if (jsonObject.has("original_title")) {
            original_title = jsonObject.getString("original_title");
        }

        if (jsonObject.has("genre_ids")) {
            genre_ids = jsonObject.getString("genre_ids");
        }

        if (jsonObject.has("backdrop_path")) {
            backdrop_path = jsonObject.getString("backdrop_path");
        }

        if (jsonObject.has("adult")) {
            adult = jsonObject.getString("adult");
        }

        if (jsonObject.has("overview")) {
            overview = jsonObject.getString("overview");
        }

        if (jsonObject.has("release_date")) {
            release_date = jsonObject.getString("release_date");
        }
    }

    @Bindable
    public String getOriginal_title() {
        return original_title;
    }

    public void setOriginal_title(String original_title) {
        this.original_title = original_title;
        notifyPropertyChanged(BR.original_title);
    }

}
