package com.blackbooks.utils;

/**
 * Singleton class to pass variables across different activities.
 */
public final class VariableUtils {

    private static final Object LOCK = new Object();
    private static VariableUtils mInstance;

    private boolean mReloadBookGroupList;
    private boolean mReloadBookList;
    private boolean mBulkSearchRunning;

    /**
     * Private constructor.
     */
    private VariableUtils() {
        mReloadBookGroupList = false;
        mReloadBookList = false;
        mBulkSearchRunning = false;
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
     * Get a value indicating if the book group list should be reloaded (for instance
     * if a book has been added/edited/removed).
     *
     * @return True if the book group list should be reloaded, false otherwise.
     */
    public boolean getReloadBookGroupList() {
        synchronized (LOCK) {
            return mReloadBookGroupList;
        }
    }

    /**
     * Set the value indicating if the book group list should be reloaded.
     */
    public void setReloadBookGroupListToFalse() {
        synchronized (LOCK) {
            mReloadBookGroupList = false;
        }
    }

    /**
     * Get a value indicating if the book list should be reloaded (for instance
     * if a book has been added/edited/removed).
     *
     * @return True if the book list should be reloaded, false otherwise.
     */
    public boolean getReloadBookList() {
        synchronized (LOCK) {
            return mReloadBookList;
        }
    }

    /**
     * Set the value indicating if the book list should be reloaded.
     * If the book list must be reloaded (i.e. parameter reloadBookList is true), the book group
     * list must also be reloaded.
     *
     * @param reloadBookList True if the book list should be reloaded, false otherwise.
     */
    public void setReloadBookList(boolean reloadBookList) {
        synchronized (LOCK) {
            mReloadBookList = reloadBookList;
            if (reloadBookList) {
                mReloadBookGroupList = reloadBookList;
            }
        }
    }

    /**
     * Return a boolean value indicating whether the bulk search is running or not.
     *
     * @return True if the search is running, false otherwise.
     */
    public boolean getBulkSearchRunning() {
        synchronized (LOCK) {
            return mBulkSearchRunning;
        }
    }

    /**
     * Set the value indicating whether the bulk search is running or not.
     *
     * @param bulkSearchRunning True if the search is running, false otherwise.
     */
    public void setBulkSearchRunning(boolean bulkSearchRunning) {
        synchronized (LOCK) {
            mBulkSearchRunning = bulkSearchRunning;
        }
    }
}
