package com.blackbooks.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.util.DisplayMetrics;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


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
     * @param context   The calling activity.
     * @param uri       The URI of the image to compress.
     * @param maxHeight Max height of the compressed image.
     * @return A byte array containing the compressed image.
     */
    public static byte[] compress(Context context, Uri uri, int maxWidth, int maxHeight) {
        Bitmap bitmap = decodeSampledBitmapFromUri(context, uri, maxWidth, maxHeight);
        Bitmap resizedBitmap = resize(bitmap, maxHeight);
        byte[] bytes;
        try {
            OutputStream outputStream = context.openFileOutput(TEMP_PNG, Context.MODE_PRIVATE);
            resizedBitmap.compress(CompressFormat.PNG, 100, outputStream);

            InputStream in = context.openFileInput(TEMP_PNG);
            bytes = FileUtils.readBytes(in);

            context.deleteFile(TEMP_PNG);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return bytes;
    }

    /**
     * Resize a bitmap if its height is greater that
     * {@link #MAX_THUMBNAIL_HEIGHT} or 1/3 of the display max dimension.
     *
     * @param activity The Activity hosting the bitmap.
     * @param bitmap   The bitmap.
     * @return A bitmap whose height is no greater than
     * {@link #MAX_THUMBNAIL_HEIGHT} or 1/3 of the display max
     * dimension.
     */
    public static Bitmap resizeThumbnailBitmap(Activity activity, Bitmap bitmap) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int maxHeight = Math.min(MAX_THUMBNAIL_HEIGHT, Math.max(metrics.widthPixels, metrics.heightPixels) / 3);
        return resize(bitmap, maxHeight);
    }

    /**
     * Rotate a bitmap by +90 or -90 degrees.
     *
     * @param bytes Content of the bitmap.
     * @param sign  A positive value will rotate the bitmap to the right, a negative value will rotate
     *              it to the left. Zero is not permitted.
     * @return Content of the rotated bitmap.
     */
    public static byte[] rotate90(byte[] bytes, int sign) {
        if (sign == 0) {
            throw new IllegalArgumentException("Parameter 'sign' must be different from zero.");
        }
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        Matrix matrix = new Matrix();
        matrix.postRotate(sign * 90 / Math.abs(sign));
        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        rotatedBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    /**
     * Calculate the sample size that should be used to load a scaled down version of an image.
     *
     * @param options   {@link android.graphics.BitmapFactory.Options} set with the height and the width of the image to read.
     * @param reqWidth  Target width of the sub sampled image.
     * @param reqHeight Target height of the sub sampled image.
     * @return Sample size to use to load the sub sampled version of the image.
     */
    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    /**
     * Load a sampled {@link android.graphics.Bitmap} of an image.
     * <p/>
     * See <a href="http://developer.android.com/training/displaying-bitmaps/load-bitmap.html">Loading Large Bitmaps Efficiently</a>.
     *
     * @param context   Context.
     * @param uri       Uri of the image to load.
     * @param reqWidth  Target width of the sub sampled image.
     * @param reqHeight Target height of the sub sampled image.
     * @return A sup sampled bitmap of the original image.
     */
    private static Bitmap decodeSampledBitmapFromUri(Context context, Uri uri, int reqWidth, int reqHeight) {

        try {
            InputStream is = context.getContentResolver().openInputStream(uri);

            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;

            BitmapFactory.decodeStream(is, null, options);

            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
            options.inJustDecodeBounds = false;

            is = context.getContentResolver().openInputStream(uri);
            return BitmapFactory.decodeStream(is, null, options);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Resize a bitmap so that its height does not exceed {@code maxHeight}.
     *
     * @param bitmap    Bitmap.
     * @param maxHeight The max height of the bitmap.
     * @return A resized version of the Bitmap its height was greater than
     * {@code maxHeight}. The Bitmap itself otherwise.
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
