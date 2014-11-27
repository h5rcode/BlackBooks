package com.blackbooks.adapters;

import java.util.List;

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
import com.blackbooks.model.nonpersistent.CategoryInfo;
import com.blackbooks.utils.StringUtils;

/**
 * An adapter handling instances of ListItem representing either a category or a
 * book.
 */
public class BooksByCategoryAdapter extends ArrayAdapter<ListItem> {

	private final LayoutInflater mInflater;
	private final ThumbnailManager mThumbnailManager;

	/**
	 * Constructor.
	 * 
	 * @param context
	 *            Context.
	 */
	public BooksByCategoryAdapter(Context context) {
		super(context, 0);
		this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mThumbnailManager = ThumbnailManager.getInstance();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;

		ListItem item = this.getItem(position);
		if (item != null) {
			ListItemType itemType = item.getListItemType();
			if (itemType == ListItemType.Entry) {
				BookItem entry = (BookItem) item;

				view = mInflater.inflate(R.layout.list_books_by_category_item_book, parent, false);

				ImageView imageView = (ImageView) view.findViewById(R.id.books_by_category_item_book_small_thumbnail);
				ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.books_by_category_item_book_progressBar);
				mThumbnailManager.drawSmallThumbnail(entry.getId(), getContext(), imageView, progressBar);
				TextView textTitle = (TextView) view.findViewById(R.id.books_by_category_item_book_title);
				textTitle.setText(entry.getTitle());

				TextView textAuthor = (TextView) view.findViewById(R.id.books_by_category_item_book_author);
				List<String> authorList = entry.getAuthors();
				String authors;
				if (authorList.size() > 0) {
					authors = StringUtils.join(entry.getAuthors().toArray(new String[] {}), ", ");
				} else {
					authors = getContext().getString(R.string.label_unspecified_author);
				}
				textAuthor.setText(authors);
			} else if (itemType == ListItemType.Header) {
				CategoryItem header = (CategoryItem) item;
				CategoryInfo category = header.getCategory();

				view = mInflater.inflate(R.layout.list_books_by_category_item_category, parent, false);

				TextView textViewName = (TextView) view.findViewById(R.id.books_by_category_item_category);
				TextView textViewTotal = (TextView) view.findViewById(R.id.books_by_category_item_total);

				textViewName.setText(category.name);
				String total = this.getContext().getString(R.string.label_total);
				total = String.format(total, category.books.size());
				textViewTotal.setText(total);
			}
		}

		return view;
	}
}
