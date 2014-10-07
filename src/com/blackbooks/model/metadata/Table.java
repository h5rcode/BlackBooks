package com.blackbooks.model.metadata;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines an annotation to apply to classes representing a table in a database.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Table {

	/**
	 * Name of the table.
	 * 
	 * @return Table name.
	 */
	public String name();

	/**
	 * The version of the database when this table was added to it.
	 * 
	 * @return Version.
	 */
	public int version();
}
