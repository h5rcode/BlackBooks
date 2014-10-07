package com.blackbooks.sql;

import java.lang.reflect.Field;
import java.util.ArrayList;

import com.blackbooks.model.metadata.Column;
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
	 * Builds the SQLite script to create a table declaration.
	 * 
	 * @param table
	 *            Table.
	 * @param columns
	 *            Columns.
	 * @return The SQL statement to create the table and its columns.
	 */
	public static String buildSqlCreateTable(Table table, ArrayList<Column> columns) {
		StringBuffer sb = new StringBuffer();
		sb.append("CREATE TABLE IF NOT EXISTS");
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
