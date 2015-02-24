package com.blackbooks.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.MenuItem;

import com.blackbooks.R;
import com.blackbooks.fragments.BulkAddFragment;

/**
 * Bulk scan activity.
 */
public final class BulkAddActivity extends FragmentActivity {

    private static final String TAG_FRAGMENT_BULK_ADD = "TAG_FRAGMENT_BULK_ADD";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_activity);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        FragmentManager fm = getSupportFragmentManager();


        BulkAddFragment fragment = (BulkAddFragment) fm.findFragmentByTag(TAG_FRAGMENT_BULK_ADD);

        if (fragment == null) {
            fragment = BulkAddFragment.newInstance();

            fm.beginTransaction() //
                    .replace(R.id.fragmentActivity_frameLayout, fragment, TAG_FRAGMENT_BULK_ADD) //
                    .commit();
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
}
