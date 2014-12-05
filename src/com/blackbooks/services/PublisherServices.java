package com.blackbooks.services;

import java.util.List;

import android.database.sqlite.SQLiteDatabase;

import com.blackbooks.model.persistent.Book;
import com.blackbooks.model.persistent.Publisher;
import com.blackbooks.sql.BrokerManager;

/**
 * Publisher services.
 */
public class PublisherServices {

	/**
	 * Delete the publishers that are not referred by any books in the database.
	 * 
	 * @param db
	 *            SQLiteDatabase.
	 */
	public static void deletePublishersWithoutBooks(SQLiteDatabase db) {
		String sql = "DELETE FROM " + Publisher.NAME + " WHERE " + Publisher.Cols.PUB_ID + " IN (SELECT pub."
				+ Publisher.Cols.PUB_ID + " FROM " + Publisher.NAME + " pub LEFT JOIN " + Book.NAME + " boo ON boo."
				+ Book.Cols.PUB_ID + " = pub." + Publisher.Cols.PUB_ID + " WHERE boo." + Book.Cols.BOO_ID + " IS NULL)";

		BrokerManager.getBroker(Publisher.class).executeSql(db, sql);
	}

	/**
	 * Get a publisher.
	 * 
	 * @param db
	 *            SQLiteDatabase.
	 * @param publisherId
	 *            Id of the publisher.
	 * @return Publisher.
	 */
	public static Publisher getPublisher(SQLiteDatabase db, long publisherId) {
		return BrokerManager.getBroker(Publisher.class).get(db, publisherId);
	}

	/**
	 * Get the one row matching a criteria. If no rows or more that one rows
	 * match the criteria, the method returns null.
	 * 
	 * @param db
	 *            SQLiteDatabase.
	 * @param criteria
	 *            The search criteria.
	 * @return Author.
	 */
	public static Publisher getPublisherByCriteria(SQLiteDatabase db, Publisher criteria) {
		return BrokerManager.getBroker(Publisher.class).getByCriteria(db, criteria);
	}

	/**
	 * Get the list of publishers whose name contains a given text.
	 * 
	 * @param db
	 *            SQLiteDatabase.
	 * @param text
	 *            Text.
	 * @return List of Publisher.
	 */
	public static List<Publisher> getPublisherListByText(SQLiteDatabase db, String text) {
		String sql = "SELECT * FROM " + Publisher.NAME + " WHERE LOWER(" + Publisher.Cols.PUB_NAME
				+ ") LIKE '%' || LOWER(?) || '%' ORDER BY " + Publisher.Cols.PUB_NAME;
		String[] selectionArgs = { text };
		return BrokerManager.getBroker(Publisher.class).rawSelect(db, sql, selectionArgs);
	}

	/**
	 * Save an publisher in the database.
	 * 
	 * @param db
	 *            SQLiteDatabase.
	 * @param publisher
	 *            Publisher.
	 * @return Id of the saved publisher.
	 */
	public static long savePublisher(SQLiteDatabase db, Publisher publisher) {
		return BrokerManager.getBroker(Publisher.class).save(db, publisher);
	}
}
