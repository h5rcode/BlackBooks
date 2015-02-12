package com.blackbooks.services;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.blackbooks.model.nonpersistent.BookGroup;
import com.blackbooks.model.persistent.Author;
import com.blackbooks.model.persistent.Book;
import com.blackbooks.model.persistent.BookAuthor;
import com.blackbooks.model.persistent.BookCategory;
import com.blackbooks.model.persistent.BookLocation;
import com.blackbooks.model.persistent.Category;
import com.blackbooks.model.persistent.Series;
import com.blackbooks.utils.LanguageUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Book group services.
 */
public final class BookGroupServices {

    private static final String SQL_AUTHOR = "SELECT" + "\n" +
            "aut." + Author.Cols.AUT_ID + "," + "\n" +
            "aut." + Author.Cols.AUT_NAME + "," + "\n" +
            "COUNT(*)" + "\n" +
            "FROM" + "\n" +
            Author.NAME + " aut" + "\n" +
            "JOIN " + BookAuthor.NAME + " bka ON bka." + BookAuthor.Cols.AUT_ID + " = aut." + Author.Cols.AUT_ID + "\n" +
            "GROUP BY" + "\n" +
            "aut." + Author.Cols.AUT_ID + "," + "\n" +
            "aut." + Author.Cols.AUT_NAME + "\n" +
            "ORDER BY" + "\n" +
            "aut." + Author.Cols.AUT_NAME + "\n" +
            "LIMIT ?" + "\n" +
            "OFFSET ?" + ";";

    private static final String SQL_BOOK_LOCATION = "SELECT" + "\n" +
            "bkl." + BookLocation.Cols.BKL_ID + "," + "\n" +
            "bkl." + BookLocation.Cols.BKL_NAME + "," + "\n" +
            "COUNT(*)" + "\n" +
            "FROM" + "\n" +
            BookLocation.NAME + " bkl" + "\n" +
            "JOIN " + Book.NAME + " boo ON boo." + Book.Cols.BKL_ID + " = bkl." + BookLocation.Cols.BKL_ID + "\n" +
            "GROUP BY" + "\n" +
            "bkl." + BookLocation.Cols.BKL_ID + "," + "\n" +
            "bkl." + BookLocation.Cols.BKL_NAME + "\n" +
            "ORDER BY" + "\n" +
            "bkl." + BookLocation.Cols.BKL_NAME + "\n" +
            "LIMIT ?" + "\n" +
            "OFFSET ?" + ";";

    private static final String SQL_CATEGORY = "SELECT" + "\n" +
            "cat." + Category.Cols.CAT_ID + "," + "\n" +
            "cat." + Category.Cols.CAT_NAME + "," + "\n" +
            "COUNT(*)" + "\n" +
            "FROM" + "\n" +
            Category.NAME + " cat" + "\n" +
            "JOIN " + BookCategory.NAME + " bca ON bca." + BookCategory.Cols.CAT_ID + " = cat." + Category.Cols.CAT_ID + "\n" +
            "GROUP BY" + "\n" +
            "cat." + Category.Cols.CAT_ID + "," + "\n" +
            "cat." + Category.Cols.CAT_NAME + "\n" +
            "ORDER BY" + "\n" +
            "cat." + Category.Cols.CAT_NAME + "\n" +
            "LIMIT ?" + "\n" +
            "OFFSET ?" + ";";

    private static final String SQL_FIRST_LETTER = "SELECT" + "\n" +
            "SUBSTR(UPPER(" + Book.Cols.BOO_TITLE + "), 1, 1)," + "\n" +
            "SUBSTR(UPPER(" + Book.Cols.BOO_TITLE + "), 1, 1)," + "\n" +
            "COUNT(*)" + "\n" +
            "FROM" + "\n" +
            Book.NAME + "\n" +
            "GROUP BY" + "\n" +
            "1" + "\n" +
            "ORDER BY" + "\n" +
            "1" + "\n" +
            "LIMIT ?" + "\n" +
            "OFFSET ?" + ";";

    private static final String SQL_LANGUAGE = "SELECT" + "\n" +
            "LOWER(" + Book.Cols.BOO_LANGUAGE_CODE + ")," + "\n" +
            "NULL," + "\n" +
            "COUNT(*)" + "\n" +
            "FROM" + "\n" +
            Book.NAME + "\n" +
            "GROUP BY" + "\n" +
            "1" + "\n" +
            "ORDER BY" + "\n" +
            "1" + "\n" +
            "LIMIT ?" + "\n" +
            "OFFSET ?" + ";";

    private static final String SQL_SERIES = "SELECT" + "\n" +
            "ser." + Series.Cols.SER_ID + "," + "\n" +
            "ser." + Series.Cols.SER_NAME + "," + "\n" +
            "COUNT(*)" + "\n" +
            "FROM" + "\n" +
            Series.NAME + " ser" + "\n" +
            "JOIN " + Book.NAME + " boo ON boo." + Book.Cols.SER_ID + " = ser." + Series.Cols.SER_ID + "\n" +
            "GROUP BY" + "\n" +
            "ser." + Series.Cols.SER_ID + "," + "\n" +
            "ser." + Series.Cols.SER_NAME + "\n" +
            "ORDER BY" + "\n" +
            "ser." + Series.Cols.SER_NAME + "\n" +
            "LIMIT ?" + "\n" +
            "OFFSET ?" + ";";

    /**
     * Return the list of groups of a certain type in the library.
     *
     * @param db            SQLiteDatabase.
     * @param bookGroupType Book group type.
     * @param limit         Max number of groups to return.
     * @param offset        Offset.
     * @return List of BookGroup.
     */
    public static List<BookGroup> getBookGroupList(SQLiteDatabase db, BookGroup.BookGroupType bookGroupType, int limit, int offset) {

        String sql;
        switch (bookGroupType) {
            case AUTHOR:
                sql = SQL_AUTHOR;
                break;
            case BOOK_LOCATION:
                sql = SQL_BOOK_LOCATION;
                break;
            case FIRST_LETTER:
                sql = SQL_FIRST_LETTER;
                break;
            case LANGUAGE:
                sql = SQL_LANGUAGE;
                break;
            case CATEGORY:
                sql = SQL_CATEGORY;
                break;
            case SERIES:
                sql = SQL_SERIES;
                break;
            default:
                throw new IllegalArgumentException(String.format("Invalid bookGroupType: %s.", bookGroupType));
        }

        String[] selectionArgs = new String[]{
                String.valueOf(limit),
                String.valueOf(offset)
        };
        Cursor cursor = db.rawQuery(sql, selectionArgs);

        List<BookGroup> bookGroupList = new ArrayList<BookGroup>();
        Integer idType = null;
        while (cursor.moveToNext()) {
            if (idType == null) {
                idType = cursor.getType(0);
            }
            Serializable id;
            if (idType == Cursor.FIELD_TYPE_INTEGER) {
                id = cursor.getLong(0);
            } else {
                id = cursor.getString(0);
            }
            String name = cursor.getString(1);
            Long count = cursor.getLong(2);

            BookGroup bookGroup = new BookGroup();
            bookGroup.id = id;
            bookGroup.name = name;
            bookGroup.count = count.intValue();

            bookGroupList.add(bookGroup);
        }

        if (bookGroupType == BookGroup.BookGroupType.LANGUAGE) {
            for (BookGroup bookGroup : bookGroupList) {
                if (bookGroup.id != null) {
                    bookGroup.name = LanguageUtils.getDisplayLanguage((String) bookGroup.id);
                }
            }
        }

        return bookGroupList;
    }
}
