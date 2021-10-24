package com.example.android.newsfeed_project;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;

public class SimpleFragmentStatePagerAdapter extends FragmentPagerAdapter {

    private int mTotalPages;
    private Context mContext;

    Fragment[] fragments = {new RecentNewsFragment(),
                            new PoliticsNewsFragment(),
                            new SportsNewsFragment(),
                            new TechnologyNewsFragment(),
                            new FoodNewsFragment(),
                            new WorldNewsFragment()};

    public SimpleFragmentStatePagerAdapter(Context context, FragmentManager fm, int totalPages) {
        super(fm);
        mContext = context;
        mTotalPages = totalPages;

    }

    @Override
    public Fragment getItem(int position) {
        return fragments[position];
    }

    @Override
    public int getCount() {
        return mTotalPages;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Recent";
            case 1:
                return "Politics";
            case 2:
                return "Sports";
            case 3:
                return "Technology";
            case 4:
                return "Food";
            case 5:
                return "World";
        }
        return super.getPageTitle(position);
    }

    public Fragment getCurrentFragment(int position) {
        if (position==0) {
            return new RecentNewsFragment();
        } else if (position==1) {
            return new PoliticsNewsFragment();
        } else if (position==2) {
            return new SportsNewsFragment();
        } else if (position==3) {
            return new TechnologyNewsFragment();
        } else if (position==4) {
            return new FoodNewsFragment();
        } else {
            return new WorldNewsFragment();
        }
    }

}
