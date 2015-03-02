package com.blackbooks.fragments.dialogs;

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
import com.blackbooks.database.SQLiteHelper;
import com.blackbooks.model.nonpersistent.BookGroup;
import com.blackbooks.model.persistent.Category;
import com.blackbooks.services.CategoryServices;

/**
 * A dialog fragment to edit a category.
 */
public final class CategoryEditFragment extends DialogFragment {

    private static final String ARG_CATEGORY = "ARG_CATEGORY";

    private CategoryEditListener mCategoryEditListener;

    private BookGroup mCategory;

    /**
     * Return a new instance of CategoryEditFragment that is initialized to edit
     * a category.
     *
     * @param bookGroup BookGroup.
     * @return CategoryEditFragment.
     */
    public static CategoryEditFragment newInstance(BookGroup bookGroup) {
        CategoryEditFragment fragment = new CategoryEditFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_CATEGORY, bookGroup);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();

        mCategory = (BookGroup) args.getSerializable(ARG_CATEGORY);
        mCategoryEditListener = (CategoryEditListener) getTargetFragment();
    }

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_edit_category);
        dialog.setTitle(R.string.title_dialog_edit_category);

        final AutoCompleteTextView textCategory = (AutoCompleteTextView) dialog.findViewById(R.id.editCategory_textCategory);
        textCategory.setText(mCategory.name);

        Button saveButton = (Button) dialog.findViewById(R.id.editCategory_confirm);
        Button cancelButton = (Button) dialog.findViewById(R.id.editCategory_cancel);

        saveButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String newName = textCategory.getText().toString();
                String errorMessage = null;

                if (newName == null || newName.trim().isEmpty()) {
                    errorMessage = getString(R.string.message_category_missing);
                } else {
                    newName = newName.trim();

                    Category category = new Category();
                    category.name = newName;

                    SQLiteDatabase db = SQLiteHelper.getInstance().getReadableDatabase();
                    Category categoryDb = CategoryServices.getCategoryByCriteria(db, category);

                    if (categoryDb != null) {
                        errorMessage = getString(R.string.message_category_already_present, newName);
                    }
                }

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
         * Called when the category is edited.
         *
         * @param bookGroup BookGroup.
         * @param newName   The new name of the category.
         */
        void onCategoryEdit(BookGroup bookGroup, String newName);
    }
}
