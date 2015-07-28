package com.blackbooks.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.blackbooks.R;
import com.blackbooks.fragments.FileChooserFragment;

import java.io.File;

/**
 * An activity to choose a file on the device.
 */
public final class FileChooserActivity extends FragmentActivity implements FileChooserFragment.FileChooserListener {

    public static final String EXTRA_CHOSEN_FILE = "EXTRA_CHOSEN_FILE";
    private static final String FILE_CHOOSER_FRAGMENT_TAG = "FILE_CHOOSER_FRAGMENT_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_activity);

        FragmentManager fm = getSupportFragmentManager();
        FileChooserFragment fileChooserFragment = (FileChooserFragment) fm.findFragmentByTag(FILE_CHOOSER_FRAGMENT_TAG);

        if (fileChooserFragment == null) {
            fileChooserFragment = new FileChooserFragment();
            fm.beginTransaction() //
                    .add(R.id.fragmentActivity_frameLayout, fileChooserFragment, FILE_CHOOSER_FRAGMENT_TAG) //
                    .commit();
        }
    }

    @Override
    public void onFileChosen(File file) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_CHOSEN_FILE, file);
        setResult(RESULT_OK, intent);
        finish();
    }
}
