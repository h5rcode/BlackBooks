package com.blackbooks.adapters;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.blackbooks.R;
import com.blackbooks.utils.StringUtils;

public class LanguagesAdapter extends ArrayAdapter<Language> {

	private LayoutInflater mInflater;
	private List<Language> mLanguageList = new ArrayList<Language>();
	private List<String> mLanguageCodeList = new ArrayList<String>();

	public LanguagesAdapter(Context context) {
		super(context, android.R.layout.simple_list_item_1);
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		initLanguages();
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
		View view = convertView;
		view = mInflater.inflate(android.R.layout.simple_list_item_1, null);

		Language language = this.mLanguageList.get(position);
		if (language != null) {
			TextView textLanguageName = (TextView) view.findViewById(android.R.id.text1);
			textLanguageName.setText(language.getDisplayName());
			view.setTag(language.getCode());
		}
		return view;
	}

	private void initLanguages() {
		Locale[] locales = Locale.getAvailableLocales();
		TreeMap<String, Language> languageMap = new TreeMap<String, Language>();
		for (Locale locale : locales) {
			String displayLanguage = StringUtils.capitalize(locale.getDisplayLanguage());
			if (!languageMap.containsKey(displayLanguage)) {
				languageMap.put(displayLanguage, new Language(locale.getLanguage(), displayLanguage));
			}
		}

		mLanguageList.add(new Language(null, this.getContext().getString(R.string.label_no_language)));
		for (Language language : languageMap.values()) {
			mLanguageList.add(language);
		}
	}
}
