package com.blackbooks.search.google;

import java.util.ArrayList;


/**
 * A class used to store the info of a book returned by the Google Books API.
 */
public class GoogleBook {
	public Long bookId;
	public String title;
	public String subtitle;
	public ArrayList<String> authors;
	public String publisher;
	public String publishedDate;
	public String description;
	public ArrayList<GoogleIndustryIdentifier> industryIdentifiers;
	public Long pageCount;
	public String height;
	public String width;
	public String thickness;
	public String printType;
	public String mainCategory;
	public ArrayList<String> categories;
	public String smallThumbnailLink;
	public String thumbnailLink;
	public byte[] smallThumbnail;
	public byte[] thumbnail;
	public String language;

	/**
	 * Default constructor.
	 */
	public GoogleBook() {
		this.authors = new ArrayList<String>();
		this.industryIdentifiers = new ArrayList<GoogleIndustryIdentifier>();
		this.categories = new ArrayList<String>();
	}
}
