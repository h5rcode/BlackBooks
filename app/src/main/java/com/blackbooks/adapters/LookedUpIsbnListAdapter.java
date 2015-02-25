package com.blackbooks.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.blackbooks.R;
import com.blackbooks.cache.ThumbnailManager;
import com.blackbooks.model.persistent.Isbn;

import java.text.DateFormat;
import java.util.Locale;

/**
 * Looked up ISBN list adapter.
 */
public final class LookedUpIsbnListAdapter extends ArrayAdapter<Isbn> {

    private final DateFormat mDateFormat = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.SHORT, Locale.getDefault());
    private final LayoutInflater mLayoutInflater;
    private final ThumbnailManager mThumbnailManager;

    /**
     * Constructor.
     *
     * @param context Context.
     */
    public LookedUpIsbnListAdapter(Context context) {
        super(context, 0);
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mThumbnailManager = ThumbnailManager.getInstance();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.list_looked_up_isbns_item_isbn, parent, false);
        }

        Isbn isbn = getItem(position);

        ImageView imageView = (ImageView) convertView.findViewById(R.id.looked_up_isbns_item_isbn_small_thumbnail);
        ProgressBar progressBar = (ProgressBar) convertView.findViewById(R.id.looked_up_isbns_item_isbn_progressBar);
        TextView textViewIsbn = (TextView) convertView.findViewById(R.id.looked_up_isbns_item_isbn_number);
        TextView textViewDateAdded = (TextView) convertView.findViewById(R.id.looked_up_isbns_item_isbn_date_added);

        if (isbn.bookId != null) {
            mThumbnailManager.drawSmallThumbnail(isbn.bookId, getContext(), imageView, progressBar);
        }
        textViewIsbn.setText(isbn.number);
        textViewDateAdded.setText(mDateFormat.format(isbn.dateAdded));

        return convertView;
    }
}
