package com.blackbooks.model.persistent;

import java.io.Serializable;

import com.blackbooks.model.metadata.Column;
import com.blackbooks.model.metadata.Column.SQLiteDataType;
import com.blackbooks.model.metadata.Table;

@Table(name = BookLocation.NAME, version = 1)
public class BookLocation implements Serializable {
	
	public static final String NAME = "BOOK_LOCATION";

	private static final long serialVersionUID = -7452261290218427907L;

	@Column(name = Cols.BKL_ID, primaryKey = true, type = SQLiteDataType.INTEGER, version = 1)
	public Long id;

	@Column(name = Cols.BKL_NAME, type = SQLiteDataType.TEXT, mandatory = true, version = 1)
	public String name;

	public class Cols {
		public static final String BKL_ID = "BKL_ID";
		public static final String BKL_NAME = "BKL_NAME";
	}
}
