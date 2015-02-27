package com.blackbooks.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.blackbooks.database.upgrades.Version2;
import com.blackbooks.database.upgrades.Version3;
import com.blackbooks.sql.Broker;
import com.blackbooks.sql.BrokerManager;
import com.blackbooks.sql.FTSBroker;
import com.blackbooks.sql.FTSBrokerManager;
import com.blackbooks.utils.LogUtils;

import java.util.List;

/**
 * Helper class to create/open/upgrade the database.
 */
public final class SQLiteHelper extends SQLiteOpenHelper {

    private static SQLiteHelper mInstance;

    /**
     * Private constructor.
     *
     * @param context Context.
     */
    private SQLiteHelper(Context context) {
        super(context, Database.NAME, null, Database.VERSION);
    }

    /**
     * Initialize the singleton.
     *
     * @param context Context.
     */
    public static synchronized void initialize(Context context) {
        mInstance = new SQLiteHelper(context);
    }

    /**
     * Return the instance.
     *
     * @return SQLiteHelper.
     */
    public static SQLiteHelper getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(LogUtils.TAG, "Creating the database.");

        Database database = Database.getInstance();
        List<Class<?>> tables = database.getTables();

        Log.i(LogUtils.TAG, "Creating tables.");
        for (Class<?> table : tables) {
            Broker<?> broker = BrokerManager.getBroker(table);
            broker.createTable(db);
        }
        Log.i(LogUtils.TAG, "Tables successfully created.");

        List<Class<?>> ftsTables = database.getFTSTables();

        Log.i(LogUtils.TAG, "Creating the Full-Text-Search tables.");
        for (Class<?> ftsTable : ftsTables) {
            FTSBroker<?> ftsBroker = FTSBrokerManager.getBroker(ftsTable);
            ftsBroker.createTable(db);
        }
        Log.i(LogUtils.TAG, "Full-Text-Search tables successfully created.");

        Log.i(LogUtils.TAG, "Database successfully created.");
    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        SQLiteDatabase db = super.getWritableDatabase();

        // Enable foreign keys. It must be executed every time we get the
        // database.
        db.execSQL("PRAGMA foreign_keys = ON;");
        return db;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (oldVersion < 2) {
            Version2.upgrade(db);
        }

        if (oldVersion < 3) {
            Version3.upgrade(db);
        }
    }
}
