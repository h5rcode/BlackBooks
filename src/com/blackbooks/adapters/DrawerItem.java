package com.blackbooks.adapters;

/**
 * A drawer item.
 */
public final class DrawerItem {

	private final int mId;
	private final String mTitle;
	private final Integer mIconId;
	private final DrawerItemType mDrawerItemType;

	/**
	 * Constructor.
	 * 
	 * @param id
	 *            Id of the drawer item.
	 * @param title
	 *            Title.
	 * @param iconId
	 *            Id of the drawable resource representing the icon of the item.
	 * @param drawerItemType
	 *            Type of item.
	 */
	public DrawerItem(int id, String title, Integer iconId, DrawerItemType drawerItemType) {
		this.mId = id;
		this.mTitle = title;
		this.mIconId = iconId;
		this.mDrawerItemType = drawerItemType;
	}

	/**
	 * Return the id of the item.
	 * 
	 * @return Id.
	 */
	public int getId() {
		return mId;
	}

	/**
	 * Return the tile of the item.
	 * 
	 * @return Title.
	 */
	public String getTitle() {
		return mTitle;
	}

	/**
	 * Return the id of the draweble resource corresponding to the icon of the
	 * item.
	 * 
	 * @return Id of the drawable.
	 */
	public Integer getIconId() {
		return mIconId;
	}

	/**
	 * Return the type of item.
	 * 
	 * @return DrawerItemType.
	 */
	public DrawerItemType getDrawerItemType() {
		return mDrawerItemType;
	}
}
