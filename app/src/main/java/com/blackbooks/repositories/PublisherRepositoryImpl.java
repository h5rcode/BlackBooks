package com.blackbooks.repositories;

import com.blackbooks.database.SQLiteHelper;
import com.blackbooks.model.persistent.Book;
import com.blackbooks.model.persistent.Publisher;
import com.blackbooks.sql.BrokerManager;

import java.util.List;

public class PublisherRepositoryImpl extends AbstractRepository implements PublisherRepository {
    public PublisherRepositoryImpl(SQLiteHelper sqLiteHelper) {
        super(sqLiteHelper);
    }

    @Override
    public void deletePublisherWithoutBooks() {
        String sql = "DELETE FROM " + Publisher.NAME + " WHERE " + Publisher.Cols.PUB_ID + " IN (SELECT pub."
                + Publisher.Cols.PUB_ID + " FROM " + Publisher.NAME + " pub LEFT JOIN " + Book.NAME + " boo ON boo."
                + Book.Cols.PUB_ID + " = pub." + Publisher.Cols.PUB_ID + " WHERE boo." + Book.Cols.BOO_ID + " IS NULL)";

        getWritableDatabase().execSQL(sql);
    }

    @Override
    public Publisher getPublisher(long publisherId) {
        return BrokerManager.getBroker(Publisher.class).get(getReadableDatabase(), publisherId);
    }

    @Override
    public Publisher getPublisherByCriteria(Publisher criteria) {
        return BrokerManager.getBroker(Publisher.class).getByCriteria(getReadableDatabase(), criteria);
    }

    @Override
    public List<Publisher> getPublisherListByText(String text) {
        String sql = "SELECT * FROM " + Publisher.NAME + " WHERE LOWER(" + Publisher.Cols.PUB_NAME
                + ") LIKE '%' || LOWER(?) || '%' ORDER BY " + Publisher.Cols.PUB_NAME;
        String[] selectionArgs = {text};
        return BrokerManager.getBroker(Publisher.class).rawSelect(getReadableDatabase(), sql, selectionArgs);
    }

    @Override
    public long savePublisher(Publisher publisher) {
        return BrokerManager.getBroker(Publisher.class).save(getWritableDatabase(), publisher);
    }

    @Override
    public void deletePublishersWithoutBooks() {
        String sql = "DELETE FROM " + Publisher.NAME + " WHERE " + Publisher.Cols.PUB_ID + " IN (SELECT pub."
                + Publisher.Cols.PUB_ID + " FROM " + Publisher.NAME + " pub LEFT JOIN " + Book.NAME + " boo ON boo."
                + Book.Cols.PUB_ID + " = pub." + Publisher.Cols.PUB_ID + " WHERE boo." + Book.Cols.BOO_ID + " IS NULL)";

        getWritableDatabase().execSQL(sql);
    }
}
