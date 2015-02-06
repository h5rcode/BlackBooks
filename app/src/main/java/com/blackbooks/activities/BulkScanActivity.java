package com.blackbooks.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.blackbooks.R;
import com.blackbooks.fragments.BulkScanFragment;

/**
 * Bulk scan activity.
 */
public class BulkScanActivity extends AbstractDrawerActivity {

    private static final String TAG_FRAGMENT_BULK_SCAN = "TAG_FRAGMENT_BULK_SCAN";

    @Override
    protected DrawerActivity getDrawerActivity() {
        return DrawerActivity.BULK_SCAN;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentManager fm = getSupportFragmentManager();


        BulkScanFragment fragment = (BulkScanFragment) fm.findFragmentByTag(TAG_FRAGMENT_BULK_SCAN);

        if (fragment == null) {
            fragment = BulkScanFragment.newInstance();

            fm.beginTransaction() //
                    .replace(R.id.abstractDrawerActivity_frameLayout, fragment, TAG_FRAGMENT_BULK_SCAN) //
                    .commit();
        }
    }
}
