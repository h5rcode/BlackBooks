package com.blackbooks.adapters;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.blackbooks.R;
import com.blackbooks.cache.ThumbnailManager;
import com.blackbooks.model.nonpersistent.BookInfo;
import com.blackbooks.model.nonpersistent.LanguageInfo;
import com.blackbooks.utils.StringUtils;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

/**
 * An adapter handling instances of ListItem representing either the language of
 * a book or a book.
 */
public class BooksByLanguageAdapter extends ArrayAdapter<ListItem> implements SectionIndexer {

    private final LayoutInflater mInflater;
    private final ThumbnailManager mThumbnailManager;
    private final Map<String, Integer> mSectionPositionMap;
    private final SparseArray<String> mPositionSectionMap;
    private String[] mSections;

    /**
     * Constructor.
     *
     * @param context Context.
     */
    public BooksByLanguageAdapter(Context context) {
        super(context, 0);
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mThumbnailManager = ThumbnailManager.getInstance();
        this.mSectionPositionMap = new TreeMap<String, Integer>();
        this.mPositionSectionMap = new SparseArray<String>();
        this.mSections = new String[]{};
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        ListItem item = this.getItem(position);
        if (item != null) {
            ListItemType itemType = item.getListItemType();
            if (itemType == ListItemType.ENTRY) {
                BookItem entry = (BookItem) item;
                BookInfo book = entry.getBook();

                view = mInflater.inflate(R.layout.list_books_by_language_item_book, parent, false);

                ImageView imageView = (ImageView) view.findViewById(R.id.books_by_language_item_book_small_thumbnail);
                ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.books_by_language_item_book_progressBar);
                ImageView imageRead = (ImageView) view.findViewById(R.id.books_by_language_item_book_imageRead);
                ImageView imageFavourite = (ImageView) view.findViewById(R.id.books_by_language_item_book_imageFavourite);
                ImageView imageLoaned = (ImageView) view.findViewById(R.id.books_by_language_item_book_imageLoaned);

                mThumbnailManager.drawSmallThumbnail(book.id, getContext(), imageView, progressBar);

                TextView textTitle = (TextView) view.findViewById(R.id.books_by_language_item_book_title);
                textTitle.setText(book.title);

                TextView textAuthor = (TextView) view.findViewById(R.id.books_by_language_item_book_author);
                String authors;
                if (book.authors.size() > 0) {
                    authors = StringUtils.joinAuthorNameList(book.authors, ", ");
                } else {
                    authors = getContext().getString(R.string.label_unspecified_author);
                }
                textAuthor.setText(authors);

                if (book.isRead != 0) {
                    imageRead.setVisibility(View.VISIBLE);
                } else {
                    imageRead.setVisibility(View.GONE);
                }
                if (book.isFavourite != 0) {
                    imageFavourite.setVisibility(View.VISIBLE);
                } else {
                    imageFavourite.setVisibility(View.GONE);
                }
                if (book.loanedTo != null) {
                    imageLoaned.setVisibility(View.VISIBLE);
                } else {
                    imageLoaned.setVisibility(View.GONE);
                }
            } else if (itemType == ListItemType.HEADER) {
                LanguageItem header = (LanguageItem) item;
                LanguageInfo language = header.getLanguage();

                view = mInflater.inflate(R.layout.list_books_by_language_item_language, parent, false);

                TextView textLanguage = (TextView) view.findViewById(R.id.books_by_language_item_language);
                TextView textViewTotal = (TextView) view.findViewById(R.id.books_by_language_item_total);

                textLanguage.setText(language.displayName);
                String total = this.getContext().getString(R.string.label_total);
                total = String.format(total, language.books.size());
                textViewTotal.setText(total);
            }
        }
        return view;
    }

    @Override
    public void addAll(Collection<? extends ListItem> collection) {
        super.addAll(collection);
        mSectionPositionMap.clear();
        mPositionSectionMap.clear();

        int position = 0;
        String currentSection = null;
        for (ListItem listItem : collection) {
            if (listItem.getListItemType() == ListItemType.HEADER) {
                LanguageItem languageItem = (LanguageItem) listItem;
                String displayName = languageItem.getLanguage().displayName;
                currentSection = displayName.substring(0, 1);
                if (!mSectionPositionMap.containsKey(currentSection)) {
                    mSectionPositionMap.put(currentSection, position);
                }
            }
            mPositionSectionMap.put(position, currentSection);
            position++;
        }
        mSections = mSectionPositionMap.keySet().toArray(new String[mSectionPositionMap.size()]);
    }

    @Override
    public Object[] getSections() {
        return mSections;
    }

    @Override
    public int getPositionForSection(int section) {
        int index = section;
        if (index >= mSections.length) {
            index = mSections.length - 1;
        }
        return mSectionPositionMap.get(mSections[index]);
    }

    @Override
    public int getSectionForPosition(int position) {
        String currentSection = mPositionSectionMap.get(position);
        int sectionIndex = 0;

        for (int i = 0; i < mSections.length; i++) {
            String section = mSections[i];
            if (section.equals(currentSection)) {
                break;
            }
            sectionIndex++;
        }
        return sectionIndex;
    }

    public static final class LanguageItem implements ListItem {

        private final LanguageInfo mLanguage;

        public LanguageItem(LanguageInfo language) {
            mLanguage = language;
        }

        @Override
        public ListItemType getListItemType() {
            return ListItemType.HEADER;
        }

        public LanguageInfo getLanguage() {
            return mLanguage;
        }
    }
}
