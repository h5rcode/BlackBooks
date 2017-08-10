package com.blackbooks.repositories;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.blackbooks.database.SQLiteHelper;
import com.blackbooks.model.persistent.Author;
import com.blackbooks.model.persistent.BookAuthor;
import com.blackbooks.sql.BrokerManager;

import java.util.List;

public class AuthorRepositoryImpl extends AbstractRepository implements AuthorRepository {
    public AuthorRepositoryImpl(SQLiteHelper sqLiteHelper) {
        super(sqLiteHelper);
    }

    @Override
    public void deleteAuthor(long authorId) {
        BrokerManager.getBroker(Author.class).delete(getWritableDatabase(), authorId);
    }

    @Override
    public List<Author> getAuthorsByIds(List<Long> authorIdList) {
        return BrokerManager.getBroker(Author.class).getAllWhereIn(getReadableDatabase(), Author.Cols.AUT_ID, authorIdList);
    }

    @Override
    public void deleteAuthorsWithoutBooks() {
        String sql = "DELETE FROM " + Author.NAME + " WHERE " + Author.Cols.AUT_ID + " IN (SELECT aut." + Author.Cols.AUT_ID
                + " FROM " + Author.NAME + " aut LEFT JOIN " + BookAuthor.NAME + " bka ON bka." + BookAuthor.Cols.AUT_ID
                + " = aut." + Author.Cols.AUT_ID + " WHERE bka." + BookAuthor.Cols.BKA_ID + " IS NULL)";

        getWritableDatabase().execSQL(sql);
    }

    @Override
    public Author getAuthor(long id) {
        return BrokerManager.getBroker(Author.class).get(getReadableDatabase(), id);
    }

    @Override
    public Author getAuthorByCriteria(Author criteria) {
        return BrokerManager.getBroker(Author.class).getByCriteria(getReadableDatabase(), criteria);
    }

    @Override
    public List<Author> getAuthorListByText(String text) {
        String sql = "SELECT * FROM " + Author.NAME + " WHERE LOWER(" + Author.Cols.AUT_NAME
                + ") LIKE '%' || LOWER(?) || '%' ORDER BY " + Author.Cols.AUT_NAME;
        String[] selectionArgs = {text};
        return BrokerManager.getBroker(Author.class).rawSelect(getReadableDatabase(), sql, selectionArgs);
    }

    @Override
    public long saveAuthor(Author author) {
        return BrokerManager.getBroker(Author.class).save(getWritableDatabase(), author);
    }

    @Override
    public void updateAuthor(long authorId, String newName) {
        ContentValues values = new ContentValues();
        values.put(Author.Cols.AUT_NAME, newName);
        String whereClause = Author.Cols.AUT_ID + " = ?";
        String[] whereArgs = new String[]{String.valueOf(authorId)};
        getWritableDatabase().updateWithOnConflict(Author.NAME, values, whereClause, whereArgs, SQLiteDatabase.CONFLICT_ROLLBACK);
    }

    @Override
    public int getAuthorCount() {
        String sql = "SELECT COUNT(*) FROM " + Author.NAME;
        return queryInt(sql);
    }

    private int queryInt(String sql) {
        Cursor cursor = getReadableDatabase().rawQuery(sql, null);
        cursor.moveToNext();
        int result = cursor.getInt(0);
        cursor.close();
        return result;
    }
}
