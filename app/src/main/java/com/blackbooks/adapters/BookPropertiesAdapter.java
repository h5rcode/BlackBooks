package com.blackbooks.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.blackbooks.model.nonpersistent.CsvColumn;
import com.blackbooks.utils.CsvUtils;

/**
 * Book properties adapter.
 */
public final class BookPropertiesAdapter extends ArrayAdapter<CsvColumn.BookProperty> {

    private final LayoutInflater mInflater;

    /**
     * Constructor.
     *
     * @param context Context.
     */
    public BookPropertiesAdapter(Context context) {
        super(context, android.R.layout.simple_list_item_1);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for (CsvColumn.BookProperty bookProperty : CsvColumn.BookProperty.values()) {
            add(bookProperty);
        }
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getBookPropertyView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getBookPropertyView(position, convertView, parent);
    }

    private View getBookPropertyView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        CsvColumn.BookProperty bookProperty = getItem(position);
        if (bookProperty != null) {
            TextView textLanguageName = (TextView) convertView.findViewById(android.R.id.text1);
            textLanguageName.setText(CsvUtils.getBookPropertyResourceId(bookProperty));
            convertView.setTag(bookProperty);
        }
        return convertView;
    }
}
