package com.rm.mydiet;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rm.mydiet.api.Api;
import com.rm.mydiet.model.Product;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.rm.mydiet.MyDietApplication.context;

/**
 * Created by alex
 */
public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ViewHolder> {

    public interface OnItemClickListener {

        <T> void onItemClick(T data, int position);
    }

    private OnItemClickListener mListener;
    private ArrayList<Product> mProductList;

    public ProductsAdapter(ArrayList<Product> productList) {

        mProductList = productList;
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
        Product item = mProductList.get(position);
        holder.mName.setText(item.getName());
        holder.mInfo.setText(item.getCalories() + " ккал");
        Picasso.with(context())
                .load(Api.getImageUrl(item.getImg()))
                .placeholder(R.drawable.ic_maps_local_see)
                .into(holder.mIcon);
    }

    @Override
    public int getItemCount() {
        return mProductList.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private OnItemClickListener mClickListener;
        private TextView mName;
        private TextView mInfo;
        private ImageView mIcon;

        public ViewHolder(View itemView) {
            super(itemView);

            mName = (TextView) itemView.findViewById(R.id.product_name);
            mInfo = (TextView) itemView.findViewById(R.id.product_info);
            mIcon = (ImageView) itemView.findViewById(R.id.product_icon);
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