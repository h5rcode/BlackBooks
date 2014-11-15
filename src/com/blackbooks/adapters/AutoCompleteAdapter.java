package com.blackbooks.adapters;

import java.util.List;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

/**
 * AutoCompleteAdapter.
 * 
 */
public final class AutoCompleteAdapter<T> extends ArrayAdapter<String> implements Filterable {

	private AutoCompleteSearcher<T> mSearcher;
	private Filter mFilter;
	private List<T> mResult;

	/**
	 * Constructor.
	 * 
	 * @param context
	 *            Context.
	 * @param resource
	 *            Resource.
	 * @param searcher
	 *            Searcher.
	 */
	public AutoCompleteAdapter(Context context, int resource, AutoCompleteSearcher<T> searcher) {
		super(context, resource);
		this.mSearcher = searcher;
		this.mFilter = new AutoCompleteFilter();
	}

	@Override
	public int getCount() {
		return mResult.size();
	}

	@Override
	public String getItem(int position) {
		return mSearcher.getDisplayLabel(mResult.get(position));
	}

	@Override
	public Filter getFilter() {
		return mFilter;
	}

	/**
	 * An interface to search items using a text input from the user.
	 * 
	 * @param <T>
	 *            The type of the searched items.
	 */
	public interface AutoCompleteSearcher<T> {
		/**
		 * Perform the search of items. Use the constraint to search the
		 * database, call a web service, etc.
		 * 
		 * @param constraint
		 *            The text input to use for the search.
		 * @return The list of items corresponding to the text input.
		 */
		List<T> search(CharSequence constraint);

		/**
		 * Return the label of an item that will be displayed in the result
		 * list.
		 * 
		 * @param item
		 *            The item to display.
		 * @return The label of the label that will appear in the result list.
		 */
		String getDisplayLabel(T item);
	}

	/**
	 * The filter class.
	 */
	private final class AutoCompleteFilter extends Filter {

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			FilterResults results = new FilterResults();

			if (constraint != null) {
				mResult = mSearcher.search(constraint);

				results.values = mResult;
				results.count = mResult.size();
			}
			return results;
		}

		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {
			if (results != null && results.count > 0) {
				notifyDataSetChanged();
			} else {
				notifyDataSetInvalidated();
			}
		}
	}
}
