package com.blackbooks.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

/**
 * Start activity that displays the BookList activity corresponding to the
 * user's preferences.
 */
public class BlackBooksStart extends Activity {

	public final static String PREFERENCES = "PREFERENCES";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SharedPreferences preferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE);

		String defaultList = preferences.getString("defaultList", null);

		Intent intent;
		if (defaultList == null) {
			intent = new Intent(this, BookListByAuthor.class);
		} else if (defaultList.equals(BookListByAuthor.class.getName())) {
			intent = new Intent(this, BookListByAuthor.class);
		} else if (defaultList.equals(BookListByFirstLetter.class.getName())) {
			intent = new Intent(this, BookListByFirstLetter.class);
		} else {
			throw new IllegalStateException();
		}
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(intent);
		finish();
	}
}
