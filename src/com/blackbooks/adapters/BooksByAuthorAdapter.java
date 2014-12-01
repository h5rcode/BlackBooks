package com.blackbooks.adapters;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.blackbooks.R;
import com.blackbooks.cache.ThumbnailManager;
import com.blackbooks.model.nonpersistent.BookInfo;

/**
 * An adapter handling instances of ListItem representing either an author or a
 * book.
 */
public class BooksByAuthorAdapter extends ArrayAdapter<ListItem> implements SectionIndexer {

	private final LayoutInflater mInflater;
	private final ThumbnailManager mThumbnailManager;
	private final Map<String, Integer> mSectionPositionMap;
	private String[] mSections;

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
		this.mThumbnailManager = ThumbnailManager.getInstance();
		this.mSectionPositionMap = new TreeMap<String, Integer>();
		this.mSections = new String[] {};
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;

		ListItem item = this.getItem(position);
		if (item != null) {
			ListItemType itemType = item.getListItemType();
			if (itemType == ListItemType.Entry) {
				BookItem entry = (BookItem) item;
				BookInfo book = entry.getBook();

				view = mInflater.inflate(R.layout.list_books_by_author_item_book, parent, false);
				ImageView imageView = (ImageView) view.findViewById(R.id.item_book_small_thumbnail);
				ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.books_by_author_item_book_progressBar);
				ImageView imageRead = (ImageView) view.findViewById(R.id.books_by_author_item_book_imageRead);
				ImageView imageFavourite = (ImageView) view.findViewById(R.id.books_by_author_item_book_imageFavourite);

				mThumbnailManager.drawSmallThumbnail(book.id, getContext(), imageView, progressBar);
				TextView textView = (TextView) view.findViewById(R.id.item_book_title);
				textView.setText(book.title);

				if (book.isRead != 0) {
					imageRead.setVisibility(View.VISIBLE);
				} else {
					imageRead.setVisibility(View.GONE);
				}

				if (book.isFavourite != 0) {
					imageFavourite.setVisibility(View.VISIBLE);
				} else {
					imageFavourite.setVisibility(View.GONE);
				}

			} else if (itemType == ListItemType.Header) {
				AuthorItem header = (AuthorItem) item;

				view = mInflater.inflate(R.layout.list_books_by_author_item_author, parent, false);

				TextView textViewName = (TextView) view.findViewById(R.id.books_by_author_name);
				textViewName.setText(header.getName());

				TextView textViewTotalBooks = (TextView) view.findViewById(R.id.books_by_author_item_total);
				String total = this.getContext().getString(R.string.label_total);
				total = String.format(total, header.getTotalBooks());
				textViewTotalBooks.setText(total);
			}
		}
		return view;
	}

	@Override
	public void addAll(Collection<? extends ListItem> collection) {
		super.addAll(collection);
		mSectionPositionMap.clear();

		int i = 0;
		for (ListItem listItem : collection) {
			if (listItem.getListItemType() == ListItemType.Header) {
				AuthorItem authorItem = (AuthorItem) listItem;
				String authorName = authorItem.getName();
				String firstLetter = authorName.substring(0, 1);
				if (!mSectionPositionMap.containsKey(firstLetter)) {
					mSectionPositionMap.put(firstLetter, i);
				}
			}
			i++;
		}
		mSections = mSectionPositionMap.keySet().toArray(new String[mSectionPositionMap.size()]);
	}

	@Override
	public Object[] getSections() {
		return mSections;
	}

	@Override
	public int getPositionForSection(int section) {
		int index = section;
		if (index >= mSections.length) {
			index = mSections.length - 1;
		}
		return mSectionPositionMap.get(mSections[index]);
	}

	@Override
	public int getSectionForPosition(int position) {
		return 0;
	}
}
