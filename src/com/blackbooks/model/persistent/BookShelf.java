package com.blackbooks.model.persistent;

import java.io.Serializable;

import com.blackbooks.model.metadata.Column;
import com.blackbooks.model.metadata.Column.SQLiteDataType;
import com.blackbooks.model.metadata.Table;

@Table(name = BookShelf.NAME, version = 1)
public class BookShelf implements Serializable {
	
	public static final String NAME = "BOOK_SHELF";

	private static final long serialVersionUID = -7452261290218427907L;

	@Column(name = Cols.BSH_ID, primaryKey = true, type = SQLiteDataType.INTEGER, version = 1)
	public Long id;

	@Column(name = Cols.BSH_NAME, type = SQLiteDataType.TEXT, mandatory = true, version = 1)
	public String name;

	public class Cols {
		public static final String BSH_ID = "BSH_ID";
		public static final String BSH_NAME = "BSH_NAME";
	}
}
