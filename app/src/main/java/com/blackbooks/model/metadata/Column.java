package com.blackbooks.model.metadata;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to apply on fields to indicate they represent a column in a
 * database.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {

	/**
	 * Column name.
	 * 
	 * @return Name.
	 */
	public String name();

	/**
	 * The type of data the corresponding column can store.
	 * 
	 * @return SQLiteDataType.
	 */
	public SQLiteDataType type();

	/**
	 * Indicates if the column is mandatory.
	 * 
	 * @return True if the column does not allow null values.
	 */
	public boolean mandatory() default false;

	/**
	 * This property has a sense only if the referencedType is set. If true,
	 * this property indicates that if the referenced row is deleted, the
	 * referencing row will also be deleted automatically.
	 * 
	 * @return True to delete the referencing row if the row referenced by this
	 *         column is deleted.
	 */
	public boolean onDeleteCascade() default false;

	/**
	 * Indicates whether the column is the primary key or not.
	 * 
	 * @return True if the column is the primary key of the table.
	 */
	public boolean primaryKey() default false;

	/**
	 * Represents the type of the table that is referenced by the column. If
	 * different from void.class, then the column contains a foreign key.
	 * 
	 * @return The type of the table referenced by the column. If void.class,
	 *         then the column does not reference an other table.
	 */
	@SuppressWarnings("rawtypes")
	public Class referencedType() default void.class;

	/**
	 * Indicates whether the column should have a unique constraint.
	 * 
	 * @return True if the column has a unique constraint.
	 */
	public boolean unique() default false;

	/**
	 * The version of the database when this column was added to it.
	 * 
	 * @return Version.
	 */
	public int version();

	/**
	 * SQLite data type enumeration.
	 */
	public enum SQLiteDataType {

		/**
		 * The value is a blob of data, stored exactly as it was input.
		 */
		BLOB,

		/**
		 * The value is a signed integer, stored in 1, 2, 3, 4, 6, or 8 bytes
		 * depending on the magnitude of the value.
		 */
		INTEGER,

		/**
		 * The value is a text string, stored using the database encoding
		 * (UTF-8, UTF-16BE or UTF-16LE).
		 */
		TEXT
	}
}
