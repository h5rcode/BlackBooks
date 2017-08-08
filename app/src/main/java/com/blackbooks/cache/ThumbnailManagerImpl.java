package com.blackbooks.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.LruCache;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.blackbooks.R;
import com.blackbooks.services.BookService;

/**
 * Singleton class for managing thumbnails, they are cached in a LruCache.
 */
public final class ThumbnailManagerImpl implements ThumbnailManager {

    private final BookService bookService;

    private final LruCache<Long, Bitmap> mSmallThumbnailCache;
    private Bitmap mUndefinedBitmap;

    /**
     * Private constructor.
     */
    public ThumbnailManagerImpl(BookService bookService) {
        this.bookService = bookService;

        int maxMemoryKB = (int) (Runtime.getRuntime().maxMemory() / 1024);
        int totalCacheSize = maxMemoryKB / 8;
        mSmallThumbnailCache = new LruCache<Long, Bitmap>(totalCacheSize) {
            @Override
            protected int sizeOf(Long key, Bitmap value) {
                return value.getRowBytes() * value.getHeight() / 1024;
            }
        };
    }

    /**
     * Draw a small thumbnail in an ImageView.
     *
     * @param bookId      Id of the book.
     * @param context     Context.
     * @param imageView   ImageView.
     * @param progressBar ProgressBar.
     */
    public void drawSmallThumbnail(long bookId, Context context, ImageView imageView, ProgressBar progressBar) {
        draw(bookId, context, imageView, progressBar);
    }

    /**
     * Remove the small and normal thumbnail corresponding to a book from the
     * cache.
     *
     * @param bookId Id of the book.
     */
    public void removeThumbnails(long bookId) {
        mSmallThumbnailCache.remove(bookId);
    }

    /**
     * Draw a thumbnail.
     *
     * @param bookId      Id of the book.
     * @param context     Context.
     * @param imageView   ImageView.
     * @param progressBar ProgressBar.
     */
    private void draw(long bookId, Context context, ImageView imageView, ProgressBar progressBar) {
        Bitmap bmp = mSmallThumbnailCache.get(bookId);
        if (bmp != null) {
            imageView.setImageBitmap(bmp);
            progressBar.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
        } else {
            BookLoadTask bookLoadTask = new BookLoadTask(context, bookId, imageView, progressBar);
            bookLoadTask.execute();
        }
    }

    /**
     * Return a bitmap containing the icon to display when a book does not have
     * a thumbnail.
     *
     * @param context Context.
     * @return Bitmap.
     */
    private Bitmap getUndefinedBitmap(Context context) {
        if (mUndefinedBitmap == null) {
            Drawable drawable = ContextCompat.getDrawable(context, R.drawable.ic_undefined_thumbnail);
            mUndefinedBitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(mUndefinedBitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
        }
        
        return mUndefinedBitmap;
    }

    /**
     * Task to load a book and set draw its small or normal thumbnail in a given
     * ImageView.
     */
    private final class BookLoadTask extends AsyncTask<Long, Void, byte[]> {

        private final Context mContext;
        private final long mBookId;
        private final ImageView mImageView;
        private final ProgressBar mProgressBar;

        /**
         * Constructor.
         *
         * @param context     Context.
         * @param bookId      Id of the book.
         * @param imageView   ImageView.
         * @param progressBar ProgressBar.
         */
        public BookLoadTask(Context context, long bookId, ImageView imageView, ProgressBar progressBar) {
            mContext = context;
            mBookId = bookId;
            mImageView = imageView;
            mProgressBar = progressBar;
        }

        @Override
        protected byte[] doInBackground(Long... params) {
            return bookService.getBookSmallThumbnail(mBookId);
        }

        @Override
        protected void onPostExecute(byte[] result) {
            super.onPostExecute(result);
            Bitmap smallThumbnailBmp;

            if (result != null && result.length > 0) {
                smallThumbnailBmp = BitmapFactory.decodeByteArray(result, 0, result.length);
            } else {
                smallThumbnailBmp = getUndefinedBitmap(mContext);
            }

            mSmallThumbnailCache.put(mBookId, smallThumbnailBmp);

            mImageView.setImageBitmap(smallThumbnailBmp);
            mProgressBar.setVisibility(View.GONE);
            mImageView.setVisibility(View.VISIBLE);
        }
    }
}
