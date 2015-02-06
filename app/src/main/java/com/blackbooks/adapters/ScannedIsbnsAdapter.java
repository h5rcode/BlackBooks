package com.blackbooks.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.blackbooks.R;
import com.blackbooks.model.persistent.ScannedIsbn;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Scanned ISBNs adapter.
 */
public final class ScannedIsbnsAdapter extends ArrayAdapter<ScannedIsbn> {

    private final SimpleDateFormat mDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    private final LayoutInflater mLayoutInflater;

    public ScannedIsbnsAdapter(Context context, List<ScannedIsbn> objects) {
        super(context, 0, objects);
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ScannedIsbn scannedIsbn = getItem(position);

        convertView = mLayoutInflater.inflate(R.layout.list_scanned_isbns_item_scanned_isbn, parent, false);

        TextView textViewIsbn = (TextView) convertView.findViewById(R.id.scanned_isbns_item_scanned_isbn_isbn);
        TextView textViewScanDate = (TextView) convertView.findViewById(R.id.scanned_isbns_item_scanned_isbn_scan_date);

        textViewIsbn.setText(scannedIsbn.isbn);
        textViewScanDate.setText(mDateFormat.format(scannedIsbn.scanDate));

        return convertView;
    }
}
