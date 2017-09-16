package com.blackbooks.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.blackbooks.R;
import com.blackbooks.activities.BulkAddActivity;
import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.model.persistent.Isbn;
import com.blackbooks.services.search.BookOnlineSearchService;
import com.blackbooks.services.IsbnService;
import com.blackbooks.utils.LogUtils;
import com.blackbooks.utils.VariableUtils;

import java.util.List;

import javax.inject.Inject;

/**
 * A service that performs background ISBN look ups.
 */
public final class BulkSearchService extends IntentService {

    private static final int NOTIFICATION_ID = 1;

    private static final int MAX_CONSECUTIVE_ERRORS = 5;

    private boolean mStop;

    @Inject
    BookOnlineSearchService bookOnlineSearchService;

    @Inject
    IsbnService isbnService;

    /**
     * Constructor.
     */
    public BulkSearchService() {
        super(BulkSearchService.class.getName());

    }

    @Override
    protected void onHandleIntent(Intent intent) {

        VariableUtils.getInstance().setBulkSearchRunning(true);

        final List<Isbn> isbnList = isbnService.getIsbnListToLookUp(Integer.MAX_VALUE, 0);

        final int isbnCount = isbnList.size();

        final Resources res = getResources();
        final String text = res.getQuantityString(R.plurals.notification_bulk_search_running_text, isbnCount, isbnCount);

        final Intent resultIntent = new Intent(this, BulkAddActivity.class);
        resultIntent.putExtra(BulkAddActivity.EXTRA_SELECTED_TAB, BulkAddActivity.TAB_LOOKED_UP);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(BulkAddActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        final PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        final NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_notify)
                .setContentTitle(getString(R.string.notification_bulk_search_running_title))
                .setContentText(text);

        builder.setContentIntent(resultPendingIntent);

        builder.setTicker(getString(R.string.notification_bulk_search_running_title));
        notificationManager.notify(NOTIFICATION_ID, builder.build());
        builder.setTicker(null);

        int consecutiveErrors = 0;
        for (int i = 0; i < isbnCount; i++) {
            if (mStop) {
                break;
            }
            if (consecutiveErrors > MAX_CONSECUTIVE_ERRORS) {
                break;
            }
            final Isbn isbn = isbnList.get(i);
            final String number = isbn.number;
            Log.d(LogUtils.TAG, String.format("Searching results for ISBN %s.", number));

            try {
                final BookInfo bookInfo = bookOnlineSearchService.search(number);
                if (bookInfo == null) {
                    Log.d(LogUtils.TAG, "No results.");
                    isbnService.markIsbnLookedUp(isbn.id, null);
                } else {
                    Log.d(LogUtils.TAG, String.format("Result: %s", bookInfo.title));
                    isbnService.saveBookInfo(bookInfo, isbn.id);
                    VariableUtils.getInstance().setReloadBookList(true);
                }

                consecutiveErrors = 0;
            } catch (InterruptedException e) {
                Log.i(LogUtils.TAG, "Service interrupted.");
                break;
            } catch (Exception e) {
                consecutiveErrors++;
                Log.e(LogUtils.TAG, "An exception occurred during the background search.", e);
            }

            builder.setProgress(isbnCount, i, false);
            final Notification notification = builder.build();
            notification.flags |= Notification.FLAG_NO_CLEAR;
            notificationManager.notify(NOTIFICATION_ID, notification);
        }

        builder.setContentTitle(getString(R.string.notification_bulk_search_finished_title));
        builder.setContentText(getString(R.string.notification_bulk_search_finished_text));
        builder.setProgress(0, 0, false);
        builder.setTicker(getString(R.string.notification_bulk_search_finished_title));
        builder.setAutoCancel(true);
        notificationManager.notify(NOTIFICATION_ID, builder.build());

        VariableUtils.getInstance().setBulkSearchRunning(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mStop = true;
    }
}
