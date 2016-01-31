package com.blackbooks.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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

        final CsvColumn csvColumn = getItem(position);
        final Context context = getContext();

        final TextView textViewIndex = (TextView) convertView.findViewById(R.id.csv_columns_item_column_index);
        final TextView textViewName = (TextView) convertView.findViewById(R.id.csv_columns_item_column_name);
        final Spinner spinnerProperty = (Spinner) convertView.findViewById(R.id.csv_columns_item_property);

        final BookPropertiesAdapter spinnerAdapter = new BookPropertiesAdapter(context);

        spinnerProperty.setAdapter(spinnerAdapter);
        spinnerProperty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                CsvColumn.BookProperty bookProperty;
                if (view == null) {
                    bookProperty = null;
                } else {
                    bookProperty = (CsvColumn.BookProperty) view.getTag();

                    if (bookProperty == CsvColumn.BookProperty.ID && bookProperty != csvColumn.getBookProperty()) {
                        final String bookIdLabel = context.getString(R.string.label_book_id);
                        final String title = context.getString(R.string.title_dialog_mapping_book_id, bookIdLabel);
                        final String message = context.getString(R.string.message_mapping_book_id, bookIdLabel);

                        new AlertDialog.Builder(context)
                                .setTitle(title)
                                .setMessage(message)
                                .setNeutralButton(R.string.label_ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Do nothing.
                                    }
                                })
                                .setCancelable(false)
                                .show();
                    }
                }
                csvColumn.setBookProperty(bookProperty);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Do nothing.
            }
        });

        textViewIndex.setText(getContext().getString(R.string.label_column, csvColumn.getIndex()));
        final String csvColumnName = csvColumn.getName();
        if (csvColumnName == null) {
            textViewName.setVisibility(View.GONE);
        } else {
            textViewName.setText(String.valueOf(csvColumnName));
        }
        final CsvColumn.BookProperty bookProperty = csvColumn.getBookProperty();
        final int bookPropertyPosition = spinnerAdapter.getPosition(bookProperty);
        spinnerProperty.setSelection(bookPropertyPosition);

        return convertView;
    }
}
