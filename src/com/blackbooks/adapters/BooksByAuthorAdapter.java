package com.blackbooks.adapters;

import java.util.List;

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
 * 
 */
public class BooksByAuthorAdapter extends ArrayAdapter<ListItem> {

	private List<ListItem> items;

	private LayoutInflater inflater;

	/**
	 * Constructor.
	 * 
	 * @param context
	 *            context.
	 * @param items
	 *            Items.
	 */
	public BooksByAuthorAdapter(Context context, List<ListItem> items) {
		super(context, 0, items);
		this.items = items;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;

		ListItem item = this.items.get(position);
		if (item != null) {
			ListItemType itemType = item.getListItemType();
			if (itemType == ListItemType.Entry) {
				BookItem entry = (BookItem) item;

				view = inflater.inflate(R.layout.list_books_by_author_item_book, null);

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

				view = inflater.inflate(R.layout.list_books_by_author_item_author, null);

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
