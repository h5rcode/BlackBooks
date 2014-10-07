package com.blackbooks.model.persistent;

import java.io.Serializable;

import com.blackbooks.model.metadata.Column;
import com.blackbooks.model.metadata.Column.SQLiteDataType;
import com.blackbooks.model.metadata.Table;

@Table(name = "IDENTIFIER", version = 1)
public class Identifier implements Serializable {

	private static final long serialVersionUID = 6710502395896044782L;

	@Column(name = Cols.IDE_ID, primaryKey = true, type = SQLiteDataType.INTEGER, version = 1)
	public Long id;

	@Column(name = Cols.IDE_IDENTIFIER, mandatory = true, type = SQLiteDataType.TEXT, unique = true, version = 1)
	public String identifier;

	@Column(name = Cols.BOO_ID, mandatory = true, type = SQLiteDataType.INTEGER, referencedType = Book.class, onDeleteCascade = true, version = 1)
	public Long bookId;

	public final static class Cols {
		public final static String IDE_ID = "IDE_ID";
		public final static String IDE_IDENTIFIER = "IDE_IDENTIFIER";
		public final static String BOO_ID = "BOO_ID";
	}
}
