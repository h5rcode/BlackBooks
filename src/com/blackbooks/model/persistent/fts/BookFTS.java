package com.blackbooks.model.persistent.fts;

import com.blackbooks.model.metadata.FTSColumn;
import com.blackbooks.model.metadata.FTSTable;
import com.blackbooks.model.metadata.FTSTable.FTSModules;
import com.blackbooks.model.persistent.Book;
import com.blackbooks.utils.StringUtils;

@FTSTable(name = BookFTS.NAME, ftsModuleVersion = FTSModules.FTS4, version = 1)
public class BookFTS {

	public static final String NAME = "BOOK_FTS";

	@FTSColumn(name = Cols.DOCID, primaryKey = true, version = 1)
	public final Long docid;

	@FTSColumn(name = Cols.TITLE, version = 1)
	public final String title;

	@FTSColumn(name = Cols.SUBTITLE, version = 1)
	public final String subtitle;

	@FTSColumn(name = Cols.DESCRIPTION, version = 1)
	public final String description;

	public BookFTS(Book book) {
		this.docid = book.id;
		this.title = StringUtils.normalize(book.title);
		this.subtitle = StringUtils.normalize(book.subtitle);
		this.description = StringUtils.normalize(book.description);
	}

	public class Cols {
		public static final String DOCID = "DOCID";
		public static final String TITLE = "TITLE";
		public static final String SUBTITLE = "SUBTITLE";
		public static final String DESCRIPTION = "DESCRIPTION";
	}
}
