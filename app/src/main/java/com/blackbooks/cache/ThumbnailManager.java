package com.blackbooks.cache;

import android.content.Context;
import android.widget.ImageView;
import android.widget.ProgressBar;

public interface ThumbnailManager {

    void drawSmallThumbnail(long bookId, Context context, ImageView imageView, ProgressBar progressBar);

    void removeThumbnails(long bookId);
}
