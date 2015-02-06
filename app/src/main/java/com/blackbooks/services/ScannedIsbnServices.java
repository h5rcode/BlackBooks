package com.blackbooks.services;

import android.database.sqlite.SQLiteDatabase;

import com.blackbooks.model.persistent.ScannedIsbn;
import com.blackbooks.sql.Broker;
import com.blackbooks.sql.BrokerManager;

import java.util.Date;
import java.util.List;

/**
 * ScannedIsbn services.
 */
public class ScannedIsbnServices {

    /**
     * Delete all the scanned ISBNs.
     *
     * @param db SQLiteDatabase.
     */
    public static void deleteAllScannedIsbns(SQLiteDatabase db) {
        db.delete(ScannedIsbn.NAME, null, null);
    }

    /**
     * Get the list of all the scanned ISBNs.
     *
     * @param db SQLiteDatabase.
     */
    public static List<ScannedIsbn> getScannedIsbnList(SQLiteDatabase db) {
        return BrokerManager.getBroker(ScannedIsbn.class).getAll(db, null, new String[]{ScannedIsbn.Cols.SCI_SCAN_DATE});
    }

    /**
     * Save a scanned ISBN. If it already in the database, just update the date.
     *
     * @param db   SQLiteDatabase.
     * @param isbn String.
     */
    public static void saveScannedIsbn(SQLiteDatabase db, String isbn) {
        db.beginTransaction();
        try {
            Broker<ScannedIsbn> broker = BrokerManager.getBroker(ScannedIsbn.class);

            ScannedIsbn criteria = new ScannedIsbn();
            criteria.isbn = isbn;
            ScannedIsbn scannedIsbn = broker.getByCriteria(db, criteria);

            if (scannedIsbn == null) {
                scannedIsbn = new ScannedIsbn();
                scannedIsbn.isbn = isbn;
            }
            scannedIsbn.scanDate = new Date();

            broker.save(db, scannedIsbn);

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }
}
