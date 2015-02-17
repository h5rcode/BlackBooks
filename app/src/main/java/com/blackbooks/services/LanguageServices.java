package com.blackbooks.services;

import android.database.sqlite.SQLiteDatabase;

import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.model.nonpersistent.LanguageInfo;
import com.blackbooks.utils.LanguageUtils;
import com.blackbooks.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Language services.
 */
public class LanguageServices {

    /**
     * Get the list of all the books, returned in lists of LanguageInfo.
     *
     * @param db SQLiteDatabase.
     * @return List of LanguageInfo.
     */
    public static List<LanguageInfo> getLanguageInfoList(SQLiteDatabase db) {
        List<BookInfo> bookInfoList = BookServices.getBookInfoList(db);

        LanguageInfo unspecifiedLanguage = null;
        Map<String, LanguageInfo> languageMap = new HashMap<String, LanguageInfo>();
        Map<String, LanguageInfo> displayNameMap = new TreeMap<String, LanguageInfo>();

        for (BookInfo book : bookInfoList) {
            String languageCode = book.languageCode;
            if (languageCode == null) {
                if (unspecifiedLanguage == null) {
                    unspecifiedLanguage = new LanguageInfo();
                }
                unspecifiedLanguage.books.add(book);
            } else {
                LanguageInfo language;
                if (languageMap.containsKey(languageCode)) {
                    language = languageMap.get(languageCode);
                } else {
                    String displayName = StringUtils.capitalize(LanguageUtils.getDisplayLanguage(languageCode));

                    language = new LanguageInfo();
                    language.languageCode = languageCode;
                    language.displayName = displayName;

                    languageMap.put(languageCode, language);

                    displayNameMap.put(displayName, language);
                }
                language.books.add(book);
            }
        }

        List<LanguageInfo> languageList = new ArrayList<LanguageInfo>();

        if (unspecifiedLanguage != null) {
            languageList.add(unspecifiedLanguage);
        }
        languageList.addAll(displayNameMap.values());
        return languageList;
    }
}
