package com.blackbooks.sql;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.blackbooks.model.metadata.Column;
import com.blackbooks.model.metadata.Table;
import com.blackbooks.utils.ReflectionUtils;
import com.blackbooks.utils.StringUtils;

import java.lang.reflect.Field;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class allows to perform all basic operations on a SQLite table.
 *
 * @param <T> Type of the persistent object to manipulate with the broker.
 */
public class Broker<T> {

    private static final int MAX_SQL_PARAMETERS = 250;

    private Class<T> mType;
    private Table mTable;
    private Field mPrimaryKeyField;
    private Column mPrimaryKeyColumn;
    private List<Column> mColumns;
    private HashMap<Field, Column> mColumnMap;

    private String mSqlCreateTable;

    /**
     * Constructor.
     *
     * @param type The type representing the table to access through the broker.
     */
    public Broker(Class<T> type) {
        mType = type;
        mTable = type.getAnnotation(Table.class);

        if (mTable == null) {
            throw new InvalidParameterException("The parameter does not have a Table annotation.");
        }

        Field[] fields = type.getFields();

        mColumns = new ArrayList<Column>();
        mColumnMap = new HashMap<Field, Column>();
        for (Field field : fields) {
            Column column = field.getAnnotation(Column.class);
            if (column != null) {
                mColumns.add(column);
                mColumnMap.put(field, column);

                if (column.primaryKey()) {
                    if (mPrimaryKeyColumn != null) {
                        throw new IllegalArgumentException("Table " + mTable.name() + " already as a primary key.");
                    }
                    mPrimaryKeyField = field;
                    mPrimaryKeyColumn = column;
                }
            }
        }

        if (mPrimaryKeyColumn == null) {
            throw new IllegalArgumentException("Table " + mTable.name() + " must have a primary key.");
        }

        mSqlCreateTable = SqlBuilder.buildSqlCreateTable(mTable, mColumns);
    }

    /**
     * Creates the table in a database.
     *
     * @param db SQLiteDatabase.
     */
    public void createTable(SQLiteDatabase db) {
        db.execSQL(mSqlCreateTable);
    }

    /**
     * Delete a row from the table.
     *
     * @param db SQLiteDatabase.
     * @param id Id of the row to delete.
     */
    public void delete(SQLiteDatabase db, long id) {
        String whereClause = mPrimaryKeyColumn.name() + " = ?";
        String whereArgs[] = new String[]{String.valueOf(id)};
        db.delete(mTable.name(), whereClause, whereArgs);
    }

    /**
     * Delete rows that match criteria.
     * <p/>
     * The criteria are build using the non null attributes of paremeter
     * "criteria".
     *
     * @param db       SQLiteDatabase.
     * @param criteria Instance of T used to build the criteria.
     */
    public void deleteAllByCriteria(SQLiteDatabase db, T criteria) {
        Filter filter = buildFilter(criteria);

        String whereClause = filter.condition;
        String[] whereArgs = filter.values;

        db.delete(mTable.name(), whereClause, whereArgs);
    }

    /**
     * Gets a row from the table using its id.
     *
     * @param db SQLiteDatabase.
     * @param id Row primary key.
     * @return Object representing the selected row.
     */
    public T get(SQLiteDatabase db, long id) {
        String selection = mPrimaryKeyColumn.name() + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(id)};
        Cursor cursor = query(db, null, selection, selectionArgs, null);
        T bean = null;
        if (cursor.moveToFirst()) {
            bean = cursorToBean(cursor);
        }
        cursor.close();
        return bean;
    }

    /**
     * Gets all the rows of the table.
     *
     * @param db SQLiteDatabase
     * @return List of T.
     */
    public List<T> getAll(SQLiteDatabase db) {
        return getAll(db, null, null);
    }

    /**
     * Gets all the rows of the table.
     *
     * @param db              SQLiteDatabase
     * @param selectedColumns The columns to select. Pass null to select all the columns of
     *                        the table.
     * @param sortingColumns  The name of the columns used to sort the results.
     * @return List of T.
     */
    public List<T> getAll(SQLiteDatabase db, String[] selectedColumns, String[] sortingColumns) {
        String orderBy = StringUtils.join(sortingColumns, ",");
        Cursor cursor = query(db, selectedColumns, null, null, orderBy);
        return cursorToBeanList(cursor);
    }

    /**
     * Get all the rows corresponding to criteria represented by the instance of
     * T.
     * <p/>
     * All the non null fields of criteria are used to select data from the
     * table.
     *
     * @param db       SQLiteDatabase.
     * @param criteria T.
     * @return List of rows corresponding to the criteria.
     */
    public List<T> getAllByCriteria(SQLiteDatabase db, T criteria) {
        Filter filter = buildFilter(criteria);

        String selection = filter.condition;
        String[] selectionArgs = filter.values;

        Cursor cursor = query(db, null, selection, selectionArgs, null);
        return cursorToBeanList(cursor);
    }

    /**
     * Return the rows where a given column matches one of the given values..
     *
     * @param db     SQLiteDatabase.
     * @param column The column used to filter the rows.
     * @param values List of values.
     * @return List of rows corresponding to the values.
     */
    public List<T> getAllWhereIn(SQLiteDatabase db, String column, List<?> values) {

        int valueCount = values.size();

        List<T> results = new ArrayList<T>();
        int startIndex = 0;
        int endIndex;
        if (valueCount <= MAX_SQL_PARAMETERS) {
            endIndex = valueCount;
        } else {
            endIndex = MAX_SQL_PARAMETERS;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM");
        sb.append(' ');
        sb.append(mTable.name());
        sb.append(' ');
        sb.append("WHERE");
        sb.append(' ');
        sb.append(column);
        sb.append(' ');
        sb.append("IN (%s);");
        String sqlFormat = sb.toString();

        boolean goOn = true;
        while (goOn) {
            List<?> valuesSubList = values.subList(startIndex, endIndex);
            if (valuesSubList.isEmpty()) {
                goOn = false;
            } else {
                StringBuilder sbSelectionArgs = new StringBuilder();
                String selectionArgs[] = new String[valuesSubList.size()];
                for (int i = 0; i < valuesSubList.size(); i++) {
                    sbSelectionArgs.append('?');
                    if (i < valuesSubList.size() - 1) {
                        sbSelectionArgs.append(", ");
                    }

                    selectionArgs[i] = valuesSubList.get(i).toString();
                }
                String sql = String.format(sqlFormat, sbSelectionArgs.toString());

                results.addAll(rawSelect(db, sql, selectionArgs));

                startIndex = endIndex;
                endIndex = endIndex + MAX_SQL_PARAMETERS;
                if (endIndex > valueCount) {
                    endIndex = valueCount;
                }
            }
        }

        return results;
    }

    /**
     * Get the one row matching a criteria. If no rows or more that one rows
     * match the criteria, the method returs null.
     *
     * @param db       SQLiteDatabase.
     * @param criteria T.
     * @return An instance of T.
     */
    public T getByCriteria(SQLiteDatabase db, T criteria) {
        List<T> list = getAllByCriteria(db, criteria);
        T result = null;
        if (list.size() == 1) {
            result = list.get(0);
        }
        return result;
    }

    /**
     * Executes an SQL statement an return the result as a list of T.
     *
     * @param db            SQLiteDatabase.
     * @param sql           The SQL statement to execute.
     * @param selectionArgs You may include ?s in where clause in the query, which will be
     *                      replaced by the values from selectionArgs. The values will be
     *                      bound as Strings.
     * @return List of T.
     */
    public List<T> rawSelect(SQLiteDatabase db, String sql, String[] selectionArgs) {
        Cursor cursor = db.rawQuery(sql, selectionArgs);
        return cursorToBeanList(cursor);
    }

    /**
     * Save a bean in the database. If the primary key of the bean is null, a
     * corresponding row is inserted. If the primary key of the bean is NOT
     * null, the corresponding row is updated.
     *
     * @param db   SQLiteDatabase.
     * @param bean T.
     * @return Id of the inserted or updated row.
     */
    public long save(SQLiteDatabase db, T bean) {
        Long id = (Long) ReflectionUtils.getFieldValue(mPrimaryKeyField, bean);

        if (id == null) {
            id = insert(db, bean);
            ReflectionUtils.setFieldValue(mPrimaryKeyField, bean, id);
        } else {
            update(db, bean);
        }

        return id;
    }

    /**
     * Creates a new instance of ContentValues and fills it with the content of
     * a bean.
     *
     * @param bean Bean.
     * @return ContentValues.
     */
    private ContentValues buildContentValues(T bean) {
        if (!mType.isAssignableFrom(bean.getClass())) {
            throw new IllegalArgumentException("Type mismatch.");
        }

        ContentValues values = new ContentValues();
        for (Field field : mColumnMap.keySet()) {
            if (field == mPrimaryKeyField) {
                continue;
            }
            Column column = mColumnMap.get(field);

            String columnName = column.name();
            switch (column.type()) {
                case BLOB:
                    byte[] byteArrayValue = (byte[]) ReflectionUtils.getFieldValue(field, bean);
                    values.put(columnName, byteArrayValue);
                    break;

                case INTEGER:
                    Long intValue = (Long) ReflectionUtils.getFieldValue(field, bean);
                    values.put(columnName, intValue);
                    break;

                case TEXT:
                    String stringValue = (String) ReflectionUtils.getFieldValue(field, bean);
                    if (stringValue != null) {
                        stringValue = stringValue.trim();
                    }
                    values.put(columnName, stringValue);
                    break;

                default:
                    break;
            }
        }
        return values;
    }

    /**
     * Build an instance of Filter from the non values of the attributes of an
     * instance of class T.
     *
     * @param criteria Instance of T.
     * @return Filter.
     */
    private Filter buildFilter(T criteria) {
        List<String> columns = new ArrayList<String>();
        List<String> filedValues = new ArrayList<String>();

        for (Field field : mColumnMap.keySet()) {
            Object fieldValue;
            fieldValue = ReflectionUtils.getFieldValue(field, criteria);
            if (fieldValue != null) {
                Column column = mColumnMap.get(field);

                columns.add(column.name());
                filedValues.add(fieldValue.toString());
            }
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < columns.size(); i++) {
            String column = columns.get(i);
            if (i > 0) {
                sb.append("AND");
                sb.append(' ');
            }

            sb.append(column);
            sb.append(" = ?");
            sb.append("\n");
        }

        String condition = sb.toString();
        String[] values = filedValues.toArray(new String[filedValues.size()]);

        Filter filter = new Filter();
        filter.condition = condition;
        filter.values = values;
        return filter;
    }

    /**
     * Creates a new instance of a bean and fill it using the cursor's current
     * result.
     *
     * @param cursor Cursor.
     * @return New instance of T filled with the content of the cursor's current
     * result.
     */
    private T cursorToBean(Cursor cursor) {
        T bean = ReflectionUtils.getNewInstance(mType);

        for (Field field : mColumnMap.keySet()) {
            Column column = mColumnMap.get(field);
            int index = cursor.getColumnIndex(column.name());

            if (index != -1 && !cursor.isNull(index)) {
                switch (column.type()) {
                    case BLOB:
                        byte[] byteArrayValue = cursor.getBlob(index);
                        ReflectionUtils.setFieldValue(field, bean, byteArrayValue);
                        break;

                    case INTEGER:
                        Long intValue = cursor.getLong(index);
                        ReflectionUtils.setFieldValue(field, bean, intValue);
                        break;

                    case TEXT:
                        String stringValue = cursor.getString(index);
                        ReflectionUtils.setFieldValue(field, bean, stringValue);
                        break;

                    default:
                        break;
                }
            }
        }
        return bean;
    }

    /**
     * Create an instance of T for each result in the given cursor.
     *
     * @param cursor Cursor.
     * @return List of T.
     */
    private List<T> cursorToBeanList(Cursor cursor) {
        List<T> result = new ArrayList<T>();
        while (cursor.moveToNext()) {
            T bean = cursorToBean(cursor);
            result.add(bean);
        }
        return result;
    }

    /**
     * Inserts a bean in the table.
     *
     * @param db   SQLiteDatabase.
     * @param bean Bean to insert.
     * @return Id of the inserted row.
     */
    private long insert(SQLiteDatabase db, T bean) {
        ContentValues values = buildContentValues(bean);
        return db.insertOrThrow(mTable.name(), null, values);
    }

    /**
     * Query the table.
     *
     * @param db            SQLiteDatabase.
     * @param columns       The selected columns. Pass null to select all the columns of
     *                      the table.
     * @param selection     A filter declaring which rows to return, formatted as an SQL
     *                      WHERE clause (excluding the WHERE itself). Passing null will
     *                      return all rows for the given table.
     * @param selectionArgs You may include ?s in selection, which will be replaced by the
     *                      values from selectionArgs, in order that they appear in the
     *                      selection. The values will be bound as Strings.
     * @param orderBy       How to order the rows, formatted as an SQL ORDER BY clause
     *                      (excluding the ORDER BY itself). Passing null will use the
     *                      default sort order, which may be unordered.
     * @return Cursor.
     */
    private Cursor query(SQLiteDatabase db, String[] columns, String selection, String[] selectionArgs, String orderBy) {
        return db.query(mTable.name(), columns, selection, selectionArgs, null, null, orderBy);
    }

    /**
     * Update the row corresponding to a bean.
     *
     * @param db   SQLiteDatabase.
     * @param bean Bean.
     */
    private void update(SQLiteDatabase db, T bean) {
        ContentValues values = buildContentValues(bean);
        String whereClause = mPrimaryKeyColumn.name() + " = ?";
        Object primaryKey = ReflectionUtils.getFieldValue(mPrimaryKeyField, bean);
        String[] whereArgs = new String[]{primaryKey.toString()};
        db.updateWithOnConflict(mTable.name(), values, whereClause, whereArgs, SQLiteDatabase.CONFLICT_ROLLBACK);
    }

    /**
     * A filter consists in:<br />
     * <br />
     * A condition (a WHERE clause).<br />
     * Example : "COLUMN_1 = ? AND COLUMN_2 = ? OR COLUMN_3 = ?"<br />
     * <br />
     * Values to be used in the condition.<br />
     * Example : ["TEST", "3", "TEST3"].
     */
    private final class Filter {
        private String condition;
        private String values[];
    }
}
