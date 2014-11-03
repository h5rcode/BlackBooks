package com.blackbooks.adapters;

import java.util.ArrayList;
import java.util.List;

public class BookItem implements ListItem {

	private long id;

	private String title;

	private byte[] smallThumbnail;

	private List<String> authors;

	public BookItem(long id, String title, byte[] smallThumbnail) {
		this.id = id;
		this.title = title;
		this.smallThumbnail = smallThumbnail;
	}

	@Override
	public ListItemType getListItemType() {
		return ListItemType.Entry;
	}

	public long getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public byte[] getSmallThumbnail() {
		return smallThumbnail;
	}
	
	public List<String> getAuthors() {
		if (authors == null)  {
			authors = new ArrayList<String>();
		}
		return authors;
	}
}
