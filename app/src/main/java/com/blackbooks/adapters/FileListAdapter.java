package com.blackbooks.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.io.File;

/**
 * File list adapter.
 */
public final class FileListAdapter extends ArrayAdapter<File> {

    private final LayoutInflater mLayoutInflater;

    /**
     * Constructor.
     *
     * @param context Context.
     */
    public FileListAdapter(Context context) {
        super(context, android.R.layout.simple_list_item_1);
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = mLayoutInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        }
        File file = getItem(position);

        TextView textView = (TextView) convertView.findViewById(android.R.id.text1);
        textView.setText(file.getAbsolutePath());

        return convertView;
    }
}
