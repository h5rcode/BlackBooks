package com.blackbooks.search.openlibrary;

import java.util.ArrayList;

public class OpenLibraryBook {

	public String title;
	public String subtitle;

	public ArrayList<String> authors;
	public ArrayList<String> publishers;
	public String publishDate;
	public String coverLinkSmall;
	public String coverLinkMedium;
	public String coverLinkLarge;
	public Long numberOfPages;
	
	public OpenLibraryBook() {
		this.authors = new ArrayList<String>();
		this.publishers = new ArrayList<String>();
	}
}
