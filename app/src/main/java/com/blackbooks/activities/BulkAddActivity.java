package com.blackbooks.activities;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;

import com.blackbooks.R;
import com.blackbooks.fragments.BulkAddFragmentLookedUp;
import com.blackbooks.fragments.BulkAddFragmentPending;

/**
 * Bulk scan activity.
 */
public final class BulkAddActivity extends FragmentActivity implements ViewPager.OnPageChangeListener, ActionBar.TabListener {

    private static final int TAB_PENDING = 0;
    private static final int TAB_LOOKED_UP = 1;

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bulk_add);

        PagerAdapter pagerAdapter = new BulkAddPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.bulkAdd_viewPager);
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setOnPageChangeListener(this);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.addTab(actionBar.newTab().setText(getString(R.string.title_tab_bulk_add_pending)).setTabListener(this));
        actionBar.addTab(actionBar.newTab().setText(getString(R.string.title_tab_bulk_add_looked_up)).setTabListener(this));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        boolean result;
        switch (item.getItemId()) {
            case android.R.id.home:
                result = true;
                finish();
                break;

            default:
                result = super.onOptionsItemSelected(item);
                break;
        }

        return result;
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
        // Do nothing.
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
        // Do nothing.
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        // Do nothing.
    }

    @Override
    public void onPageSelected(int position) {
        getActionBar().setSelectedNavigationItem(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        // Do nothing.
    }

    private final class BulkAddPagerAdapter extends FragmentPagerAdapter {

        public BulkAddPagerAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public Fragment getItem(int position) {

            Fragment fragment;

            switch (position) {
                case TAB_PENDING:
                    fragment = BulkAddFragmentPending.newInstance();
                    break;

                case TAB_LOOKED_UP:
                    fragment = new BulkAddFragmentLookedUp();
                    break;

                default:
                    throw new IllegalArgumentException();
            }

            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
