package com.blackbooks.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blackbooks.R;
import com.blackbooks.activities.BookEditActivity;
import com.blackbooks.database.SQLiteHelper;
import com.blackbooks.fragments.dialogs.ImageDisplayFragment;
import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.services.BookServices;
import com.blackbooks.utils.BitmapUtils;
import com.blackbooks.utils.DateUtils;
import com.blackbooks.utils.StringUtils;
import com.blackbooks.utils.VariableUtils;

import java.util.Locale;

/**
 * Fragment to display the information of a book.
 */
public class BookDisplayDetailFragment extends Fragment {

    private static final String ARG_BOOK_ID = "ARG_BOOK_ID";
    private static final String IMAGE_DISPLAY_FRAGMENT_TAG = "IMAGE_DISPLAY_FRAGMENT_TAG";

    private static final int REQUEST_CODE_EDIT_BOOK = 1;

    private BookInfo mBookInfo;

    private ImageView mImageCover;
    private ImageView mImageRead;
    private ImageView mImageFavourite;
    private TextView mTextTitle;
    private TextView mTextSeries;
    private TextView mTextSubtitle;
    private TextView mTextAuthor;
    private TextView mTextIsbn10;
    private TextView mTextIsbn13;
    private TextView mTextPageCount;
    private TextView mTextPublisher;
    private TextView mTextPublishedDate;
    private TextView mTextCategory;
    private TextView mTextLanguage;
    private TextView mTextDescription;
    private TextView mTextComment;

    private LinearLayout mGroupInfo;
    private LinearLayout mGroupDescription;
    private LinearLayout mGroupComment;
    private LinearLayout mGroupIsbn;

    private LinearLayout mGroup1PageCount;
    private LinearLayout mGroupCategories;
    private LinearLayout mGroup1Language;
    private LinearLayout mGroupIsbnIsbn10;
    private LinearLayout mGroupIsbnIsbn13;
    private LinearLayout mGroupPublisherPublisher;
    private LinearLayout mGroupPublisherPublishedDate;

    private BookDisplayListener mBookDisplayListener;

    /**
     * Create a new instance of BookDisplayFragment, initialized to display a
     * book.
     *
     * @param bookId Id of the book.
     * @return BookDisplayFragment.
     */
    public static BookDisplayDetailFragment newInstance(long bookId) {
        BookDisplayDetailFragment instance = new BookDisplayDetailFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_BOOK_ID, bookId);
        instance.setArguments(args);
        return instance;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof BookDisplayListener) {
            mBookDisplayListener = (BookDisplayListener) activity;
        }
        Bundle args = getArguments();
        long bookId = args.getLong(ARG_BOOK_ID);
        loadBookInfo(bookId);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_display_detail, container, false);
        findViews(view);

        android.view.View.OnClickListener listener = new android.view.View.OnClickListener() {

            @Override
            public void onClick(View v) {
                byte[] image = mBookInfo.thumbnail;
                if (image != null && image.length > 0) {
                    FragmentManager fm = BookDisplayDetailFragment.this.getFragmentManager();
                    ImageDisplayFragment fragment = ImageDisplayFragment.newInstance(image);
                    fragment.show(fm, IMAGE_DISPLAY_FRAGMENT_TAG);
                }
            }
        };
        mImageCover.setOnClickListener(listener);

        renderBookInfo();
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_EDIT_BOOK) {
                loadBookInfo(mBookInfo.id);
                renderBookInfo();
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.book_display, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean result;
        switch (item.getItemId()) {
            case R.id.bookDisplay_actionEdit:
                Intent i = new Intent(this.getActivity(), BookEditActivity.class);
                i.putExtra(BookEditActivity.EXTRA_MODE, BookEditActivity.MODE_EDIT);
                i.putExtra(BookEditActivity.EXTRA_BOOK_ID, mBookInfo.id);
                startActivityForResult(i, REQUEST_CODE_EDIT_BOOK);
                result = true;
                break;

            case R.id.bookDisplay_actionDelete:
                showDeleteConfirmDialog();
                result = true;
                break;
            default:
                result = super.onOptionsItemSelected(item);
                break;
        }
        return result;
    }

    /**
     * Delete the current book.
     */
    private void deleteBook() {
        String title = mBookInfo.title;
        String message = String.format(getString(R.string.message_book_deleted), title);

        SQLiteDatabase db = SQLiteHelper.getInstance().getWritableDatabase();
        BookServices.deleteBook(db, mBookInfo.id);
        VariableUtils.getInstance().setReloadBookList(true);
        Toast.makeText(this.getActivity(), message, Toast.LENGTH_SHORT).show();

        if (mBookDisplayListener != null) {
            mBookDisplayListener.onBookDeleted();
        }
    }

    /**
     * Find the views of the activity that will contain the book information.
     *
     * @param view View.
     */
    private void findViews(View view) {
        mImageCover = (ImageView) view.findViewById(R.id.bookDisplayDetail_imageCover);
        mImageRead = (ImageView) view.findViewById(R.id.bookDisplayDetail_imageRead);
        mImageFavourite = (ImageView) view.findViewById(R.id.bookDisplayDetail_imageFavourite);
        mTextTitle = (TextView) view.findViewById(R.id.bookDisplayDetail_textTitle);
        mTextSeries = (TextView) view.findViewById(R.id.bookDisplayDetail_textSeries);
        mTextSubtitle = (TextView) view.findViewById(R.id.bookDisplayDetail_textSubtitle);
        mTextAuthor = (TextView) view.findViewById(R.id.bookDisplayDetail_textAuthor);
        mTextIsbn10 = (TextView) view.findViewById(R.id.bookDisplayDetail_textIsbn10);
        mTextIsbn13 = (TextView) view.findViewById(R.id.bookDisplayDetail_textIsbn13);
        mTextPageCount = (TextView) view.findViewById(R.id.bookDisplayDetail_textPageCount);
        mTextPublisher = (TextView) view.findViewById(R.id.bookDisplayDetail_textPublisher);
        mTextPublishedDate = (TextView) view.findViewById(R.id.bookDisplayDetail_textPublishedDate);
        mTextCategory = (TextView) view.findViewById(R.id.bookDisplayDetail_textCategory);
        mTextLanguage = (TextView) view.findViewById(R.id.bookDisplayDetail_textLanguage);
        mTextLanguage = (TextView) view.findViewById(R.id.bookDisplayDetail_textLanguage);
        mTextDescription = (TextView) view.findViewById(R.id.bookDisplayDetail_textDescription);
        mTextComment = (TextView) view.findViewById(R.id.bookDisplayDetail_textComment);

        mGroupInfo = (LinearLayout) view.findViewById(R.id.bookDisplayDetail_groupInfo);
        mGroupCategories = (LinearLayout) view.findViewById(R.id.bookDisplayDetail_groupCategories);
        mGroupDescription = (LinearLayout) view.findViewById(R.id.bookDisplayDetail_groupDescription);
        mGroupComment = (LinearLayout) view.findViewById(R.id.bookDisplayDetail_groupComment);
        mGroupIsbn = (LinearLayout) view.findViewById(R.id.bookDisplayDetail_groupIsbn);

        mGroup1PageCount = (LinearLayout) view.findViewById(R.id.bookDisplayDetail_groupInfo_pageCount);
        mGroup1Language = (LinearLayout) view.findViewById(R.id.bookDisplayDetail_groupInfo_language);
        mGroupPublisherPublisher = (LinearLayout) view.findViewById(R.id.bookDisplayDetail_groupInfo_publisher);
        mGroupPublisherPublishedDate = (LinearLayout) view.findViewById(R.id.bookDisplayDetail_groupInfo_publishedDate);
        mGroupIsbnIsbn10 = (LinearLayout) view.findViewById(R.id.bookDisplayDetail_groupInfoIsbn_isbn10);
        mGroupIsbnIsbn13 = (LinearLayout) view.findViewById(R.id.bookDisplayDetail_groupInfoIsbn_isbn13);
    }

    /**
     * Load the book from the database.
     *
     * @param bookId Id of a book.
     */
    private void loadBookInfo(long bookId) {
        SQLiteDatabase db = SQLiteHelper.getInstance().getReadableDatabase();
        mBookInfo = BookServices.getBookInfo(db, bookId);

        if (mBookDisplayListener != null) {
            mBookDisplayListener.onBookLoaded(mBookInfo);
        }
    }

    /**
     * Update the views of the activity using the book information.
     */
    private void renderBookInfo() {
        boolean hasPageCount = mBookInfo.pageCount != null;
        boolean hasCategories = mBookInfo.categories.size() > 0;
        boolean hasLanguage = mBookInfo.languageCode != null;
        boolean hasIsbn10 = mBookInfo.isbn10 != null;
        boolean hasIsbn13 = mBookInfo.isbn13 != null;
        boolean hasPublisher = mBookInfo.publisher.name != null;
        boolean hasPublishedDate = mBookInfo.publishedDate != null;

        boolean showGroupInfo = hasPageCount || hasLanguage || hasPublisher || hasPublishedDate;
        boolean showGroupIsbn = hasIsbn10 || hasIsbn13;

        // Title.
        mTextTitle.setText(mBookInfo.title);

        // Series.
        String series = null;
        if (mBookInfo.series.name != null) {
            series = mBookInfo.series.name;
        }
        if (mBookInfo.number != null) {
            String number = String.format(getString(R.string.label_series_number_format), mBookInfo.number);
            if (series == null) {
                series = number;
            } else {
                series += " " + number;
            }
        }
        if (series == null) {
            mTextSeries.setVisibility(View.GONE);
        } else {
            mTextSeries.setText(series);
            mTextSeries.setVisibility(View.VISIBLE);
        }

        // "Read" icon.
        if (mBookInfo.isRead == 0) {
            mImageRead.setVisibility(View.GONE);
        } else {
            mImageRead.setVisibility(View.VISIBLE);
        }

        // "Favourite" icon.
        if (mBookInfo.isFavourite == 0) {
            mImageFavourite.setVisibility(View.GONE);
        } else {
            mImageFavourite.setVisibility(View.VISIBLE);
        }

        // Thumbnail.
        if (mBookInfo.thumbnail != null && mBookInfo.thumbnail.length > 0) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(mBookInfo.thumbnail, 0, mBookInfo.thumbnail.length);
            bitmap = BitmapUtils.resizeThumbnailBitmap(getActivity(), bitmap);
            mImageCover.setImageBitmap(bitmap);
            mImageCover.setVisibility(View.VISIBLE);
        } else {
            mImageCover.setVisibility(View.GONE);
        }

        // Subtitle.
        if (mBookInfo.subtitle != null) {
            mTextSubtitle.setText(mBookInfo.subtitle);
            mTextSubtitle.setVisibility(View.VISIBLE);
        } else {
            mTextSubtitle.setVisibility(View.GONE);
        }

        // Authors.
        if (mBookInfo.authors.size() > 0) {
            String authorsFormat = getString(R.string.label_authors_format);
            String authors = String.format(authorsFormat, StringUtils.joinAuthorNameList(mBookInfo.authors, ", "));
            mTextAuthor.setText(authors);
        } else {
            mTextAuthor.setText(getString(R.string.label_unspecified_author));
        }

        // Page count, language, publisher and published date.
        if (showGroupInfo) {
            if (hasPageCount) {
                String pageCount = getString(R.string.label_page_count_format);
                pageCount = String.format(pageCount, mBookInfo.pageCount);
                mTextPageCount.setText(pageCount);
                mGroup1PageCount.setVisibility(View.VISIBLE);
            } else {
                mGroup1PageCount.setVisibility(View.GONE);
            }
            if (hasLanguage) {
                Locale locale = new Locale(mBookInfo.languageCode);
                String language = locale.getDisplayLanguage();
                mTextLanguage.setText(StringUtils.capitalize(language));
                mGroup1Language.setVisibility(View.VISIBLE);
            } else {
                mGroup1Language.setVisibility(View.GONE);
            }
            if (hasPublisher) {
                mTextPublisher.setText(mBookInfo.publisher.name);
                mGroupPublisherPublisher.setVisibility(View.VISIBLE);
            } else {
                mGroupPublisherPublisher.setVisibility(View.GONE);
            }
            if (hasPublishedDate) {
                mTextPublishedDate.setText(DateUtils.DEFAULT_DATE_FORMAT.format(mBookInfo.publishedDate));
                mGroupPublisherPublishedDate.setVisibility(View.VISIBLE);
            } else {
                mGroupPublisherPublishedDate.setVisibility(View.GONE);
            }
            mGroupInfo.setVisibility(View.VISIBLE);
        } else {
            mGroupInfo.setVisibility(View.GONE);
        }

        // Categories.
        if (hasCategories) {
            mTextCategory.setText(StringUtils.joinCategoryNameList(mBookInfo.categories, ", "));
            mGroupCategories.setVisibility(View.VISIBLE);
        } else {
            mGroupCategories.setVisibility(View.GONE);
        }

        // Description.
        if (mBookInfo.description != null) {
            mTextDescription.setText(mBookInfo.description);
            mGroupDescription.setVisibility(View.VISIBLE);

        } else {
            mGroupDescription.setVisibility(View.GONE);
        }

        // Comment.
        if (mBookInfo.comment != null && !mBookInfo.comment.isEmpty()) {
            mTextComment.setText(mBookInfo.comment);
            mGroupComment.setVisibility(View.VISIBLE);
        } else {
            mGroupComment.setVisibility(View.GONE);
        }

        // ISBN-10 and ISBN-13.
        if (showGroupIsbn) {
            if (hasIsbn10) {
                mTextIsbn10.setText(mBookInfo.isbn10);
                mGroupIsbnIsbn10.setVisibility(View.VISIBLE);
            } else {
                mGroupIsbnIsbn10.setVisibility(View.INVISIBLE);
            }
            if (hasIsbn13) {
                mTextIsbn13.setText(mBookInfo.isbn13);
                mGroupIsbnIsbn13.setVisibility(View.VISIBLE);
            } else {
                mGroupIsbnIsbn13.setVisibility(View.INVISIBLE);
            }
            mGroupIsbn.setVisibility(View.VISIBLE);
        } else {
            mGroupIsbn.setVisibility(View.GONE);
        }
    }

    /**
     * Show the delete confirm dialog.
     */
    private void showDeleteConfirmDialog() {
        String message = getString(R.string.message_confirm_delete_book);
        message = String.format(message, mBookInfo.title);

        new AlertDialog.Builder(this.getActivity()) //
                .setTitle(R.string.title_dialog_delete_book) //
                .setMessage(message) //
                .setPositiveButton(R.string.message_confirm_delete_book_confirm, new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        BookDisplayDetailFragment.this.deleteBook();
                    }
                }) //
                .setNegativeButton(R.string.message_confirm_delete_book_cancel, new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing.
                    }
                }).show();
    }

    /**
     * An activity hosting a {@link BookDisplayDetailFragment} should implement this
     * interface to be notified when the book is loaded, deleted.
     */
    public interface BookDisplayListener {

        /**
         * Called when the book is loaded.
         *
         * @param bookInfo BookInfo.
         */
        void onBookLoaded(BookInfo bookInfo);

        /**
         * Called when the book is deleted.
         */
        void onBookDeleted();
    }
}
