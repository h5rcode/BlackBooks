package com.blackbooks.utils;

import com.blackbooks.model.nonpersistent.Language;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;

/**
 * Language utility class.
 */
public final class LanguageUtils {

    /**
     * Return the display name corresponding to a language code.
     *
     * @param languageCode Language code.
     * @return Display name.
     */
    public static String getDisplayLanguage(String languageCode) {
        String displayName = null;
        if (languageCode != null) {
            Locale[] locales = Locale.getAvailableLocales();

            for (Locale locale : locales) {
                if (locale.getLanguage().equals(languageCode)) {
                    displayName = locale.getDisplayName(Locale.getDefault());
                    break;
                }
            }
        }
        return displayName;
    }

    /**
     * Return all the languages available on the phone, sorted by display name.
     *
     * @param labelNoLanguage If set, the returned list will contain an extra value at the
     *                        begining of the list representing "No language".
     * @return List of {@link Language}.
     */
    public static List<Language> getLanguageList(String labelNoLanguage) {
        Locale[] locales = Locale.getAvailableLocales();
        TreeMap<String, Language> languageMap = new TreeMap<String, Language>();
        for (Locale locale : locales) {
            String displayLanguage = StringUtils.capitalize(locale.getDisplayLanguage());
            if (!languageMap.containsKey(displayLanguage)) {
                languageMap.put(displayLanguage, new Language(locale.getLanguage(), displayLanguage));
            }
        }

        List<Language> languageList = new ArrayList<Language>();
        if (labelNoLanguage != null) {
            languageList.add(new Language(null, labelNoLanguage));
        }
        for (Language language : languageMap.values()) {
            languageList.add(language);
        }
        return languageList;
    }
}
