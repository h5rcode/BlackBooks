package com.blackbooks.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class BookListLauncher extends Activity {

	public final static String PREFERENCES = "PREFERENCES";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SharedPreferences preferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE);

		String defaultList = preferences.getString("defaultList", null);

		Intent intent;
		if (defaultList == null) {
			intent = new Intent(this, BookListByFirstLetter.class);
		} else if (defaultList.equals(BookList.class.getName())) {
			intent = new Intent(this, BookList.class);
		} else if (defaultList.equals(BookListByFirstLetter.class.getName())) {
			intent = new Intent(this, BookListByFirstLetter.class);
		} else {
			throw new IllegalStateException();
		}
		
		startActivity(intent);
		finish();
	}
}
