package com.blackbooks.utils;

/**
 * Singleton class to pass variables across different activities.
 */
public final class VariableUtils {

    private final static VariableUtils mInstance = new VariableUtils();

    private boolean mReloadBookList;

    /**
     * Private constructor.
     */
    private VariableUtils() {
    }

    /**
     * Get the unique instance of the class.
     *
     * @return VariableUtils.
     */
    public static VariableUtils getInstance() {
        return mInstance;
    }

    /**
     * Get a value indicating if the book list should be reloaded (for instance
     * if a book has been added/edited/removed).
     *
     * @return True if the book list should be reloaded, false otherwise.
     */
    public boolean getReloadBookList() {
        return mReloadBookList;
    }

    /**
     * Set the value indicating if the book list should be reloaded.
     *
     * @param reloadBookList True if the book list should be reloaded, false otherwise.
     */
    public void setReloadBookList(boolean reloadBookList) {
        mReloadBookList = reloadBookList;
    }
}
