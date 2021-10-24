package com.example.android.newsfeed_project;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class NewsActivity extends AppCompatActivity {

    private static final int mTotalPages = 6;
    private TabLayout tabLayout;
    private SimpleFragmentStatePagerAdapter pagerAdapter;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);

        pagerAdapter =
                new SimpleFragmentStatePagerAdapter(this, getSupportFragmentManager(), mTotalPages);

        for (int i = 0; i < mTotalPages; i++) {
            tabLayout.addTab(tabLayout.newTab().setText(pagerAdapter.getPageTitle(i)), i);
        }

        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
//            Toast.makeText(this, "No: " + tabLayout.getSelectedTabPosition(), Toast.LENGTH_SHORT).show();

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.detach(pagerAdapter.getItem(tabLayout.getSelectedTabPosition())).commitNow();
            ft.attach(pagerAdapter.getItem(tabLayout.getSelectedTabPosition())).commit();
            pagerAdapter.notifyDataSetChanged();
        }
        return super.onOptionsItemSelected(item);
    }
}