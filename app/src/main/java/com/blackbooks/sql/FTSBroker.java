package com.blackbooks.sql;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.blackbooks.model.metadata.FTSColumn;
import com.blackbooks.model.metadata.FTSTable;
import com.blackbooks.utils.ReflectionUtils;

import java.lang.reflect.Field;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class allows to perform basic operations (insert/update/delete) on a
 * SQLite Full-Text-Search table.
 *
 * @param <T> Type of the persistent object to manipulate with the broker.
 */
public class FTSBroker<T> {

    private static final String DOCID = "DOCID";

    private Class<T> mType;
    private FTSTable mTable;
    private List<FTSColumn> mColumns;
    private HashMap<Field, FTSColumn> mColumnMap;
    private Field mPrimaryKeyField;
    private FTSColumn mPrimaryKeyColumn;

    private String mSqlCreateTable;

    /**
     * Constructor.
     *
     * @param type The type representing the table to access through the broker.
     */
    public FTSBroker(Class<T> type) {
        mType = type;
        mTable = type.getAnnotation(FTSTable.class);

        if (mTable == null) {
            throw new InvalidParameterException("The parameter does not have a FTSTable annotation.");
        }

        Field[] fields = type.getFields();

        mColumns = new ArrayList<FTSColumn>();
        mColumnMap = new HashMap<Field, FTSColumn>();
        for (Field field : fields) {
            FTSColumn column = field.getAnnotation(FTSColumn.class);
            if (column != null) {
                mColumns.add(column);
                mColumnMap.put(field, column);

                if (column.primaryKey() && column.name().compareToIgnoreCase(DOCID) == 0) {
                    mPrimaryKeyColumn = column;
                    mPrimaryKeyField = field;
                }
            }
        }

        if (mPrimaryKeyColumn == null) {
            throw new IllegalArgumentException("FTS Table " + mTable.name() + " must have a primary key named " + DOCID + ".");
        }

        mSqlCreateTable = SqlBuilder.buildSqlCreateFTSTable(mTable, mColumns);
    }

    /**
     * Creates the FTS table in a database.
     *
     * @param db SQLiteDatabase.
     */
    public void createTable(SQLiteDatabase db) {
        db.execSQL(mSqlCreateTable);
    }

    /**
     * Insert a new line in the FTS table. The primary key of the bean must have
     * a value.
     *
     * @param db   SQLiteDatabase.
     * @param bean Bean.
     * @return The id of the inserted row (the same value as bean's primary
     * key).
     */
    public long insert(SQLiteDatabase db, T bean) {
        ContentValues values = buildContentValues(bean);
        return db.insertOrThrow(mTable.name(), null, values);
    }

    /**
     * Update a line in the FTS table.
     *
     * @param db   SQLiteDatabase.
     * @param bean Bean.
     */
    public void update(SQLiteDatabase db, T bean) {
        ContentValues values = buildContentValues(bean);
        String whereClause = mPrimaryKeyColumn.name() + " = ?";
        Object primaryKey = ReflectionUtils.getFieldValue(mPrimaryKeyField, bean);
        String[] whereArgs = new String[]{primaryKey.toString()};
        db.updateWithOnConflict(mTable.name(), values, whereClause, whereArgs, SQLiteDatabase.CONFLICT_ROLLBACK);

    }

    /**
     * Delete a row from the FTS table.
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
            FTSColumn column = mColumnMap.get(field);
            String columnName = column.name();

            if (column.primaryKey()) {
                Long intValue = (Long) ReflectionUtils.getFieldValue(field, bean);
                values.put(columnName, intValue);
            } else {
                String stringValue = (String) ReflectionUtils.getFieldValue(field, bean);
                if (stringValue != null) {
                    stringValue = stringValue.trim();
                }
                values.put(columnName, stringValue);
            }
        }
        return values;
    }

}
