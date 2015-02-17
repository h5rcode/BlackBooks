package com.blackbooks.model.nonpersistent;

import java.io.Serializable;

/**
 * A book group.
 */
public final class BookGroup {

    public Serializable id;
    public String name;
    public int count;

    /**
     * Enumeration of the different book groups.
     */
    public enum BookGroupType {

        /**
         * Author.
         */
        AUTHOR,

        /**
         * Book location.
         */
        BOOK_LOCATION,

        /**
         * Category.
         */
        CATEGORY,

        /**
         * Favourite.
         */
        FAVOURITE,

        /**
         * First letter of the title.
         */
        FIRST_LETTER,

        /**
         * Language.
         */
        LANGUAGE,

        /**
         * Loan.
         */
        LOAN,

        /**
         * Series.
         */
        SERIES,

        /**
         * To read.
         */
        TO_READ
    }
}
