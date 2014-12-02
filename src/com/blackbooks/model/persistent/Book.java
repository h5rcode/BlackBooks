package com.blackbooks.model.persistent;

import java.io.Serializable;

import com.blackbooks.model.metadata.Column;
import com.blackbooks.model.metadata.Column.SQLiteDataType;
import com.blackbooks.model.metadata.Table;

@Table(name = Book.NAME, version = 1)
public class Book implements Serializable {

	public static final String NAME = "BOOK";

	private static final long serialVersionUID = 7409388866384981075L;

	@Column(name = Cols.BOO_ID, primaryKey = true, type = SQLiteDataType.INTEGER, version = 1)
	public Long id;

	@Column(name = Cols.BOO_TITLE, type = SQLiteDataType.TEXT, mandatory = true, version = 1)
	public String title;

	@Column(name = Cols.BOO_SUBTITLE, type = SQLiteDataType.TEXT, version = 1)
	public String subtitle;

	@Column(name = Cols.BOO_LANGUAGE_CODE, type = SQLiteDataType.TEXT, version = 1)
	public String languageCode;

	@Column(name = Cols.PUB_ID, type = SQLiteDataType.INTEGER, referencedType = Publisher.class, version = 1)
	public Long publisherId;

	@Column(name = Cols.BOO_PUBLISHED_DATE, type = SQLiteDataType.TEXT, version = 1)
	public String publishedDate;

	@Column(name = Cols.BOO_DESCRIPTION, type = SQLiteDataType.TEXT, version = 1)
	public String description;

	@Column(name = Cols.BOO_PAGE_COUNT, type = SQLiteDataType.INTEGER, version = 1)
	public Long pageCount;

	@Column(name = Cols.BOO_HEIGHT, type = SQLiteDataType.TEXT, version = 1)
	public String height;

	@Column(name = Cols.BOO_WIDTH, type = SQLiteDataType.TEXT, version = 1)
	public String width;

	@Column(name = Cols.BOO_THICKNESS, type = SQLiteDataType.TEXT, version = 1)
	public String thickness;

	@Column(name = Cols.BOO_PRINT_TYPE, type = SQLiteDataType.TEXT, version = 1)
	public String printType;

	@Column(name = Cols.BOO_MAIN_CATEGORY, type = SQLiteDataType.TEXT, version = 1)
	public String mainCategory;

	@Column(name = Cols.BOO_SMALL_THUMBNAIL, type = SQLiteDataType.BLOB, version = 1)
	public byte[] smallThumbnail;

	@Column(name = Cols.BOO_THUMBNAIL, type = SQLiteDataType.BLOB, version = 1)
	public byte[] thumbnail;

	@Column(name = Cols.BOO_IS_READ, type = SQLiteDataType.INTEGER, mandatory = true, version = 1)
	public Long isRead;

	@Column(name = Cols.BOO_IS_FAVOURITE, type = SQLiteDataType.INTEGER, mandatory = true, version = 1)
	public Long isFavourite;

	@Column(name = Cols.BOO_ISBN_10, type = SQLiteDataType.TEXT, version = 1)
	public String isbn10;

	@Column(name = Cols.BOO_ISBN_13, type = SQLiteDataType.TEXT, version = 1)
	public String isbn13;

	@Column(name = Cols.BSH_ID, type = SQLiteDataType.INTEGER, referencedType = BookShelf.class, version = 1)
	public Long bookShelfId;

	public Book() {
		this.isRead = 0L;
		this.isFavourite = 0L;
	}

	public final static class Cols {
		public final static String BOO_ID = "BOO_ID";
		public final static String BOO_TITLE = "BOO_TITLE";
		public final static String BOO_SUBTITLE = "BOO_SUBTITLE";
		public final static String BOO_LANGUAGE_CODE = "BOO_LANGUAGE_CODE";
		public final static String PUB_ID = "PUB_ID";
		public final static String BOO_PUBLISHED_DATE = "BOO_PUBLISHED_DATE";
		public final static String BOO_DESCRIPTION = "BOO_DESCRIPTION";
		public final static String BOO_PAGE_COUNT = "BOO_PAGE_COUNT";
		public final static String BOO_HEIGHT = "BOO_HEIGHT";
		public final static String BOO_WIDTH = "BOO_WIDTH";
		public final static String BOO_THICKNESS = "BOO_THICKNESS";
		public final static String BOO_PRINT_TYPE = "BOO_PRINT_TYPE";
		public final static String BOO_MAIN_CATEGORY = "BOO_MAIN_CATEGORY";
		public final static String BOO_SMALL_THUMBNAIL = "BOO_SMALL_THUMBNAIL";
		public final static String BOO_THUMBNAIL = "BOO_THUMBNAIL";
		public final static String BOO_IS_READ = "BOO_IS_READ";
		public final static String BOO_IS_FAVOURITE = "BOO_IS_FAVOURITE";
		public final static String BOO_ISBN_10 = "BOO_ISBN_10";
		public final static String BOO_ISBN_13 = "BOO_ISBN_13";
		public static final String BSH_ID = "BSH_ID";
	}
}
