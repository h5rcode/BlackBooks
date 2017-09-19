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
import com.blackbooks.model.persistent.Book;

import java.util.List;

/**
 * Adapter used to display duplicate books.
 */
public final class DuplicateBooksAdapter extends ArrayAdapter<Book> {

    private final LayoutInflater mLayoutInflater;
    private final ThumbnailManager mThumbnailManager;

    public DuplicateBooksAdapter(Context context, List<Book> bookList, ThumbnailManager thumbnailManager) {
        super(context, 0, bookList);
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mThumbnailManager = thumbnailManager;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.list_duplicate_books_item_book, parent, false);
        }

        Book book = getItem(position);

        TextView textView = (TextView) convertView.findViewById(R.id.duplicate_books_item_book_title);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.duplicate_books_item_book_small_thumbnail);
        ProgressBar progressBar = (ProgressBar) convertView.findViewById(R.id.duplicate_books_item_book_progressBar);

        textView.setText(book.title);
        mThumbnailManager.drawSmallThumbnail(book.id, getContext(), imageView, progressBar);

        return convertView;
    }
}
