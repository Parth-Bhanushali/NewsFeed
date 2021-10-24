package com.example.android.newsfeed_project;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.ViewGroup;

/**
 * It is the required solution to make all the tabs be as wide as they want with Scrollable mode and also let them cover the whole area
 * Use this ExtendedTabLayout in the XML file instead of TabLayout
 */
public class ExtendedTabLayout extends TabLayout {
    public ExtendedTabLayout(Context context) {
        super(context);
    }

    public ExtendedTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExtendedTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    // Also make sure to not provide any minimum width value in the XML file otherwise it will be useless
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        ViewGroup tabLayout = (ViewGroup) getChildAt(0);
        // Get the total number of tabs
        int childCount = tabLayout.getChildCount();

        // If there are tabs
        if (childCount != 0) {
            // Get the DisplayMetrics object from resources of the context
            DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();

            // Minimum width value of tabs
            // As because they can take up the whole blank space available there
            int tabMinWidth = displayMetrics.widthPixels / childCount;

            // Set minimum width for the tabs
            for (int i = 0; i < childCount; i++) {
                tabLayout.getChildAt(i).setMinimumWidth(tabMinWidth);
            }
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


}
