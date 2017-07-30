package com.blackbooks.repositories;

import android.database.sqlite.SQLiteDatabase;

import com.blackbooks.model.persistent.Book;
import com.blackbooks.model.persistent.Publisher;
import com.blackbooks.sql.BrokerManager;

import java.util.List;

public class PublisherRepositoryImpl implements PublisherRepository {
    private SQLiteDatabase db;

    public PublisherRepositoryImpl(SQLiteDatabase db) {
        this.db = db;
    }

    @Override
    public void deletePublisherWithoutBooks() {
        String sql = "DELETE FROM " + Publisher.NAME + " WHERE " + Publisher.Cols.PUB_ID + " IN (SELECT pub."
                + Publisher.Cols.PUB_ID + " FROM " + Publisher.NAME + " pub LEFT JOIN " + Book.NAME + " boo ON boo."
                + Book.Cols.PUB_ID + " = pub." + Publisher.Cols.PUB_ID + " WHERE boo." + Book.Cols.BOO_ID + " IS NULL)";

        db.execSQL(sql);
    }

    @Override
    public Publisher getPublisher(long publisherId) {
        return BrokerManager.getBroker(Publisher.class).get(db, publisherId);
    }

    @Override
    public Publisher getPublisherByCriteria(Publisher criteria) {
        return BrokerManager.getBroker(Publisher.class).getByCriteria(db, criteria);
    }

    @Override
    public List<Publisher> getPublisherListByText(String text) {
        String sql = "SELECT * FROM " + Publisher.NAME + " WHERE LOWER(" + Publisher.Cols.PUB_NAME
                + ") LIKE '%' || LOWER(?) || '%' ORDER BY " + Publisher.Cols.PUB_NAME;
        String[] selectionArgs = {text};
        return BrokerManager.getBroker(Publisher.class).rawSelect(db, sql, selectionArgs);
    }

    @Override
    public long savePublisher(Publisher publisher) {
        return BrokerManager.getBroker(Publisher.class).save(db, publisher);
    }

    @Override
    public void deletePublishersWithoutBooks() {
        String sql = "DELETE FROM " + Publisher.NAME + " WHERE " + Publisher.Cols.PUB_ID + " IN (SELECT pub."
                + Publisher.Cols.PUB_ID + " FROM " + Publisher.NAME + " pub LEFT JOIN " + Book.NAME + " boo ON boo."
                + Book.Cols.PUB_ID + " = pub." + Publisher.Cols.PUB_ID + " WHERE boo." + Book.Cols.BOO_ID + " IS NULL)";

        db.execSQL(sql);
    }
}
