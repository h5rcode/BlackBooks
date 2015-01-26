package com.blackbooks.model.metadata;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FTSColumn {

    /**
     * Name of the column.
     *
     * @return Name.
     */
    String name();

    /**
     * Indicates whether the column is the primary key or not.
     *
     * @return True if the column is the primary key of the table.
     */
    boolean primaryKey() default false;

    /**
     * The version of the database when this column was added to it.
     *
     * @return Version.
     */
    int version();
}
