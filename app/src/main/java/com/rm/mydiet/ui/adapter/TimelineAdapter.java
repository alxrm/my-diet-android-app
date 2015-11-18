package com.rm.mydiet.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rm.mydiet.R;
import com.rm.mydiet.model.EatenProduct;

import java.util.ArrayList;

/**
 * Created by alex
 */
public class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.ViewHolder> {

    public interface OnItemClickListener {
        <T> void onItemClick(T data, int position);
    }

    private OnItemClickListener mListener;
    private ArrayList<EatenProduct> mEatenProductList;

    public TimelineAdapter(ArrayList<EatenProduct> eatenProductList) {

        mEatenProductList = eatenProductList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.product_item, parent, false);

        ViewHolder holder = new ViewHolder(itemView);

        if (mListener != null)
            holder.setOnItemClickListener(mListener);

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        /**
         *
         * Set data to views here
         * For example:
         *
         * holder.someTextView.setText("Hello World");
         *
         */
    }

    @Override
    public int getItemCount() {
        return mEatenProductList.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {

        mListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private OnItemClickListener mClickListener;

        public ViewHolder(View itemView) {
            super(itemView);

            /**
             * Inflate Views here
             *
             * For example:
             *
             * TextView someTextView = (TextView) itemView.findViewById(...);
             */

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            if (mClickListener != null)
                this.mClickListener.onItemClick(v, getPosition());
        }

        public void setOnItemClickListener(OnItemClickListener listener) {

            mClickListener = listener;
        }
    }
}