package com.android.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.android.popularmovies.databinding.ActivityMainBinding;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener,
        MyAdapter.ListItemClickListener, LoaderManager.LoaderCallbacks<List<Movie>>, View.OnTouchListener {

    public int pageNoInteger = 1;
    ActivityMainBinding binding;
    private View view;
    private MyAdapter moviesAdapter;
    private SharedPreferences sharedPreferences;
    private String sortBy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        int columns = setupSharedPreferences();
        setAdapter(columns, new ArrayList<Movie>());
        getSupportLoaderManager().initLoader(1, null, this).forceLoad();

        binding.recyclerView.setOnTouchListener(this);

    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (binding.recyclerView.getLayoutManager().canScrollVertically() && (moviesAdapter.getItemCount() / 20) == pageNoInteger) {
            pageNoInteger = (moviesAdapter.getItemCount() / 20) + 1;
            getSupportLoaderManager().restartLoader(1, null, MainActivity.this).forceLoad();
        }
        return false;
    }

    public int setupSharedPreferences() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        int columns = sharedPreferences.getInt(getString(R.string.pref_number_of_columns_key), R.integer.number_of_columns);
        sortBy = sharedPreferences.getString(getString(R.string.pref_sort_by_key), getString(R.string.pref_sort_by_most_popular_default));
        return columns;
    }

    private void setAdapter(int columns, List<Movie> movieList) {
        GridLayoutManager layoutManager = new GridLayoutManager(this, columns);
        binding.recyclerView.setLayoutManager(layoutManager);
        binding.recyclerView.setHasFixedSize(true);
        moviesAdapter = new MyAdapter(movieList, this, columns);
        binding.recyclerView.setAdapter(moviesAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        Movie movie = (Movie) moviesAdapter.getDataAtPosition(clickedItemIndex);
        String jsonString = new Gson().toJson(movie);
        Intent startDetail = new Intent(this, MovieDetailActivity.class);
        startDetail.putExtra("movie", jsonString);
        startActivity(startDetail);
    }

    @Override
    public Loader<List<Movie>> onCreateLoader(int id, Bundle args) {
        return new MoviesLoader(MainActivity.this, sortBy, String.valueOf(pageNoInteger));
    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> data) {
        binding.loadingIndicator.setVisibility(View.GONE);
        if (pageNoInteger == 1) {
            moviesAdapter.clearMovies();
        }
        if (data != null && !data.isEmpty()) {
            moviesAdapter.setMovies(data);
        } else {
            binding.emptyView.setText(R.string.no_movies);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {
        moviesAdapter.setMovies(new ArrayList<Movie>());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_sortBy:
                openBottomDialog();
                break;
            case R.id.one_column:
                sharedPreferences.edit().putInt(getString(R.string.pref_number_of_columns_key), 1).apply();
                break;
            case R.id.two_columns:
                sharedPreferences.edit().putInt(getString(R.string.pref_number_of_columns_key), 2).apply();
                break;
            case R.id.three_columns:
                sharedPreferences.edit().putInt(getString(R.string.pref_number_of_columns_key), 3).apply();
                break;
            case R.id.four_columns:
                sharedPreferences.edit().putInt(getString(R.string.pref_number_of_columns_key), 4).apply();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openBottomDialog() {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null) {
                parent.removeView(view);
            }
        } else {
            view = getLayoutInflater().inflate(R.layout.menu_bottom, null);
        }
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(view);
        dialog.show();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_number_of_columns_key))) {
            int columns = sharedPreferences.getInt(key, R.integer.number_of_columns);
            setAdapter(columns, moviesAdapter.getData());
        } else if (key.equals(getString(R.string.pref_sort_by_key))) {
            pageNoInteger = 1;
            sortBy = sharedPreferences.getString(key, getString(R.string.pref_sort_by_most_popular_default));
            getSupportLoaderManager().restartLoader(1, null, this).forceLoad();
        }
    }

}
