package com.blackbooks.fragments.dialogs;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.blackbooks.R;
import com.blackbooks.adapters.AutoCompleteAdapter;
import com.blackbooks.adapters.AutoCompleteAdapter.AutoCompleteSearcher;
import com.blackbooks.database.SQLiteHelper;
import com.blackbooks.model.persistent.Category;
import com.blackbooks.services.CategoryServices;

/**
 * A dialog fragment to edit a category.
 */
public class CategoryEditFragment extends DialogFragment {

	private static final String ARG_CATEGORY = "ARG_CATEGORY";
	private static final String ARG_ENABLE_AUTOCOMPLETE = "ARG_ENABLE_AUTOCOMPLETE";

	private CategoryEditListener mCategoryEditListener;
	private AutoCompleteAdapter<Category> mAutoCompleteAdapter;

	private Category mCategory;

	/**
	 * Return a new instance of CategoryEditFragment that is initialized to edit
	 * a category.
	 * 
	 * @param category
	 *            Category.
	 * @param enableAutoComplete
	 *            True to enable category auto-completion, false otherwise.
	 * @return CategoryEditFragment.
	 */
	public static CategoryEditFragment newInstance(Category category, boolean enableAutoComplete) {
		CategoryEditFragment fragment = new CategoryEditFragment();
		Bundle args = new Bundle();
		args.putSerializable(ARG_CATEGORY, category);
		args.putBoolean(ARG_ENABLE_AUTOCOMPLETE, enableAutoComplete);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();

		mCategory = (Category) args.getSerializable(ARG_CATEGORY);
		boolean enableAutoComplete = args.getBoolean(ARG_ENABLE_AUTOCOMPLETE);

		mCategoryEditListener = (CategoryEditListener) getTargetFragment();

		if (enableAutoComplete) {
			mAutoCompleteAdapter = new AutoCompleteAdapter<Category>(getActivity(), android.R.layout.simple_list_item_1,
					new AutoCompleteSearcher<Category>() {

						@Override
						public List<Category> search(CharSequence constraint) {
							SQLiteHelper mDbHelper = new SQLiteHelper(getActivity());
							SQLiteDatabase db = mDbHelper.getReadableDatabase();
							List<Category> categoryList = CategoryServices.getCategoryListByText(db, constraint.toString());
							db.close();
							return categoryList;
						}

						@Override
						public String getDisplayLabel(Category item) {
							return item.name;
						}
					});
		}
	}

	@SuppressLint("InflateParams")
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final Dialog dialog = new Dialog(getActivity());
		dialog.setContentView(R.layout.dialog_edit_category);
		dialog.setTitle(R.string.title_dialog_edit_category);

		final AutoCompleteTextView textCategory = (AutoCompleteTextView) dialog.findViewById(R.id.editCategory_textCategory);
		textCategory.setText(mCategory.name);
		textCategory.setAdapter(mAutoCompleteAdapter);

		Button saveButton = (Button) dialog.findViewById(R.id.editCategory_confirm);
		Button cancelButton = (Button) dialog.findViewById(R.id.editCategory_cancel);

		saveButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String newName = textCategory.getText().toString();
				String errorMessage = mCategoryEditListener.checkNewName(mCategory, newName);
				if (errorMessage == null) {
					textCategory.setText(null);
					textCategory.setError(null);
					dismiss();
					mCategoryEditListener.onCategoryEdit(mCategory, newName);
				} else {
					textCategory.setError(errorMessage);
				}
			}
		});
		cancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
			}
		});

		return dialog;
	}

	/**
	 * Activities hosting a {@link CategoryEditFragment} should implement this
	 * interface to be notified when the category is edited.
	 */
	public interface CategoryEditListener {

		/**
		 * Called when a new category name has been entered.
		 * 
		 * @param category
		 *            The edited category.
		 * @param newName
		 *            The new name of the category.
		 * @return An error message that should be displayed in the text box.
		 *         Null if everything went well and the dialog should be
		 *         dismissed.
		 */
		String checkNewName(Category category, String newName);

		/**
		 * Called when the category is edited.
		 * 
		 * @param category
		 *            The edited category.
		 * @param newName
		 *            The new name of the category.
		 */
		void onCategoryEdit(Category category, String newName);
	}
}
