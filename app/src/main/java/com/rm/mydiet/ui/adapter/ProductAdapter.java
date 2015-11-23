package com.rm.mydiet.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rm.mydiet.R;
import com.rm.mydiet.api.Api;
import com.rm.mydiet.model.EatenProduct;
import com.rm.mydiet.model.Product;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by alex
 */
public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    public interface OnItemClickListener {

        void onItemClick(View v, int position);
    }

    private OnItemClickListener mListener;
    private ArrayList<Product> mProductList;
    private ArrayList<EatenProduct> mEatenList;
    private Context mContext;

    public ProductAdapter(ArrayList<Product> productList, Context context) {
        mContext = context;
        mProductList = productList;
        Log.d("ProductAdapter", "ProductAdapter list size " + mProductList.size());
    }

    public ProductAdapter(ArrayList<EatenProduct> productList) {
        mEatenList = productList;
        Log.d("ProductAdapter", "ProductAdapter list size " + mEatenList.size());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);

        ViewHolder holder = new ViewHolder(itemView);

        if (mListener != null)
            holder.setOnItemClickListener(mListener);

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Product currentProd = mEatenList == null ?
                mProductList.get(position) : mEatenList.get(position).getProduct();

        holder.mProductName.setText(currentProd.getName());
        holder.mProductCals.setText(currentProd.getCalories() + " ккал");

        Picasso.with(mContext)
                .load(Api.getImageUrl(currentProd.getImg()))
                .placeholder(R.drawable.photo)
                .into(holder.mProductIcon);
    }

    @Override
    public int getItemCount() {
        return mEatenList == null ?
                mProductList.size() : mEatenList.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {

        mListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private OnItemClickListener mClickListener;
        private ImageView mProductIcon;
        private TextView mProductName;
        private TextView mProductCals;

        public ViewHolder(View itemView) {
            super(itemView);
            mProductIcon = (ImageView) itemView.findViewById(R.id.product_icon);
            mProductName = (TextView) itemView.findViewById(R.id.product_name);
            mProductCals = (TextView) itemView.findViewById(R.id.product_cals);
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