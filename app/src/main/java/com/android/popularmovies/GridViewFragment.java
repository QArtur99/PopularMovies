package com.android.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.android.popularmovies.adapter.Movie;
import com.android.popularmovies.adapter.MyAdapter;
import com.android.popularmovies.background.MoviesLoader;
import com.android.popularmovies.databinding.FragmentGridViewBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ART_F on 2017-07-19.
 */

public class GridViewFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener,
        MyAdapter.ListItemClickListener, LoaderManager.LoaderCallbacks<List<Movie>>, View.OnTouchListener {

    FragmentGridViewBinding binding;
    private int pageNoInteger = 1;
    private View rootView;
    private OnImageClickListener mCallback;
    private MyAdapter moviesAdapter;
    private SharedPreferences sharedPreferences;
    private String sortBy;

    public GridViewFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_grid_view, container, false);
        rootView = binding.getRoot();

        binding.emptyView.setVisibility(View.GONE);
        binding.loadingIndicator.setVisibility(View.GONE);

        int columns = setupSharedPreferences();
        setAdapter(columns, new ArrayList<Movie>());

        if (sortBy.equals(getString(R.string.pref_sort_by_favorite))) {
            binding.recyclerView.setOnTouchListener(null);
            getActivity().getSupportLoaderManager().initLoader(0, null, this).forceLoad();
        } else if (checkConnection()) {
            binding.recyclerView.setOnTouchListener(this);
            getActivity().getSupportLoaderManager().initLoader(0, null, this).forceLoad();
        } else {
            setInfoNoConnection();
        }
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // This makes sure that the host activity has implemented the callback interface
        // If not, it throws an exception
        try {
            mCallback = (OnImageClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnImageClickListener");
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu, menu);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            menu.findItem(R.id.action_favorite).setVisible(true);
        } else {
            menu.findItem(R.id.action_favorite).setVisible(false);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_favorite:
                ((MainActivity) getActivity()).addFavorite(null);
                break;
            case R.id.action_refresh:
                restartLoader();
                break;
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
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        View lastView = binding.recyclerView.getLayoutManager().findViewByPosition(moviesAdapter.getItemCount() - 1);
        View firstView = binding.recyclerView.getLayoutManager().findViewByPosition(moviesAdapter.getItemCount() - 20);
        if (binding.recyclerView.getLayoutManager().canScrollVertically() && (moviesAdapter.getItemCount() / 20) == pageNoInteger) {
            if (lastView != null && lastView.isShown() || firstView != null && firstView.isShown()) {
                if (checkConnection()) {
                    pageNoInteger = (moviesAdapter.getItemCount() / 20) + 1;
                    getActivity().getSupportLoaderManager().restartLoader(0, null, this).forceLoad();
                } else {
                    setInfoNoConnection();
                }
            }
        }
        return false;
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        Movie movie = (Movie) moviesAdapter.getDataAtPosition(clickedItemIndex);
        mCallback.onImageSelected(movie);
    }

    @Override
    public Loader<List<Movie>> onCreateLoader(int id, Bundle args) {
        return new MoviesLoader(getContext(), sortBy, String.valueOf(pageNoInteger));
    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, final List<Movie> data) {
        binding.loadingIndicator.setVisibility(View.GONE);
        if (pageNoInteger == 1) {
            if (moviesAdapter != null) {
                moviesAdapter.clearMovies();
            }
        }
        if (data != null && !data.isEmpty()) {
            if (rootView.getRootView().findViewById(R.id.detailsViewFrame) != null && moviesAdapter.getData().isEmpty()) {
                rootView.post(new Runnable() {
                    @Override
                    public void run() {
                        mCallback.onImageSelected(data.get(0));
                    }
                });
            }
            binding.emptyView.setVisibility(View.GONE);
            moviesAdapter.setMovies(data);
        } else {
            binding.emptyView.setVisibility(View.VISIBLE);
            binding.emptyTitleText.setText(getString(R.string.server_problem));
            binding.emptySubtitleText.setText(getString(R.string.server_problem_sub_text));
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {
        moviesAdapter.setMovies(new ArrayList<Movie>());
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_number_of_columns_key))) {
            int columns = sharedPreferences.getInt(key, R.integer.number_of_columns);
            setAdapter(columns, moviesAdapter.getData());
        } else if (key.equals(getString(R.string.pref_sort_by_key))) {
            pageNoInteger = 1;
            sortBy = sharedPreferences.getString(key, getString(R.string.pref_sort_by_most_popular_default));
            if (sortBy.equals(getString(R.string.pref_sort_by_favorite))) {
                binding.recyclerView.setOnTouchListener(null);
                restartLoader();
            } else {
                binding.recyclerView.setOnTouchListener(this);
                restartLoader();
            }
        }
    }

    private void restartLoader() {
        if (checkConnection()) {
            moviesAdapter.clearMovies();
            getActivity().getSupportLoaderManager().restartLoader(0, null, this).forceLoad();
        } else {
            setInfoNoConnection();
        }
    }

    private boolean checkConnection() {
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    private void setInfoNoConnection() {
        binding.recyclerView.setVisibility(View.GONE);
        binding.loadingIndicator.setVisibility(View.GONE);
        binding.emptyView.setVisibility(View.VISIBLE);
        binding.emptyTitleText.setText(getString(R.string.no_connection));
        binding.emptySubtitleText.setText(getString(R.string.no_connection_sub_text));
    }

    public int setupSharedPreferences() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        int columns = sharedPreferences.getInt(getString(R.string.pref_number_of_columns_key), R.integer.number_of_columns);
        sortBy = sharedPreferences.getString(getString(R.string.pref_sort_by_key), getString(R.string.pref_sort_by_most_popular_default));
        return columns;
    }

    public void setAdapter(int columns, List<Movie> movieList) {
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), columns);
        layoutManager.setSpanCount(columns);
        binding.recyclerView.setLayoutManager(layoutManager);
        binding.recyclerView.setHasFixedSize(true);
        moviesAdapter = new MyAdapter(movieList, this, columns);
        binding.recyclerView.setAdapter(moviesAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (sharedPreferences != null) {
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        }
    }

    private void openBottomDialog() {
        View view = getLayoutInflater(new Bundle()).inflate(R.layout.menu_bottom, null);
        SettingsBottomSheetDialog dialog = new SettingsBottomSheetDialog(getContext());
        dialog.setContentView(view);
        dialog.show();
    }

    public interface OnImageClickListener {
        void onImageSelected(Movie movie);
    }

}