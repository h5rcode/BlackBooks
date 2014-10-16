package com.blackbooks.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.blackbooks.R;

/**
 * An adapter handling instances of ListItem representing either an author or a
 * book.
 */
public class BooksByAuthorAdapter extends ArrayAdapter<ListItem> {

	private LayoutInflater mInflater;

	/**
	 * Constructor.
	 * 
	 * @param context
	 *            context.
	 * @param items
	 *            Items.
	 */
	public BooksByAuthorAdapter(Context context) {
		super(context, 0);
		this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;

		ListItem item = this.getItem(position);
		if (item != null) {
			ListItemType itemType = item.getListItemType();
			if (itemType == ListItemType.Entry) {
				BookItem entry = (BookItem) item;

				view = mInflater.inflate(R.layout.list_books_by_author_item_book, null);

				byte[] smallThumbnail = entry.getSmallThumbnail();
				if (smallThumbnail != null && smallThumbnail.length > 0) {
					ImageView imageView = (ImageView) view.findViewById(R.id.item_book_small_thumbnail);
					Bitmap bitmap = BitmapFactory.decodeByteArray(smallThumbnail, 0, smallThumbnail.length);
					imageView.setImageBitmap(bitmap);
				}

				TextView textView = (TextView) view.findViewById(R.id.item_book_title);
				textView.setText(entry.getText());

			} else if (itemType == ListItemType.Header) {
				AuthorItem header = (AuthorItem) item;

				view = mInflater.inflate(R.layout.list_books_by_author_item_author, null);

				TextView textViewName = (TextView) view.findViewById(R.id.header_author_name);
				textViewName.setText(header.getName());

				/*
				 * TextView textViewTotalBooks = (TextView)
				 * view.findViewById(R.id.header_author_total_books);
				 * textViewTotalBooks
				 * .setText(String.valueOf(header.getTotalBooks()));
				 */
			}
		}

		return view;
	}
}
