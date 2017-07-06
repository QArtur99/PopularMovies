package com.android.popularmovies;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * Created by ART_F on 2017-07-03.
 */

public class SettingsFragment extends Fragment implements View.OnClickListener {
    private SharedPreferences sharedPreferences;
    private TextView mostPopular;
    private TextView highestRated;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_sort_by, container, false);
        loadViews(rootView);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String sortBy = sharedPreferences.getString(getString(R.string.pref_sort_by_key), getString(R.string.pref_sort_by_most_popular_default));
        setSelected(sortBy);

        return rootView;
    }

    private void loadViews(View rootView) {
        mostPopular = rootView.findViewById(R.id.mostPopular);
        highestRated = rootView.findViewById(R.id.highestRated);

        mostPopular.setOnClickListener(this);
        highestRated.setOnClickListener(this);
    }

    public void setSelected(String sortBy) {
        if (sortBy.matches(getString(R.string.pref_sort_by_most_popular_default))) {
            mostPopular.setSelected(true);
        } else if (sortBy.matches(getString(R.string.pref_sort_by_highest_rated))) {
            highestRated.setSelected(true);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.mostPopular:
                selectorOff();
                view.setSelected(true);
                sharedPreferences.edit().putString(getString(R.string.pref_sort_by_key), getString(R.string.pref_sort_by_most_popular_default)).apply();
                break;
            case R.id.highestRated:
                selectorOff();
                view.setSelected(true);
                sharedPreferences.edit().putString(getString(R.string.pref_sort_by_key), getString(R.string.pref_sort_by_highest_rated)).apply();
                break;

        }
    }

    public void selectorOff() {
        mostPopular.setSelected(false);
        highestRated.setSelected(false);
    }

}
