package com.blackbooks.fragments;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blackbooks.R;
import com.blackbooks.database.SQLiteHelper;
import com.blackbooks.services.ExportServices;
import com.blackbooks.utils.FileUtils;
import com.blackbooks.utils.LogUtils;

/**
 * Fragment where the user can export the list of books as a CSV file.
 * 
 * TODO: Handle orientation changes correctly with pickers.
 * 
 */
public class BookExportFragment extends Fragment {

	private static final String TAG_TEXT_QUALIFIER_PICKER = "TAG_TEXT_QUALIFIER_PICKER";
	private static final String TAG_COLUMN_SEPARATOR_PICKER = "TAG_COLUMN_SEPARATOR_PICKER";

	private final TextQualifier mDefaultTextQualifier;
	private final ColumnSeparator mDefaultColumnSeparator;

	private static final List<TextQualifier> TEXT_QUALIFIERS = new ArrayList<TextQualifier>();
	private static final List<ColumnSeparator> COLUMN_SEPARATORS = new ArrayList<ColumnSeparator>();

	private LinearLayout mLayoutQualifier;
	private LinearLayout mLayoutSeparator;
	private TextView mTextViewQualifier;
	private TextView mTextViewSeparator;
	private CheckBox mCheckBoxFirstRowContainsHeaders;
	private TextView mTextPreview;

	private char mTextQualifier;
	private char mColumnSeparator;
	private boolean mFirstRowContainsHeader = true;

	public BookExportFragment() {
		super();
		mDefaultTextQualifier = new TextQualifier('"', R.string.label_text_qualifier_double_quote);
		mDefaultColumnSeparator = new ColumnSeparator(';', R.string.label_column_separator_semicolon);

		TEXT_QUALIFIERS.add(mDefaultTextQualifier);
		TEXT_QUALIFIERS.add(new TextQualifier('\'', R.string.label_text_qualifier_single_quote));

		COLUMN_SEPARATORS.add(mDefaultColumnSeparator);
		COLUMN_SEPARATORS.add(new ColumnSeparator(',', R.string.label_column_separator_comma));
		COLUMN_SEPARATORS.add(new ColumnSeparator('|', R.string.label_column_separator_pipe));
		COLUMN_SEPARATORS.add(new ColumnSeparator(' ', R.string.label_column_separator_space));
		COLUMN_SEPARATORS.add(new ColumnSeparator('\t', R.string.label_column_separator_tab));

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_book_export, container, false);

		mTextQualifier = mDefaultTextQualifier.getCharacter();
		mColumnSeparator = mDefaultColumnSeparator.getCharacter();

		mLayoutQualifier = (LinearLayout) view.findViewById(R.id.bookExport_layoutQualifier);
		mLayoutSeparator = (LinearLayout) view.findViewById(R.id.bookExport_layoutSeparator);
		mTextViewQualifier = (TextView) view.findViewById(R.id.bookExport_textQualifier);
		mTextViewSeparator = (TextView) view.findViewById(R.id.bookExport_textSeparator);

		mTextViewQualifier.setText(mDefaultTextQualifier.getResourceId());
		mTextViewSeparator.setText(mDefaultColumnSeparator.getResourceId());

		mLayoutQualifier.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				TextQualifierPicker picker = new TextQualifierPicker();
				picker.show(getFragmentManager(), TAG_TEXT_QUALIFIER_PICKER);
			}
		});

		mLayoutSeparator.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ColumnSeparatorPicker picker = new ColumnSeparatorPicker();
				picker.show(getFragmentManager(), TAG_COLUMN_SEPARATOR_PICKER);
			}
		});

		mCheckBoxFirstRowContainsHeaders = (CheckBox) view.findViewById(R.id.bookExport_checkBoxFirstRowContainsHeaders);
		mTextPreview = (TextView) view.findViewById(R.id.bookExport_preview);

		mCheckBoxFirstRowContainsHeaders.setChecked(true);

		mCheckBoxFirstRowContainsHeaders.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				mFirstRowContainsHeader = isChecked;
				renderPreview();
			}
		});

		renderPreview();

		return view;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.book_export, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean result;
		switch (item.getItemId()) {
		case R.id.bookExport_actionExport:
			exportBooks();
			result = true;
			break;

		default:
			result = super.onOptionsItemSelected(item);
			break;
		}

		return result;
	}

	/**
	 * Export the books as a CSV file using the current parameters.
	 */
	private void exportBooks() {
		SQLiteHelper dbHelper = new SQLiteHelper(getActivity());
		SQLiteDatabase db = null;
		try {
			db = dbHelper.getReadableDatabase();
			File exportFile = FileUtils.createFileInAppDir("Export.csv");
			ExportServices.exportBookList(db, exportFile, mTextQualifier, mColumnSeparator, mFirstRowContainsHeader);

			MediaScannerConnection.scanFile(getActivity(), new String[] { exportFile.getAbsolutePath() }, null, null);
			String message = String.format(getString(R.string.message_file_saved), exportFile.getName(), exportFile
					.getParentFile().getName());
			Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
		} catch (IOException e) {
			Log.e(LogUtils.TAG, e.getMessage(), e);
			Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	/**
	 * Render a preview of the book export with the current parameters.
	 */
	private void renderPreview() {
		SQLiteHelper dbHelper = new SQLiteHelper(getActivity());
		SQLiteDatabase db = null;
		try {
			db = dbHelper.getReadableDatabase();
			String preview = ExportServices.previewBookExport(db, mTextQualifier, mColumnSeparator, mFirstRowContainsHeader);

			mTextPreview.setText(preview);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	/**
	 * Text qualifier picker dialog.
	 */
	public final class TextQualifierPicker extends DialogFragment {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			String[] qualifiers = new String[TEXT_QUALIFIERS.size()];
			int selectedQualifier = 0;

			int i = 0;
			for (TextQualifier textQualifier : TEXT_QUALIFIERS) {
				qualifiers[i] = getString(textQualifier.getResourceId());
				if (textQualifier.getCharacter() == mTextQualifier) {
					selectedQualifier = i;
				}
				i++;
			}

			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(R.string.title_dialog_text_qualifier_picker) //
					.setSingleChoiceItems(qualifiers, selectedQualifier, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							TextQualifier textQualifier = TEXT_QUALIFIERS.get(which);
							mTextQualifier = textQualifier.getCharacter();
							mTextViewQualifier.setText(textQualifier.getResourceId());
							renderPreview();
							dismiss();
						}
					});
			return builder.create();
		}
	}

	/**
	 * Column separator picker dialog.
	 */
	public final class ColumnSeparatorPicker extends DialogFragment {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			String[] separators = new String[COLUMN_SEPARATORS.size()];
			int selectedSeparator = 0;
			int i = 0;
			for (ColumnSeparator separator : COLUMN_SEPARATORS) {
				separators[i] = getString(separator.getResourceId());
				if (separator.getCharacter() == mColumnSeparator) {
					selectedSeparator = i;
				}
				i++;
			}

			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(R.string.title_dialog_column_separator_picker) //
					.setSingleChoiceItems(separators, selectedSeparator, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							ColumnSeparator columnSeparator = COLUMN_SEPARATORS.get(which);
							mColumnSeparator = columnSeparator.getCharacter();
							mTextViewSeparator.setText(columnSeparator.getResourceId());
							renderPreview();
							dismiss();
						}
					});
			return builder.create();
		}
	}

	/**
	 * Text qualifier.
	 */
	private final class TextQualifier {

		private final char mCharacter;
		private final int mResourceId;

		/**
		 * Constructor.
		 * 
		 * @param character
		 *            Character.
		 * @param resourceId
		 *            Id of the resource representing the qualifier's name.
		 */
		public TextQualifier(char character, int resourceId) {
			mCharacter = character;
			mResourceId = resourceId;
		}

		/**
		 * Return the character.
		 * 
		 * @return Character.
		 */
		public char getCharacter() {
			return mCharacter;
		}

		/**
		 * Return the resource id.
		 * 
		 * @return Resource id.
		 */
		public int getResourceId() {
			return mResourceId;
		}
	}

	/**
	 * Column separator.
	 */
	private final class ColumnSeparator {

		private final char mCharacter;
		private final int mResourceId;

		/**
		 * Constructor.
		 * 
		 * @param character
		 *            Character.
		 * @param resourceId
		 *            Id of the resource representing the separator's name.
		 */
		public ColumnSeparator(char character, int resourceId) {
			mCharacter = character;
			mResourceId = resourceId;
		}

		/**
		 * Return the character.
		 * 
		 * @return Character.
		 */
		public char getCharacter() {
			return mCharacter;
		}

		/**
		 * Return the resource id.
		 * 
		 * @return Resource id.
		 */
		public int getResourceId() {
			return mResourceId;
		}
	}
}
