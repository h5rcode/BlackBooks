package com.blackbooks.model.metadata;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface FTSTable {

    /**
     * Name of the FTS table.
     *
     * @return Name.
     */
    String name();

    /**
     * Version of the Full-Text-Search module to use for the table.
     *
     * @return FTSModules.
     */
    FTSModules ftsModuleVersion();

    /**
     * The version of the database when this table was added to it.
     *
     * @return Version.
     */
    int version();

    /**
     * Enumeration of the available SQLITE Full-Text-Search modules.
     */
    public enum FTSModules {

        /**
         * FTS3.
         */
        FTS3,

        /**
         * FTS4.
         */
        FTS4
    }
}
