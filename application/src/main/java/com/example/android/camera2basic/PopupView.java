package com.example.android.camera2basic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Baobomb on 2015/9/25.
 */
public abstract class PopupView {
    LayoutInflater layoutInflater;
    int resId;
    View view;
    View extendPopupView;

    public PopupView(Context context, int resId) {
        layoutInflater = LayoutInflater.from(context);
        this.resId = resId;
        view = layoutInflater.inflate(resId, null);
        //extendPopupView = layoutInflater.inflate(resId,null);
        setViewsElements(view);
        //setViewsElements(extendPopupView);
    }

    public View getPopupView() {
        return view;
    }

    public abstract void setViewsElements(View view);
}
