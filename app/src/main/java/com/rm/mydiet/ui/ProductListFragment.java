package com.rm.mydiet.ui;


import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

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
        mProducts.setLayoutManager(new LinearLayoutManager(getActivity()));
        mProductsAdapter = new ProductAdapter(mProductList, false);
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

        DatabaseManager.getInstance().retrieveProducts(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_add_product, menu);
        MenuItem searchMenuItem = menu.getItem(0);

        mSearchView = (SearchView) searchMenuItem.getActionView();
        mSearchView.setIconified(false);
        mSearchView.setQueryHint("Search");

        View searchPlate = SearchViewHacker.getSearchPlate(mSearchView);

        int searchTextId = searchPlate
                .getContext()
                .getResources()
                .getIdentifier("android:id/search_src_text", null, null);

        TextView searchText = (TextView) searchPlate.findViewById(searchTextId);

        if (searchText != null) {
            searchText.setTextColor(Color.parseColor("#000000"));
            searchText.setHintTextColor(Color.parseColor("#0c000000"));
            searchText.setHint("Search");
        }


        SearchViewHacker.disablePlateBackGround(mSearchView);
        SearchViewHacker.disableHintImage(mSearchView);
//        SearchViewHacker.setCloseIcon(mSearchView, R.drawable.bar_clear_search);

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
                    DatabaseManager.getInstance().retrieveProducts(ProductListFragment.this);
                } else {
//                    SearchViewHacker.setCloseIcon(mSearchView, R.drawable.bar_clear_search);
                    DatabaseManager.getInstance().retrieveProducts(query, ProductListFragment.this);
                }
                return false;
            }
        });

        mSearchView.requestFocusFromTouch();
        SearchViewHacker.disableCloseButton(mSearchView);
        InputMethodManager inputMethodManager =
                (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
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
        mProductsAdapter.updateList(mProductList);
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
