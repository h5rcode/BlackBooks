package com.blackbooks.activities;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.blackbooks.adapters.AutoCompleteAdapter;
import com.blackbooks.adapters.EditableArrayAdapter;

/**
 * Activity to edit the authors of a book.
 */
public abstract class ListEdit<T> extends Activity {

	public static final String EXTRA_BOOK_TITLE = "EXTRA_BOOK_TITLE";

	private static final String BOOK_TITLE = "BOOK_TITLE";

	private String mBookTitle;
	protected ArrayList<T> mObjectList;
	private LinkedHashMap<String, T> mObjectMap;
	private AutoCompleteAdapter<T> mAutoCompleteAdapter;
	private EditableArrayAdapter<T> mEditableArrayAdapter;

	private TextView mTextInfo;
	private AutoCompleteTextView mTextObject;
	private ListView mListObjects;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(getMenuId(), menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		boolean result = false;
		int itemId = item.getItemId();

		if (itemId == getActionIdSave()) {
			addObjectList();
			result = true;
		}
		return result;
	}

	/**
	 * Add the object to the list.
	 * 
	 * @param view
	 *            View.
	 */
	public void addObject(View view) {
		String objectName = mTextObject.getText().toString().trim();

		if (objectName.length() == 0) {
			mTextObject.setError(getString(getStringIdObjectMissing()));
		} else {
			if (mObjectMap.containsKey(objectName)) {
				String message = getString(getStringIdObjectAlreadyPresent());
				message = String.format(message, objectName);
				mTextObject.setError(message);
			} else {
				T object = getObject(objectName);

				mObjectMap.put(objectName, object);
				mEditableArrayAdapter.add(object);

				String message = getString(getStringIdObjectAdded());
				message = String.format(message, objectName);
				Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

				mTextObject.setText(null);
				mTextObject.setError(null);
			}
		}
	}

	protected abstract int getActionIdSave();

	protected abstract int getListViewId();

	protected abstract int getAutoCompleteTextViewId();

	protected abstract int getTextViewIdInfo();

	protected abstract int getLayoutIdActivity();

	protected abstract int getStringIdObjectAdded();

	protected abstract int getStringIdObjectAlreadyPresent();

	protected abstract int getStringIdObjectMissing();

	protected abstract int getStringIdEditBook();

	protected abstract int getStringIdEditUntitledBook();

	protected abstract T getObject(String objectKey);

	protected abstract String getObjectKey(T object);

	protected abstract AutoCompleteAdapter<T> getAutoCompleteAdapter();

	protected abstract EditableArrayAdapter<T> getEditableArrayAdapter();

	protected abstract String getExtraObjectListName();

	protected abstract int getMenuId();

	protected abstract String getSavedInstanceObjectListName();

	/**
	 * Remove an author from the list.
	 * 
	 * @param view
	 *            View.
	 */
	public void removeObject(View view) {
		@SuppressWarnings("unchecked")
		T object = (T) view.getTag();
		mObjectMap.remove(getObjectKey(object));
		mEditableArrayAdapter.remove(object);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(getLayoutIdActivity());

		if (savedInstanceState != null) {
			mBookTitle = savedInstanceState.getString(BOOK_TITLE);
			mObjectList = (ArrayList<T>) savedInstanceState.getSerializable(getSavedInstanceObjectListName());
		} else {
			Intent intent = getIntent();
			mBookTitle = intent.getStringExtra(EXTRA_BOOK_TITLE);
			mObjectList = (ArrayList<T>) intent.getSerializableExtra(getExtraObjectListName());
		}
		if (mObjectList == null) {
			mObjectList = new ArrayList<T>();
		}

		mObjectMap = new LinkedHashMap<String, T>();
		for (T object : mObjectList) {
			mObjectMap.put(getObjectKey(object), object);
		}

		mAutoCompleteAdapter = getAutoCompleteAdapter();
		mEditableArrayAdapter = getEditableArrayAdapter();

		mTextInfo = (TextView) findViewById(getTextViewIdInfo());
		mTextObject = (AutoCompleteTextView) findViewById(getAutoCompleteTextViewId());
		mListObjects = (ListView) findViewById(getListViewId());

		if (mBookTitle == null || mBookTitle.trim().equals("")) {
			mTextInfo.setText(getString(getStringIdEditUntitledBook()));
		} else {
			String message = getString(getStringIdEditBook());
			message = String.format(message, mBookTitle.trim());
			mTextInfo.setText(message);
		}
		mTextObject.setAdapter(mAutoCompleteAdapter);
		mListObjects.setAdapter(mEditableArrayAdapter);

		TextView emptyText = (TextView) findViewById(android.R.id.empty);
		mListObjects.setEmptyView(emptyText);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(BOOK_TITLE, mBookTitle);
		outState.putSerializable(getSavedInstanceObjectListName(), mObjectList);
	}

	/**
	 * Finish the activity and add the list of authors to the result of the
	 * activity.
	 */
	private void addObjectList() {
		Intent intent = new Intent();
		ArrayList<T> objectList = new ArrayList<T>(mObjectMap.values());
		intent.putExtra(getExtraObjectListName(), objectList);
		setResult(RESULT_OK, intent);
		finish();
	}
}
