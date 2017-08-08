package com.blackbooks.repositories;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.model.persistent.Book;
import com.blackbooks.model.persistent.BookAuthor;
import com.blackbooks.model.persistent.BookCategory;
import com.blackbooks.sql.BrokerManager;
import com.blackbooks.utils.StringUtils;

import java.util.List;

public class BookRepositoryImpl implements BookRepository {

    private SQLiteDatabase db;

    public BookRepositoryImpl(SQLiteDatabase db) {
        this.db = db;
    }

    @Override
    public Book getBook(long bookId) {
        return BrokerManager.getBroker(Book.class).get(db, bookId);
    }

    @Override
    public long save(BookInfo bookInfo) {
        return BrokerManager.getBroker(Book.class).save(db, bookInfo);
    }

    @Override
    public void deleteBook(long bookId) {
        BrokerManager.getBroker(Book.class).delete(db, bookId);
    }

    @Override
    public List<Book> getBooksBySeries(long seriesId, int limit, int offset) {
        String[] selectedColumnList = new String[]{
                "boo." + Book.Cols.BOO_ID,
                "boo." + Book.Cols.BOO_TITLE,
                "boo." + Book.Cols.BOO_IS_READ,
                "boo." + Book.Cols.BOO_IS_FAVOURITE,
                "boo." + Book.Cols.BOO_LOANED_TO
        };

        String selectedColumns = StringUtils.join(selectedColumnList, ", ");
        String sql = "SELECT " + selectedColumns + " FROM " + Book.NAME + " boo WHERE boo." + Book.Cols.SER_ID + " = ? ORDER BY boo." + Book.Cols.BOO_TITLE + " COLLATE NOCASE LIMIT ? OFFSET ?;";

        String[] selectionArgs = new String[]{
                String.valueOf(seriesId),
                String.valueOf(limit),
                String.valueOf(offset)
        };

        return BrokerManager.getBroker(Book.class).rawSelect(db, sql, selectionArgs);
    }

    @Override
    public List<Book> getFavouriteBooks(int limit, int offset) {
        String[] selectedColumnList = new String[]{
                "boo." + Book.Cols.BOO_ID,
                "boo." + Book.Cols.BOO_TITLE,
                "boo." + Book.Cols.BOO_IS_READ,
                "boo." + Book.Cols.BOO_IS_FAVOURITE,
                "boo." + Book.Cols.BOO_LOANED_TO
        };

        String selectedColumns = StringUtils.join(selectedColumnList, ", ");
        String sql = "SELECT " + selectedColumns + " FROM " + Book.NAME + " boo WHERE boo." + Book.Cols.BOO_IS_FAVOURITE + " = ? ORDER BY boo." + Book.Cols.BOO_TITLE + " COLLATE NOCASE LIMIT ? OFFSET ?;";

        String[] selectionArgs = new String[]{
                String.valueOf(1L),
                String.valueOf(limit),
                String.valueOf(offset)
        };

        return BrokerManager.getBroker(Book.class).rawSelect(db, sql, selectionArgs);
    }

    @Override
    public List<Book> getBookInfoListToRead(int limit, int offset) {

        String[] selectedColumnList = new String[]{
                "boo." + Book.Cols.BOO_ID,
                "boo." + Book.Cols.BOO_TITLE,
                "boo." + Book.Cols.BOO_IS_READ,
                "boo." + Book.Cols.BOO_IS_FAVOURITE,
                "boo." + Book.Cols.BOO_LOANED_TO
        };

        String selectedColumns = StringUtils.join(selectedColumnList, ", ");
        String sql = "SELECT " + selectedColumns + " FROM " + Book.NAME + " boo WHERE boo." + Book.Cols.BOO_IS_READ + " = ? ORDER BY boo." + Book.Cols.BOO_TITLE + " COLLATE NOCASE LIMIT ? OFFSET ?;";

        String[] selectionArgs = new String[]{
                String.valueOf(0L),
                String.valueOf(limit),
                String.valueOf(offset)
        };

        return BrokerManager.getBroker(Book.class).rawSelect(db, sql, selectionArgs);
    }

    @Override
    public int getBookCountByAuthor(long authorId) {
        String sql = "SELECT COUNT(*) FROM " + BookAuthor.NAME + " WHERE " + BookAuthor.Cols.AUT_ID + " = ?;";
        String[] selectionArgs = {String.valueOf(authorId)};
        return queryInt(db, sql, selectionArgs);
    }

    @Override
    public int getBookCountByBookLocation(Long bookLocationId) {
        String sql = "SELECT COUNT(*) FROM " + Book.NAME + " WHERE " + Book.Cols.BKL_ID;
        String[] selectionArgs;
        if (bookLocationId == null) {
            sql += " IS NULL;";
            selectionArgs = null;
        } else {
            sql += " = ?;";
            selectionArgs = new String[]{String.valueOf(bookLocationId)};
        }

        return queryInt(db, sql, selectionArgs);
    }

    @Override
    public int getBookCountByCategory(Long categoryId) {
        String sql = "SELECT COUNT(*) FROM " + BookCategory.NAME + " WHERE " + BookCategory.Cols.CAT_ID;

        String[] selectionArgs;
        if (categoryId == null) {
            sql += " IS NULL";
            selectionArgs = null;
        } else {
            sql += " = ?;";
            selectionArgs = new String[]{String.valueOf(categoryId)};
        }

        return queryInt(db, sql, selectionArgs);
    }

    @Override
    public int getBookCountByFirstLetter(String firstLetter) {
        String sql = "SELECT COUNT(*) FROM " + Book.NAME + " WHERE SUBSTR(UPPER(" + Book.Cols.BOO_TITLE + "), 1, 1) = ?;";

        return queryInt(db, sql, new String[]{firstLetter});
    }

    @Override
    public int getBookCountByLanguage(String languageCode) {
        String sql = "SELECT COUNT(*) FROM " + Book.NAME + " WHERE " + Book.Cols.BOO_LANGUAGE_CODE;

        String[] selectionArgs;
        if (languageCode == null) {
            sql += " IS NULL;";
            selectionArgs = null;
        } else {
            sql += " = ?;";
            selectionArgs = new String[]{languageCode};
        }
        return queryInt(db, sql, selectionArgs);
    }

    @Override
    public int getBookCountByLoanedTo(String loanedTo) {
        String sql = "SELECT COUNT(*) FROM " + Book.NAME + " WHERE " + Book.Cols.BOO_LOANED_TO;

        String[] selectionArgs;
        if (loanedTo == null) {
            sql += " IS NULL;";
            selectionArgs = null;
        } else {
            sql += " = ?;";
            selectionArgs = new String[]{loanedTo};
        }
        return queryInt(db, sql, selectionArgs);
    }

    @Override
    public int getBookCountBySeries(Long seriesId) {
        String sql = "SELECT COUNT(*) FROM " + Book.NAME + " WHERE " + Book.Cols.SER_ID;

        String[] selectionArgs;
        if (seriesId == null) {
            sql += " IS NULL;";
            selectionArgs = null;
        } else {
            sql += " = ?;";
            selectionArgs = new String[]{String.valueOf(seriesId)};
        }

        return queryInt(db, sql, selectionArgs);
    }

    @Override
    public int getBookCount() {
        String sql = "SELECT COUNT(*) FROM " + Book.NAME;
        return queryInt(db, sql);
    }

    @Override
    public int getBookToReadCount() {
        String sql = "SELECT COUNT(*) FROM " + Book.NAME + " WHERE " + Book.Cols.BOO_IS_READ + " = 0;";
        return queryInt(db, sql);
    }

    @Override
    public int getBookLoanCount() {
        String sql = "SELECT COUNT(DISTINCT " + Book.Cols.BOO_LOANED_TO + ") FROM " + Book.NAME + " WHERE " + Book.Cols.BOO_LOANED_TO + " IS NOT NULL;";
        return queryInt(db, sql);
    }

    @Override
    public int getFavouriteBooks() {
        String sql = "SELECT COUNT(*) FROM " + Book.NAME + " WHERE " + Book.Cols.BOO_IS_FAVOURITE + " = 1;";
        return queryInt(db, sql);
    }

    @Override
    public int getLanguageCount() {
        String sql = "SELECT COUNT(DISTINCT " + Book.Cols.BOO_LANGUAGE_CODE + ") FROM " + Book.NAME;
        return queryInt(db, sql);
    }

    @Override
    public int getFirstLetterCount() {
        String sql = "SELECT COUNT(DISTINCT UPPER(SUBSTR(" + Book.Cols.BOO_TITLE + ", 1, 1))) FROM " + Book.NAME;
        return queryInt(db, sql);
    }

    @Override
    public List<Book> getBooksByLoanedTo(String loanedTo, int limit, int offset) {
        String[] selectedColumnList = new String[]{
                "boo." + Book.Cols.BOO_ID,
                "boo." + Book.Cols.BOO_TITLE,
                "boo." + Book.Cols.BOO_IS_READ,
                "boo." + Book.Cols.BOO_IS_FAVOURITE,
                "boo." + Book.Cols.BOO_LOANED_TO
        };

        String selectedColumns = StringUtils.join(selectedColumnList, ", ");
        String sql = "SELECT " + selectedColumns + " FROM " + Book.NAME + " boo WHERE boo." + Book.Cols.BOO_LOANED_TO + " = ? ORDER BY boo." + Book.Cols.BOO_TITLE + " COLLATE NOCASE LIMIT ? OFFSET ?;";

        String[] selectionArgs = new String[]{
                loanedTo,
                String.valueOf(limit),
                String.valueOf(offset)
        };

        return BrokerManager.getBroker(Book.class).rawSelect(db, sql, selectionArgs);
    }

    @Override
    public List<Book> getBooksByLanguage(String languageCode, int limit, int offset) {
        String[] selectedColumnList = new String[]{
                "boo." + Book.Cols.BOO_ID,
                "boo." + Book.Cols.BOO_TITLE,
                "boo." + Book.Cols.BOO_IS_READ,
                "boo." + Book.Cols.BOO_IS_FAVOURITE,
                "boo." + Book.Cols.BOO_LOANED_TO
        };

        String selectedColumns = StringUtils.join(selectedColumnList, ", ");
        String sql = "SELECT " + selectedColumns + " FROM " + Book.NAME + " boo WHERE boo." + Book.Cols.BOO_LANGUAGE_CODE + " = ? COLLATE NOCASE ORDER BY " + Book.Cols.BOO_TITLE + " COLLATE NOCASE LIMIT ? OFFSET ?;";

        String[] selectionArgs = new String[]{
                String.valueOf(languageCode),
                String.valueOf(limit),
                String.valueOf(offset)
        };

        return BrokerManager.getBroker(Book.class).rawSelect(db, sql, selectionArgs);
    }

    @Override
    public byte[] getBookSmallThumbnail(long bookId) {
        String sql = "SELECT " + Book.Cols.BOO_SMALL_THUMBNAIL + " FROM " + Book.NAME + " WHERE " + Book.Cols.BOO_ID + " = ?;";
        String[] selectionArgs = new String[]{String.valueOf(bookId)};
        Book book = BrokerManager.getBroker(Book.class).rawSelect(db, sql, selectionArgs).get(0);
        return book.smallThumbnail;
    }

    @Override
    public void markBookAsFavourite(long bookId) {
        db.beginTransaction();
        try {
            String sql = "UPDATE " + Book.NAME + " SET " + Book.Cols.BOO_IS_FAVOURITE + " = 1 - " + Book.Cols.BOO_IS_FAVOURITE
                    + " Where " + Book.Cols.BOO_ID + " = " + bookId + ";";
            db.execSQL(sql);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void markBookAsRead(long bookId) {
        db.beginTransaction();
        try {
            String sql = "UPDATE " + Book.NAME + " SET " + Book.Cols.BOO_IS_READ + " = 1 - " + Book.Cols.BOO_IS_READ + " Where "
                    + Book.Cols.BOO_ID + " = " + bookId + ";";
            db.execSQL(sql);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void returnBook(long bookId) {
        db.beginTransaction();
        try {
            String sql = "UPDATE " + Book.NAME + " SET " + Book.Cols.BOO_LOANED_TO + " = null, " + Book.Cols.BOO_LOAN_DATE
                    + " = null" + " Where " + Book.Cols.BOO_ID + " = " + bookId + ";";
            db.execSQL(sql);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public List<Book> getBooksByFirstLetter(String firstLetter, int limit, int offset) {
        String[] selectedColumnList = new String[]{
                "boo." + Book.Cols.BOO_ID,
                "boo." + Book.Cols.BOO_TITLE,
                "boo." + Book.Cols.BOO_IS_READ,
                "boo." + Book.Cols.BOO_IS_FAVOURITE,
                "boo." + Book.Cols.BOO_LOANED_TO
        };

        String selectedColumns = StringUtils.join(selectedColumnList, ", ");
        String sql = "SELECT " + selectedColumns + " FROM " + Book.NAME + " boo WHERE boo." + Book.Cols.BOO_TITLE + " LIKE ? || '%' COLLATE NOCASE ORDER BY " + Book.Cols.BOO_TITLE + " COLLATE NOCASE LIMIT ? OFFSET ?;";

        String[] selectionArgs = new String[]{
                String.valueOf(firstLetter),
                String.valueOf(limit),
                String.valueOf(offset)
        };

        return BrokerManager.getBroker(Book.class).rawSelect(db, sql, selectionArgs);
    }

    @Override
    public List<Book> getBooksByCategory(long categoryId, int limit, int offset) {
        String[] selectedColumnList = new String[]{
                "boo." + Book.Cols.BOO_ID,
                "boo." + Book.Cols.BOO_TITLE,
                "boo." + Book.Cols.BOO_IS_READ,
                "boo." + Book.Cols.BOO_IS_FAVOURITE,
                "boo." + Book.Cols.BOO_LOANED_TO
        };

        String selectedColumns = StringUtils.join(selectedColumnList, ", ");
        String sql = "SELECT " + selectedColumns + " FROM " + Book.NAME + " boo JOIN " + BookCategory.NAME + " bca ON bca." + BookCategory.Cols.BOO_ID + " = boo." + Book.Cols.BOO_ID + " WHERE bca." + BookCategory.Cols.CAT_ID + " = ? ORDER BY boo." + Book.Cols.BOO_TITLE + " LIMIT ? OFFSET ?;";

        String[] selectionArgs = new String[]{
                String.valueOf(categoryId),
                String.valueOf(limit),
                String.valueOf(offset)
        };

        return BrokerManager.getBroker(Book.class).rawSelect(db, sql, selectionArgs);
    }

    @Override
    public List<Book> getBooksByBookLocation(long bookLocationId, int limit, int offset) {
        String[] selectedColumnList = new String[]{
                "boo." + Book.Cols.BOO_ID,
                "boo." + Book.Cols.BOO_TITLE,
                "boo." + Book.Cols.BOO_IS_READ,
                "boo." + Book.Cols.BOO_IS_FAVOURITE,
                "boo." + Book.Cols.BOO_LOANED_TO
        };

        String selectedColumns = StringUtils.join(selectedColumnList, ", ");
        String sql = "SELECT " + selectedColumns + " FROM " + Book.NAME + " boo WHERE boo." + Book.Cols.BKL_ID + " = ? ORDER BY boo." + Book.Cols.BOO_TITLE + " LIMIT ? OFFSET ?;";

        String[] selectionArgs = new String[]{
                String.valueOf(bookLocationId),
                String.valueOf(limit),
                String.valueOf(offset)
        };

        return BrokerManager.getBroker(Book.class).rawSelect(db, sql, selectionArgs);
    }

    @Override
    public List<Book> getBooksByAuthor(long authorId, int limit, int offset) {
        String[] selectedColumnList = new String[]{
                "boo." + Book.Cols.BOO_ID,
                "boo." + Book.Cols.BOO_TITLE,
                "boo." + Book.Cols.BOO_IS_READ,
                "boo." + Book.Cols.BOO_IS_FAVOURITE,
                "boo." + Book.Cols.BOO_LOANED_TO
        };

        String selectedColumns = StringUtils.join(selectedColumnList, ", ");
        String sql = "SELECT " + selectedColumns + " FROM " + Book.NAME + " boo JOIN " + BookAuthor.NAME + " bka ON bka." + BookAuthor.Cols.BOO_ID + " = boo." + Book.Cols.BOO_ID + " WHERE bka." + BookAuthor.Cols.AUT_ID + " = ? ORDER BY boo." + Book.Cols.BOO_TITLE + " LIMIT ? OFFSET ?;";

        String[] selectionArgs = new String[]{
                String.valueOf(authorId),
                String.valueOf(limit),
                String.valueOf(offset)
        };

        return BrokerManager.getBroker(Book.class).rawSelect(db, sql, selectionArgs);
    }

    @Override
    public List<Book> getBooksByIsbn10(String isbn) {
        String[] selectedColumns = new String[]{
                Book.Cols.BOO_ID,
                Book.Cols.BOO_TITLE
        };

        String select = StringUtils.join(selectedColumns, ", ");
        String sql = "SELECT " + select + " FROM " + Book.NAME + " WHERE " + Book.Cols.BOO_ISBN_10 + " = ? COLLATE NOCASE ORDER BY " + Book.Cols.BOO_ID;
        String[] selectionArgs = new String[]{isbn};

        return BrokerManager.getBroker(Book.class).rawSelect(db, sql, selectionArgs);
    }

    @Override
    public List<Book> getBooksByIsbn13(String isbn) {
        String[] selectedColumns = new String[]{
                Book.Cols.BOO_ID,
                Book.Cols.BOO_TITLE
        };

        String select = StringUtils.join(selectedColumns, ", ");
        String sql = "SELECT " + select + " FROM " + Book.NAME + " WHERE " + Book.Cols.BOO_ISBN_13 + " = ? COLLATE NOCASE ORDER BY " + Book.Cols.BOO_ID;
        String[] selectionArgs = new String[]{isbn};

        return BrokerManager.getBroker(Book.class).rawSelect(db, sql, selectionArgs);
    }

    /**
     * Execute a SQL query that returns an integer.
     *
     * @param db            SQLiteDatabase.
     * @param sql           SQL query.
     * @param selectionArgs Selection arguments.
     * @return Integer value.
     */
    private int queryInt(SQLiteDatabase db, String sql, String[] selectionArgs) {
        Cursor cursor = db.rawQuery(sql, selectionArgs);
        cursor.moveToNext();
        int result = cursor.getInt(0);
        cursor.close();
        return result;
    }

    private int queryInt(SQLiteDatabase db, String sql) {
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToNext();
        int result = cursor.getInt(0);
        cursor.close();
        return result;
    }
}
