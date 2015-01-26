package com.blackbooks.search;

/**
 * The source for the result of a book search. As all results are merged, each
 * source has a unique merge order that should be accessed using method
 * {@link #getMergeOrder()}.
 */
public enum BookSearchResultSource {

    /**
     * Amazon.
     */
    AMAZON(1),

    /**
     * Open Library.
     */
    OPEN_LIBRARY(2),

    /**
     * Google Books.
     */
    GOOGLE_BOOKS(3);
    private int mMergeOrder;

    private BookSearchResultSource(int mergeOrder) {
        this.mMergeOrder = mergeOrder;
    }

    /**
     * Return the merge order for the books found using this source. The lowest
     * order is used first, the the other results will be merged to it, erasing
     * its properties.
     *
     * @return Merge order.
     */
    public int getMergeOrder() {
        return mMergeOrder;
    }
}
