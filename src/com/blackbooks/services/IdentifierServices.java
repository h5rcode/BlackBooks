package com.blackbooks.services;

import java.util.ArrayList;

import android.database.sqlite.SQLiteDatabase;

import com.blackbooks.model.persistent.Identifier;
import com.blackbooks.sql.BrokerManager;

/**
 * Identifier services.
 */
public class IdentifierServices {

	/**
	 * Delete all the itentifiers of a book.
	 * 
	 * @param db
	 *            SQLiteDatabase.
	 * @param bookId
	 *            Id of a book.
	 */
	public static void deleteIdentifierListByBook(SQLiteDatabase db, long bookId) {
		Identifier identifier = new Identifier();
		identifier.bookId = bookId;
		BrokerManager.getBroker(Identifier.class).deleteAllByCriteria(db, identifier);
	}

	/**
	 * Get the list of identifiers of a book.
	 * 
	 * @param db
	 *            SQLiteDatabase.
	 * @param bookId
	 *            Id of the referenced book.
	 * @return List of Identifier.
	 */
	public static ArrayList<Identifier> getIdentifierListByBook(SQLiteDatabase db, long bookId) {
		Identifier identifier = new Identifier();
		identifier.bookId = bookId;
		return BrokerManager.getBroker(Identifier.class).getAllByCriteria(db, identifier);
	}

	/**
	 * Save a bool identifier if it is valid.
	 * 
	 * @param db
	 *            SQLiteDatabase.
	 * @param identifier
	 *            Identifier.
	 * @return Id of the saved identifier.
	 */
	public static long saveIdentifier(SQLiteDatabase db, Identifier identifier) {
		return BrokerManager.getBroker(Identifier.class).save(db, identifier);
	}
}
