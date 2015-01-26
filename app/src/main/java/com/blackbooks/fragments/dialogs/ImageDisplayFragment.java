package com.blackbooks.fragments.dialogs;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.blackbooks.R;

/**
 * A fragment that displays an image.
 */
public class ImageDisplayFragment extends DialogFragment {

    private static final String ARG_IMAGE = "ARG_IMAGE";

    private byte[] mImage;

    /**
     * Returns a new instance of ImageDisplayFragment that is initialized to
     * display an image.
     *
     * @param image The image to display.
     * @return ImageDisplayFragment.
     */
    public static ImageDisplayFragment newInstance(byte[] image) {
        ImageDisplayFragment fragment = new ImageDisplayFragment();
        Bundle args = new Bundle();
        args.putByteArray(ARG_IMAGE, image);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImage = getArguments().getByteArray(ARG_IMAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_display, container);

        Window window = getDialog().getWindow();
        window.requestFeature(Window.FEATURE_NO_TITLE);

        ImageView imageView = (ImageView) view.findViewById(R.id.imageDisplay_image);
        Bitmap bitmap = BitmapFactory.decodeByteArray(mImage, 0, mImage.length);
        imageView.setImageBitmap(bitmap);
        imageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                ImageDisplayFragment.this.dismiss();
            }
        });
        return view;
    }
}
