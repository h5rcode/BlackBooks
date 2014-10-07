package com.blackbooks.adapters;

public class BookItem implements ListItem {

	private long id;

	private String text;

	private byte[] smallThumbnail;

	public BookItem(long id, String text, byte[] smallThumbnail) {
		this.id = id;
		this.text = text;
		this.smallThumbnail = smallThumbnail;
	}

	@Override
	public ListItemType getListItemType() {
		return ListItemType.Entry;
	}

	public long getId() {
		return id;
	}

	public String getText() {
		return text;
	}

	public byte[] getSmallThumbnail() {
		return smallThumbnail;
	}
}
