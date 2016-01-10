package com.blackbooks.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.blackbooks.R;

/**
 * About activity.
 */
public final class AboutActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        PackageInfo packageInfo;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
        String versionName = packageInfo.versionName;
        String version = getString(R.string.label_version, versionName);

        TextView textVersion = (TextView) findViewById(R.id.aboutActivity_textVersion);
        textVersion.setText(version);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        boolean result;
        switch (item.getItemId()) {

            case android.R.id.home:
                result = true;
                finish();
                break;

            default:
                result = super.onMenuItemSelected(featureId, item);
                break;
        }
        return result;
    }
}
