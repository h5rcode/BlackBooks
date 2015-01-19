package com.blackbooks.fragments.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.blackbooks.R;
import com.blackbooks.utils.Pic2ShopUtils;

/**
 * A DialogFragment used to redirect the user to a code scanner application if
 * there is none on the device.
 */
public class ScannerInstallFragment extends DialogFragment {

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		builder.setTitle(R.string.title_dialog_install_scanner);
		builder.setMessage(getString(R.string.message_scanner_install));

		builder.setPositiveButton(getString(R.string.button_go_to_pic2shop_page), new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Pic2ShopUtils.URI_MARKET));
				startActivity(marketIntent);
			}
		});

		builder.setNegativeButton(getString(R.string.button_cancel_scanner_install), new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// Do nothing.
			}
		});

		return builder.create();
	}
}
