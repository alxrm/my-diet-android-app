package com.rm.mydiet.utils;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import java.lang.reflect.Field;

/**
 * Created by alex
 */
public class SearchViewHacker {
    private static Drawable findDrawable(View source, String name) {
        try {
            Field field = source.getClass().getDeclaredField(name);
            field.setAccessible(true);
            return (Drawable) field.get(source);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        throw new RuntimeException();
    }

    private static View findView(View root, String name) {
        try {
            Field field = root.getClass().getDeclaredField(name);
            field.setAccessible(true);
            return (View) field.get(root);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        throw new RuntimeException();
    }

    public static void setCloseIcon(SearchView searchView, int res) {
        ImageView searchImageView = (ImageView) findView(searchView, "mCloseButton");
        searchImageView.setVisibility(View.VISIBLE);
        searchImageView.setAdjustViewBounds(false);
        searchImageView.setImageResource(res);
    }

    public static void disableCloseButton(SearchView searchView){
        ImageView searchImageView = (ImageView) findView(searchView, "mCloseButton");
        searchImageView.setVisibility(View.GONE);
        searchImageView.setImageBitmap(null);
        searchImageView.setAdjustViewBounds(true);
    }

    public static void disablePlateBackGround(SearchView searchView) {
        View plateView = findView(searchView, "mSearchPlate");
        plateView.setBackgroundColor(Color.TRANSPARENT);
    }

    public static void disableHintImage(SearchView searchView) {
        Drawable hint = findDrawable(searchView, "mSearchHintIcon");
        hint.setBounds(0, 0, 0, 0);
    }

    public static void setHint(SearchView searchView, String hint) {
        EditText editText = (EditText) findView(searchView, "mSearchSrcTextView");
        editText.setHintTextColor(Color.parseColor("#26000000"));
        editText.setHint(hint);
    }
}