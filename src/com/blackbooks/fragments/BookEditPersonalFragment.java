package com.blackbooks.fragments;

import com.blackbooks.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Fragment to edit the information concerning the owner of the book.
 * 
 */
public class BookEditPersonalFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_book_edit_personal, container, false);
	}
}
