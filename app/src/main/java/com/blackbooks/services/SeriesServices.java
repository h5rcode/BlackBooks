package com.blackbooks.services;

import java.util.ArrayList;
import java.util.List;

import android.database.sqlite.SQLiteDatabase;
import android.support.v4.util.LongSparseArray;

import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.model.nonpersistent.SeriesInfo;
import com.blackbooks.model.persistent.Book;
import com.blackbooks.model.persistent.Series;
import com.blackbooks.sql.BrokerManager;

/**
 * Series services.
 */
public class SeriesServices {

	/**
	 * Delete the publishers that are not referred by any books in the database.
	 * 
	 * @param db
	 *            SQLiteDatabase.
	 */
	public static void deleteSeriesWithoutBooks(SQLiteDatabase db) {
		String sql = "DELETE FROM " + Series.NAME + " WHERE " + Series.Cols.SER_ID + " IN (SELECT ser." + Series.Cols.SER_ID
				+ " FROM " + Series.NAME + " ser LEFT JOIN " + Book.NAME + " boo ON boo." + Book.Cols.SER_ID + " = ser."
				+ Series.Cols.SER_ID + " WHERE boo." + Book.Cols.BOO_ID + " IS NULL)";

		BrokerManager.getBroker(Series.class).executeSql(db, sql);
	}

	/**
	 * Get the one row matching a criteria. If no rows or more that one rows
	 * match the criteria, the method returns null.
	 * 
	 * @param db
	 *            SQLiteDatabase.
	 * @param criteria
	 *            The search criteria.
	 * @return Series.
	 */
	public static Series getSeriesByCriteria(SQLiteDatabase db, Series criteria) {
		return BrokerManager.getBroker(Series.class).getByCriteria(db, criteria);
	}

	/**
	 * Get the info of all the series in the database.
	 * 
	 * @param db
	 *            SQLiteDatabase.
	 * @return List of SeriesInfo.
	 */
	public static List<SeriesInfo> getSeriesInfoList(SQLiteDatabase db) {
		List<Series> seriesList = BrokerManager.getBroker(Series.class).getAll(db);
		List<BookInfo> bookInfoList = BookServices.getBookInfoList(db);
		LongSparseArray<SeriesInfo> seriesMap = new LongSparseArray<SeriesInfo>();

		List<SeriesInfo> seriesInfoList = new ArrayList<SeriesInfo>();
		for (Series series : seriesList) {
			SeriesInfo seriesInfo = new SeriesInfo(series);
			seriesMap.put(series.id, seriesInfo);
			seriesInfoList.add(seriesInfo);
		}

		SeriesInfo unspecifiedSeries = new SeriesInfo();
		for (BookInfo bookInfo : bookInfoList) {
			if (bookInfo.seriesId == null) {
				unspecifiedSeries.books.add(bookInfo);
			} else {
				SeriesInfo seriesInfo = seriesMap.get(bookInfo.seriesId);
				seriesInfo.books.add(bookInfo);
			}
		}

		if (!unspecifiedSeries.books.isEmpty()) {
			seriesInfoList.add(unspecifiedSeries);
		}

		return seriesInfoList;
	}

	/**
	 * Get the list of series whose name contains a given text.
	 * 
	 * @param db
	 *            SQLiteDatabase.
	 * @param text
	 *            Text.
	 * @return List of Series.
	 */
	public static List<Series> getSeriesListByText(SQLiteDatabase db, String text) {
		String sql = "SELECT * FROM " + Series.NAME + " WHERE LOWER(" + Series.Cols.SER_NAME
				+ ") LIKE '%' || LOWER(?) || '%' ORDER BY " + Series.Cols.SER_NAME;
		String[] selectionArgs = { text };
		return BrokerManager.getBroker(Series.class).rawSelect(db, sql, selectionArgs);
	}

	/**
	 * Save a series.
	 * 
	 * @param db
	 *            SQLiteDatabase.
	 * @param series
	 *            Series.
	 * @return Id of the saved Series.
	 */
	public static long saveSeries(SQLiteDatabase db, Series series) {
		return BrokerManager.getBroker(Series.class).save(db, series);
	}

	/**
	 * Get a series from the database.
	 * 
	 * @param db
	 *            SQLiteDatabase.
	 * @param serId
	 *            Id of the series.
	 * @return Series.
	 */
	public static Series getSeries(SQLiteDatabase db, long serId) {
		return BrokerManager.getBroker(Series.class).get(db, serId);
	}
}
