package com.example.android.camera2basic;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.ArrayList;

/**
 * Created by Zoe_Xu on 11/29/2015.
 */
public class PopupListView extends RelativeLayout {

    private final String TAG = "PopupListView";

    Context context;
    ListView listView;
    LinearLayout extendView;
    PopupListAdapter popupListAdapter;
    int heightSpace = 0;

    public PopupListView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.context = context;
        popupListAdapter = new PopupListAdapter();
    }

    public void init(ListView customListView) {
        setHeightSpace();
        RelativeLayout.LayoutParams listParams = new RelativeLayout.LayoutParams(ViewGroup
                .LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        LinearLayout.LayoutParams extendsParams = new LinearLayout.LayoutParams(ViewGroup
                .LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        if (customListView == null) {
            listView = new ListView(context);
        } else {
            listView = customListView;
        }

        if (extendView == null) {
            extendView = new LinearLayout(context);
            extendView.setOrientation(LinearLayout.VERTICAL);
        }
        listView.setDivider(null);
        listView.setLayoutParams(listParams);
        listView.setAdapter(popupListAdapter);
        extendView.setLayoutParams(extendsParams);
        extendView.setVisibility(GONE);
        this.addView(listView);
        this.addView(extendView);
    }

    public void setItemViews(ArrayList<? extends PopupView> items) {
        popupListAdapter.setItems(items);
    }

   /* private AdapterView.OnItemClickListener extend = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            //TODO GET POSITION AND START ANIMATION
            Log.d(TAG, "function_flag changed");
            switch (i){
                case 0:
                    StartScreen.function_flag = 4;//TODO random (1,6)
                    break;
                case 1:
                    StartScreen.function_flag = 1;
                    break;
                case 2:
                    StartScreen.function_flag = 2;
                    break;
                case 3:
                    StartScreen.function_flag = 3;
                    break;
                case 4:
                    StartScreen.function_flag = 4;
                    break;
                case 5:
                    StartScreen.function_flag = 5;
                    break;
                case 6:
                    StartScreen.function_flag = 6;
                    break;
            }
            StartScreen.isInit = true;
            Log.d(TAG, i + " click!!!!!!!! " + l);
        }
    };*/

    public void setHeightSpace() {
        int actionBarHeight = 0;
        TypedValue tv = new TypedValue();
        if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources()
                    .getDisplayMetrics());
        }

        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        this.heightSpace = actionBarHeight + result;
    }
}
