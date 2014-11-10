package com.blackbooks.activities;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.MenuItem;

import com.blackbooks.R;
import com.blackbooks.fragments.BookEditFragment;
import com.blackbooks.fragments.BookEditFragment.BookEditListener;

/**
 * Activity used to add a new book or edit an existing one.
 */
public final class BookEdit extends FragmentActivity implements BookEditListener, OnPageChangeListener, TabListener {

	public static final String EXTRA_BOOK_ID = "EXTRA_BOOK_ID";
	public static final String EXTRA_MODE = "EXTRA_MODE";
	public static final String EXTRA_ISBN = "EXTRA_ISBN";

	public static final int MODE_ADD = 1;
	public static final int MODE_EDIT = 2;

	private BookEditPagerAdapter mPagerAdapter;
	private ViewPager mViewPager;

	private int mMode;
	private String mIsbn;
	private long mBookId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_book_edit);

		mPagerAdapter = new BookEditPagerAdapter(getSupportFragmentManager());
		mViewPager = (ViewPager) findViewById(R.id.bookEdit_viewPager);
		mViewPager.setAdapter(mPagerAdapter);
		mViewPager.setOnPageChangeListener(this);

		ActionBar actionBar = getActionBar();

		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.addTab(actionBar.newTab().setText(getString(R.string.title_tab_book_edit)).setTabListener(this));

		Intent intent = this.getIntent();

		mMode = intent.getIntExtra(EXTRA_MODE, 0);

		switch (mMode) {
		case MODE_ADD:
			if (intent.hasExtra(EXTRA_ISBN)) {
				mIsbn = intent.getStringExtra(EXTRA_ISBN);
			}
			break;

		case MODE_EDIT:
			if (intent.hasExtra(EXTRA_BOOK_ID)) {
				mBookId = intent.getLongExtra(EXTRA_BOOK_ID, 0);
			} else {
				throw new IllegalStateException("Extra " + EXTRA_BOOK_ID + " not set.");
			}
			break;

		default:
			throw new IllegalStateException("Extra " + EXTRA_MODE + " not set");
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean result;
		switch (item.getItemId()) {

		case android.R.id.home:
			finish();
			result = true;
			break;

		default:
			result = super.onOptionsItemSelected(item);
			break;
		}
		return result;
	}

	@Override
	public void onSaved() {
		switch (mMode) {
		case MODE_ADD:
			NavUtils.navigateUpFromSameTask(this);
			break;

		case MODE_EDIT:
			setResult(RESULT_OK);
			finish();
			break;

		default:
			throw new IllegalStateException("Invalid mode.");
		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int position) {
		getActionBar().setSelectedNavigationItem(position);
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
	}

	/**
	 * FragmentPagerAdapter used to define the different tabs of the activity.
	 */
	public class BookEditPagerAdapter extends FragmentPagerAdapter {

		public BookEditPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment = null;
			switch (position) {
			case 0:
				fragment = createBookEditFragment();
				break;

			default:
				throw new IllegalStateException();
			}
			return fragment;
		}

		@Override
		public int getCount() {
			return 1;
		}

		/**
		 * Return a new instance of BookEditFragment, depending on the current
		 * mode of the activity (add or edit).
		 * 
		 * @return BookEditFragment.
		 */
		private Fragment createBookEditFragment() {
			Fragment fragment = null;
			switch (mMode) {
			case MODE_ADD:
				fragment = BookEditFragment.newInstanceAddMode(mIsbn);
				break;

			case MODE_EDIT:
				fragment = BookEditFragment.newInstanceEditMode(mBookId);
				break;

			default:
				throw new IllegalStateException();
			}
			return fragment;
		}
	}
}
