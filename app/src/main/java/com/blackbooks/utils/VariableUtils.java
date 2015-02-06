package com.blackbooks.utils;

/**
 * Singleton class to pass variables across different activities.
 */
public final class VariableUtils {

    private static final Object LOCK_RELOAD_BOOK_LIST = new Object();
    private static VariableUtils mInstance;
    private boolean mReloadBookList;

    /**
     * Private constructor.
     */
    private VariableUtils() {
        mReloadBookList = false;
    }

    /**
     * Get the unique instance of the class.
     *
     * @return VariableUtils.
     */
    public static synchronized VariableUtils getInstance() {
        if (mInstance == null) {
            mInstance = new VariableUtils();
        }
        return mInstance;
    }

    /**
     * Get a value indicating if the book list should be reloaded (for instance
     * if a book has been added/edited/removed).
     *
     * @return True if the book list should be reloaded, false otherwise.
     */
    public boolean getReloadBookList() {
        synchronized (LOCK_RELOAD_BOOK_LIST) {
            return mReloadBookList;
        }
    }

    /**
     * Set the value indicating if the book list should be reloaded.
     *
     * @param reloadBookList True if the book list should be reloaded, false otherwise.
     */
    public void setReloadBookList(boolean reloadBookList) {
        synchronized (LOCK_RELOAD_BOOK_LIST) {
            mReloadBookList = reloadBookList;
        }
    }
}
