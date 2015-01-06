package com.blackbooks.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * An abstract array adapter that will render a list where each row has a
 * button.
 * 
 * @param <T>
 *            The type of the listed items.
 */
public abstract class EditableArrayAdapter<T> extends ArrayAdapter<T> {

	private LayoutInflater mInflater;
	private int mLayoutList;
	private int mTextViewId;
	private int mButtonId;

	/**
	 * Constructor.
	 * 
	 * @param context
	 *            Context.
	 * @param resource
	 *            Id of the list view of the adapter.
	 * @param layoutItem
	 *            Id of the layout that will be used to render each item of the
	 *            list.
	 * @param textViewId
	 *            Id of the text view where the label of each item will be
	 *            rendered.
	 * @param buttonId
	 *            Id of the button of each row of the list.
	 * @param objects
	 *            The initial list of objects to display.
	 */
	public EditableArrayAdapter(Context context, int resource, int layoutItem, int textViewId, int buttonId, List<T> objects) {
		super(context, resource, objects);
		this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mLayoutList = layoutItem;
		this.mTextViewId = textViewId;
		this.mButtonId = buttonId;
	}

	/**
	 * Return the label of the object that will be displayed in the list.
	 * 
	 * @param object
	 *            Object.
	 * @return Label of the object that will appear in the list.
	 */
	protected abstract String getDisplayLabel(T object);

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;

		T item = this.getItem(position);

		if (item != null) {
			view = mInflater.inflate(mLayoutList, null);

			TextView textView = (TextView) view.findViewById(mTextViewId);
			textView.setText(getDisplayLabel(item));
			textView.setTag(item);

			ImageButton button = (ImageButton) view.findViewById(mButtonId);
			button.setTag(item);
		}

		return view;
	}

	@Override
	public void add(T object) {
		super.add(object);
		this.notifyDataSetChanged();
	}

	@Override
	public void remove(T object) {
		super.remove(object);
		this.notifyDataSetChanged();
	}
}
