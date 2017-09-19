package com.blackbooks.services;

import com.blackbooks.model.persistent.Series;

import java.util.List;

public interface SeriesService {
    void deleteSeries(long seriesId);

    void deleteSeriesWithoutBooks();

    Series getSeriesByCriteria(Series criteria);

    List<Series> getSeriesListByText(String text);

    long saveSeries(Series series);

    Series getSeries(long serId);

    void updateSeries(long seriesId, String newName);
}
