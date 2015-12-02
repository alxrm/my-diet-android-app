package com.rm.mydiet.ui;


import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rm.mydiet.R;
import com.rm.mydiet.utils.InputValidator.IntegerValidator;
import com.rm.mydiet.utils.Prefs;
import com.rm.mydiet.utils.TimeUtil;
import com.rm.mydiet.utils.base.BaseFragment;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends BaseFragment
        implements DatePickerDialog.OnDateSetListener {

    public static final String KEY_WEIGHT = "weight_key";
    public static final String KEY_HEIGHT = "height_key";
    public static final String KEY_BIRTH = "birth_key";
    public static final String KEY_GENDER = "gender_key";
    public static final String KEY_ACTIVITY = "activity_key";

    private RelativeLayout mWeightCell;
    private RelativeLayout mHeightCell;
    private RelativeLayout mBirthCell;
    private RelativeLayout mGenderCell;
    private RelativeLayout mActivityCell;
    private TextView mWeightText;
    private TextView mHeightText;
    private TextView mBirthText;
    private TextView mGenderText;
    private TextView mActivityText;

    private EditText mDialogInput;
    private View mInputDialog;

    private int mWeight;
    private int mHeigh;
    private long mBirth;
    private int mGender;
    private int mActivity;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initFields();

        mWeightCell = (RelativeLayout) findViewById(R.id.weight_cell);
        mWeightCell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeInputDialog(
                        "Введите свой вес",
                        Prefs.get().getInt(KEY_WEIGHT, 62),
                        KEY_WEIGHT,
                        new IntegerValidator() {
                            @Override
                            public boolean isValid(int data) {
                                return data >= 30 && data <= 300;
                            }
                        });
            }
        });

        mHeightCell = (RelativeLayout) findViewById(R.id.height_cell);
        mHeightCell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeInputDialog(
                        "Введите свой рост",
                        Prefs.get().getInt(KEY_HEIGHT, 168),
                        KEY_HEIGHT,
                        new IntegerValidator() {
                            @Override
                            public boolean isValid(int data) {
                                return data >= 40 && data <= 250 ;
                            }
                        });
            }
        });

        mBirthCell = (RelativeLayout) findViewById(R.id.birth_cell);
        mBirthCell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar birth = Calendar.getInstance();
                birth.setTimeInMillis(Prefs.get().getLong(KEY_BIRTH, TimeUtil.getToday()));
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        SettingsFragment.this,
                        birth.get(Calendar.YEAR),
                        birth.get(Calendar.MONTH),
                        birth.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "dpd");
            }
        });

        mGenderCell = (RelativeLayout) findViewById(R.id.gender_cell);
        mGenderCell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeSingleChoiceDialog(
                        "Выберите пол",
                        Prefs.get().getInt(KEY_GENDER, 0),
                        new String[] {"Мужской", "Женский"},
                        KEY_GENDER
                );
            }
        });

        mActivityCell = (RelativeLayout) findViewById(R.id.activity_cell);
        mActivityCell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeSingleChoiceDialog(
                        "Выберите уровень вашей подвижности",
                        Prefs.get().getInt(KEY_ACTIVITY, 1),
                        new String[] {"Низкая", "Средняя", "Высокая"},
                        KEY_ACTIVITY
                );
            }
        });

    }

    @SuppressLint("InflateParams")
    private void makeInputDialog(String msg,
                                 int data,
                                 final String field,
                                 final IntegerValidator validator) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        mInputDialog = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_input, null);
        mDialogInput = (EditText) mInputDialog.findViewById(R.id.input_data);
        mDialogInput.setText(String.valueOf(data));

        builder.setTitle(msg)
                .setCancelable(true)
                .setView(mInputDialog)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        int newData = Integer.parseInt(mDialogInput.getText().toString());
                        if (validator.isValid(newData)) updateField(newData, field);
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void makeSingleChoiceDialog(String msg,
                                        int data,
                                        String[] variants,
                                        final String field) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(msg)
                .setCancelable(true)
                .setSingleChoiceItems(variants, data, null)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        int selectedPosition =
                                ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                        updateField(selectedPosition, field);
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private <T> void updateField(T data, String field) {
        Prefs.put(field, data);
        initFields();

    }

    @SuppressLint("SetTextI18n")
    private void initFields() {

        mWeight = Prefs.get().getInt(KEY_WEIGHT, 62);
        mHeigh = Prefs.get().getInt(KEY_HEIGHT, 168);
        mBirth = Prefs.get().getLong(KEY_BIRTH, TimeUtil.getToday());
        mGender = Prefs.get().getInt(KEY_GENDER, 0);
        mActivity = Prefs.get().getInt(KEY_ACTIVITY, 1);

        mWeightText = (TextView) findViewById(R.id.weight_text);
        mHeightText = (TextView) findViewById(R.id.height_text);
        mBirthText = (TextView) findViewById(R.id.birth_text);
        mGenderText = (TextView) findViewById(R.id.gender_text);
        mActivityText = (TextView) findViewById(R.id.activity_text);

        mWeightText.setText(mWeight + " кг");
        mHeightText.setText(mHeigh + " см");
        mBirthText.setText(TimeUtil.formatBirthDate(mBirth));
        mGenderText.setText(mGender == 0 ? "Мужской" : "Женский");
        mActivityText.setText(
                mActivity == 0 ? "Низкая"
                : mActivity == 1 ? "Средная" : "Высокая"
        );
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        updateField(TimeUtil.getDate(year, monthOfYear, dayOfMonth), KEY_BIRTH);
    }
}
