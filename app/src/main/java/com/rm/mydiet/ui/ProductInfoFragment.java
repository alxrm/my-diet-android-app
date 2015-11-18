package com.rm.mydiet.ui;


import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.rm.mydiet.R;
import com.rm.mydiet.model.EatenProduct;
import com.rm.mydiet.model.Product;
import com.rm.mydiet.utils.InputValidator;
import com.rm.mydiet.utils.TextWatcherAdapter;
import com.rm.mydiet.utils.TimeUtil;
import com.rm.mydiet.utils.base.BaseFragment;

import static com.rm.mydiet.ui.MainFragment.KEY_INFO_EATEN;
import static com.rm.mydiet.ui.MainFragment.KEY_INFO_PRODUCT;
import static com.rm.mydiet.ui.OnFragmentInteractionListener.FRAGMENT_PROD_INFO;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProductInfoFragment extends BaseFragment {

    private EatenProduct mEaten;
    private Product mProd;
    private int mCount;
    private long mTime;

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
    private InputValidator mValidator;
    private Spinner mTypeSelector;

    public static ProductInfoFragment newInstance(Product product,
                                                  OnFragmentInteractionListener listener) {
        Bundle arguments = new Bundle();
        arguments.putParcelable(KEY_INFO_PRODUCT, product);
        ProductInfoFragment fragment = new ProductInfoFragment();
        fragment.setArguments(arguments);
        fragment.setInteractionListener(listener);
        return fragment;
    }

    public static ProductInfoFragment newInstance(EatenProduct eatenProduct,
                                                  OnFragmentInteractionListener listener) {
        Bundle arguments = new Bundle();
        arguments.putParcelable(MainFragment.KEY_INFO_EATEN, eatenProduct);
        ProductInfoFragment fragment = new ProductInfoFragment();
        fragment.setArguments(arguments);
        fragment.setInteractionListener(listener);
        return fragment;
    }

    public ProductInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEaten = (EatenProduct) getArguments().getParcelable(KEY_INFO_EATEN);
        if (mEaten != null) {
            mProd = mEaten.getProduct();
            mTime = mEaten.getTime();
            mCount = mEaten.getCount();
        } else {
            mProd = (Product) getArguments().getParcelable(KEY_INFO_PRODUCT);
            mCount = 1;
            mTime = TimeUtil.unixTime();
        }
        mValidator = new InputValidator() {
            @Override
            public boolean isValid(int data) {
                return data > 0 && data <= 1000;
            }
        };
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
        mProdName.setText(mProd.getName());

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

        initProductStats(mCount);
        initAddProductBox();
    }

    private void initAddProductBox() {
        mCountInput.setText(String.valueOf(mCount));
        mCountInput.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                mCount = TextUtils.isEmpty(s.toString()) ?
                        0 : Integer.parseInt(s.toString());
                if (mCount > 1000) {
                    mCount = 1000;
                    mCountInput.setText("1000");
                }
                if (mCount < 0) {
                    mCount = 0;
                    mCountInput.setText("0");
                }
                initProductStats(mCount);
            }
        });

        if (mEaten == null)
        mProceedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EatenProduct eatenProduct = getEatenProduct();
                if (eatenProduct == null) return;

                mInteractionListener.onFragmentAction(
                        eatenProduct,
                        FRAGMENT_PROD_INFO
                );
            }
        });

    }

    private EatenProduct getEatenProduct() {
        if (!mValidator.isValid(mCount)) return null;
        mEaten = mEaten == null ? new EatenProduct(mTime) : mEaten;
        mEaten.setCount(mCount);
        return mEaten;
    }

    private void initProductStats(int scalar) {
        float proteins = mProd.getProteins() * scalar;
        float carbs = mProd.getCarbohydrates() * scalar;
        float fats = mProd.getFats() * scalar;
        int cals = mProd.getCalories() * scalar;
        float full = proteins + carbs + fats;

        mProteinsText.setText(String.format("%s г.", proteins));
        mProteinsProgress.setProgress(getProgress(proteins, full));
        mProteinsBadge.setText("Белки");
        mCarbsText.setText(String.format("%s г.", carbs));
        mCarbsProgress.setProgress(getProgress(carbs, full));
        mCarbsBadge.setText("Углеводы");
        mFatsText.setText(String.format("%s г.", fats));
        mFatsProgress.setProgress(getProgress(fats, full));
        mFatsBadge.setText("Жиры");
        mCalsNum.setText(String.valueOf(cals));
    }

    private int getProgress(float value, float full) {
        return (int) (value / full * 100);
    }
}
