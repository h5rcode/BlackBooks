package com.blackbooks.model.persistent;

import java.io.Serializable;

import com.blackbooks.model.metadata.Column;
import com.blackbooks.model.metadata.Column.SQLiteDataType;
import com.blackbooks.model.metadata.Table;

@Table(name = "AUTHOR", version = 1)
public class Author implements Serializable {

	private static final long serialVersionUID = -654434686257571358L;

	@Column(name = Cols.AUT_ID, primaryKey = true, type = SQLiteDataType.INTEGER, version = 1)
	public Long id;

	@Column(name = Cols.AUT_NAME, mandatory = true, type = SQLiteDataType.TEXT, version = 1)
	public String name;

	public final static class Cols {
		public final static String AUT_ID = "AUT_ID";
		public final static String AUT_NAME = "AUT_NAME";
	}
}
