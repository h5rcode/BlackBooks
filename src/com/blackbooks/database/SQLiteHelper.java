package com.blackbooks.database;

import java.util.ArrayList;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.blackbooks.sql.Broker;
import com.blackbooks.sql.BrokerManager;

/**
 * Helper class to create/open/upgrade the database.
 */
public class SQLiteHelper extends SQLiteOpenHelper {

	private final static String TAG = SQLiteHelper.class.getName();

	/**
	 * Constructor.
	 * 
	 * @param context
	 *            Context.
	 */
	public SQLiteHelper(Context context) {
		super(context, Database.NAME, null, Database.VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.i(TAG, "Creating the database.");

		Database database = Database.getInstance();
		ArrayList<Class<?>> tables = database.getTables();

		Log.i(TAG, "Creating tables.");
		for (Class<?> table : tables) {
			Broker<?> broker = BrokerManager.getBroker(table);
			broker.createTable(db);
		}
		Log.i(TAG, "Tables successfully created.");

		Log.i(TAG, "Database successfully created.");
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
	}
}
