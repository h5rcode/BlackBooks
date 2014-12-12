package com.blackbooks.utils;

import android.graphics.Bitmap;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;

/**
 * Bitmap utility class.
 */
public final class BitmapUtils {

	/**
	 * The max height of the thumbnail in pixels.
	 */
	public static final int MAX_THUMBNAIL_HEIGHT = 256;

	/**
	 * Private constructor.
	 */
	private BitmapUtils() {
	}

	/**
	 * Resize a bitmap if its height is greater that
	 * {@link #MAX_THUMBNAIL_HEIGHT} or 1/3 of the display max dimension.
	 * 
	 * @param activity
	 *            The FragmentActivity hosting the bitmap.
	 * @param bitmap
	 *            The bitmap.
	 * @return A bitmap whose height is no greater than
	 *         {@link #MAX_THUMBNAIL_HEIGHT} or 1/3 of the display max dimension.
	 */
	public static Bitmap resizeThumbnailBitmap(FragmentActivity activity, Bitmap bitmap) {
		Bitmap result = bitmap;
		DisplayMetrics metrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

		int maxHeight = Math.min(MAX_THUMBNAIL_HEIGHT, Math.max(metrics.widthPixels, metrics.heightPixels) / 3);

		int height = bitmap.getHeight();
		if (height > maxHeight) {
			int width = bitmap.getWidth();
			float widthHeightRatio = (float) width / (float) height;
			int newWidth = (int) (widthHeightRatio * maxHeight);
			result = Bitmap.createScaledBitmap(bitmap, newWidth, maxHeight, false);
		}
		return result;
	}
}
