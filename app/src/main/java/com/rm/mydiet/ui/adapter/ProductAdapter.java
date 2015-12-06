package com.rm.mydiet.ui.adapter;

import android.support.v7.widget.RecyclerView;
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
import java.util.Collection;

import static com.rm.mydiet.MyDietApplication.context;
import static com.rm.mydiet.utils.StringUtils.formatFloat;

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

    @SuppressWarnings("unchecked")
    public ProductAdapter(Collection<?> productList, boolean isEaten) {
        if (isEaten) {
            mEatenList = (ArrayList<EatenProduct>) productList;
        } else {
            mProductList = (ArrayList<Product>) productList;
        }
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
        holder.mProductCals.setText(formatFloat(currentProd.getCalories(), 0) + " ккал");
        holder.mDivider.setVisibility(View.VISIBLE);

        Picasso.with(context())
                .load(Api.getImageUrl(currentProd.getImg()))
                .placeholder(R.drawable.photo)
                .into(holder.mProductIcon);

        if (position == getItemCount() - 1) {
            holder.mDivider.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mEatenList == null ?
                mProductList.size() : mEatenList.size();
    }


    @SuppressWarnings("unchecked")
    public void updateList(Collection<?> dataSet, boolean isEaten) {
        if (isEaten)
            mEatenList = (ArrayList<EatenProduct>) dataSet;
        else
            mProductList = (ArrayList<Product>) dataSet;
        notifyDataSetChanged();
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
        private View mDivider;

        public ViewHolder(View itemView) {
            super(itemView);
            mProductIcon = (ImageView) itemView.findViewById(R.id.product_icon);
            mProductName = (TextView) itemView.findViewById(R.id.product_name);
            mProductCals = (TextView) itemView.findViewById(R.id.product_cals);
            mDivider = itemView.findViewById(R.id.product_divider);
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