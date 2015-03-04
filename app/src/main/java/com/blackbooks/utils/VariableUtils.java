package com.blackbooks.utils;

/**
 * Singleton class to pass variables across different activities.
 */
public final class VariableUtils {

    private static final Object LOCK = new Object();
    private static VariableUtils mInstance;

    private boolean mReloadBookGroupList;
    private boolean mReloadBookList;
    private boolean mReloadIsbnListLookedUp;
    private boolean mReloadIsbnListPending;
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
     * @param reloadBookGroupList True if the list of book groups should be reloaded.
     */
    public void setReloadBookGroupList(boolean reloadBookGroupList) {
        synchronized (LOCK) {
            mReloadBookGroupList = reloadBookGroupList;
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
                mReloadBookGroupList = true;
                mReloadIsbnListLookedUp = true;
                mReloadIsbnListPending = true;
            }
        }
    }

    /**
     * Return the value indicating whether the list of ISBNs that have been looked up should be reloaded.
     *
     * @return True if the list should be reloaded, false otherwise.
     */
    public boolean getReloadIsbnListLookedUp() {
        synchronized (LOCK) {
            return mReloadIsbnListLookedUp;
        }
    }

    /**
     * Set the value indicating whether the list of ISBNs that have been looked up should be reloaded.
     *
     * @param reloadIsbnListLookedUp True if the list should be reloaded, false otherwise.
     */
    public void setReloadIsbnListLookedUp(boolean reloadIsbnListLookedUp) {
        synchronized (LOCK) {
            mReloadIsbnListLookedUp = reloadIsbnListLookedUp;
        }
    }

    /**
     * Return the value indicating whether the list of pending ISBNs should be reloaded.
     *
     * @return True if the list should be reloaded, false otherwise.
     */
    public boolean getReloadIsbnListPending() {
        synchronized (LOCK) {
            return mReloadIsbnListPending;
        }
    }

    /**
     * Set the value indicating whether the list pending ISBNs should be reloaded.
     *
     * @param reloadIsbnListPending True if the list should be reloaded, false otherwise.
     */
    public void setReloadIsbnListPending(boolean reloadIsbnListPending) {
        synchronized (LOCK) {
            mReloadIsbnListPending = reloadIsbnListPending;
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
