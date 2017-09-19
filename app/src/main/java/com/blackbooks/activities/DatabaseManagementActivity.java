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
import com.blackbooks.fragments.databasebackup.DatabaseBackupFragment;
import com.blackbooks.fragments.databasedelete.DatabaseDeleteFragment;
import com.blackbooks.fragments.databaserestore.DatabaseRestoreFragment;

/**
 * Database management activity.
 */
public final class DatabaseManagementActivity extends FragmentActivity implements ViewPager.OnPageChangeListener, ActionBar.TabListener {

    private static final int TAB_BACKUP = 0;
    private static final int TAB_RESTORE = 1;
    private static final int TAB_DELETE = 2;

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabbed_fragment_activity);

        PagerAdapter pagerAdapter = new DatabaseManagementPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.tabbedFragmentActivity_viewPager);
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setOnPageChangeListener(this);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
            actionBar.addTab(actionBar.newTab().setText(getString(R.string.title_tab_database_backup)).setTabListener(this));
            actionBar.addTab(actionBar.newTab().setText(getString(R.string.title_tab_database_restore)).setTabListener(this));
            actionBar.addTab(actionBar.newTab().setText(getString(R.string.title_tab_database_delete)).setTabListener(this));
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
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        // Do nothing.
    }

    @Override
    public void onPageSelected(int position) {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setSelectedNavigationItem(position);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        // Do nothing.
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        int tabPosition = tab.getPosition();
        mViewPager.setCurrentItem(tabPosition);
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
        // Do nothing.
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
        // Do nothing.
    }

    /**
     * Database managemnt pager adapter.
     */
    private final class DatabaseManagementPagerAdapter extends FragmentPagerAdapter {

        public DatabaseManagementPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            final Fragment fragment;
            switch (position) {
                case TAB_BACKUP:
                    fragment = new DatabaseBackupFragment();
                    break;
                case TAB_RESTORE:
                    fragment = new DatabaseRestoreFragment();
                    break;
                case TAB_DELETE:
                    fragment = new DatabaseDeleteFragment();
                    break;
                default:
                    throw new IllegalArgumentException();

            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}
