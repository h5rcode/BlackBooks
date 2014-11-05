package com.blackbooks.fragments;

import com.blackbooks.R;
import com.blackbooks.activities.BookDisplay;
import com.blackbooks.adapters.BookItem;
import com.blackbooks.adapters.ListItem;
import com.blackbooks.adapters.ListItemType;
import com.blackbooks.utils.VariableUtils;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * Abstract book list fragment.
 */
public abstract class AbstractBookListFragment extends ListFragment {

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_book_list, container, false);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		ListItem item = (ListItem) getListAdapter().getItem(position);
		ListItemType itemType = item.getListItemType();

		if (itemType == ListItemType.Entry) {
			BookItem bookItem = (BookItem) item;
			Intent i = new Intent(this.getActivity(), BookDisplay.class);
			i.putExtra(BookDisplay.EXTRA_BOOK_ID, bookItem.getId());
			this.startActivity(i);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (getReloadBookList()) {
			loadBookList();
			setReloadBookListToFalse();
		}
	}

	/**
	 * Load the book list and notify the list adapter of the fragment.
	 */
	protected abstract void loadBookList();

	/**
	 * Return a value indicating if the book list should be reloaded.
	 * 
	 * @return True to refresh the book list, false otherwise.
	 */
	protected boolean getReloadBookList() {
		return VariableUtils.getInstance().getReloadBookList();
	}

	/**
	 * Set the value indicating if the book list should be reloaded to false.
	 */
	protected void setReloadBookListToFalse() {
		VariableUtils.getInstance().setReloadBookList(false);
	}
}
