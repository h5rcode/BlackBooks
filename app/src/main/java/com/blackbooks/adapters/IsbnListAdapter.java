package com.blackbooks.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.blackbooks.R;
import com.blackbooks.model.persistent.Isbn;

import java.text.SimpleDateFormat;

/**
 * ISBN list adapter.
 */
public final class IsbnListAdapter extends ArrayAdapter<Isbn> {

    private final SimpleDateFormat mDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    private final LayoutInflater mLayoutInflater;

    public IsbnListAdapter(Context context) {
        super(context, 0);
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Isbn isbn = getItem(position);

        convertView = mLayoutInflater.inflate(R.layout.list_isbns_item_isbn, parent, false);

        TextView textViewIsbn = (TextView) convertView.findViewById(R.id.isbns_item_isbn_number);
        TextView textViewDateAdded = (TextView) convertView.findViewById(R.id.isbns_item_isbn_date_added);

        textViewIsbn.setText(isbn.number);
        textViewDateAdded.setText(mDateFormat.format(isbn.dateAdded));

        return convertView;
    }
}
