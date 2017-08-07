package com.android.popularmovies.adapters;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.popularmovies.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class DetailsAdapter extends RecyclerView.Adapter<DetailsAdapter.ViewHolder> {

    final private ListItemClickListener mOnClickListener;
    private List<JSONObject> data;
    private int loaderId;


    public DetailsAdapter(List<JSONObject> data, ListItemClickListener mOnClickListener, int loaderId) {
        this.data = data;
        this.mOnClickListener = mOnClickListener;
        this.loaderId = loaderId;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = null;
        if (loaderId == 2) {
            view = inflater.inflate(R.layout.row_detail_review, viewGroup, false);
        } else if (loaderId == 3) {
            view = inflater.inflate(R.layout.row_detail_trailer, viewGroup, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(List<JSONObject> data) {
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }

    class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        @BindView(R.id.detailAuthor) TextView detailAuthor;
        @Nullable
        @BindView(R.id.detailContent)
        TextView detailContent;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        void bind(int listIndex) {
            JSONObject jsonObject = data.get(listIndex);
            switch (loaderId) {
                case 2:
                    try {
                        detailAuthor.setText(jsonObject.getString("author"));
                        detailContent.setText(jsonObject.getString("content"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 3:
                    try {
                        detailAuthor.setText(jsonObject.getString("name"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }
    }
}
