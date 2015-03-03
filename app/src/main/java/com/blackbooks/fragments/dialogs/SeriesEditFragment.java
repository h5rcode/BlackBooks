package com.blackbooks.fragments.dialogs;

import android.app.Dialog;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.blackbooks.R;
import com.blackbooks.database.SQLiteHelper;
import com.blackbooks.model.nonpersistent.BookGroup;
import com.blackbooks.model.persistent.Series;
import com.blackbooks.services.SeriesServices;

/**
 * A dialog fragment to edit a series.
 */
public final class SeriesEditFragment extends DialogFragment {

    private static final String ARG_BOOK_GROUP = "ARG_BOOK_GROUP";

    private SeriesEditListener mSeriesEditListener;
    private BookGroup mBookGroup;

    /**
     * Return a new instance of SeriesEditFragment that is initialized to edit
     * a series.
     *
     * @param bookGroup BookGroup.
     * @return SeriesEditFragment.
     */
    public static SeriesEditFragment newInstance(BookGroup bookGroup) {
        SeriesEditFragment fragment = new SeriesEditFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_BOOK_GROUP, bookGroup);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();

        mBookGroup = (BookGroup) args.getSerializable(ARG_BOOK_GROUP);
        mSeriesEditListener = (SeriesEditListener) getTargetFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_edit_series);
        dialog.setTitle(R.string.title_dialog_edit_series);

        final EditText textSeries = (EditText) dialog.findViewById(R.id.editSeries_textSeries);
        textSeries.setText(mBookGroup.name);

        Button saveButton = (Button) dialog.findViewById(R.id.editSeries_confirm);
        Button cancelButton = (Button) dialog.findViewById(R.id.editSeries_cancel);

        saveButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String newName = textSeries.getText().toString();

                String errorMessage = null;
                if (newName == null || newName.trim().isEmpty()) {
                    errorMessage = getString(R.string.message_series_missing);
                } else {
                    newName = newName.trim();

                    SQLiteDatabase db = SQLiteHelper.getInstance().getReadableDatabase();
                    Series series = new Series();
                    series.name = newName;
                    Series seriesDb = SeriesServices.getSeriesByCriteria(db, series);

                    if (seriesDb != null) {
                        errorMessage = getString(R.string.message_series_already_present, newName);
                    }
                }

                if (errorMessage == null) {
                    textSeries.setText(null);
                    textSeries.setError(null);
                    dismiss();
                    mSeriesEditListener.onSeriesEdit(mBookGroup, newName);
                } else {
                    textSeries.setError(errorMessage);
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
     * Activities hosting a {@link com.blackbooks.fragments.dialogs.SeriesEditFragment} should implement this
     * interface to be notified when the series is edited.
     */
    public interface SeriesEditListener {

        /**
         * Called when the series is edited.
         *
         * @param bookGroup The edited series.
         * @param newName   The new name of the series.
         */
        void onSeriesEdit(BookGroup bookGroup, String newName);
    }
}
