package com.rm.mydiet.ui;


import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.rm.mydiet.R;
import com.rm.mydiet.model.Product;
import com.rm.mydiet.ui.adapter.ProductAdapter;
import com.rm.mydiet.utils.base.BaseFragment;
import com.rm.mydiet.utils.persistence.DatabaseListener;
import com.rm.mydiet.utils.persistence.DatabaseManager;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProductListFragment extends BaseFragment implements DatabaseListener {

    private RecyclerView mProducts;
    private RelativeLayout mAddProductButton;
    private ArrayList<Product> mProductList;
    private ProductAdapter mProductsAdapter;

    public ProductListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_product_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mProducts = (RecyclerView) findViewById(R.id.prod_list);
        mProducts.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAddProductButton = (RelativeLayout) findViewById(R.id.prod_list_create_button);
        mAddProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ProductListFragment", "onClick STUB");
            }
        });
        DatabaseManager.getInstance().retrieveProducts(this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onReceiveData(Collection<?> data) {
        mProductList = (ArrayList<Product>) data;
        mProductsAdapter = new ProductAdapter(mProductList, false);
        mProductsAdapter.setOnItemClickListener(new ProductAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                if (position != -1) {
                    mParent.addFragment(
                            ProductInfoFragment.newInstance(mProductList.get(position)),
                            "Информация о продукте");
                }
            }
        });
        mProducts.setAdapter(mProductsAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mProductList = null;
    }

    @Override
    public void onError(Exception e) {
        Log.d("ProductListFragment", "onError exc " + e);
    }
}
