package com.blackbooks.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.blackbooks.model.nonpersistent.Language;

/**
 * Language adapter.
 */
public final class LanguagesAdapter extends ArrayAdapter<Language> {

    private final LayoutInflater mInflater;

    public LanguagesAdapter(Context context) {
        super(context, android.R.layout.simple_list_item_1);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getLanguageView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getLanguageView(position, convertView, parent);
    }

    private View getLanguageView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        }
        Language language = this.getItem(position);
        if (language != null) {
            TextView textLanguageName = (TextView) convertView.findViewById(android.R.id.text1);
            textLanguageName.setText(language.getDisplayName());
            convertView.setTag(language.getCode());
        }
        return convertView;
    }
}
