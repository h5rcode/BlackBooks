package com.blackbooks.service;

import android.app.IntentService;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.blackbooks.database.SQLiteHelper;
import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.model.persistent.ScannedIsbn;
import com.blackbooks.search.BookSearcher;
import com.blackbooks.services.BookServices;
import com.blackbooks.services.ScannedIsbnServices;
import com.blackbooks.utils.LogUtils;
import com.blackbooks.utils.VariableUtils;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.conn.HttpHostConnectException;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

/**
 * A service that performs background ISBN look ups.
 */
public final class BulkSearchService extends IntentService {

    /**
     * Constructor.
     */
    public BulkSearchService() {
        super(BulkSearchService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(LogUtils.TAG, String.format("%s starting.", BulkSearchService.class.getName()));

        SQLiteDatabase db = SQLiteHelper.getInstance().getWritableDatabase();

        List<ScannedIsbn> scannedIsbnList = ScannedIsbnServices.getScannedIsbnList(db);

        Log.i(LogUtils.TAG, String.format("%d ISBNs to search.", scannedIsbnList.size()));

        for (ScannedIsbn scannedIsbn : scannedIsbnList) {
            String isbn = scannedIsbn.isbn;
            Log.i(LogUtils.TAG, String.format("Searching results for ISBN %s.", isbn));

            try {
                BookInfo bookInfo = BookSearcher.search(isbn);
                if (bookInfo == null) {
                    Log.i(LogUtils.TAG, "No results.");
                } else {
                    Log.i(LogUtils.TAG, String.format("Result: %s", bookInfo.title));
                    BookServices.saveBookInfo(db, bookInfo);
                    VariableUtils.getInstance().setReloadBookList(true);
                }

            } catch (ClientProtocolException e) {
                Log.e(LogUtils.TAG, "Connection problem.", e);
            } catch (HttpHostConnectException e) {
                Log.e(LogUtils.TAG, "Connection problem.", e);
            } catch (UnknownHostException e) {
                Log.e(LogUtils.TAG, "Connection problem.", e);
            } catch (IOException e) {
                Log.e(LogUtils.TAG, "An exception occurred during the background search.", e);
            } catch (InterruptedException e) {
                Log.i(LogUtils.TAG, "Service interrupted.");
                break;
            }
        }

        Log.i(LogUtils.TAG, String.format("%s stopping.", BulkSearchService.class.getName()));
    }
}
