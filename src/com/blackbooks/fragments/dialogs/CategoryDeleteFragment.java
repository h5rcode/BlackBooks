package com.blackbooks.fragments.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.blackbooks.R;
import com.blackbooks.model.persistent.Category;

/**
 * A fragment dialog to delete a category.
 */
public class CategoryDeleteFragment extends DialogFragment {

	private static final String ARG_CATEGORY = "ARG_CATEGORY";

	private Category mCategory;
	private CategoryDeleteListener mCategoryDeleteListener;

	/**
	 * Return a new instance of CategoryRemoveFragment that is initialized to
	 * remove a category.
	 * 
	 * @param category
	 *            Category.
	 * @return CategoryRemoveFragment.
	 */
	public static CategoryDeleteFragment newInstance(Category category) {
		CategoryDeleteFragment fragment = new CategoryDeleteFragment();
		Bundle args = new Bundle();
		args.putSerializable(ARG_CATEGORY, category);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mCategory = (Category) getArguments().getSerializable(ARG_CATEGORY);
		mCategoryDeleteListener = (CategoryDeleteListener) getTargetFragment();
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		String message = getString(R.string.message_confirm_delete_category);
		message = String.format(message, mCategory.name);

		builder.setMessage(message)
				.setPositiveButton(R.string.message_confirm_delete_category_confirm, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int id) {
						mCategoryDeleteListener.onCategoryDeleted(mCategory);
					}
				}).setNegativeButton(R.string.message_confirm_delete_category_cancel, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int id) {
						// Do nothing.
					}
				});
		return builder.create();
	}

	/**
	 * Activities hosting a {@link CategoryDeleteFragment} should implement this
	 * interface to be notified when the category is deleted.
	 */
	public interface CategoryDeleteListener {

		/**
		 * Called when the category is deleted.
		 * 
		 * @param category
		 *            Category.
		 */
		public void onCategoryDeleted(Category category);
	}
}
