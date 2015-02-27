package com.blackbooks.activities;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
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
import com.blackbooks.utils.VariableUtils;

/**
 * Bulk scan activity.
 */
public final class BulkAddActivity extends FragmentActivity implements ViewPager.OnPageChangeListener, ActionBar.TabListener {

    /**
     * The tab to select when displaying the activity.
     */
    public static final String EXTRA_SELECTED_TAB = "EXTRA_SELECTED_TAB";

    /**
     * Index of the tab "Pending ISBNs".
     */
    public static final int TAB_PENDING = 0;

    /**
     * Index of the tab "Looked up ISBNs".
     */
    public static final int TAB_LOOKED_UP = 1;

    private final VariableUtils mVariableUtils = VariableUtils.getInstance();

    private PagerAdapter mPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bulk_add);

        mPagerAdapter = new BulkAddPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.bulkAdd_viewPager);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOnPageChangeListener(this);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.addTab(actionBar.newTab().setText(getString(R.string.title_tab_bulk_add_pending)).setTabListener(this));
        actionBar.addTab(actionBar.newTab().setText(getString(R.string.title_tab_bulk_add_looked_up)).setTabListener(this));

        Intent i = getIntent();
        if (i.hasExtra(EXTRA_SELECTED_TAB)) {
            int selectedTab = i.getIntExtra(EXTRA_SELECTED_TAB, -1);
            switch (selectedTab) {
                case TAB_LOOKED_UP:
                    mViewPager.setCurrentItem(TAB_LOOKED_UP);
                    break;

                case TAB_PENDING:
                    mViewPager.setCurrentItem(TAB_PENDING);
                    break;

                default:
                    throw new IllegalArgumentException();
            }
        }
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
        int tabPosition = tab.getPosition();
        mViewPager.setCurrentItem(tabPosition);

        if ((tabPosition == TAB_PENDING && mVariableUtils.getReloadIsbnListPending()) || (tabPosition == TAB_LOOKED_UP && mVariableUtils.getReloadIsbnListLookedUp())) {
            mPagerAdapter.notifyDataSetChanged();
        }
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

        @Override
        public int getItemPosition(Object object) {
            int itemPosition = super.getItemPosition(object);
            if (object instanceof BulkAddFragmentPending && mVariableUtils.getReloadIsbnListPending()) {
                itemPosition = POSITION_NONE;
            } else if (object instanceof BulkAddFragmentLookedUp && mVariableUtils.getReloadIsbnListLookedUp()) {
                itemPosition = POSITION_NONE;
            }
            return itemPosition;
        }
    }
}
