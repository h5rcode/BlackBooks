package com.blackbooks.model.persistent;

import com.blackbooks.model.metadata.Column;
import com.blackbooks.model.metadata.Column.SQLiteDataType;
import com.blackbooks.model.metadata.Table;

import java.io.Serializable;

@Table(name = Series.NAME, version = 1)
public class Series implements Serializable {

    public static final String NAME = "SERIES";

    private static final long serialVersionUID = 1101490965585938183L;

    @Column(name = Cols.SER_ID, primaryKey = true, type = SQLiteDataType.INTEGER, version = 1)
    public Long id;

    @Column(name = Cols.SER_NAME, type = SQLiteDataType.TEXT, mandatory = true, version = 1)
    public String name;

    /**
     * Default constructor.
     */
    public Series() {
    }

    /**
     * Default constructor.
     *
     * @param series Series.
     */
    public Series(Series series) {
        this.id = series.id;
        this.name = series.name;
    }

    public class Cols {
        public static final String SER_ID = "SER_ID";
        public static final String SER_NAME = "SER_NAME";
    }
}
