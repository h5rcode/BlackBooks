package com.blackbooks.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.blackbooks.R;
import com.blackbooks.model.persistent.Isbn;

import java.text.DateFormat;
import java.util.Locale;

/**
 * Pending sISBN list adapter.
 */
public final class PendingIsbnListAdapter extends ArrayAdapter<Isbn> {

    private final DateFormat mDateFormat = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.SHORT, Locale.getDefault());
    private final LayoutInflater mLayoutInflater;

    public PendingIsbnListAdapter(Context context) {
        super(context, 0);
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.list_pending_isbns_item_isbn, parent, false);
        }

        Isbn isbn = getItem(position);

        TextView textViewIsbn = (TextView) convertView.findViewById(R.id.pending_isbns_item_isbn_number);
        TextView textViewDateAdded = (TextView) convertView.findViewById(R.id.pending_isbns_item_isbn_date_added);

        textViewIsbn.setText(isbn.number);
        textViewDateAdded.setText(mDateFormat.format(isbn.dateAdded));

        return convertView;
    }
}
