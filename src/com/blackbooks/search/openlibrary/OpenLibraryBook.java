package com.blackbooks.search.openlibrary;

import java.util.ArrayList;

public class OpenLibraryBook {

	public String title;
	public String subtitle;

	public ArrayList<String> authors;
	public ArrayList<String> publishers;
	public String isbn10;
	public String isbn13;
	public ArrayList<String> subjects;
	public String publishDate;
	public String coverLinkSmall;
	public String coverLinkMedium;
	public String coverLinkLarge;
	public Long numberOfPages;
	public byte[] coverSmall;
	public byte[] coverMedium;
	
	public OpenLibraryBook() {
		this.authors = new ArrayList<String>();
		this.publishers = new ArrayList<String>();
		this.subjects = new ArrayList<String>();
	}
}
