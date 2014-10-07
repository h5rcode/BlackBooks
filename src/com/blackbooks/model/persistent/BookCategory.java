package com.blackbooks.model.persistent;

import com.blackbooks.model.metadata.Column;
import com.blackbooks.model.metadata.Column.SQLiteDataType;
import com.blackbooks.model.metadata.Table;

@Table(name = "BOOK_CATEGORY", version = 1)
public class BookCategory {

	@Column(name = Cols.BCA_ID, primaryKey = true, type = SQLiteDataType.INTEGER, version = 1)
	public Long id;

	@Column(name = Cols.BOO_ID, type = SQLiteDataType.INTEGER, mandatory = true, referencedType = Book.class, onDeleteCascade = true, version = 1)
	public Long bookId;

	@Column(name = Cols.CAT_ID, type = SQLiteDataType.INTEGER, mandatory = true, referencedType = Category.class, onDeleteCascade = true, version = 1)
	public Long categoryId;

	public final static class Cols {
		public final static String BCA_ID = "BCA_ID";
		public final static String BOO_ID = "BOO_ID";
		public final static String CAT_ID = "CAT_ID";
	}
}
