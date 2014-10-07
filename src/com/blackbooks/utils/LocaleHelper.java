package com.blackbooks.utils;

import java.util.Locale;

public final class LocaleHelper {

	public static String getLocale(String languageCode) {
		String result = null;
		Locale[] locales = Locale.getAvailableLocales();
		for (Locale locale : locales) {
			String language = locale.getLanguage();
			if (language.equals(languageCode)) {
				result = locale.getDisplayLanguage();
				break;
			}
		}
		return result;
	}
}
