package com.android.popularmovies.adapter;

import android.content.Context;
import android.graphics.Point;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.popularmovies.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ART_F on 2017-07-20.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MyViewHolder> {


    final private MoviesAdapter.ListItemClickListener mOnClickListener;
    private List<Movie> data;
    private int columns;

    public MoviesAdapter(List<Movie> myDataset, MoviesAdapter.ListItemClickListener listener, int columns) {
        data = myDataset;
        mOnClickListener = listener;
        this.columns = columns;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.row_movie_item, parent, false);
        RelativeLayout relativeLayout = view.findViewById(R.id.gridView);
        setViewSize(parent.getContext(), relativeLayout);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.bind(position);
    }

    public int getNumberOfColumns() {
        return columns;
    }


    public Object getDataAtPosition(int position) {
        return data.get(position);
    }


    public void setViewSize(Context context, RelativeLayout relativeLayout) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);
        int width = size.x / getNumberOfColumns();
        GridLayoutManager.LayoutParams layoutParams = (GridLayoutManager.LayoutParams) relativeLayout.getLayoutParams();
        layoutParams.height = (int) (width * 1.5);
        relativeLayout.setLayoutParams(layoutParams);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void clearMovies() {
        this.data.clear();
        notifyDataSetChanged();
    }

    public void setMovies(List<Movie> data) {
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    public List<Movie> getData() {
        return data;
    }

    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.itemImage) ImageView poster;
        @BindView(R.id.movieTitle) TextView movieTitle;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        public void bind(int position) {
            Movie movie = (Movie) getDataAtPosition(position);
            String posterURL = "http://image.tmdb.org/t/p/w185/" + movie.poster_path;
            Picasso.with(poster.getContext()).load(posterURL).into(poster);
//            Glide.with(poster.getContext()).load(posterURL).into(poster);
            movieTitle.setText(movie.original_title);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }

    }

}
