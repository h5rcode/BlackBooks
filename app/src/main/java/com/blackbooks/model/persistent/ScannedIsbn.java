package com.blackbooks.model.persistent;

import com.blackbooks.model.metadata.Column;
import com.blackbooks.model.metadata.Column.SQLiteDataType;
import com.blackbooks.model.metadata.Table;

import java.io.Serializable;
import java.util.Date;

@Table(name = ScannedIsbn.NAME, version = 3)
public class ScannedIsbn implements Serializable {

    public static final String NAME = "SCANNED_ISBN";

    @Column(name = Cols.SCI_ID, primaryKey = true, type = SQLiteDataType.INTEGER, version = 3)
    public Long id;

    @Column(name = Cols.SCI_ISBN, mandatory = true, unique = true, type = SQLiteDataType.TEXT, version = 3)
    public String isbn;

    @Column(name = Cols.SCI_SCAN_DATE, mandatory = true, type = SQLiteDataType.INTEGER, version = 3)
    public Date scanDate;

    /**
     * Default constructor.
     */
    public ScannedIsbn() {
    }

    public static final class Cols {
        public static final String SCI_ID = "SCI_ID";
        public static final String SCI_ISBN = "SCN_ISBN";
        public static final String SCI_SCAN_DATE = "SCI_SCAN_DATE";
    }
}
