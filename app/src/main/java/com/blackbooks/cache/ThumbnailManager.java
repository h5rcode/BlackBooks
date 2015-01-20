package com.blackbooks.cache;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.blackbooks.R;
import com.blackbooks.database.SQLiteHelper;
import com.blackbooks.model.persistent.Book;
import com.blackbooks.services.BookServices;

/**
 * Singleton class for managing thumbnails, they are cached in a LruCache.
 */
public final class ThumbnailManager {

	private static ThumbnailManager mInstance;
	private final LruCache<Long, Bitmap> mSmallThumbnailCache;
	private final LruCache<Long, Bitmap> mThumbnailCache;
	private Bitmap mUndefinedBitmap;

	/**
	 * Private constructor.
	 */
	private ThumbnailManager() {
		int maxMemoryKB = (int) (Runtime.getRuntime().maxMemory() / 1024);
		int totalCacheSize = maxMemoryKB / 8;
		int smallThumbnailCacheSize = totalCacheSize / 4;
		int thumbnailCacheSize = totalCacheSize - smallThumbnailCacheSize;
		mSmallThumbnailCache = new LruCache<Long, Bitmap>(smallThumbnailCacheSize);
		mThumbnailCache = new LruCache<Long, Bitmap>(thumbnailCacheSize);
	}

	/**
	 * Get the instance of ThumbnailManager.
	 * 
	 * @return ThumbnailManager.
	 */
	public static ThumbnailManager getInstance() {
		if (mInstance == null) {
			mInstance = new ThumbnailManager();
		}
		return mInstance;
	}

	/**
	 * Draw a small thumbnail in an ImageView.
	 * 
	 * @param bookId
	 *            Id of the book.
	 * @param context
	 *            Context.
	 * @param imageView
	 *            ImageView.
	 * @param progressBar
	 *            ProgressBar.
	 */
	public void drawSmallThumbnail(long bookId, Context context, ImageView imageView, ProgressBar progressBar) {
		draw(bookId, context, imageView, progressBar, true);
	}

	/**
	 * Draw a thumbnail in an ImageView.
	 * 
	 * @param bookId
	 *            Id of the book.
	 * @param context
	 *            Context.
	 * @param imageView
	 *            ImageView.
	 * @param progressBar
	 *            ProgressBar.
	 */
	public void drawThumbnail(long bookId, Context context, ImageView imageView, ProgressBar progressBar) {
		draw(bookId, context, imageView, progressBar, false);
	}

	/**
	 * Remove the small and normal thumbnail corresponding to a book from the
	 * cache.
	 * 
	 * @param bookId
	 *            Id of the book.
	 */
	public void removeThumbnails(long bookId) {
		mSmallThumbnailCache.remove(bookId);
		mThumbnailCache.remove(bookId);
	}

	/**
	 * Draw a thumbnail.
	 * 
	 * @param bookId
	 *            Id of the book.
	 * @param context
	 *            Context.
	 * @param imageView
	 *            ImageView.
	 * @param progressBar
	 *            ProgressBar.
	 * @param isSmallThumbnail
	 *            True to draw a small thumbnail, false to drow a normal
	 *            thumnbail.
	 */
	private void draw(long bookId, Context context, ImageView imageView, ProgressBar progressBar, boolean isSmallThumbnail) {
		Bitmap bmp;
		if (isSmallThumbnail) {
			bmp = mSmallThumbnailCache.get(bookId);
		} else {
			bmp = mThumbnailCache.get(bookId);
		}
		if (bmp != null) {
			imageView.setImageBitmap(bmp);
			progressBar.setVisibility(View.GONE);
			imageView.setVisibility(View.VISIBLE);
		} else {
			BookLoadTask bookLoadTask = new BookLoadTask(context, bookId, imageView, progressBar, isSmallThumbnail);
			bookLoadTask.execute();
		}
	}

	/**
	 * Return a bitmap containing the icon to display when a book does not have
	 * a thumbnail.
	 * 
	 * @param context
	 *            Context.
	 * @return Bitmap.
	 */
	private Bitmap getUndefinedBitmap(Context context) {
		if (mUndefinedBitmap == null) {
			mUndefinedBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_undefined_thumbnail);
		}
		return mUndefinedBitmap;
	}

	/**
	 * Task to load a book and set draw its small or normal thumbnail in a given
	 * ImageView.
	 */
	private final class BookLoadTask extends AsyncTask<Long, Void, Book> {

		private final Context mContext;
		private final long mBookId;
		private final ImageView mImageView;
		private ProgressBar mProgressBar;
		private final boolean mIsSmallThumbnail;

		/**
		 * Constructor.
		 * 
		 * @param context
		 *            Context.
		 * @param bookId
		 *            Id of the book.
		 * @param imageView
		 *            ImageView.
		 * @param progressBar
		 *            ProgressBar.
		 * @param isSmallThumbnail
		 *            True to draw a small thumbnail, false to drow a normal
		 *            thumnbail.
		 */
		public BookLoadTask(Context context, long bookId, ImageView imageView, ProgressBar progressBar, boolean isSmallThumbnail) {
			mContext = context;
			mBookId = bookId;
			mImageView = imageView;
			mProgressBar = progressBar;
			mIsSmallThumbnail = isSmallThumbnail;
		}

		@Override
		protected Book doInBackground(Long... params) {
			SQLiteHelper dbHelper = new SQLiteHelper(mContext);
			SQLiteDatabase db = null;
			Book book = null;
			try {
				db = dbHelper.getReadableDatabase();
				book = BookServices.getBook(db, mBookId);
			} finally {
				if (db != null) {
					db.close();
				}
			}
			return book;
		}

		@Override
		protected void onPostExecute(Book result) {
			super.onPostExecute(result);

			Bitmap smallThumbnailBmp;
			Bitmap thumnailBmp;
			byte[] smallThumbnail = result.smallThumbnail;
			byte[] thumbnail = result.thumbnail;

			if (smallThumbnail != null) {
				smallThumbnailBmp = BitmapFactory.decodeByteArray(smallThumbnail, 0, smallThumbnail.length);
			} else {
				smallThumbnailBmp = getUndefinedBitmap(mContext);
			}
			if (thumbnail != null) {
				thumnailBmp = BitmapFactory.decodeByteArray(thumbnail, 0, thumbnail.length);
			} else {
				thumnailBmp = getUndefinedBitmap(mContext);
			}

			mSmallThumbnailCache.put(mBookId, smallThumbnailBmp);
			mThumbnailCache.put(mBookId, thumnailBmp);

			if (mIsSmallThumbnail) {
				if (smallThumbnailBmp != null) {
					mImageView.setImageBitmap(smallThumbnailBmp);
				}
			} else {
				if (thumnailBmp != null) {
					mImageView.setImageBitmap(thumnailBmp);
				}
			}
			mProgressBar.setVisibility(View.GONE);
			mImageView.setVisibility(View.VISIBLE);
		}
	}
}
