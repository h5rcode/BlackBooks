package com.blackbooks.helpers;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.os.Environment;

/**
 * Helper class for file handling.
 * 
 */
public final class FileHelper {

	/**
	 * UTF-8 Byte Order Mark (BOM).
	 */
	public static final String UTF8_BOM = "\uFEFF";

	/**
	 * Private constructor.
	 */
	private FileHelper() {
	}

	/**
	 * Copy a file.
	 * 
	 * @param src
	 *            File to copy.
	 * @param dst
	 *            Destination of the copy.
	 * @throws IOException
	 *             IOException.
	 */
	public static void copy(File src, File dst) throws IOException {
		InputStream in = new FileInputStream(src);
		OutputStream out = new FileOutputStream(dst);

		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		in.close();
		out.close();
	}

	/**
	 * Indicates whether the external storage is writable or not.
	 * 
	 * @return True if the external storage is writable, false otherwise.
	 */
	public static boolean isExternalStorageWritable() {
		String state = Environment.getExternalStorageState();
		boolean result = false;
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			result = true;
		}
		return result;
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
}
