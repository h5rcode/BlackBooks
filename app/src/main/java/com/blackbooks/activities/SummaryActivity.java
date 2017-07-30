package com.blackbooks.activities;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.widget.SearchView;

import com.blackbooks.R;
import com.blackbooks.fragments.summary.SummaryFragment;

/**
 * The activity that displays an overview of the library.
 */
public final class SummaryActivity extends AbstractDrawerActivity {

    private static final String SUMMARY_FRAGMENT_TAG = "SUMMARY_FRAGMENT_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentManager fm = getSupportFragmentManager();
        SummaryFragment summaryFragment = (SummaryFragment) fm.findFragmentByTag(SUMMARY_FRAGMENT_TAG);

        if (summaryFragment == null) {
            summaryFragment = new SummaryFragment();
            fm.beginTransaction() //
                    .add(R.id.abstractDrawerActivity_frameLayout, summaryFragment, SUMMARY_FRAGMENT_TAG) //
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.summary, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.bookList_actionSearch).getActionView();
        ComponentName componentName = getComponentName();
        SearchableInfo searchableInfo = searchManager.getSearchableInfo(componentName);
        searchView.setSearchableInfo(searchableInfo);
        return true;
    }
}
