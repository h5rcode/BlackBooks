package com.blackbooks.sql;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.blackbooks.model.metadata.Column;
import com.blackbooks.model.metadata.FTSColumn;
import com.blackbooks.model.metadata.FTSTable;
import com.blackbooks.model.metadata.Table;

/**
 * Helper class to build SQL queries.
 */
final class SqlBuilder {

	/**
	 * Private constructor.
	 */
	private SqlBuilder() {
	}

	/**
	 * Builds the SQLite script to create a table.
	 * 
	 * @param table
	 *            Table.
	 * @param columns
	 *            Columns.
	 * @return The SQL statement to create the table and its columns.
	 */
	public static String buildSqlCreateTable(Table table, List<Column> columns) {
		StringBuffer sb = new StringBuffer();
		sb.append("CREATE TABLE");
		sb.append(' ');
		sb.append(table.name());
		sb.append(' ');
		sb.append(" (");

		for (int i = 0; i < columns.size(); i++) {
			sb.append('\n');
			Column column = columns.get(i);
			sb.append('\t');
			sb.append(buildSqlCreateColumn(column));
			if (i < columns.size() - 1) {
				sb.append(',');
			}
		}

		for (Column column : columns) {
			String foreignKey = buildSqlCreateForeignKey(column);
			if (!foreignKey.equals("")) {
				sb.append(',');
				sb.append('\n');
				sb.append('\t');
				sb.append(foreignKey);
			}
		}

		sb.append('\n');
		sb.append(')');
		sb.append(';');

		return sb.toString();
	}

	/**
	 * Builds the SQLite script to create a FTS table.
	 * 
	 * @param table
	 *            Table.
	 * @param columns
	 *            Columns.
	 * @return The SQL statement to create the FTS table and its columns.
	 */
	public static String buildSqlCreateFTSTable(FTSTable table, List<FTSColumn> columns) {
		StringBuffer sb = new StringBuffer();
		sb.append("CREATE VIRTUAL TABLE");
		sb.append(' ');
		sb.append(table.name());
		sb.append(' ');
		sb.append("USING");
		sb.append(' ');
		sb.append(table.ftsModuleVersion().name());
		sb.append(" (");

		List<FTSColumn> filteredColumns = new ArrayList<FTSColumn>();

		for (FTSColumn ftsColumn : columns) {
			if (!ftsColumn.primaryKey()) {
				filteredColumns.add(ftsColumn);
			}
		}

		int size = filteredColumns.size();
		for (int i = 0; i < size; i++) {
			sb.append('\n');
			FTSColumn column = filteredColumns.get(i);
			sb.append('\t');
			sb.append(column.name());
			if (i < size - 1) {
				sb.append(',');
			}
		}

		sb.append('\n');
		sb.append(')');
		sb.append(';');

		return sb.toString();
	}

	/**
	 * Returns the SQLite code to create a column.
	 * 
	 * @param column
	 *            Column.
	 * @return SQLite column declaration.
	 */
	private static String buildSqlCreateColumn(Column column) {
		StringBuffer sb = new StringBuffer();

		sb.append(column.name());
		sb.append(' ');

		if (column.primaryKey()) {
			sb.append("INTEGER PRIMARY KEY AUTOINCREMENT");
		} else {
			sb.append(column.type().name());
			if (column.mandatory()) {
				sb.append(' ');
				sb.append("NOT NULL");
			}
			if (column.unique()) {
				sb.append(' ');
				sb.append("UNIQUE");
			}
		}
		return sb.toString();
	}

	/**
	 * Builds the SQLite script to create foreign keys.
	 * 
	 * @param column
	 *            Column.
	 */
	private static String buildSqlCreateForeignKey(Column column) {
		StringBuffer sb = new StringBuffer();
		Class<?> referencedType = column.referencedType();
		if (referencedType != void.class) {
			Table referencedTable = referencedType.getAnnotation(Table.class);

			if (referencedTable == null) {
				throw new IllegalArgumentException("The referenced type must have a Table annotation.");
			}

			Column referencedTablePrimaryKey = null;
			for (Field field : referencedType.getFields()) {
				Column fieldColumn = field.getAnnotation(Column.class);
				if (fieldColumn != null && fieldColumn.primaryKey()) {
					referencedTablePrimaryKey = fieldColumn;
				}
			}

			sb.append("FOREIGN KEY (");
			sb.append(column.name());
			sb.append(')');
			sb.append(' ');
			sb.append("REFERENCES");
			sb.append(' ');
			sb.append(referencedTable.name());
			sb.append('(');
			sb.append(referencedTablePrimaryKey.name());
			sb.append(')');
			if (column.onDeleteCascade()) {
				sb.append(' ');
				sb.append("ON DELETE CASCADE");
			}
		}
		return sb.toString();
	}
}
