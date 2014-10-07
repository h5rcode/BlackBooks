package com.blackbooks.adapters;

public class AuthorItem implements ListItem {
	
	private Long id;
	private String name;
	private int totalBooks;

	public AuthorItem(Long id, String name, int totalBooks) {
		this.id = id;
		this.name = name;
		this.totalBooks = totalBooks;
	}

	@Override
	public ListItemType getListItemType() {
		return ListItemType.Header;
	}
	
	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	
	public int getTotalBooks() {
		return totalBooks;
	}
}
