package com.blackbooks.model.persistent;

import com.blackbooks.model.metadata.Column;
import com.blackbooks.model.metadata.Column.SQLiteDataType;
import com.blackbooks.model.metadata.Table;

@Table(name = BookAuthor.NAME, version = 1)
public class BookAuthor {

    public static final String NAME = "BOOK_AUTHOR";

    @Column(name = Cols.BKA_ID, primaryKey = true, type = SQLiteDataType.INTEGER, version = 1)
    public Long id;

    @Column(name = Cols.BOO_ID, mandatory = true, type = SQLiteDataType.INTEGER, referencedType = Book.class, onDeleteCascade = true, version = 1)
    public Long bookId;

    @Column(name = Cols.AUT_ID, mandatory = true, type = SQLiteDataType.INTEGER, referencedType = Author.class, onDeleteCascade = true, version = 1)
    public Long authorId;

    public final static class Cols {
        public final static String BKA_ID = "BKA_ID";
        public final static String BOO_ID = "BOO_ID";
        public final static String AUT_ID = "AUT_ID";
    }
}
