package com.blackbooks.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.os.Environment;
import android.util.Log;

/**
 * File handling utility class.
 * 
 */
public final class FileUtils {

	/**
	 * Application file directory.
	 */
	public static final String BLACK_BOOKS_DIR = "com.blackbooks";

	/**
	 * UTF-8 Byte Order Mark (BOM).
	 */
	public static final String UTF8_BOM = "\uFEFF";

	/**
	 * Private constructor.
	 */
	private FileUtils() {
	}

	/**
	 * Copy a file.
	 * 
	 * @param src
	 *            File to copy.
	 * @param dst
	 *            Destination of the copy.
	 * @return True if the copy succeeded, false otherwise.
	 */
	public static boolean copy(File src, File dst) {
		boolean success = false;
		try {
			InputStream in = new FileInputStream(src);
			OutputStream out = new FileOutputStream(dst);

			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
			success = true;
		} catch (IOException e) {
			Log.e(LogUtils.TAG, "Could not copy file.", e);
		}
		return success;
	}

	/**
	 * Create a new file in the application directory on the external storage
	 * drive.
	 * 
	 * @param fileName
	 *            Name of the file to create.
	 * @return File or null if the file could not be created.
	 */
	public static File createFileInAppDir(String fileName) {
		File file = null;
		if (isExternalStorageWritable()) {
			File externalStorageDir = Environment.getExternalStorageDirectory();
			File appDir = new File(externalStorageDir, BLACK_BOOKS_DIR);
			file = new File(appDir, fileName);

			file.getParentFile().mkdirs();
			try {
				file.createNewFile();
			} catch (IOException e) {
				Log.e(LogUtils.TAG, "Could not create file.", e);
			}
		}
		return file;
	}

	/**
	 * Return all the bytes contained in an InputStream.
	 * 
	 * @param stream
	 *            InputStream.
	 * @return Byte array.
	 * @throws IOException
	 *             If the stream could not be read.
	 */
	public static byte[] readBytes(InputStream stream) throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		int nRead;
		byte[] data = new byte[1024];

		while ((nRead = stream.read(data, 0, data.length)) != -1) {
			buffer.write(data, 0, nRead);
		}

		buffer.flush();

		return buffer.toByteArray();
	}

	/**
	 * Indicates whether the external storage is writable or not.
	 * 
	 * @return True if the external storage is writable, false otherwise.
	 */
	private static boolean isExternalStorageWritable() {
		String state = Environment.getExternalStorageState();
		boolean result = false;
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			result = true;
		}
		return result;
	}
}
