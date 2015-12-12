package com.rm.mydiet.ui;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.rm.mydiet.R;
import com.rm.mydiet.model.Product;
import com.rm.mydiet.ui.adapter.ProductAdapter;
import com.rm.mydiet.utils.KeyBoardUtil;
import com.rm.mydiet.utils.SearchViewHacker;
import com.rm.mydiet.utils.base.BaseFragment;
import com.rm.mydiet.utils.persistence.DatabaseListener;
import com.rm.mydiet.utils.persistence.DatabaseManager;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProductListFragment extends BaseFragment implements DatabaseListener {

    private SearchView mSearchView;
    private RecyclerView mProducts;
    private RelativeLayout mAddProductButton;
    private ArrayList<Product> mProductList = new ArrayList<>();
    private ProductAdapter mProductsAdapter;
    private ProgressBar mProgress;

    public ProductListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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
        mProgress = (ProgressBar) findViewById(R.id.prod_list_progress);
        mProducts.setLayoutManager(new LinearLayoutManager(getActivity()));
        mProductsAdapter = new ProductAdapter(mProductList, false);
        mProducts.setAdapter(mProductsAdapter);
        mProductsAdapter.setOnItemClickListener(new ProductAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                if (position != -1) {
                    KeyBoardUtil.hide(mSearchView, getActivity());
                    mParent.addFragment(
                            ProductInfoFragment.newInstance(mProductList.get(position)),
                            "Информация о продукте");
                }
            }
        });

        mAddProductButton = (RelativeLayout) findViewById(R.id.prod_list_create_button);
        mAddProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ProductListFragment", "onClick STUB");
            }
        });

        DatabaseManager.getInstance().retrieveProducts(null, this);
        setListIsLoading(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_add_product, menu);
        MenuItem searchMenuItem = menu.getItem(0);

        mSearchView = (SearchView) searchMenuItem.getActionView();
        mSearchView.setIconified(false);

        SearchViewHacker.disablePlateBackGround(mSearchView);
        SearchViewHacker.disableHintImage(mSearchView);
        SearchViewHacker.setHint(mSearchView, "Search");
        SearchViewHacker.setCloseIcon(mSearchView, R.drawable.ic_search_clear);

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                KeyBoardUtil.hide(mSearchView, getActivity());
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                if (query.equals("")) {
                    SearchViewHacker.disableCloseButton(mSearchView);
                    DatabaseManager.getInstance().retrieveProducts(null, ProductListFragment.this);
                } else {
                    SearchViewHacker.setCloseIcon(mSearchView, R.drawable.ic_search_clear);
                    DatabaseManager.getInstance().retrieveProducts(query, ProductListFragment.this);
                }
                setListIsLoading(true);
                return false;
            }
        });

        mSearchView.requestFocusFromTouch();
        SearchViewHacker.disableCloseButton(mSearchView);
        InputMethodManager inputMethodManager =
                (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    private void setListIsLoading(boolean isLoading) {
        mProducts.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        mProgress.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                KeyBoardUtil.hide(mSearchView, getActivity());
                onBackPressed();
                break;
            default: break;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onReceiveData(Collection<?> data) {
        mProductList = (ArrayList<Product>) data;
        mProductsAdapter.updateList(mProductList, false);
        setListIsLoading(false);
    }

    @Override
    public void onStop() {
        super.onStop();
        KeyBoardUtil.hide(mSearchView, getActivity());
    }

    @Override
    public void onError(Exception e) {
        Log.d("ProductListFragment", "onError exc " + e);
    }
}
