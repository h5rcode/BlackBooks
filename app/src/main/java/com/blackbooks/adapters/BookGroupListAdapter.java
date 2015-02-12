package com.blackbooks.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.blackbooks.R;
import com.blackbooks.model.nonpersistent.BookGroup;

/**
 * Adapter of the book group list.
 */
public final class BookGroupListAdapter extends ArrayAdapter<BookGroup> {

    private final LayoutInflater mLayoutInflater;

    /**
     * Constructor.
     *
     * @param context Context.
     */
    public BookGroupListAdapter(Context context) {
        super(context, 0);
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mLayoutInflater.inflate(R.layout.list_book_groups_item, parent, false);

        TextView textViewName = (TextView) convertView.findViewById(R.id.book_groups_item_name);
        TextView textViewCount = (TextView) convertView.findViewById(R.id.book_groups_item_count);

        BookGroup bookGroup = getItem(position);

        textViewName.setText(bookGroup.name);
        textViewCount.setText(String.valueOf(bookGroup.count));

        return convertView;
    }
}
