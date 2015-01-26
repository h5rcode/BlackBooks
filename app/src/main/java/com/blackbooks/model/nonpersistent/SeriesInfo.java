package com.blackbooks.model.nonpersistent;

import com.blackbooks.model.persistent.Series;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * All the info about a series.
 */
public class SeriesInfo extends Series implements Serializable {

    private static final long serialVersionUID = 3177599050568320257L;

    public List<BookInfo> books;

    /**
     * Default constructor.
     */
    public SeriesInfo() {
        this.books = new ArrayList<BookInfo>();
    }

    /**
     * Constructor that creates a copy of an instance of Series.
     *
     * @param series Series.
     */
    public SeriesInfo(Series series) {
        this();
        this.id = series.id;
        this.name = series.name;
    }
}
