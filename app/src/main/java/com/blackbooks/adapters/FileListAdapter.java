package com.blackbooks.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.blackbooks.R;

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
            convertView = mLayoutInflater.inflate(R.layout.list_files_item, parent, false);
        }
        File file = getItem(position);

        ImageView imageView = (ImageView) convertView.findViewById(R.id.files_item_icon);
        if (file.isDirectory()) {
            imageView.setImageResource(R.drawable.ic_folder_open_black);
        } else {
            imageView.setImageResource(R.drawable.ic_description_black);
        }
        TextView textView = (TextView) convertView.findViewById(R.id.files_item_fileName);
        textView.setText(file.getName());

        return convertView;
    }
}
