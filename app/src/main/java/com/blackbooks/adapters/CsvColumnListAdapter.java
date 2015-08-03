package com.blackbooks.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.blackbooks.R;
import com.blackbooks.model.nonpersistent.CsvColumn;

/**
 * CSV column list adapter.
 */
public final class CsvColumnListAdapter extends ArrayAdapter<CsvColumn> {

    private final LayoutInflater mLayoutInflater;

    /**
     * Constructor.
     *
     * @param context Context.
     */
    public CsvColumnListAdapter(Context context) {
        super(context, 0);
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.list_csv_columns_item_column, parent, false);
        }
        TextView textViewIndex = (TextView) convertView.findViewById(R.id.csv_columns_item_column_index);
        TextView textViewName = (TextView) convertView.findViewById(R.id.csv_columns_item_column_name);

        CsvColumn csvColumn = getItem(position);

        textViewIndex.setText(getContext().getString(R.string.label_column, csvColumn.getIndex()));
        String csvColumnName = csvColumn.getName();
        if (csvColumnName == null) {
            textViewName.setVisibility(View.GONE);
        } else {
            textViewName.setText(String.valueOf(csvColumnName));
        }

        return convertView;
    }
}
