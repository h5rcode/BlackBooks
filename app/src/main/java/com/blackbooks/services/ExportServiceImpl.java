package com.blackbooks.services;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.blackbooks.database.SQLiteHelper;
import com.blackbooks.model.nonpersistent.BookExport;
import com.blackbooks.model.persistent.Author;
import com.blackbooks.model.persistent.Book;
import com.blackbooks.model.persistent.BookAuthor;
import com.blackbooks.model.persistent.BookCategory;
import com.blackbooks.model.persistent.Category;
import com.blackbooks.model.persistent.Publisher;
import com.blackbooks.model.persistent.Series;

import java.util.ArrayList;
import java.util.List;

/**
 * Export services.
 */
public final class ExportServiceImpl implements ExportService {

    private final SQLiteHelper sqLiteHelper;

    public ExportServiceImpl(SQLiteHelper sqLiteHelper) {
        this.sqLiteHelper = sqLiteHelper;
    }

    /**
     * Return a preview of the book export as a CSV file. The preview contains
     * the five first books.
     *
     * @param textQualifier           Text qualifier.
     * @param columnSeparator         Column separator.
     * @param firstRowContainsHeaders True if the first row should contain the column headers.
     * @return Export preview.
     */
    public String previewBookExport(char textQualifier, char columnSeparator,
                                    boolean firstRowContainsHeaders) {

        List<BookExport> bookExportList = getBookExportList(5);
        return preview(bookExportList, textQualifier, columnSeparator, firstRowContainsHeaders);
    }

    /**
     * Get the list of objects
     *
     * @param limit The number of books to export. Null to export all books.
     * @return List of BookExport.
     */
    public List<BookExport> getBookExportList(Integer limit) {
        StringBuilder sb = new StringBuilder();

        String id = "Id";
        String title = "Title";
        String subtitle = "Subtitle";
        String authors = "Authors";
        String categories = "Categories";
        String series = "Series";
        String number = "Number";
        String pageCount = "PageCount";
        String languageCode = "LanguageCode";
        String description = "Description";
        String publisher = "Publisher";
        String publishedDate = "PublishedDate";
        String isbn10 = "Isbn10";
        String isbn13 = "Isbn13";

        sb.append("SELECT\n");
        sb.append("boo." + Book.Cols.BOO_ID + " AS " + id + ",\n");
        sb.append("boo." + Book.Cols.BOO_TITLE + " AS " + title + ",\n");
        sb.append("boo." + Book.Cols.BOO_SUBTITLE + " AS " + subtitle + ",\n");
        sb.append("aut.Authors AS " + authors + ",\n");
        sb.append("cat.Categories AS " + categories + ",\n");
        sb.append("ser." + Series.Cols.SER_NAME + " AS " + series + ",\n");
        sb.append("boo." + Book.Cols.BOO_NUMBER + " AS " + number + ",\n");
        sb.append("boo." + Book.Cols.BOO_PAGE_COUNT + " AS " + pageCount + ",\n");
        sb.append("boo." + Book.Cols.BOO_LANGUAGE_CODE + " AS " + languageCode + ",\n");
        sb.append("boo." + Book.Cols.BOO_DESCRIPTION + " AS " + description + ",\n");
        sb.append("pub." + Publisher.Cols.PUB_NAME + " AS " + publisher + ",\n");
        sb.append("strftime(\"%d/%m/%Y\", boo." + Book.Cols.BOO_PUBLISHED_DATE + " / 1000, \'unixepoch\', \'localtime\') AS " + publishedDate + ",\n");
        sb.append("boo." + Book.Cols.BOO_ISBN_10 + " AS " + isbn10 + ",\n");
        sb.append("boo." + Book.Cols.BOO_ISBN_13 + " AS " + isbn13 + "\n");
        sb.append("FROM\n");
        sb.append(Book.NAME + " boo\n");
        sb.append("LEFT JOIN (\n");
        sb.append("SELECT\n");
        sb.append("ba." + BookAuthor.Cols.BOO_ID + ",\n");
        sb.append("group_concat(aut." + Author.Cols.AUT_NAME + ") AS Authors\n");
        sb.append("FROM\n");
        sb.append(BookAuthor.NAME + " ba\n");
        sb.append("JOIN " + Author.NAME + " aut ON aut." + Author.Cols.AUT_ID + " = ba." + BookAuthor.Cols.AUT_ID + "\n");
        sb.append("GROUP BY\n");
        sb.append("ba." + BookAuthor.Cols.BOO_ID + "\n");
        sb.append(") AS aut ON aut.BOO_ID = boo." + Book.Cols.BOO_ID + "\n");
        sb.append("LEFT JOIN (\n");
        sb.append("SELECT\n");
        sb.append("bc." + BookCategory.Cols.BOO_ID + ",\n");
        sb.append("group_concat(cat." + Category.Cols.CAT_NAME + ") AS Categories\n");
        sb.append("FROM\n");
        sb.append(BookCategory.NAME + " bc\n");
        sb.append("JOIN " + Category.NAME + " cat ON cat." + Category.Cols.CAT_ID + " = bc." + BookCategory.Cols.CAT_ID + "\n");
        sb.append("GROUP BY\n");
        sb.append("bc." + BookCategory.Cols.BOO_ID + "\n");
        sb.append(") AS cat ON cat.BOO_ID = boo." + Book.Cols.BOO_ID + "\n");
        sb.append("LEFT JOIN " + Series.NAME + " ser ON ser." + Series.Cols.SER_ID + " = boo." + Book.Cols.SER_ID + "\n");
        sb.append("LEFT JOIN " + Publisher.NAME + " pub ON pub." + Publisher.Cols.PUB_ID + " = boo." + Book.Cols.PUB_ID + "\n");
        sb.append("GROUP BY\n");
        sb.append("boo." + Book.Cols.BOO_ID + ",\n");
        sb.append("boo." + Book.Cols.BOO_TITLE + ",\n");
        sb.append("boo." + Book.Cols.BOO_DESCRIPTION + "\n");

        if (limit != null) {
            sb.append("LIMIT " + limit);
        }

        sb.append(";");

        String sql = sb.toString();

        SQLiteDatabase db = sqLiteHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);

        int idxId = cursor.getColumnIndex(id);
        int idxTitle = cursor.getColumnIndex(title);
        int idxSubTitle = cursor.getColumnIndex(subtitle);
        int idxAuthors = cursor.getColumnIndex(authors);
        int idxCategories = cursor.getColumnIndex(categories);
        int idxSeries = cursor.getColumnIndex(series);
        int idxNumber = cursor.getColumnIndex(number);
        int idxPageCount = cursor.getColumnIndex(pageCount);
        int idxLanguageCode = cursor.getColumnIndex(languageCode);
        int idxDescription = cursor.getColumnIndex(description);
        int idxPublisher = cursor.getColumnIndex(publisher);
        int idxPublishedDate = cursor.getColumnIndex(publishedDate);
        int idxIsbn10 = cursor.getColumnIndex(isbn10);
        int idxIsbn13 = cursor.getColumnIndex(isbn13);

        List<BookExport> bookExportList = new ArrayList<>();
        while (cursor.moveToNext()) {
            BookExport bookExport = new BookExport();
            bookExport.id = cursor.getLong(idxId);
            bookExport.title = cursor.getString(idxTitle);
            bookExport.subtitle = getString(cursor, idxSubTitle);
            bookExport.authors = getString(cursor, idxAuthors);
            bookExport.categories = getString(cursor, idxCategories);
            bookExport.series = getString(cursor, idxSeries);
            bookExport.number = getLong(cursor, idxNumber);
            bookExport.pageCount = getLong(cursor, idxPageCount);
            bookExport.languageCode = getString(cursor, idxLanguageCode);
            bookExport.description = getString(cursor, idxDescription);
            bookExport.publisher = getString(cursor, idxPublisher);
            bookExport.publishedDate = getString(cursor, idxPublishedDate);
            bookExport.isbn10 = getString(cursor, idxIsbn10);
            bookExport.isbn13 = getString(cursor, idxIsbn13);

            bookExportList.add(bookExport);
        }

        return bookExportList;
    }

    /**
     * Returns the value of the requested column as a long.
     *
     * @param cursor Cursor.
     * @param index  Index of the column.
     * @return Long value or null.
     */
    private Long getLong(Cursor cursor, int index) {
        return cursor.isNull(index) ? null : cursor.getLong(index);
    }

    /**
     * Returns the value of the requested column as a String.
     *
     * @param cursor Cursor.
     * @param index  Index of the column.
     * @return String value or null.
     */
    private String getString(Cursor cursor, int index) {
        return cursor.isNull(index) ? null : cursor.getString(index);
    }

    /**
     * Build the preview string of the CSV export.
     *
     * @param bookExportList         List of BookExport.
     * @param qualifier              The text qualifier.
     * @param separator              The column separator.
     * @param firstRowContainsHeader True if the first row should contain the column headers.
     * @return Preview of the CSV export.
     */
    private String preview(List<BookExport> bookExportList, char qualifier, char separator, boolean firstRowContainsHeader) {
        StringBuilder sb = new StringBuilder();

        if (firstRowContainsHeader) {
            sb.append(BookExport.getCsvHeader(qualifier, separator));
            sb.append('\n');
        }

        for (BookExport bookExport : bookExportList) {
            sb.append(bookExport.toCsv(qualifier, separator));
            sb.append('\n');
        }

        return sb.toString();
    }
}
