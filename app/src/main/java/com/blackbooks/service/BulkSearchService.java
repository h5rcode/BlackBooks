package com.blackbooks.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.blackbooks.BlackBooksApplication;
import com.blackbooks.R;
import com.blackbooks.activities.BulkAddActivity;
import com.blackbooks.database.SQLiteHelper;
import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.model.persistent.Isbn;
import com.blackbooks.search.BookSearcher;
import com.blackbooks.services.IsbnServices;
import com.blackbooks.utils.LogUtils;
import com.blackbooks.utils.VariableUtils;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.List;

/**
 * A service that performs background ISBN look ups.
 */
public final class BulkSearchService extends IntentService {

    private static final int NOTIFICATION_ID = 1;

    private static final int MAX_CONSECUTIVE_ERRORS = 5;

    private boolean mStop;

    private Tracker mTracker;

    /**
     * Constructor.
     */
    public BulkSearchService() {
        super(BulkSearchService.class.getName());
    }

    @Override
    public void onCreate() {
        super.onCreate();

        final BlackBooksApplication application = (BlackBooksApplication) getApplication();
        mTracker = application.getTracker();
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        VariableUtils.getInstance().setBulkSearchRunning(true);

        final SQLiteDatabase db = SQLiteHelper.getInstance().getWritableDatabase();
        final List<Isbn> isbnList = IsbnServices.getIsbnListToLookUp(db, Integer.MAX_VALUE, 0);

        final int isbnCount = isbnList.size();
        sendIsbnEvent(R.string.analytics_action_bulk_lookup_start);

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

        sendIsbnEvent(R.string.analytics_action_bulk_lookup_start);

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
                final BookInfo bookInfo = BookSearcher.search(number);
                if (bookInfo == null) {
                    Log.d(LogUtils.TAG, "No results.");
                    IsbnServices.markIsbnLookedUp(db, isbn.id, null);
                } else {
                    Log.d(LogUtils.TAG, String.format("Result: %s", bookInfo.title));
                    IsbnServices.saveBookInfo(db, bookInfo, isbn.id);
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
        sendIsbnEvent(R.string.analytics_action_bulk_lookup_stop);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mStop = true;
    }

    /**
     * Send an event of the category "ISBN" to Google Analytics.
     *
     * @param actionResourceId Id of the resource corresponding to the action of the event.
     */
    private void sendIsbnEvent(int actionResourceId) {
        mTracker.send(
                new HitBuilders.EventBuilder()
                        .setCategory(getString(R.string.analytics_category_isbn))
                        .setAction(getString(actionResourceId))
                        .build()
        );
    }
}
