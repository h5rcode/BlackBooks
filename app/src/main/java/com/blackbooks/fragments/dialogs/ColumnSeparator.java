package com.blackbooks.fragments.dialogs;

import java.io.Serializable;

/**
 * Column separator.
 */
public final class ColumnSeparator implements Serializable {

    private final char mCharacter;
    private final int mResourceId;

    /**
     * Constructor.
     *
     * @param character  Character.
     * @param resourceId Id of the resource representing the separator's name.
     */
    public ColumnSeparator(char character, int resourceId) {
        mCharacter = character;
        mResourceId = resourceId;
    }

    /**
     * Return the character.
     *
     * @return Character.
     */
    public char getCharacter() {
        return mCharacter;
    }

    /**
     * Return the resource id.
     *
     * @return Resource id.
     */
    public int getResourceId() {
        return mResourceId;
    }
}
