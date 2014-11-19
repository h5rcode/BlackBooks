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
import com.blackbooks.utils.StringUtils;

/**
 * An adapter handling instances of ListItem representing either the language of
 * a book or a book.
 */
public class BooksByLanguageAdapter extends ArrayAdapter<ListItem> {

	private LayoutInflater mInflater;

	/**
	 * Constructor.
	 * 
	 * @param context
	 *            Context.
	 */
	public BooksByLanguageAdapter(Context context) {
		super(context, 0);
		this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;

		ListItem item = this.getItem(position);
		if (item != null) {
			ListItemType itemType = item.getListItemType();
			if (itemType == ListItemType.Entry) {
				BookItem entry = (BookItem) item;

				view = mInflater.inflate(R.layout.list_books_by_language_item_book, parent, false);

				byte[] smallThumbnail = entry.getSmallThumbnail();
				if (smallThumbnail != null && smallThumbnail.length > 0) {
					ImageView imageView = (ImageView) view.findViewById(R.id.books_by_language_item_book_small_thumbnail);
					Bitmap bitmap = BitmapFactory.decodeByteArray(smallThumbnail, 0, smallThumbnail.length);
					imageView.setImageBitmap(bitmap);
				}

				TextView textTitle = (TextView) view.findViewById(R.id.books_by_language_item_book_title);
				textTitle.setText(entry.getTitle());

				TextView textAuthor = (TextView) view.findViewById(R.id.books_by_language_item_book_author);
				List<String> authorList = entry.getAuthors();
				String authors;
				if (authorList.size() > 0) {
					authors = StringUtils.join(entry.getAuthors().toArray(new String[] {}), ", ");
				} else {
					authors = getContext().getString(R.string.label_unspecified_author);
				}
				textAuthor.setText(authors);
			} else if (itemType == ListItemType.Header) {
				LanguageItem header = (LanguageItem) item;

				view = mInflater.inflate(R.layout.list_books_by_language_item_language, parent, false);

				TextView textLanguage = (TextView) view.findViewById(R.id.books_by_language_item_language);
				TextView textViewTotal = (TextView) view.findViewById(R.id.books_by_language_item_total);

				textLanguage.setText(header.getDisplayName());
				String total = this.getContext().getString(R.string.label_total);
				total = String.format(total, header.getTotal());
				textViewTotal.setText(total);
			}
		}

		return view;
	}
}
