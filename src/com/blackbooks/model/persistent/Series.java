package com.blackbooks.model.persistent;

import java.io.Serializable;

import com.blackbooks.model.metadata.Column;
import com.blackbooks.model.metadata.Column.SQLiteDataType;
import com.blackbooks.model.metadata.Table;

@Table(name = Series.NAME, version = 1)
public class Series implements Serializable {

	public static final String NAME = "SERIES";

	private static final long serialVersionUID = 1101490965585938183L;

	@Column(name = Cols.SER_ID, primaryKey = true, type = SQLiteDataType.INTEGER, version = 1)
	public Long id;

	@Column(name = Cols.SER_NAME, type = SQLiteDataType.TEXT, mandatory = true, version = 1)
	public String name;

	public class Cols {
		public static final String SER_ID = "SER_ID";
		public static final String SER_NAME = "SER_NAME";
	}
}
