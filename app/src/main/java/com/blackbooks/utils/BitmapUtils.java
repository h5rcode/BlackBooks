package com.blackbooks.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;


/**
 * Bitmap utility class.
 */
public final class BitmapUtils {

	/**
	 * The max height of the thumbnail in pixels.
	 */
	public static final int MAX_THUMBNAIL_HEIGHT = 256;

	private static final String TEMP_PNG = "tmp.png";

	/**
	 * Private constructor.
	 */
	private BitmapUtils() {
	}

	/**
	 * Compress an image and resize it before if necessary.
	 * 
	 * @param activity
	 *            The calling activity.
	 * @param image
	 *            The image to compress.
	 * @param maxHeight
	 *            Max height of the compressed image.
	 * @return A byte array containing the compressed image.
	 */
	public static byte[] compress(Activity activity, byte[] image, int maxHeight) {
		Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
		Bitmap resizedBitmap = resize(bitmap, maxHeight);
		byte[] bytes;
		try {
			OutputStream outputStream = activity.openFileOutput(TEMP_PNG, Context.MODE_PRIVATE);
			resizedBitmap.compress(CompressFormat.PNG, 100, outputStream);

			InputStream in = activity.openFileInput(TEMP_PNG);
			bytes = FileUtils.readBytes(in);

			activity.deleteFile(TEMP_PNG);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return bytes;
	}

	/**
	 * Get the byte array representing a Bitmap.
	 * 
	 * @param bitmap
	 *            Bitmap.
	 * @return Byte array.
	 */
	public static byte[] getBytes(Bitmap bitmap) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
		return stream.toByteArray();
	}

	/**
	 * Resize a bitmap if its height is greater that
	 * {@link #MAX_THUMBNAIL_HEIGHT} or 1/3 of the display max dimension.
	 * 
	 * @param activity
	 *            The Activity hosting the bitmap.
	 * @param bitmap
	 *            The bitmap.
	 * @return A bitmap whose height is no greater than
	 *         {@link #MAX_THUMBNAIL_HEIGHT} or 1/3 of the display max
	 *         dimension.
	 */
	public static Bitmap resizeThumbnailBitmap(Activity activity, Bitmap bitmap) {
		DisplayMetrics metrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

		int maxHeight = Math.min(MAX_THUMBNAIL_HEIGHT, Math.max(metrics.widthPixels, metrics.heightPixels) / 3);
        Bitmap result = resize(bitmap, maxHeight);
		return result;
	}

	/**
	 * Resize a bitmap so that its height does not exceed {@code maxHeight}.
	 * 
	 * @param bitmap
	 *            Bitmap.
	 * @param maxHeight
	 *            The max height of the bitmap.
	 * @return A resized version of the Bitmap its height was greater than
	 *         {@code maxHeight}. The Bitmap itself otherwise.
	 */
	private static Bitmap resize(Bitmap bitmap, int maxHeight) {
		Bitmap result = bitmap;
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
