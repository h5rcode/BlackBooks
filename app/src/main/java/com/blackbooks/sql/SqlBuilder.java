package com.blackbooks.sql;

import com.blackbooks.model.metadata.Column;
import com.blackbooks.model.metadata.FTSColumn;
import com.blackbooks.model.metadata.FTSTable;
import com.blackbooks.model.metadata.Table;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

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
     * @param table   Table.
     * @param columns Columns.
     * @return The SQL statement to create the table and its columns.
     */
    public static String buildSqlCreateTable(Table table, List<Column> columns) {
        StringBuilder sb = new StringBuilder();
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
     * @param table   Table.
     * @param columns Columns.
     * @return The SQL statement to create the FTS table and its columns.
     */
    public static String buildSqlCreateFTSTable(FTSTable table, List<FTSColumn> columns) {
        StringBuilder sb = new StringBuilder();
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
     * Build the indexes declaration script.
     *
     * @param table   Table.
     * @param columns Columns.
     * @return Indexes declarations scripts.
     */
    public static List<String> buildSqlCreateIndexes(Table table, List<Column> columns) {
        final List<String> indexesDeclarations = new ArrayList<String>();

        for (final Column column : columns) {
            final String indexDeclarationScript = buildSqlCreateIndex(table, column);
            if (!indexDeclarationScript.equals("")) {
                indexesDeclarations.add(indexDeclarationScript);
            }
        }
        return indexesDeclarations;
    }

    /**
     * Returns the SQLite code to create a column.
     *
     * @param column Column.
     * @return SQLite column declaration.
     */
    private static String buildSqlCreateColumn(Column column) {
        StringBuilder sb = new StringBuilder();

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
     * @param column Column.
     * @return Foreign key creation script.
     */
    private static String buildSqlCreateForeignKey(Column column) {
        StringBuilder sb = new StringBuilder();
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

    /**
     * Builds the SQLite script to create an index on a column.
     *
     * @param table  The table.
     * @param column The column to be indexed.
     * @return SQL index declaration.
     */
    private static String buildSqlCreateIndex(Table table, Column column) {
        StringBuilder sb = new StringBuilder();
        Class<?> referencedType = column.referencedType();
        if (referencedType != void.class) {
            Table referencedTable = referencedType.getAnnotation(Table.class);

            if (referencedTable == null) {
                throw new IllegalArgumentException("The referenced type must have a Table annotation.");
            }

            sb.append("CREATE INDEX");
            sb.append(' ');
            sb.append(table.name()).append('_').append(column.name());
            sb.append(' ');
            sb.append("ON");
            sb.append(' ');
            sb.append(table.name());
            sb.append(' ');
            sb.append('(');
            sb.append(column.name());
            sb.append(')');
            sb.append(';');
        }

        return sb.toString();
    }
}
