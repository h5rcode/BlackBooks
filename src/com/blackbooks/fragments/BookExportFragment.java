package com.blackbooks.fragments;

import java.io.File;
import java.io.IOException;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
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
 */
public class BookExportFragment extends Fragment {

	private static final Character[] TEXT_QUALIFIERS = new Character[] { '"', '\'' };
	private static final Character[] COLUMN_SEPARATORS = new Character[] { ';', ',', '|', '\t', ' ' };

	private Spinner mSpinnerTextQualifier;
	private Spinner mSpinnerColumnSeparator;
	private RadioGroup mRadioGroupFirstRowContainsHeaders;
	private TextView mTextPreview;

	private char mTextQualifier;
	private char mColumnSeparator;
	private boolean mFirstRowContainsHeader;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_book_export, container, false);

		mSpinnerTextQualifier = (Spinner) view.findViewById(R.id.bookExport_spinnerTextQualifier);
		mSpinnerColumnSeparator = (Spinner) view.findViewById(R.id.bookExport_spinnerColumnSeparator);
		mRadioGroupFirstRowContainsHeaders = (RadioGroup) view.findViewById(R.id.bookExport_radioGroupFirstRowContainsHeaders);
		mTextPreview = (TextView) view.findViewById(R.id.bookExport_preview);

		mSpinnerTextQualifier.setAdapter(new ArrayAdapter<Character>(getActivity(), android.R.layout.simple_list_item_1,
				TEXT_QUALIFIERS));
		mSpinnerColumnSeparator.setAdapter(new ArrayAdapter<Character>(getActivity(), android.R.layout.simple_list_item_1,
				COLUMN_SEPARATORS));

		mRadioGroupFirstRowContainsHeaders.check(R.id.bookExport_radioButtonFirstRowYes);

		mSpinnerTextQualifier.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				renderPreview();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// Do nothing.
			}
		});

		mSpinnerColumnSeparator.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				renderPreview();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// Do nothing.
			}
		});

		mRadioGroupFirstRowContainsHeaders.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				renderPreview();
			}
		});

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
		readParameters();
		SQLiteHelper dbHelper = new SQLiteHelper(getActivity());
		SQLiteDatabase db = null;
		try {
			db = dbHelper.getReadableDatabase();
			File exportFile = FileUtils.createFileInAppDir("Export.csv");
			ExportServices.exportBookList(db, exportFile, mTextQualifier, mColumnSeparator, mFirstRowContainsHeader);
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
		readParameters();
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
	 * Read the export parameters.
	 */
	private void readParameters() {
		mTextQualifier = (Character) mSpinnerTextQualifier.getSelectedItem();
		mColumnSeparator = (Character) mSpinnerColumnSeparator.getSelectedItem();
		mFirstRowContainsHeader = mRadioGroupFirstRowContainsHeaders.getCheckedRadioButtonId() == R.id.bookExport_radioButtonFirstRowYes;
	}
}
