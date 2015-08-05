package com.blackbooks.model.nonpersistent;

import java.io.Serializable;

/**
 * This class represents a column of a CSV file.
 */
public final class CsvColumn implements Serializable {

    private final int mIndex;
    private final String mName;
    private BookProperty mBookProperty;

    /**
     * Constructor.
     *
     * @param index Index of the column in the file (the first row is at index 0).
     * @param name  Name of the column.
     */
    public CsvColumn(int index, String name) {
        mIndex = index;
        mName = name;
    }

    /**
     * Returns the book property associated to this field.
     *
     * @return BookProperty.
     */
    public BookProperty getBookProperty() {
        return mBookProperty;
    }

    /**
     * Returns the index of the column in the file.
     *
     * @return Index.
     */
    public int getIndex() {
        return mIndex;
    }

    /**
     * Returns the name of the column in the file.
     *
     * @return Name.
     */
    public String getName() {
        return mName;
    }

    /**
     * Sets the field associated to this column.
     *
     * @param associatedProperty Associated field.
     */
    public void setBookProperty(BookProperty associatedProperty) {
        mBookProperty = associatedProperty;
    }

    /**
     * The enumeration of the different properties of a book that a CSV column can be mapped to.
     */
    public enum BookProperty {

        /**
         * No book property.
         */
        NONE,

        /**
         * Id of the book in the SQLite database.
         */
        ID,

        /**
         * Title of the book.
         */
        TITLE,

        /**
         * Subtitle of the book.
         */
        SUBTITLE,

        /**
         * The authors of the book.
         */
        AUTHORS,

        /**
         * The categories of the book.
         */
        CATEGORIES,

        /**
         * Series.
         */
        SERIES,

        /**
         * Number.
         */
        NUMBER,
        /**
         * Page count.
         */
        PAGE_COUNT,

        /**
         * Two-letter ISO language code of the book.
         */
        LANGUAGE_CODE,

        /**
         * Description.
         */
        DESCRIPTION,


        /**
         * Publisher.
         */
        PUBLISHER,

        /**
         * Published date.
         */
        PUBLISHED_DATE,

        /**
         * ISBN 10.
         */
        ISBN_10,

        /**
         * ISBN 13.
         */
        ISBN_13
    }
}
