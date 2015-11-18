package com.rm.mydiet.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.rm.mydiet.R;
import com.rm.mydiet.model.DayPart;

import java.util.ArrayList;

/**
 * Created by alex
 */
public class DayPartsAdapter extends RecyclerView.Adapter<DayPartsAdapter.ViewHolder> {

    public interface OnItemClickListener {

        <T> void onItemClick(T data, int position);
    }

    private OnItemClickListener mListener;
    private ArrayList<DayPart> mDayPartList;

    public DayPartsAdapter(ArrayList<DayPart> dayPartList) {
        mDayPartList = dayPartList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_day_part, parent, false);

        ViewHolder holder = new ViewHolder(itemView);

        if (mListener != null)
            holder.setOnItemClickListener(mListener);

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DayPart dayPart = mDayPartList.get(position);
        holder.mBadge.setVisibility(dayPart.getEatenProducts().isEmpty() ? View.GONE : View.VISIBLE);
        holder.itemView.setSelected(dayPart.isSelected());
        switch (dayPart.getPartId()) {
            case DayPart.PART_BREAKFAST:
                holder.mIcon.setImageResource(R.drawable.breakfast);
                break;
            case DayPart.PART_LUNCH:
                holder.mIcon.setImageResource(R.drawable.lunch);
                break;
            case DayPart.PART_SNACK:
                holder.mIcon.setImageResource(R.drawable.snack);
                break;
            case DayPart.PART_DINNER:
                holder.mIcon.setImageResource(R.drawable.dinner);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mDayPartList.size();
    }

    public void setItemSelected(int position) {
        for (DayPart dayPart : mDayPartList)
            dayPart.setSelected(dayPart.getPartId() == position);
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private OnItemClickListener mClickListener;
        private ImageView mBadge;
        private ImageView mIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            mIcon = (ImageView) itemView.findViewById(R.id.day_part_icon);
            mBadge = (ImageView) itemView.findViewById(R.id.day_part_badge);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            if (mClickListener != null)
                this.mClickListener.onItemClick(v, getAdapterPosition());
        }

        public void setOnItemClickListener(OnItemClickListener listener) {

            mClickListener = listener;
        }
    }
}