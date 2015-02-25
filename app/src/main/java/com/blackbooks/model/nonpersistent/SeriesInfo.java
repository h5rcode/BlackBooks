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

    public final List<BookInfo> books = new ArrayList<BookInfo>();

    /**
     * Default constructor.
     */
    public SeriesInfo() {
        super();
    }

    /**
     * Copy constructor.
     *
     * @param series Series.
     */
    public SeriesInfo(Series series) {
        super(series);
    }
}
