package com.blackbooks.adapters;

/**
 * An item of a list.
 */
public interface ListItem {

    /**
     * Return the type of this item.
     *
     * @return A value for the enumeration {@link ListItemType}.
     */
    ListItemType getListItemType();
}
