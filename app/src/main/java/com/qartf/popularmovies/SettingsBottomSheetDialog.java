package com.qartf.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;


public class SettingsBottomSheetDialog extends BottomSheetDialog implements View.OnClickListener {
    private Context context;
    private SharedPreferences sharedPreferences;
    private TextView favorite;
    private TextView mostPopular;
    private TextView highestRated;

    public SettingsBottomSheetDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.dialog_settings, null);
        setContentView(bottomSheetView);
        loadViews(bottomSheetView);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String sortBy = sharedPreferences.getString(context.getString(R.string.pref_sort_by_key), context.getString(R.string.pref_sort_by_most_popular_default));
        setSelected(sortBy);
    }

    private void loadViews(View rootView) {
        favorite = rootView.findViewById(R.id.favorite);
        mostPopular = rootView.findViewById(R.id.mostPopular);
        highestRated = rootView.findViewById(R.id.highestRated);

        favorite.setOnClickListener(this);
        mostPopular.setOnClickListener(this);
        highestRated.setOnClickListener(this);
    }

    public void setSelected(String sortBy) {
        if (sortBy.equals(context.getString(R.string.pref_sort_by_most_popular_default))) {
            mostPopular.setSelected(true);
        } else if (sortBy.equals(context.getString(R.string.pref_sort_by_highest_rated))) {
            highestRated.setSelected(true);
        } else if (sortBy.equals(context.getString(R.string.pref_sort_by_favorite))) {
            favorite.setSelected(true);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.favorite:
                selectorOff();
                view.setSelected(true);
                sharedPreferences.edit().putString(context.getString(R.string.pref_sort_by_key), context.getString(R.string.pref_sort_by_favorite)).apply();
                break;
            case R.id.mostPopular:
                selectorOff();
                view.setSelected(true);
                sharedPreferences.edit().putString(context.getString(R.string.pref_sort_by_key), context.getString(R.string.pref_sort_by_most_popular_default)).apply();
                break;
            case R.id.highestRated:
                selectorOff();
                view.setSelected(true);
                sharedPreferences.edit().putString(context.getString(R.string.pref_sort_by_key), context.getString(R.string.pref_sort_by_highest_rated)).apply();
                break;

        }
        dismiss();
    }

    public void selectorOff() {
        favorite.setSelected(false);
        mostPopular.setSelected(false);
        highestRated.setSelected(false);
    }

}
