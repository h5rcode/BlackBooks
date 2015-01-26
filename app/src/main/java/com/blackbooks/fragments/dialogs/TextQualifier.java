package com.blackbooks.fragments.dialogs;

/**
 * Text qualifier.
 */
public final class TextQualifier {

    private final char mCharacter;
    private final int mResourceId;

    /**
     * Constructor.
     *
     * @param character  Character.
     * @param resourceId Id of the resource representing the qualifier's name.
     */
    public TextQualifier(char character, int resourceId) {
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
