package com.blackbooks.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.blackbooks.R;
import com.blackbooks.model.nonpersistent.Language;
import com.blackbooks.utils.LanguageUtils;

import java.util.ArrayList;
import java.util.List;

public class LanguagesAdapter extends ArrayAdapter<Language> {

    private final LayoutInflater mInflater;
    private final List<Language> mLanguageList;
    private final List<String> mLanguageCodeList;

    public LanguagesAdapter(Context context) {
        super(context, android.R.layout.simple_list_item_1);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mLanguageList = LanguageUtils.getLanguageList(context.getString(R.string.label_no_language));
        mLanguageCodeList = new ArrayList<String>();
        for (Language language : mLanguageList) {
            super.add(language);
            mLanguageCodeList.add(language.getCode());
        }
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getLanguageView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getLanguageView(position, convertView, parent);
    }

    public int getPosition(String languageCode) {
        return mLanguageCodeList.indexOf(languageCode);
    }

    @SuppressLint("InflateParams")
    private View getLanguageView(int position, View convertView, ViewGroup parent) {
        View view = mInflater.inflate(android.R.layout.simple_list_item_1, null);

        Language language = this.mLanguageList.get(position);
        if (language != null) {
            TextView textLanguageName = (TextView) view.findViewById(android.R.id.text1);
            textLanguageName.setText(language.getDisplayName());
            view.setTag(language.getCode());
        }
        return view;
    }
}
