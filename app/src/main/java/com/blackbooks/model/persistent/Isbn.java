package com.blackbooks.model.persistent;

import com.blackbooks.model.metadata.Column;
import com.blackbooks.model.metadata.Column.SQLiteDataType;
import com.blackbooks.model.metadata.Table;

import java.io.Serializable;
import java.util.Date;

@Table(name = Isbn.NAME, version = 3)
public class Isbn implements Serializable {

    public static final String NAME = "ISBN";

    @Column(name = Cols.ISB_ID, primaryKey = true, type = SQLiteDataType.INTEGER, version = 3)
    public Long id;

    @Column(name = Cols.ISB_NUMBER, mandatory = true, unique = true, type = SQLiteDataType.TEXT, version = 3)
    public String number;

    @Column(name = Cols.ISB_DATE_ADDED, mandatory = true, type = SQLiteDataType.INTEGER, version = 3)
    public Date dateAdded;

    @Column(name = Cols.ISB_LOOKED_UP, mandatory = true, type = SQLiteDataType.INTEGER, version = 3)
    public Long lookedUp;

    @Column(name = Cols.ISB_SEARCH_SUCCESSFUL, mandatory = true, type = SQLiteDataType.INTEGER, version = 3)
    public Long searchSuccessful;

    /**
     * Default constructor.
     */
    public Isbn() {
        this.lookedUp = 0L;
        this.searchSuccessful = 0L;
    }

    public static final class Cols {
        public static final String ISB_ID = "ISB_ID";
        public static final String ISB_NUMBER = "ISB_NUMBER";
        public static final String ISB_DATE_ADDED = "ISB_DATE_ADDED";
        public static final String ISB_LOOKED_UP = "ISB_LOOKED_UP";
        public static final String ISB_SEARCH_SUCCESSFUL = "ISB_SEARCH_SUCCESSFUL";
    }
}
