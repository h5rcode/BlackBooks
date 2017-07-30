package com.blackbooks.services;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.blackbooks.model.persistent.Book;
import com.blackbooks.model.persistent.Series;
import com.blackbooks.repositories.SeriesRepository;
import com.blackbooks.sql.BrokerManager;

import java.util.List;

/**
 * Series services.
 */
public final class SeriesServiceImpl implements SeriesService {

    private final SeriesRepository seriesRepository;

    public SeriesServiceImpl(SeriesRepository seriesRepository) {
        this.seriesRepository = seriesRepository;
    }

    /**
     * Delete a series.
     *
     * @param seriesId Id of the series.
     */
    public void deleteSeries(long seriesId) {
        seriesRepository.deleteSeries(seriesId);
    }

    /**
     * Delete the publishers that are not referred by any books in the database.
     */
    public void deleteSeriesWithoutBooks() {
        seriesRepository.deleteSeriesWithoutBooks();
    }

    /**
     * Get the one row matching a criteria. If no rows or more that one rows
     * match the criteria, the method returns null.
     *
     * @param criteria The search criteria.
     * @return Series.
     */
    public Series getSeriesByCriteria(Series criteria) {
        return seriesRepository.getSeriesByCriteria(criteria);
    }

    /**
     * Get the list of series whose name contains a given text.
     *
     * @param text Text.
     * @return List of Series.
     */
    public List<Series> getSeriesListByText(String text) {
        return seriesRepository.getSeriesListByText(text);
    }

    /**
     * Save a series.
     *
     * @param series Series.
     * @return Id of the saved Series.
     */
    public long saveSeries(Series series) {
        return seriesRepository.saveSeries(series);
    }

    /**
     * Get a series from the database.
     *
     * @param serId Id of the series.
     * @return Series.
     */
    public Series getSeries(long serId) {
        return seriesRepository.getSeries(serId);
    }

    /**
     * Update a series.
     *
     * @param seriesId Id of the series.
     * @param newName  New name.
     */
    public void updateSeries(long seriesId, String newName) {
        seriesRepository.updateSeries(seriesId, newName);
    }
}
