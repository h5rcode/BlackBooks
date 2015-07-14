package com.blackbooks.fragments.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.blackbooks.R;
import com.blackbooks.adapters.LanguagesAdapter;
import com.blackbooks.model.nonpersistent.Language;
import com.blackbooks.utils.LanguageUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment to pick one of the languages available on the device.
 */
public final class LanguagePickerFragment extends DialogFragment {

    private LanguagePickerListener mLanguagePickerListener;
    private LanguagesAdapter mLanguagesAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLanguagePickerListener = (LanguagePickerListener) getTargetFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Dialog dialog = new Dialog(getActivity());
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.dialog_language_picker);
        dialog.setTitle(R.string.title_dialog_language_picker);

        final ListView listView = (ListView) dialog.findViewById(R.id.language_picker_list);
        mLanguagesAdapter = new LanguagesAdapter(getActivity());
        setLanguageList(null);
        listView.setAdapter(mLanguagesAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Language language = (Language) parent.getItemAtPosition(position);
                LanguagePickerFragment.this.dismiss();
                mLanguagePickerListener.onLanguagePicked(language);
            }
        });

        final EditText editTextFilter = (EditText) dialog.findViewById(R.id.language_picker_filter);
        editTextFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Do nothing.
            }

            @Override
            public void afterTextChanged(Editable s) {
                String filter;
                if (s == null) {
                    filter = null;
                } else {
                    filter = s.toString().toLowerCase();
                }
                setLanguageList(filter);
            }
        });

        return dialog;
    }

    /**
     * Set the list of languages.
     *
     * @param filter Text used to filter the list of languages. If not null or empty, the language list
     *               will only contain the languages starting with this filter.
     */
    private void setLanguageList(String filter) {
        List<Language> filteredLanguages;
        List<Language> languages = LanguageUtils.getLanguageList(getString(R.string.label_no_language));
        if (filter == null || filter.isEmpty()) {
            filteredLanguages = languages;
        } else {
            filteredLanguages = new ArrayList<Language>();
            for (Language language : languages) {
                boolean addLanguage = false;
                String code = language.getCode();
                if (code == null) {
                    addLanguage = true;
                } else {
                    String displayName = language.getDisplayName();
                    if (displayName.toLowerCase().startsWith(filter)) {
                        addLanguage = true;
                    }
                }

                if (addLanguage) {
                    filteredLanguages.add(language);
                }
            }
        }

        mLanguagesAdapter.clear();
        mLanguagesAdapter.addAll(filteredLanguages);
        mLanguagesAdapter.notifyDataSetChanged();
    }

    /**
     * Fragments hosting a LanguagePickerFragment should implement this interface to be notified
     * when a language is picked.
     */
    public interface LanguagePickerListener {

        /**
         * Called when a language is picked.
         *
         * @param language The selected language.
         */
        void onLanguagePicked(Language language);
    }
}
