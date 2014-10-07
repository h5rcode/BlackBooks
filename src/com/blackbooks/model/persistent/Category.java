package com.blackbooks.model.persistent;

import java.io.Serializable;

import com.blackbooks.model.metadata.Column;
import com.blackbooks.model.metadata.Column.SQLiteDataType;
import com.blackbooks.model.metadata.Table;

@Table(name = "CATEGORY", version = 1)
public class Category implements Serializable {

	private static final long serialVersionUID = -1958430806785527505L;

	@Column(name = Cols.CAT_ID, primaryKey = true, type = SQLiteDataType.INTEGER, version = 1)
	public Long id;

	@Column(name = Cols.CAT_NAME, type = SQLiteDataType.TEXT, mandatory = true, version = 1)
	public String name;

	public final static class Cols {
		public final static String CAT_ID = "CAT_ID";
		public final static String CAT_NAME = "CAT_NAME";
	}
}
