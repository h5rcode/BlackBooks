package com.blackbooks.fragments;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.blackbooks.R;
import com.blackbooks.activities.BookAuthorsEditActivity;
import com.blackbooks.activities.BookCategoriesEditActivity;
import com.blackbooks.adapters.AutoCompleteAdapter;
import com.blackbooks.adapters.AutoCompleteAdapter.AutoCompleteSearcher;
import com.blackbooks.database.SQLiteHelper;
import com.blackbooks.fragments.dialogs.DatePickerFragment;
import com.blackbooks.fragments.dialogs.DatePickerFragment.DatePickerListener;
import com.blackbooks.fragments.dialogs.ImageDisplayFragment;
import com.blackbooks.fragments.dialogs.LanguagePickerFragment;
import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.model.nonpersistent.Language;
import com.blackbooks.model.persistent.Author;
import com.blackbooks.model.persistent.Category;
import com.blackbooks.model.persistent.Publisher;
import com.blackbooks.model.persistent.Series;
import com.blackbooks.services.AuthorServices;
import com.blackbooks.services.CategoryServices;
import com.blackbooks.services.PublisherServices;
import com.blackbooks.services.SeriesServices;
import com.blackbooks.utils.BitmapUtils;
import com.blackbooks.utils.DateUtils;
import com.blackbooks.utils.IsbnUtils;
import com.blackbooks.utils.LanguageUtils;
import com.blackbooks.utils.StringUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Fragment to edit the general information of a book.
 */
public final class BookEditGeneralFragment extends Fragment implements DatePickerListener, LanguagePickerFragment.LanguagePickerListener {

    private static final String ARG_BOOK = "ARG_BOOK";
    private static final String TAG_IMAGE_DISPLAY_FRAGMENT = "TAG_IMAGE_DISPLAY_FRAGMENT";
    private static final String TAG_DATE_PICKER_FRAGMENT = "TAG_DATE_PICKER_FRAGMENT";
    private static final String TAG_LANGUAGE_PICKER_FRAGMENT = "TAG_LANGUAGE_PICKER_FRAGMENT";

    private static final int ITEM_THUMBNAIL_REMOVE = 1;
    private static final int ITEM_ROTATE_LEFT = 2;
    private static final int ITEM_ROTATE_RIGHT = 3;
    private static final int ITEM_TAKE_PICTURE = 4;
    private static final int ITEM_PICK_IMAGE = 5;

    private static final int REQUEST_EDIT_AUTHORS = 1;
    private static final int REQUEST_EDIT_CATEGORIES = 2;
    private static final int REQUEST_PICK_IMAGE = 3;
    private static final int REQUEST_TAKE_PICTURE = 4;

    private ImageView mImageThumbnail;
    private EditText mTextTitle;
    private EditText mTextSubtitle;
    private Button mButtonEditLanguage;
    private Button mButtonEditAuthors;
    private EditText mTextIsbn10;
    private EditText mTextIsbn13;
    private EditText mTextPageCount;
    private AutoCompleteTextView mTextPublisher;
    private AutoCompleteTextView mTextSeries;
    private EditText mTextNumber;
    private EditText mTextPublishedDate;
    private ImageButton mButtonPublishedDate;
    private Button mButtonEditCategories;
    private EditText mTextDescription;

    private BookInfo mBookInfo;

    private boolean mValidBookInfo;

    /**
     * Create a new instance of BookEditGeneralFragment.
     *
     * @param bookInfo Book to edit.
     * @return BookEditGeneralFragment.
     */
    public static BookEditGeneralFragment newInstance(BookInfo bookInfo) {
        BookEditGeneralFragment instance = new BookEditGeneralFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_BOOK, bookInfo);
        instance.setArguments(args);
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        menu.setHeaderTitle(R.string.title_menu_edit_thumbnail);
        if (mBookInfo.thumbnail != null) {
            menu.add(Menu.NONE, ITEM_ROTATE_LEFT, Menu.NONE, R.string.action_rotate_left);
            menu.add(Menu.NONE, ITEM_ROTATE_RIGHT, Menu.NONE, R.string.action_rotate_right);
            menu.add(Menu.NONE, ITEM_THUMBNAIL_REMOVE, Menu.NONE, R.string.action_remove_thumbnail);
        }
        menu.add(Menu.NONE, ITEM_TAKE_PICTURE, Menu.NONE, R.string.action_take_picture);
        menu.add(Menu.NONE, ITEM_PICK_IMAGE, Menu.NONE, R.string.action_pick_image);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        boolean result;
        Intent intent;

        switch (item.getItemId()) {
            case ITEM_THUMBNAIL_REMOVE:
                mBookInfo.thumbnail = null;
                mBookInfo.smallThumbnail = null;
                setImageThumbnail();
                result = true;
                break;

            case ITEM_ROTATE_LEFT:
                mBookInfo.thumbnail = BitmapUtils.rotate90(mBookInfo.thumbnail, -1);
                mBookInfo.smallThumbnail = BitmapUtils.rotate90(mBookInfo.smallThumbnail, -1);
                setImageThumbnail();
                result = true;
                break;

            case ITEM_ROTATE_RIGHT:
                mBookInfo.thumbnail = BitmapUtils.rotate90(mBookInfo.thumbnail, 1);
                mBookInfo.smallThumbnail = BitmapUtils.rotate90(mBookInfo.smallThumbnail, 1);
                setImageThumbnail();
                result = true;
                break;

            case ITEM_TAKE_PICTURE:
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Activity activity = getActivity();
                if (intent.resolveActivity(activity.getPackageManager()) != null) {
                    startActivityForResult(intent, REQUEST_TAKE_PICTURE);
                }

                result = true;
                break;

            case ITEM_PICK_IMAGE:
                intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, REQUEST_PICK_IMAGE);
                result = true;
                break;

            default:
                result = super.onContextItemSelected(item);
                break;
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_EDIT_AUTHORS) {
                mBookInfo.authors = (ArrayList<Author>) data.getSerializableExtra(BookAuthorsEditActivity.EXTRA_AUTHOR_LIST);
                setButtonEditAuthorsText();
            } else if (requestCode == REQUEST_EDIT_CATEGORIES) {
                mBookInfo.categories = (ArrayList<Category>) data.getSerializableExtra(BookCategoriesEditActivity.EXTRA_CATEGORY_LIST);
                setButtonEditCategoriesText();
            } else if (requestCode == REQUEST_PICK_IMAGE || requestCode == REQUEST_TAKE_PICTURE) {
                Uri uri = data.getData();
                mBookInfo.smallThumbnail = BitmapUtils.compress(getActivity(), uri, 160, 160);
                mBookInfo.thumbnail = BitmapUtils.compress(getActivity(), uri, 500, 500);
                setImageThumbnail();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_book_edit_general, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        findViews();

        registerForContextMenu(mImageThumbnail);

        android.view.View.OnClickListener listener = new android.view.View.OnClickListener() {

            @Override
            public void onClick(View v) {
                byte[] image = mBookInfo.thumbnail;
                if (image != null && image.length > 0) {
                    FragmentManager fm = BookEditGeneralFragment.this.getFragmentManager();
                    ImageDisplayFragment fragment = ImageDisplayFragment.newInstance(image);
                    fragment.show(fm, TAG_IMAGE_DISPLAY_FRAGMENT);
                }
            }
        };
        mImageThumbnail.setOnClickListener(listener);

        mButtonEditAuthors.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                BookEditGeneralFragment.this.editAuthors();
            }
        });
        mButtonEditCategories.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                BookEditGeneralFragment.this.editCategories();

            }
        });
        mButtonEditLanguage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                LanguagePickerFragment languagePickerFragment = new LanguagePickerFragment();
                languagePickerFragment.setTargetFragment(BookEditGeneralFragment.this, 0);
                languagePickerFragment.show(getFragmentManager(), TAG_LANGUAGE_PICKER_FRAGMENT);
            }
        });

        AutoCompleteAdapter<Publisher> publisherAutoCompleteAdapter = new AutoCompleteAdapter<Publisher>(this.getActivity(),
                android.R.layout.simple_list_item_1, new AutoCompleteSearcher<Publisher>() {

            @Override
            public List<Publisher> search(CharSequence constraint) {
                SQLiteDatabase db = SQLiteHelper.getInstance().getReadableDatabase();
                return PublisherServices.getPublisherListByText(db, constraint.toString());
            }

            @Override
            public String getDisplayLabel(Publisher item) {
                return item.name;
            }
        });
        mTextPublisher.setAdapter(publisherAutoCompleteAdapter);

        mButtonPublishedDate.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                DatePickerFragment datePicker = new DatePickerFragment();
                datePicker.setTargetFragment(BookEditGeneralFragment.this, 0);
                datePicker.show(getFragmentManager(), TAG_DATE_PICKER_FRAGMENT);
            }
        });

        AutoCompleteAdapter<Series> seriesAutoCompleteAdapter = new AutoCompleteAdapter<Series>(this.getActivity(),
                android.R.layout.simple_list_item_1, new AutoCompleteSearcher<Series>() {

            @Override
            public List<Series> search(CharSequence constraint) {
                SQLiteDatabase db = SQLiteHelper.getInstance().getReadableDatabase();
                return SeriesServices.getSeriesListByText(db, constraint.toString());
            }

            @Override
            public String getDisplayLabel(Series item) {
                return item.name;
            }
        });
        mTextSeries.setAdapter(seriesAutoCompleteAdapter);

        handleArguments();
        renderBookInfo();
    }

    @Override
    public void onDateSet(Date date) {
        mTextPublishedDate.setError(null);
        mTextPublishedDate.setText(DateUtils.DEFAULT_DATE_FORMAT.format(date));
    }


    @Override
    public void onLanguagePicked(Language language) {
        mBookInfo.languageCode = language.getCode();
        setButtonEditLanguageText();
    }

    /**
     * Validate the user input and read the book info from the view.
     *
     * @param bookInfo BookInfo.
     * @return True if the book information is valid, false otherwise.
     */
    public boolean readBookInfo(BookInfo bookInfo) {
        mValidBookInfo = true;

        String title = getEditTextValue(mTextTitle, true);
        String subtitle = getEditTextValue(mTextSubtitle, false);
        String isbn10 = getEditTextValue(mTextIsbn10, false);
        if (isbn10 != null && !IsbnUtils.isValidIsbn10(isbn10)) {
            mTextIsbn10.setError(getString(R.string.message_isbn_search_invalid_isbn10));
            if (mValidBookInfo) {
                mValidBookInfo = false;
                mTextIsbn10.requestFocus();
            }
        }
        String isbn13 = getEditTextValue(mTextIsbn13, false);
        if (isbn13 != null && !IsbnUtils.isValidIsbn13(isbn13)) {
            mTextIsbn13.setError(getString(R.string.message_isbn_search_invalid_isbn13));
            if (mValidBookInfo) {
                mValidBookInfo = false;
                mTextIsbn13.requestFocus();
            }
        }
        String pageCountString = getEditTextValue(mTextPageCount, false);
        String publisherName = getEditTextValue(mTextPublisher, false);
        String publishedDate = getEditTextValue(mTextPublishedDate, false);
        String description = getEditTextValue(mTextDescription, false);
        String seriesName = getEditTextValue(mTextSeries, false);
        String numberString = getEditTextValue(mTextNumber, false);

        bookInfo.title = title;
        bookInfo.subtitle = subtitle;
        bookInfo.isbn10 = isbn10;
        bookInfo.isbn13 = isbn13;
        if (pageCountString != null && StringUtils.isInteger(pageCountString)) {
            bookInfo.pageCount = Long.valueOf(pageCountString);
        } else {
            bookInfo.pageCount = null;
        }
        if (publishedDate != null) {
            try {
                bookInfo.publishedDate = DateUtils.DEFAULT_DATE_FORMAT.parse(publishedDate);
            } catch (ParseException e) {
                mTextPublishedDate.setError(getString(R.string.field_invalid_date));
            }
        } else {
            bookInfo.publishedDate = null;
        }
        if (numberString != null && StringUtils.isInteger(numberString)) {
            bookInfo.number = Long.valueOf(numberString);
        } else {
            bookInfo.number = null;
        }
        bookInfo.description = description;

        SQLiteDatabase db = SQLiteHelper.getInstance().getReadableDatabase();

        List<Author> authors = new ArrayList<Author>();
        for (Author author : bookInfo.authors) {
            Author authorDb = AuthorServices.getAuthorByCriteria(db, author);
            if (authorDb != null) {
                authors.add(authorDb);
            } else {
                authors.add(author);
            }
        }
        bookInfo.authors = authors;

        Publisher publisher = new Publisher();
        if (publisherName != null) {
            publisher.name = publisherName;

            Publisher publisherDb = PublisherServices.getPublisherByCriteria(db, publisher);
            if (publisherDb != null) {
                publisher = publisherDb;
            }
        }
        bookInfo.publisher = publisher;

        Series series = new Series();
        if (seriesName != null) {
            series.name = seriesName;

            Series seriesDb = SeriesServices.getSeriesByCriteria(db, series);
            if (seriesDb != null) {
                series = seriesDb;
            }
        }
        bookInfo.series = series;

        List<Category> categories = new ArrayList<Category>();
        for (Category category : bookInfo.categories) {
            Category categoryDb = CategoryServices.getCategoryByCriteria(db, category);
            if (categoryDb != null) {
                categories.add(categoryDb);
            } else {
                categories.add(category);
            }
        }
        bookInfo.categories = categories;

        return mValidBookInfo;
    }

    /**
     * Start the activity to edit the list of authors.
     */
    private void editAuthors() {
        Intent intent = new Intent(this.getActivity(), BookAuthorsEditActivity.class);
        intent.putExtra(BookAuthorsEditActivity.EXTRA_BOOK_TITLE, mTextTitle.getText().toString());
        intent.putExtra(BookAuthorsEditActivity.EXTRA_AUTHOR_LIST, (ArrayList<Author>) mBookInfo.authors);
        startActivityForResult(intent, REQUEST_EDIT_AUTHORS);
    }

    /**
     * Start the activity to edit the list of categories.
     */
    private void editCategories() {
        Intent intent = new Intent(this.getActivity(), BookCategoriesEditActivity.class);
        intent.putExtra(BookCategoriesEditActivity.EXTRA_BOOK_TITLE, mTextTitle.getText().toString());
        intent.putExtra(BookCategoriesEditActivity.EXTRA_CATEGORY_LIST, (ArrayList<Category>) mBookInfo.categories);
        startActivityForResult(intent, REQUEST_EDIT_CATEGORIES);
    }

    /**
     * Find the views of the activity that will contain the book information.
     */
    private void findViews() {
        View view = getView();
        mImageThumbnail = (ImageView) view.findViewById(R.id.bookEditGeneral_buttonThumbnail);
        mTextTitle = (EditText) view.findViewById(R.id.bookEditGeneral_textTitle);
        mTextSubtitle = (EditText) view.findViewById(R.id.bookEditGeneral_textSubtitle);
        mButtonEditLanguage = (Button) view.findViewById(R.id.bookEditGeneral_buttonEditLanguage);
        mButtonEditAuthors = (Button) view.findViewById(R.id.bookEditGeneral_buttonEditAuthors);
        mTextIsbn10 = (EditText) view.findViewById(R.id.bookEditGeneral_textIsbn10);
        mTextIsbn13 = (EditText) view.findViewById(R.id.bookEditGeneral_textIsbn13);
        mTextPageCount = (EditText) view.findViewById(R.id.bookEditGeneral_textPageCount);
        mTextPublisher = (AutoCompleteTextView) view.findViewById(R.id.bookEditGeneral_textPublisher);
        mTextPublishedDate = (EditText) view.findViewById(R.id.bookEditGeneral_textPublishedDate);
        mButtonPublishedDate = (ImageButton) view.findViewById(R.id.bookEditGeneral_buttonPickPublishedDate);
        mTextSeries = (AutoCompleteTextView) view.findViewById(R.id.bookEditGeneral_textSeries);
        mTextNumber = (EditText) view.findViewById(R.id.bookEditGeneral_textNumber);
        mButtonEditCategories = (Button) view.findViewById(R.id.bookEditGeneral_buttonEditCategories);
        mTextDescription = (EditText) view.findViewById(R.id.bookEditGeneral_textDescription);
    }

    /**
     * Get the value from an EditText.
     *
     * @param editText  EditText.
     * @param mandatory True if the the field is mandatory.
     * @return The text value from the field.
     */
    private String getEditTextValue(EditText editText, boolean mandatory) {
        String text = editText.getText().toString().trim();

        if (text.length() == 0) {
            text = null;
            if (mandatory) {
                editText.setError(getString(R.string.field_mandatory));
                if (mValidBookInfo) {
                    mValidBookInfo = false;
                    editText.requestFocus();
                }
            }
        } else if (editText.getError() != null) {
            editText.setError(null);
        }
        return text;
    }

    /**
     * Handle the arguments of the fragment.
     */
    private void handleArguments() {
        Bundle args = getArguments();
        mBookInfo = (BookInfo) args.getSerializable(ARG_BOOK);
    }

    /**
     * Update the views of the activity using the book information.
     */
    private void renderBookInfo() {
        setImageThumbnail();
        mTextTitle.setText(mBookInfo.title);
        mTextSubtitle.setText(mBookInfo.subtitle);
        mTextIsbn10.setText(mBookInfo.isbn10);
        mTextIsbn13.setText(mBookInfo.isbn13);
        setButtonEditLanguageText();
        setButtonEditAuthorsText();
        if (mBookInfo.pageCount != null) {
            mTextPageCount.setText(String.format(Locale.getDefault(), "%d", mBookInfo.pageCount));
        }
        mTextPublisher.setText(mBookInfo.publisher.name);
        if (mBookInfo.publishedDate != null) {
            mTextPublishedDate.setText(DateUtils.DEFAULT_DATE_FORMAT.format(mBookInfo.publishedDate));
        }
        mTextSeries.setText(mBookInfo.series.name);
        if (mBookInfo.number != null) {
            mTextNumber.setText(String.format(Locale.getDefault(), "%d", mBookInfo.number));
        }
        setButtonEditCategoriesText();
        mTextDescription.setText(mBookInfo.description);
    }

    /**
     * Set the text of the "Edit author(s)" button.
     */
    private void setButtonEditAuthorsText() {
        if (mBookInfo.authors == null || mBookInfo.authors.size() == 0) {
            mButtonEditAuthors.setText(getString(R.string.button_edit_authors));
        } else {
            String authors = StringUtils.joinAuthorNameList(mBookInfo.authors, ", ");
            mButtonEditAuthors.setText(authors);
        }
    }

    /**
     * Set the text of the "Edit categories" button.
     */
    private void setButtonEditCategoriesText() {
        if (mBookInfo.categories == null || mBookInfo.categories.size() == 0) {
            mButtonEditCategories.setText(getString(R.string.button_edit_categories));
        } else {
            String categories = StringUtils.joinCategoryNameList(mBookInfo.categories, ", ");
            mButtonEditCategories.setText(categories);
        }
    }

    /**
     * Set the text of the "Edit language" button.
     */
    private void setButtonEditLanguageText() {
        String languageCode = mBookInfo.languageCode;
        if (languageCode == null) {
            mButtonEditLanguage.setText(R.string.label_no_language);
        } else {
            String displayLanguage = LanguageUtils.getDisplayLanguage(languageCode);
            displayLanguage = StringUtils.capitalize(displayLanguage);
            mButtonEditLanguage.setText(displayLanguage);
        }
    }

    /**
     * Displays the thumbnail if there is one.
     */
    private void setImageThumbnail() {
        Bitmap bitmap;
        byte[] thumbnail = mBookInfo.thumbnail;
        if (thumbnail != null && thumbnail.length > 0) {
            bitmap = BitmapFactory.decodeByteArray(thumbnail, 0, thumbnail.length);
            bitmap = BitmapUtils.resizeThumbnailBitmap(getActivity(), bitmap);
        } else {
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_undefined_thumbnail);
        }
        mImageThumbnail.setImageBitmap(bitmap);
    }
}
