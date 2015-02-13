package com.blackbooks.model.persistent;

import com.blackbooks.model.metadata.Column;
import com.blackbooks.model.metadata.Column.SQLiteDataType;
import com.blackbooks.model.metadata.Table;

import java.io.Serializable;

@Table(name = BookLocation.NAME, version = 1)
public class BookLocation implements Serializable {

    public static final String NAME = "BOOK_LOCATION";

    private static final long serialVersionUID = -7452261290218427907L;

    @Column(name = Cols.BKL_ID, primaryKey = true, type = SQLiteDataType.INTEGER, version = 1)
    public Long id;

    @Column(name = Cols.BKL_NAME, mandatory = true, unique = true, type = SQLiteDataType.TEXT, version = 1)
    public String name;

    /**
     * Default constructor.
     */
    public BookLocation() {
    }

    /**
     * Copy constructor.
     *
     * @param bookLocation BookLocation.
     */
    public BookLocation(BookLocation bookLocation) {
        this.id = bookLocation.id;
        this.name = bookLocation.name;
    }

    public class Cols {
        public static final String BKL_ID = "BKL_ID";
        public static final String BKL_NAME = "BKL_NAME";
    }
}
