package com.blackbooks.model.persistent;

import com.blackbooks.model.metadata.Column;
import com.blackbooks.model.metadata.Column.SQLiteDataType;
import com.blackbooks.model.metadata.Table;

import java.io.Serializable;

@Table(name = Category.NAME, version = 1)
public class Category implements Serializable {

    public static final String NAME = "CATEGORY";

    private static final long serialVersionUID = -1958430806785527505L;

    @Column(name = Cols.CAT_ID, primaryKey = true, type = SQLiteDataType.INTEGER, version = 1)
    public Long id;

    @Column(name = Cols.CAT_NAME, type = SQLiteDataType.TEXT, unique = true, mandatory = true, version = 1)
    public String name;

    /**
     * Default constructor.
     */
    public Category() {
    }

    /**
     * Copy constructor.
     *
     * @param category Category.
     */
    public Category(Category category) {
        this();
        this.id = category.id;
        this.name = category.name;
    }

    public final static class Cols {
        public final static String CAT_ID = "CAT_ID";
        public final static String CAT_NAME = "CAT_NAME";
    }
}
