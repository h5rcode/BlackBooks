package com.blackbooks.repositories;

import android.database.Cursor;

import com.blackbooks.database.SQLiteHelper;
import com.blackbooks.model.persistent.Book;
import com.blackbooks.model.persistent.fts.BookFTS;
import com.blackbooks.sql.BrokerManager;
import com.blackbooks.sql.FTSBrokerManager;

import java.util.List;

public class BookFTSRepositoryImpl extends AbstractRepository implements BookFTSRepository {
    public BookFTSRepositoryImpl(SQLiteHelper sqLiteHelper) {
        super(sqLiteHelper);
    }

    @Override
    public int getSearchResultCount(String query) {

        String select = "SELECT COUNT(*) FROM " + BookFTS.NAME + " book_fts WHERE book_fts MATCH ?;";

        Cursor cursor = getReadableDatabase().rawQuery(select, new String[]{query});
        cursor.moveToNext();
        int resultCount = cursor.getInt(0);
        cursor.close();
        return resultCount;
    }

    @Override
    public List<Book> searchBooks(String query, int limit, int offset) {
        String sql = "SELECT book." + Book.Cols.BOO_ID + ", book." + Book.Cols.BOO_TITLE + ", book." + Book.Cols.BOO_SUBTITLE
                + ", book." + Book.Cols.BOO_DESCRIPTION + " FROM " + BookFTS.NAME + " book_fts JOIN " + Book.NAME
                + " book ON book." + Book.Cols.BOO_ID + " = book_fts." + BookFTS.Cols.DOCID + " WHERE book_fts MATCH ? ORDER BY "
                + Book.Cols.BOO_TITLE + " COLLATE NOCASE LIMIT ? OFFSET ?;";

        String selection[] = new String[]{
                query,
                String.valueOf(limit),
                String.valueOf(offset)
        };

        return BrokerManager.getBroker(Book.class).rawSelect(getReadableDatabase(), sql, selection);
    }

    @Override
    public void deleteBook(long bookId) {
        FTSBrokerManager.getBroker(BookFTS.class).delete(getWritableDatabase(), bookId);
    }

    @Override
    public void insert(BookFTS bookFts) {
        FTSBrokerManager.getBroker(BookFTS.class).insert(getWritableDatabase(), bookFts);
    }

    @Override
    public void update(BookFTS bookFts) {
        FTSBrokerManager.getBroker(BookFTS.class).update(getWritableDatabase(), bookFts);
    }
}
