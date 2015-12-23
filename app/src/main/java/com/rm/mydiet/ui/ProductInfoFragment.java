package com.rm.mydiet.ui;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.rm.mydiet.R;
import com.rm.mydiet.model.EatenProduct;
import com.rm.mydiet.model.Product;
import com.rm.mydiet.utils.base.BaseFragment;
import com.rm.mydiet.utils.view.TextWatcherAdapter;

import static com.rm.mydiet.ui.DataTransferring.FRAGMENT_PRODUCT_INFO_KEY_DAY_PART;
import static com.rm.mydiet.ui.DataTransferring.FRAGMENT_PRODUCT_INFO_KEY_EATEN_PRODUCT;
import static com.rm.mydiet.ui.DataTransferring.FRAGMENT_PRODUCT_INFO_KEY_PRODUCT;
import static com.rm.mydiet.ui.DataTransferring.PARENT_PRODUCT_INFO_DAY_PART;
import static com.rm.mydiet.ui.DataTransferring.PARENT_PRODUCT_INFO_TIME;
import static com.rm.mydiet.ui.OnFragmentInteractionListener.FRAGMENT_DIARY_EATEN_PRODUCT_CREATED;
import static com.rm.mydiet.utils.StringUtils.formatFloat;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProductInfoFragment extends BaseFragment {

    //region Vars and consts
    private boolean mIsInteractive;
    private Bundle mParentData;

    private EatenProduct mEaten;
    private Product mProduct;
    private int mCount;
    private float mScalar;
    private int mScalarId;
    private long mTime;
    private int mPart;

    private RelativeLayout mProteinsBox;
    private TextView mProteinsText;
    private ProgressBar mProteinsProgress;
    private TextView mProteinsBadge;

    private RelativeLayout mCarbsBox;
    private TextView mCarbsText;
    private ProgressBar mCarbsProgress;
    private TextView mCarbsBadge;

    private RelativeLayout mFatsBox;
    private TextView mFatsText;
    private ProgressBar mFatsProgress;
    private TextView mFatsBadge;

    private TextView mProdName;
    private TextView mCalsNum;

    private RelativeLayout mAddBox;
    private ImageView mProceedButton;
    private EditText mCountInput;
    private Spinner mTypeSelector;
    //endregion

    public static ProductInfoFragment newInstance(Product product) {
        Bundle arguments = new Bundle();
        arguments.putParcelable(FRAGMENT_PRODUCT_INFO_KEY_PRODUCT, product);
        ProductInfoFragment fragment = new ProductInfoFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    public static ProductInfoFragment newInstance(EatenProduct eatenProduct, int dayPart) {
        Bundle arguments = new Bundle();
        arguments.putParcelable(FRAGMENT_PRODUCT_INFO_KEY_EATEN_PRODUCT, eatenProduct);
        arguments.putInt(FRAGMENT_PRODUCT_INFO_KEY_DAY_PART, dayPart);
        ProductInfoFragment fragment = new ProductInfoFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    public ProductInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getActivity() instanceof OnFragmentInteractionListener)
            mInteractionListener = (OnFragmentInteractionListener) getActivity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mEaten = getArguments().getParcelable(FRAGMENT_PRODUCT_INFO_KEY_EATEN_PRODUCT);

        if (mEaten != null) {
            setIsInteractive(true);

            mPart = getArguments().getInt(FRAGMENT_PRODUCT_INFO_KEY_DAY_PART);
            mProduct = mEaten.getProduct();
            mTime = mEaten.getTime();
            mCount = mEaten.getCount();
            mScalarId = mEaten.getScalarId();
            mScalar = EatenProduct.getScalars(mProduct).get(mScalarId);
        } else {
            mProduct = getArguments().getParcelable(FRAGMENT_PRODUCT_INFO_KEY_PRODUCT);
            mCount = EatenProduct.DEFAULT_COUNT;
            mScalarId = EatenProduct.DEFAULT_SCALAR_ID;
            mScalar = EatenProduct.getScalars(mProduct).get(mScalarId);
            mParentData = mParent.getParentData();

            if (mParentData != null) {
                setIsInteractive(true);

                mTime = mParentData.getLong(PARENT_PRODUCT_INFO_TIME);
                mPart = mParentData.getInt(PARENT_PRODUCT_INFO_DAY_PART);
            } else {
                setIsInteractive(false);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_product_info, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mProdName = (TextView) findViewById(R.id.prod_info_name);
        mProdName.setText(mProduct.getName());

        mProteinsBox = (RelativeLayout) findViewById(R.id.prod_info_proteins_box);
        mProteinsText = (TextView) mProteinsBox.findViewById(R.id.prod_info_stat_text);
        mProteinsProgress = (ProgressBar) mProteinsBox.findViewById(R.id.prod_info_stat_progress);
        mProteinsBadge = (TextView) mProteinsBox.findViewById(R.id.prod_info_stat_badge);

        mCarbsBox = (RelativeLayout) findViewById(R.id.prod_info_carbs_box);
        mCarbsText = (TextView) mCarbsBox.findViewById(R.id.prod_info_stat_text);
        mCarbsProgress = (ProgressBar) mCarbsBox.findViewById(R.id.prod_info_stat_progress);
        mCarbsBadge = (TextView) mCarbsBox.findViewById(R.id.prod_info_stat_badge);

        mFatsBox = (RelativeLayout) findViewById(R.id.prod_info_fats_box);
        mFatsText = (TextView) mFatsBox.findViewById(R.id.prod_info_stat_text);
        mFatsProgress = (ProgressBar) mFatsBox.findViewById(R.id.prod_info_stat_progress);
        mFatsBadge = (TextView) mFatsBox.findViewById(R.id.prod_info_stat_badge);

        mCalsNum = (TextView) findViewById(R.id.prod_info_cals_num);

        mAddBox = (RelativeLayout) findViewById(R.id.prod_info_add_box);
        mTypeSelector = (Spinner) findViewById(R.id.prod_info_count_selector);
        mCountInput = (EditText) findViewById(R.id.prod_info_count_text);
        mProceedButton = (ImageView) findViewById(R.id.prod_info_add_btn);

        setupScalarSelector();
        setupProductStats();
        setupAddProductBox();
    }

    private void setupScalarSelector() {
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_dropdown_item_1line,
                EatenProduct.getScalarNames(mProduct)
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTypeSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mScalarId = position;
                mScalar = EatenProduct.getScalars(mProduct).get(mScalarId);
                setupProductStats();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mTypeSelector.setAdapter(spinnerAdapter);
        mTypeSelector.setSelection(mScalarId, false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            default: break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupAddProductBox() {
        mCountInput.setText(String.valueOf(mCount));
        mCountInput.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                mCount = TextUtils.isEmpty(s.toString()) ?
                        EatenProduct.DEFAULT_COUNT : Integer.parseInt(s.toString());
                setupProductStats();
            }
        });

        mProceedButton.setVisibility(isInteractive() ? View.VISIBLE : View.GONE);
        mProceedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                completeAddProduct();
            }
        });
    }

    private void completeAddProduct() {
        EatenProduct eatenProduct = getEatenProduct();
        if (eatenProduct == null) return;

        Bundle data = new Bundle();
        data.putParcelable(DataTransferring.CALLBACK_PRODUCT_INFO_EATEN_PRODUCT, eatenProduct);
        data.putInt(DataTransferring.CALLBACK_PRODUCT_INFO_DAY_PART, mPart);
        mInteractionListener.onFragmentAction(data, FRAGMENT_DIARY_EATEN_PRODUCT_CREATED);
    }

    private EatenProduct getEatenProduct() {
        if (mCount <= 0 || mProduct == null) return null;
        mEaten = mEaten == null ? new EatenProduct(mTime) : mEaten;
        mEaten.setCount(mCount);
        mEaten.setScalarId(mScalarId);
        mEaten.setProduct(mProduct);
        return mEaten;
    }

    private void setupProductStats() {
//        Log.d("ProductInfoFragment", "product info " + mProduct.getInfo());
        float proteins = mProduct.getProteins() * mCount * mScalar;
        float carbs = mProduct.getCarbohydrates() * mCount * mScalar;
        float fats = mProduct.getFats() * mCount * mScalar;
        float cals = mProduct.getCalories() * mCount * mScalar;
        float full = proteins + carbs + fats;

        mProteinsText.setText(String.format("%s г.", formatFloat(proteins)));
        mProteinsProgress.setProgress(calculateProgress(proteins, full));
        mProteinsBadge.setText("Белки");
        mCarbsText.setText(String.format("%s г.", formatFloat(carbs)));
        mCarbsProgress.setProgress(calculateProgress(carbs, full));
        mCarbsBadge.setText("Углеводы");
        mFatsText.setText(String.format("%s г.", formatFloat(fats)));
        mFatsProgress.setProgress(calculateProgress(fats, full));
        mFatsBadge.setText("Жиры");
        mCalsNum.setText(formatFloat(cals));
    }

    private int calculateProgress(float value, float full) {
        return (int) (value / full * 100);
    }

    private boolean isInteractive() {
        return mIsInteractive;
    }

    private void setIsInteractive(boolean isInteractive) {
        mIsInteractive = isInteractive;
    }
}
