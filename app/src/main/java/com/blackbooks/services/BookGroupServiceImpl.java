package com.blackbooks.services;

import android.database.Cursor;

import com.blackbooks.database.SQLiteHelper;
import com.blackbooks.model.nonpersistent.BookGroup;
import com.blackbooks.model.persistent.Author;
import com.blackbooks.model.persistent.Book;
import com.blackbooks.model.persistent.BookAuthor;
import com.blackbooks.model.persistent.BookCategory;
import com.blackbooks.model.persistent.BookLocation;
import com.blackbooks.model.persistent.Category;
import com.blackbooks.model.persistent.Series;
import com.blackbooks.repositories.AbstractRepository;
import com.blackbooks.utils.LanguageUtils;
import com.blackbooks.utils.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * Book group services.
 */
public final class BookGroupServiceImpl extends AbstractRepository implements BookGroupService {

    public BookGroupServiceImpl(SQLiteHelper sqLiteHelper) {
        super(sqLiteHelper);
    }

    /**
     * Load the list of authors.
     *
     * @param limit  Limit.
     * @param offset Offset.
     * @return List of BookGroup.
     */
    public List<BookGroup> getBookGroupListAuthor(int limit, int offset) {
        String sql = "SELECT" + "\n" +
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
                "aut." + Author.Cols.AUT_NAME + " COLLATE NOCASE" + "\n" +
                "LIMIT ?" + "\n" +
                "OFFSET ?" + ";";

        String[] selectionArgs = new String[]{
                String.valueOf(limit),
                String.valueOf(offset)
        };
        return queryBookGroupList(sql, selectionArgs);
    }

    /**
     * Load the list of book locations.
     *
     * @param limit  Limit.
     * @param offset Offset.
     * @return List of BookGroup.
     */
    public List<BookGroup> getBookGroupListBookLocation(int limit, int offset) {
        String sql = "SELECT" + "\n" +
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
                "bkl." + BookLocation.Cols.BKL_NAME + " COLLATE NOCASE" + "\n" +
                "LIMIT ?" + "\n" +
                "OFFSET ?" + ";";

        String[] selectionArgs = new String[]{
                String.valueOf(limit),
                String.valueOf(offset)
        };
        return queryBookGroupList(sql, selectionArgs);
    }

    /**
     * Load the list of categories.
     *
     * @param limit  Limit.
     * @param offset Offset.
     * @return List of BookGroup.
     */
    public List<BookGroup> getBookGroupListCategory(int limit, int offset) {
        String sql = "SELECT" + "\n" +
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
                "cat." + Category.Cols.CAT_NAME + " COLLATE NOCASE" + "\n" +
                "LIMIT ?" + "\n" +
                "OFFSET ?" + ";";

        String[] selectionArgs = new String[]{
                String.valueOf(limit),
                String.valueOf(offset)
        };
        return queryBookGroupList(sql, selectionArgs);
    }

    /**
     * Load the list of first characters of book titles.
     *
     * @param limit  Limit.
     * @param offset Offset.
     * @return List of BookGroup.
     */
    public List<BookGroup> getBookGroupListFirstLetter(int limit, int offset) {
        String sql = "SELECT" + "\n" +
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

        String[] selectionArgs = new String[]{
                String.valueOf(limit),
                String.valueOf(offset)
        };
        return queryBookGroupList(sql, selectionArgs);
    }

    /**
     * Load the list of languages.
     *
     * @return List of BookGroup.
     */
    public List<BookGroup> getBookGroupListLanguage() {
        String sql = "SELECT" + "\n" +
                "LOWER(" + Book.Cols.BOO_LANGUAGE_CODE + ")," + "\n" +
                "NULL," + "\n" +
                "COUNT(*)" + "\n" +
                "FROM" + "\n" +
                Book.NAME + "\n" +
                "WHERE " + Book.Cols.BOO_LANGUAGE_CODE + " IS NOT NULL" + "\n" +
                "GROUP BY" + "\n" +
                "1;";


        TreeMap<String, BookGroup> bookGroupMap = new TreeMap<>();
        for (BookGroup bookGroup : queryBookGroupList(sql, null)) {
            if (bookGroup.id != null) {
                bookGroup.name = StringUtils.capitalize(LanguageUtils.getDisplayLanguage((String) bookGroup.id));
            }
            bookGroupMap.put(bookGroup.name, bookGroup);
        }
        return new ArrayList<>(bookGroupMap.values());
    }

    /**
     * Load the list of persons who are loaned a book.
     *
     * @param limit  Limit.
     * @param offset Offset.
     * @return List of BookGroup.
     */
    public List<BookGroup> getBookGroupListLoan(int limit, int offset) {
        String sql = "SELECT" + "\n" +
                Book.Cols.BOO_LOANED_TO + "," + "\n" +
                Book.Cols.BOO_LOANED_TO + "," + "\n" +
                "COUNT(*)" + "\n" +
                "FROM" + "\n" +
                Book.NAME + "\n" +
                "WHERE " + Book.Cols.BOO_LOANED_TO + " IS NOT NULL" + "\n" +
                "GROUP BY" + "\n" +
                "1" + "\n" +
                "ORDER BY" + "\n" +
                "1" + "\n" +
                "LIMIT ?" + "\n" +
                "OFFSET ?" + ";";

        String[] selectionArgs = new String[]{
                String.valueOf(limit),
                String.valueOf(offset)
        };
        return queryBookGroupList(sql, selectionArgs);
    }

    /**
     * Load the list of series.
     *
     * @param limit  Limit.
     * @param offset Offset.
     * @return List of BookGroup.
     */
    public List<BookGroup> getBookGroupListSeries(int limit, int offset) {
        String sql = "SELECT" + "\n" +
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
                "ser." + Series.Cols.SER_NAME + " COLLATE NOCASE" + "\n" +
                "LIMIT ?" + "\n" +
                "OFFSET ?" + ";";

        String[] selectionArgs = new String[]{
                String.valueOf(limit),
                String.valueOf(offset)
        };
        return queryBookGroupList(sql, selectionArgs);
    }

    /**
     * Executes a query and returns a list of BookGroup.
     *
     * @param sql           SQL query.
     * @param selectionArgs Selection arguments.
     * @return List of BookGroup.
     */
    private List<BookGroup> queryBookGroupList(String sql, String[] selectionArgs) {
        Cursor cursor = getReadableDatabase().rawQuery(sql, selectionArgs);
        List<BookGroup> bookGroupList = new ArrayList<>();
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
        cursor.close();
        return bookGroupList;
    }
}
