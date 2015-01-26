package com.blackbooks.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.blackbooks.R;
import com.blackbooks.fragments.SummaryFragment;

public class SummaryActivity extends AbstractDrawerActivity {

    private static final String SUMMARY_FRAGMENT_TAG = "SUMMARY_FRAGMENT_TAG";

    @Override
    protected DrawerActivity getDrawerActivity() {
        return DrawerActivity.SUMMARY;
    }

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
}
