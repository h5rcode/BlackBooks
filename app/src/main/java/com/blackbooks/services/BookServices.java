package com.blackbooks.services;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.util.LongSparseArray;

import com.blackbooks.cache.ThumbnailManager;
import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.model.persistent.Author;
import com.blackbooks.model.persistent.Book;
import com.blackbooks.model.persistent.BookAuthor;
import com.blackbooks.model.persistent.BookCategory;
import com.blackbooks.model.persistent.BookLocation;
import com.blackbooks.model.persistent.Category;
import com.blackbooks.model.persistent.Publisher;
import com.blackbooks.model.persistent.fts.BookFTS;
import com.blackbooks.sql.BrokerManager;
import com.blackbooks.sql.FTSBroker;
import com.blackbooks.sql.FTSBrokerManager;
import com.blackbooks.utils.IsbnUtils;
import com.blackbooks.utils.StringUtils;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

/**
 * Book services.
 */
public class BookServices {

    /**
     * Delete a book, and its links to its authors. If the deleted book was the
     * only book of its author(s) in the database, the author(s) is (are) also
     * deleted.
     *
     * @param db     SQLiteDatabase.
     * @param bookId Id of a book.
     */
    public static void deleteBook(SQLiteDatabase db, long bookId) {
        db.beginTransaction();
        try {
            List<BookAuthor> baListByBook = BookAuthorServices.getBookAuthorListByBook(db, bookId);
            List<BookCategory> bcListByBook = BookCategoryServices.getBookCategoryListByBook(db, bookId);
            BrokerManager.getBroker(Book.class).delete(db, bookId);

            for (BookAuthor ba : baListByBook) {
                List<BookAuthor> baListByAuthor = BookAuthorServices.getBookAuthorListByAuthor(db, ba.authorId);

                if (baListByAuthor.isEmpty()) {
                    BrokerManager.getBroker(Author.class).delete(db, ba.authorId);
                }
            }

            for (BookCategory bc : bcListByBook) {
                List<BookCategory> bcListByCategory = BookCategoryServices.getBookCategoryListByCategory(db, bc.categoryId);

                if (bcListByCategory.isEmpty()) {
                    BrokerManager.getBroker(Category.class).delete(db, bc.categoryId);
                }
            }

            FTSBrokerManager.getBroker(BookFTS.class).delete(db, bookId);

            PublisherServices.deletePublishersWithoutBooks(db);
            SeriesServices.deleteSeriesWithoutBooks(db);
            BookLocationServices.deleteBookLocationsWithoutBooks(db);

            ThumbnailManager.getInstance().removeThumbnails(bookId);

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * Get a book.
     *
     * @param db     SQLiteDatabase.
     * @param bookId Id of a book.
     * @return Book.
     */
    public static Book getBook(SQLiteDatabase db, long bookId) {
        return BrokerManager.getBroker(Book.class).get(db, bookId);
    }

    /**
     * Get book info.
     *
     * @param db     SQLiteDatabase.
     * @param bookId Id of a book.
     * @return BookInfo.
     */
    public static BookInfo getBookInfo(SQLiteDatabase db, long bookId) {
        Book book = BrokerManager.getBroker(Book.class).get(db, bookId);
        BookInfo bookInfo = new BookInfo(book);

        List<BookAuthor> bookAuthorList = BookAuthorServices.getBookAuthorListByBook(db, book.id);
        for (BookAuthor bookAuthor : bookAuthorList) {
            Author author = AuthorServices.getAuthor(db, bookAuthor.authorId);
            bookInfo.authors.add(author);
        }

        if (book.publisherId != null) {
            bookInfo.publisher = PublisherServices.getPublisher(db, book.publisherId);
        }
        if (book.bookLocationId != null) {
            bookInfo.bookLocation = BookLocationServices.getBookLocation(db, book.bookLocationId);
        }
        if (book.seriesId != null) {
            bookInfo.series = SeriesServices.getSeries(db, book.seriesId);
        }

        List<BookCategory> bookCategoryList = BookCategoryServices.getBookCategoryListByBook(db, book.id);
        for (BookCategory bookCategory : bookCategoryList) {
            Category category = CategoryServices.getCategory(db, bookCategory.categoryId);
            bookInfo.categories.add(category);
        }

        return bookInfo;
    }

    /**
     * Get all the books in the database.
     *
     * @param db SQLiteDatabase.
     * @return List of BookInfo.
     */
    public static List<Book> getBookList(SQLiteDatabase db) {
        String[] selectedColumns = new String[]{
                Book.Cols.BOO_ID,
                Book.Cols.BOO_TITLE,
                Book.Cols.BOO_DESCRIPTION,
                Book.Cols.SER_ID,
                Book.Cols.BOO_NUMBER,
                Book.Cols.BOO_IS_READ,
                Book.Cols.BOO_IS_FAVOURITE,
                Book.Cols.BOO_LOANED_TO,
                Book.Cols.BOO_LOAN_DATE,
                Book.Cols.BOO_LANGUAGE_CODE,
                Book.Cols.BKL_ID
        };

        String select = StringUtils.join(selectedColumns, ", ");
        String sql = "SELECT " + select + " FROM " + Book.NAME + " ORDER BY " + Book.Cols.BOO_TITLE + " COLLATE NOCASE";

        return BrokerManager.getBroker(Book.class).rawSelect(db, sql, null);
    }

    /**
     * Return the number of books of a given author.
     *
     * @param db       SQLiteDatabase.
     * @param authorId Author id.
     * @return Book count.
     */
    public static int getBookCountByAuthor(SQLiteDatabase db, long authorId) {
        String sql = "SELECT COUNT(*) FROM " + BookAuthor.NAME + " WHERE " + BookAuthor.Cols.AUT_ID + " = ?;";

        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(authorId)});
        cursor.moveToNext();
        return cursor.getInt(0);
    }

    /**
     * Return the number of books of a given book location.
     *
     * @param db             SQLiteDatabase.
     * @param bookLocationId Book location id.
     * @return Book count.
     */
    public static int getBookCountByBookLocation(SQLiteDatabase db, Long bookLocationId) {
        String sql = "SELECT COUNT(*) FROM " + Book.NAME + " WHERE " + Book.Cols.BKL_ID;
        String[] selectionArgs;
        if (bookLocationId == null) {
            sql += " IS NULL;";
            selectionArgs = null;
        } else {
            sql += " = ?;";
            selectionArgs = new String[]{String.valueOf(bookLocationId)};
        }

        Cursor cursor = db.rawQuery(sql, selectionArgs);
        cursor.moveToNext();
        return cursor.getInt(0);
    }

    /**
     * Return the number of books of a given book category.
     *
     * @param db         SQLiteDatabase.
     * @param categoryId Category id.
     * @return Book count.
     */
    public static int getBookCountByCategory(SQLiteDatabase db, Long categoryId) {
        String sql = "SELECT COUNT(*) FROM " + BookCategory.NAME + " WHERE " + BookCategory.Cols.CAT_ID;

        String[] selectionArgs;
        if (categoryId == null) {
            sql += " IS NULL";
            selectionArgs = null;
        } else {
            sql += " = ?;";
            selectionArgs = new String[]{String.valueOf(categoryId)};
        }

        Cursor cursor = db.rawQuery(sql, selectionArgs);
        cursor.moveToNext();
        return cursor.getInt(0);
    }

    /**
     * Return the number of books whose title begins with a given letter.
     *
     * @param db          SQLiteDatabase.
     * @param firstLetter First letter of the title.
     * @return Book count.
     */
    public static int getBookCountByFirstLetter(SQLiteDatabase db, String firstLetter) {
        String sql = "SELECT COUNT(*) FROM " + Book.NAME + " WHERE SUBSTR(UPPER(" + Book.Cols.BOO_TITLE + "), 1, 1) = ?;";

        Cursor cursor = db.rawQuery(sql, new String[]{firstLetter});
        cursor.moveToNext();
        return cursor.getInt(0);
    }

    /**
     * Return the number of books whose title begins with a given letter.
     *
     * @param db           SQLiteDatabase.
     * @param languageCode Language code.
     * @return Book count.
     */
    public static int getBookCountByLanguage(SQLiteDatabase db, String languageCode) {
        String sql = "SELECT COUNT(*) FROM " + Book.NAME + " WHERE " + Book.Cols.BOO_LANGUAGE_CODE;

        String[] selectionArgs;
        if (languageCode == null) {
            sql += " IS NULL;";
            selectionArgs = null;
        } else {
            sql += " = ?;";
            selectionArgs = new String[]{languageCode};
        }
        Cursor cursor = db.rawQuery(sql, selectionArgs);
        cursor.moveToNext();
        return cursor.getInt(0);
    }

    /**
     * Return the number of books whose title begins with a given letter.
     *
     * @param db       SQLiteDatabase.
     * @param loanedTo Language code.
     * @return Book count.
     */
    public static int getBookCountByLoanedTo(SQLiteDatabase db, String loanedTo) {
        String sql = "SELECT COUNT(*) FROM " + Book.NAME + " WHERE " + Book.Cols.BOO_LOANED_TO;

        String[] selectionArgs;
        if (loanedTo == null) {
            sql += " IS NULL;";
            selectionArgs = null;
        } else {
            sql += " = ?;";
            selectionArgs = new String[]{loanedTo};
        }
        Cursor cursor = db.rawQuery(sql, selectionArgs);
        cursor.moveToNext();
        return cursor.getInt(0);
    }

    /**
     * Return the number of books of a given series.
     *
     * @param db       SQLiteDatabase.
     * @param seriesId Series id.
     * @return Book count.
     */
    public static int getBookCountBySeries(SQLiteDatabase db, Long seriesId) {
        String sql = "SELECT COUNT(*) FROM " + Book.NAME + " WHERE " + Book.Cols.SER_ID;

        String[] selectionArgs;
        if (seriesId == null) {
            sql += " IS NULL;";
            selectionArgs = null;
        } else {
            sql += " = ?;";
            selectionArgs = new String[]{String.valueOf(seriesId)};
        }

        Cursor cursor = db.rawQuery(sql, selectionArgs);
        cursor.moveToNext();
        return cursor.getInt(0);
    }

    /**
     * Return the books corresponding to a given ISBN number.
     *
     * @param db   SQLiteDatabase.
     * @param isbn ISBN-10 or ISBN-13.
     * @return The list of books with this ISBN number.
     */
    public static List<Book> getBookListByIsbn(SQLiteDatabase db, String isbn) {
        String testedColumn;
        if (IsbnUtils.isValidIsbn10(isbn)) {
            testedColumn = Book.Cols.BOO_ISBN_10;
        } else if (IsbnUtils.isValidIsbn13(isbn)) {
            testedColumn = Book.Cols.BOO_ISBN_13;
        } else {
            throw new InvalidParameterException("number");
        }

        String[] selectedColumns = new String[]{
                Book.Cols.BOO_ID,
                Book.Cols.BOO_TITLE
        };

        String select = StringUtils.join(selectedColumns, ", ");
        String sql = "SELECT " + select + " FROM " + Book.NAME + " WHERE " + testedColumn + " = ? COLLATE NOCASE ORDER BY " + Book.Cols.BOO_ID;
        String[] selectionArgs = new String[]{isbn};

        return BrokerManager.getBroker(Book.class).rawSelect(db, sql, selectionArgs);
    }

    /**
     * Get the info of all the books in the database.
     *
     * @param db SQLiteDatabase.
     * @return List of BookInfo.
     */
    public static List<BookInfo> getBookInfoList(SQLiteDatabase db) {
        List<Book> bookList = getBookList(db);
        return getBookInfoListFromBookList(db, bookList);
    }

    /**
     * Return the list of books of a given author.
     *
     * @param db       SQLiteDatabase.
     * @param authorId Author id.
     * @param limit    Max number of books to return.
     * @param offset   Offset.
     * @return List of BookInfo.
     */
    @Deprecated
    public static List<BookInfo> getBookInfoListByAuthor(SQLiteDatabase db, long authorId, int limit, int offset) {
        String[] selectedColumnList = new String[]{
                "boo." + Book.Cols.BOO_ID,
                "boo." + Book.Cols.BOO_TITLE,
                "boo." + Book.Cols.BOO_IS_READ,
                "boo." + Book.Cols.BOO_IS_FAVOURITE
        };

        String selectedColumns = StringUtils.join(selectedColumnList, ", ");
        String sql = "SELECT " + selectedColumns + " FROM " + Book.NAME + " boo JOIN " + BookAuthor.NAME + " bka ON bka." + BookAuthor.Cols.BOO_ID + " = boo." + Book.Cols.BOO_ID + " WHERE bka." + BookAuthor.Cols.AUT_ID + " = ? ORDER BY boo." + Book.Cols.BOO_TITLE + " LIMIT ? OFFSET ?;";

        String[] selectionArgs = new String[]{
                String.valueOf(authorId),
                String.valueOf(limit),
                String.valueOf(offset)
        };

        List<Book> bookList = BrokerManager.getBroker(Book.class).rawSelect(db, sql, selectionArgs);
        return getBookInfoListFromBookList(db, bookList);
    }

    /**
     * Return the list of books in a given location.
     *
     * @param db             SQLiteDatabase.
     * @param bookLocationId Location id.
     * @param limit          Max number of books to return.
     * @param offset         Offset.
     * @return List of BookInfo.
     */
    @Deprecated
    public static List<BookInfo> getBookInfoListByBookLocation(SQLiteDatabase db, long bookLocationId, int limit, int offset) {
        String[] selectedColumnList = new String[]{
                "boo." + Book.Cols.BOO_ID,
                "boo." + Book.Cols.BOO_TITLE,
                "boo." + Book.Cols.BOO_IS_READ,
                "boo." + Book.Cols.BOO_IS_FAVOURITE
        };

        String selectedColumns = StringUtils.join(selectedColumnList, ", ");
        String sql = "SELECT " + selectedColumns + " FROM " + Book.NAME + " boo WHERE boo." + Book.Cols.BKL_ID + " = ? ORDER BY boo." + Book.Cols.BOO_TITLE + " LIMIT ? OFFSET ?;";

        String[] selectionArgs = new String[]{
                String.valueOf(bookLocationId),
                String.valueOf(limit),
                String.valueOf(offset)
        };

        List<Book> bookList = BrokerManager.getBroker(Book.class).rawSelect(db, sql, selectionArgs);
        return getBookInfoListFromBookList(db, bookList);
    }

    /**
     * Return the list of books of a given category.
     *
     * @param db         SQLiteDatabase.
     * @param categoryId Category id.
     * @param limit      Max number of books to return.
     * @param offset     Offset.
     * @return List of BookInfo.
     */
    @Deprecated
    public static List<BookInfo> getBookInfoListByCategory(SQLiteDatabase db, long categoryId, int limit, int offset) {
        String[] selectedColumnList = new String[]{
                "boo." + Book.Cols.BOO_ID,
                "boo." + Book.Cols.BOO_TITLE,
                "boo." + Book.Cols.BOO_IS_READ,
                "boo." + Book.Cols.BOO_IS_FAVOURITE
        };

        String selectedColumns = StringUtils.join(selectedColumnList, ", ");
        String sql = "SELECT " + selectedColumns + " FROM " + Book.NAME + " boo JOIN " + BookCategory.NAME + " bca ON bca." + BookCategory.Cols.BOO_ID + " = boo." + Book.Cols.BOO_ID + " WHERE bca." + BookCategory.Cols.CAT_ID + " = ? ORDER BY boo." + Book.Cols.BOO_TITLE + " LIMIT ? OFFSET ?;";

        String[] selectionArgs = new String[]{
                String.valueOf(categoryId),
                String.valueOf(limit),
                String.valueOf(offset)
        };

        List<Book> bookList = BrokerManager.getBroker(Book.class).rawSelect(db, sql, selectionArgs);
        return getBookInfoListFromBookList(db, bookList);
    }

    /**
     * Return the list of books whose title begins with a given letter.
     *
     * @param db          SQLiteDatabase.
     * @param firstLetter First letter of the title.
     * @param limit       Max number of books to return.
     * @param offset      Offset.
     * @return List of BookInfo.
     */
    @Deprecated
    public static List<BookInfo> getBookInfoListByFirstLetter(SQLiteDatabase db, String firstLetter, int limit, int offset) {
        String[] selectedColumnList = new String[]{
                "boo." + Book.Cols.BOO_ID,
                "boo." + Book.Cols.BOO_TITLE,
                "boo." + Book.Cols.BOO_IS_READ,
                "boo." + Book.Cols.BOO_IS_FAVOURITE
        };

        String selectedColumns = StringUtils.join(selectedColumnList, ", ");
        String sql = "SELECT " + selectedColumns + " FROM " + Book.NAME + " boo WHERE boo." + Book.Cols.BOO_TITLE + " LIKE ? || '%' COLLATE NOCASE ORDER BY " + Book.Cols.BOO_TITLE + " COLLATE NOCASE LIMIT ? OFFSET ?;";

        String[] selectionArgs = new String[]{
                String.valueOf(firstLetter),
                String.valueOf(limit),
                String.valueOf(offset)
        };

        List<Book> bookList = BrokerManager.getBroker(Book.class).rawSelect(db, sql, selectionArgs);
        return getBookInfoListFromBookList(db, bookList);
    }

    /**
     * Return the list of books of a given language.
     *
     * @param db           SQLiteDatabase.
     * @param languageCode Language code.
     * @param limit        Max number of books to return.
     * @param offset       Offset.
     * @return List of BookInfo.
     */
    @Deprecated
    public static List<BookInfo> getBookInfoListByLanguage(SQLiteDatabase db, String languageCode, int limit, int offset) {
        String[] selectedColumnList = new String[]{
                "boo." + Book.Cols.BOO_ID,
                "boo." + Book.Cols.BOO_TITLE,
                "boo." + Book.Cols.BOO_IS_READ,
                "boo." + Book.Cols.BOO_IS_FAVOURITE
        };

        String selectedColumns = StringUtils.join(selectedColumnList, ", ");
        String sql = "SELECT " + selectedColumns + " FROM " + Book.NAME + " boo WHERE boo." + Book.Cols.BOO_LANGUAGE_CODE + " = ? ORDER BY " + Book.Cols.BOO_TITLE + " COLLATE NOCASE LIMIT ? OFFSET ?;";

        String[] selectionArgs = new String[]{
                String.valueOf(languageCode),
                String.valueOf(limit),
                String.valueOf(offset)
        };

        List<Book> bookList = BrokerManager.getBroker(Book.class).rawSelect(db, sql, selectionArgs);
        return getBookInfoListFromBookList(db, bookList);
    }

    /**
     * Return the list of books loaned to a given person.
     *
     * @param db       SQLiteDatabase.
     * @param loanedTo Name of the person the books are loaned to.
     * @param limit    Max number of books to return.
     * @param offset   Offset.
     * @return List of BookInfo.
     */
    @Deprecated
    public static List<BookInfo> getBookInfoListByLoanedTo(SQLiteDatabase db, String loanedTo, int limit, int offset) {
        String[] selectedColumnList = new String[]{
                "boo." + Book.Cols.BOO_ID,
                "boo." + Book.Cols.BOO_TITLE,
                "boo." + Book.Cols.BOO_IS_READ,
                "boo." + Book.Cols.BOO_IS_FAVOURITE
        };

        String selectedColumns = StringUtils.join(selectedColumnList, ", ");
        String sql = "SELECT " + selectedColumns + " FROM " + Book.NAME + " boo WHERE boo." + Book.Cols.BOO_LOANED_TO + " = ? ORDER BY boo." + Book.Cols.BOO_TITLE + " COLLATE NOCASE LIMIT ? OFFSET ?;";

        String[] selectionArgs = new String[]{
                loanedTo,
                String.valueOf(limit),
                String.valueOf(offset)
        };

        List<Book> bookList = BrokerManager.getBroker(Book.class).rawSelect(db, sql, selectionArgs);
        return getBookInfoListFromBookList(db, bookList);
    }

    /**
     * Return the list of books of a given series.
     *
     * @param db       SQLiteDatabase.
     * @param seriesId Series id.
     * @param limit    Max number of books to return.
     * @param offset   Offset.
     * @return List of BookInfo.
     */
    @Deprecated
    public static List<BookInfo> getBookInfoListBySeries(SQLiteDatabase db, long seriesId, int limit, int offset) {
        String[] selectedColumnList = new String[]{
                "boo." + Book.Cols.BOO_ID,
                "boo." + Book.Cols.BOO_TITLE,
                "boo." + Book.Cols.BOO_IS_READ,
                "boo." + Book.Cols.BOO_IS_FAVOURITE
        };

        String selectedColumns = StringUtils.join(selectedColumnList, ", ");
        String sql = "SELECT boo." + selectedColumns + " FROM " + Book.NAME + " boo WHERE boo." + Book.Cols.SER_ID + " = ? ORDER BY boo." + Book.Cols.BOO_TITLE + " COLLATE NOCASE LIMIT ? OFFSET ?;";

        String[] selectionArgs = new String[]{
                String.valueOf(seriesId),
                String.valueOf(limit),
                String.valueOf(offset)
        };

        List<Book> bookList = BrokerManager.getBroker(Book.class).rawSelect(db, sql, selectionArgs);
        return getBookInfoListFromBookList(db, bookList);
    }

    /**
     * Return the list of books marked as favourites.
     *
     * @param db     SQLiteDatabase.
     * @param limit  Max number of books to return.
     * @param offset Offset.
     * @return List of BookInfo.
     */
    @Deprecated
    public static List<BookInfo> getBookInfoListFavourite(SQLiteDatabase db, int limit, int offset) {
        String[] selectedColumnList = new String[]{
                "boo." + Book.Cols.BOO_ID,
                "boo." + Book.Cols.BOO_TITLE,
                "boo." + Book.Cols.BOO_IS_READ,
                "boo." + Book.Cols.BOO_IS_FAVOURITE
        };

        String selectedColumns = StringUtils.join(selectedColumnList, ", ");
        String sql = "SELECT boo." + selectedColumns + " FROM " + Book.NAME + " boo WHERE boo." + Book.Cols.BOO_IS_FAVOURITE + " = ? ORDER BY boo." + Book.Cols.BOO_TITLE + " COLLATE NOCASE LIMIT ? OFFSET ?;";

        String[] selectionArgs = new String[]{
                String.valueOf(1L),
                String.valueOf(limit),
                String.valueOf(offset)
        };

        List<Book> bookList = BrokerManager.getBroker(Book.class).rawSelect(db, sql, selectionArgs);
        return getBookInfoListFromBookList(db, bookList);
    }

    /**
     * Return the list of books to read.
     *
     * @param db     SQLiteDatabase.
     * @param limit  Max number of books to return.
     * @param offset Offset.
     * @return List of BookInfo.
     */
    @Deprecated
    public static List<BookInfo> getBookInfoListToRead(SQLiteDatabase db, int limit, int offset) {
        String[] selectedColumnList = new String[]{
                "boo." + Book.Cols.BOO_ID,
                "boo." + Book.Cols.BOO_TITLE,
                "boo." + Book.Cols.BOO_IS_READ,
                "boo." + Book.Cols.BOO_IS_FAVOURITE
        };

        String selectedColumns = StringUtils.join(selectedColumnList, ", ");
        String sql = "SELECT boo." + selectedColumns + " FROM " + Book.NAME + " boo WHERE boo." + Book.Cols.BOO_IS_READ + " = ? ORDER BY boo." + Book.Cols.BOO_TITLE + " COLLATE NOCASE LIMIT ? OFFSET ?;";

        String[] selectionArgs = new String[]{
                String.valueOf(0L),
                String.valueOf(limit),
                String.valueOf(offset)
        };

        List<Book> bookList = BrokerManager.getBroker(Book.class).rawSelect(db, sql, selectionArgs);
        return getBookInfoListFromBookList(db, bookList);
    }

    /**
     * When given a list of {@link Book}, build a list of {@link BookInfo}
     * containing the original information of the books plus the list of their
     * authors.
     *
     * @param db       SQLiteDatabase.
     * @param bookList List of {@link Book}.
     * @return List of {@link BookInfo}.
     */
    public static List<BookInfo> getBookInfoListFromBookList(SQLiteDatabase db, List<Book> bookList) {
        List<BookInfo> bookInfoList = new ArrayList<BookInfo>();

        if (!bookList.isEmpty()) {
            List<Long> bookIdList = new ArrayList<Long>();
            for (Book book : bookList) {
                bookIdList.add(book.id);
            }

            List<BookAuthor> bookAuthorList = BrokerManager.getBroker(BookAuthor.class).getAllWhereIn(db, BookAuthor.Cols.BOO_ID,
                    bookIdList);

            List<Long> authorIdList = new ArrayList<Long>();
            for (BookAuthor bookAuthor : bookAuthorList) {
                if (!authorIdList.contains(bookAuthor.authorId)) {
                    authorIdList.add(bookAuthor.authorId);
                }
            }

            List<Author> authorList = BrokerManager.getBroker(Author.class).getAllWhereIn(db, Author.Cols.AUT_ID, authorIdList);

            LongSparseArray<List<BookAuthor>> bookAuthorMap = new LongSparseArray<List<BookAuthor>>();
            LongSparseArray<Author> authorMap = new LongSparseArray<Author>();
            for (BookAuthor bookAuthor : bookAuthorList) {
                if (bookAuthorMap.get(bookAuthor.bookId) == null) {
                    bookAuthorMap.put(bookAuthor.bookId, new ArrayList<BookAuthor>());
                }
                List<BookAuthor> baList = bookAuthorMap.get(bookAuthor.bookId);
                baList.add(bookAuthor);
            }
            for (Author author : authorList) {
                authorMap.put(author.id, author);
            }

            for (Book book : bookList) {
                BookInfo bookInfo = new BookInfo(book);

                List<BookAuthor> baList = bookAuthorMap.get(book.id);
                if (baList != null) {
                    for (BookAuthor bookAuthor : baList) {
                        Author author = authorMap.get(bookAuthor.authorId);
                        bookInfo.authors.add(author);
                    }
                }

                bookInfoList.add(bookInfo);
            }
        }
        return bookInfoList;
    }

    /**
     * Load the small thumbnail of a book.
     *
     * @param db     SQLiteDatabase.
     * @param bookId Id of a book.
     * @return Small thumbnail as a byte array.
     */
    public static byte[] getBookSmallThumbnail(SQLiteDatabase db, long bookId) {
        String sql = "SELECT " + Book.Cols.BOO_SMALL_THUMBNAIL + " FROM " + Book.NAME + " WHERE " + Book.Cols.BOO_ID + " = ?;";
        String[] selectionArgs = new String[]{String.valueOf(bookId)};
        Book book = BrokerManager.getBroker(Book.class).rawSelect(db, sql, selectionArgs).get(0);
        return book.smallThumbnail;
    }

    /**
     * Mark or unmark a book as favourite.
     *
     * @param db     SQLiteDatabase.
     * @param bookId Id of book.
     */
    public static void markBookAsFavourite(SQLiteDatabase db, long bookId) {
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

    /**
     * Mark or unmark a book as read.
     *
     * @param db     SQLiteDatabase.
     * @param bookId Id of book.
     */
    public static void markBookAsRead(SQLiteDatabase db, long bookId) {
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

    /**
     * Return a book (sets {@link Book#loanedTo} and {@link Book#loanDate} to
     * null and save it).
     *
     * @param db     SQLiteDatabase.
     * @param bookId Id of a book.
     */
    public static void returnBook(SQLiteDatabase db, long bookId) {
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

    /**
     * Save a BookInfo.
     *
     * @param db       SQLiteDatabase.
     * @param bookInfo BookInfo.
     */
    public static void saveBookInfo(SQLiteDatabase db, BookInfo bookInfo) {
        db.beginTransaction();
        try {
            boolean isCreation = bookInfo.id == null;

            if (bookInfo.publisher.name != null) {

                Publisher criteria = new Publisher();
                criteria.name = bookInfo.publisher.name;

                Publisher publisherDb = PublisherServices.getPublisherByCriteria(db, criteria);

                long publisherId;
                if (publisherDb != null) {
                    publisherId = publisherDb.id;
                } else {
                    PublisherServices.savePublisher(db, bookInfo.publisher);
                    publisherId = bookInfo.publisher.id;
                }
                bookInfo.publisherId = publisherId;
            } else {
                bookInfo.publisherId = null;
            }

            if (bookInfo.bookLocation.name != null) {
                BookLocation criteria = new BookLocation();
                criteria.name = bookInfo.bookLocation.name;

                BookLocation bookLocationDb = BookLocationServices.getBookLocationByCriteria(db, criteria);

                long bookLocationId;
                if (bookLocationDb != null) {
                    bookLocationId = bookLocationDb.id;
                } else {
                    BookLocationServices.saveBookLocation(db, bookInfo.bookLocation);
                    bookLocationId = bookInfo.bookLocation.id;
                }

                bookInfo.bookLocationId = bookLocationId;
            } else {
                bookInfo.bookLocationId = null;
            }

            if (bookInfo.series.name != null) {
                SeriesServices.saveSeries(db, bookInfo.series);
                bookInfo.seriesId = bookInfo.series.id;
            } else {
                bookInfo.seriesId = null;
            }

            if (bookInfo.isbn10 != null && !IsbnUtils.isValidIsbn10(bookInfo.isbn10)) {
                throw new InvalidParameterException("Invalid ISBN-10.");
            }

            if (bookInfo.isbn13 != null && !IsbnUtils.isValidIsbn13(bookInfo.isbn13)) {
                throw new InvalidParameterException("Invalid ISBN-13.");
            }

            BrokerManager.getBroker(Book.class).save(db, bookInfo);

            FTSBroker<BookFTS> brokerBookFTS = FTSBrokerManager.getBroker(BookFTS.class);
            BookFTS bookFts = new BookFTS(bookInfo);
            if (isCreation) {
                brokerBookFTS.insert(db, bookFts);
            } else {
                brokerBookFTS.update(db, bookFts);

                PublisherServices.deletePublishersWithoutBooks(db);
                SeriesServices.deleteSeriesWithoutBooks(db);
                BookLocationServices.deleteBookLocationsWithoutBooks(db);

                ThumbnailManager.getInstance().removeThumbnails(bookInfo.id);
            }

            updateBookAuthorList(db, bookInfo);
            updateBookCategoryList(db, bookInfo);

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * Delete the previous BookAuthor relationships of a book and create the new
     * ones.
     *
     * @param db       SQLiteDatabase.
     * @param bookInfo BookInfo.
     */
    private static void updateBookAuthorList(SQLiteDatabase db, BookInfo bookInfo) {
        BookAuthorServices.deleteBookAuthorListByBook(db, bookInfo.id);
        for (Author author : bookInfo.authors) {
            Author criteria = new Author();
            criteria.name = author.name;

            Author authorDb = AuthorServices.getAuthorByCriteria(db, criteria);

            long authorId;
            if (authorDb != null) {
                authorId = authorDb.id;
            } else {
                AuthorServices.saveAuthor(db, author);
                authorId = author.id;
            }

            BookAuthor bookAuthor = new BookAuthor();
            bookAuthor.authorId = authorId;
            bookAuthor.bookId = bookInfo.id;

            BookAuthorServices.saveBookAuthor(db, bookAuthor);
        }
        AuthorServices.deleteAuthorsWithoutBooks(db);
    }

    /**
     * Delete the previous BookCategory relationships of a book and create the
     * new ones.
     *
     * @param db       SQLiteDatabase.
     * @param bookInfo BookInfo.
     */
    private static void updateBookCategoryList(SQLiteDatabase db, BookInfo bookInfo) {
        BookCategoryServices.deleteBookCategoryListByBook(db, bookInfo.id);
        for (Category category : bookInfo.categories) {

            Category criteria = new Category();
            criteria.name = category.name;

            Category categoryDb = CategoryServices.getCategoryByCriteria(db, criteria);

            long categoryId;
            if (categoryDb != null) {
                categoryId = categoryDb.id;
            } else {
                CategoryServices.saveCategory(db, category);
                categoryId = category.id;
            }

            BookCategory bookCategory = new BookCategory();
            bookCategory.bookId = bookInfo.id;
            bookCategory.categoryId = categoryId;

            BookCategoryServices.saveBookCategory(db, bookCategory);
        }
        CategoryServices.deleteCategoriesWithoutBooks(db);
    }
}
