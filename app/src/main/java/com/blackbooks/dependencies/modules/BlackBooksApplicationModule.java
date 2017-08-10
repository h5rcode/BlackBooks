package com.blackbooks.dependencies.modules;

import com.blackbooks.activities.BookAuthorsEditActivity;
import com.blackbooks.activities.BookCategoriesEditActivity;
import com.blackbooks.activities.BookEditActivity;
import com.blackbooks.activities.BookListActivity;
import com.blackbooks.activities.IsbnLookupActivity;
import com.blackbooks.cache.ThumbnailManager;
import com.blackbooks.cache.ThumbnailManagerImpl;
import com.blackbooks.database.SQLiteHelper;
import com.blackbooks.database.TransactionManager;
import com.blackbooks.database.TransactionManagerImpl;
import com.blackbooks.fragments.bookdisplay.BookDisplayDetailFragment;
import com.blackbooks.fragments.bookedit.BookEditGeneralFragment;
import com.blackbooks.fragments.bookedit.BookEditPersonalFragment;
import com.blackbooks.fragments.bookexport.BookExportFragment;
import com.blackbooks.fragments.bookgrouplist.BookGroupListAuthorFragment;
import com.blackbooks.fragments.bookgrouplist.BookGroupListBookLocationFragment;
import com.blackbooks.fragments.bookgrouplist.BookGroupListCategoryFragment;
import com.blackbooks.fragments.bookgrouplist.BookGroupListFirstLetterFragment;
import com.blackbooks.fragments.bookgrouplist.BookGroupListLanguageFragment;
import com.blackbooks.fragments.bookgrouplist.BookGroupListLoanFragment;
import com.blackbooks.fragments.bookgrouplist.BookGroupListSeriesFragment;
import com.blackbooks.fragments.bookimport.BookImportColumnMappingFragment;
import com.blackbooks.fragments.bookimport.BookImportFragment;
import com.blackbooks.fragments.booklist.BookListByAuthorFragment;
import com.blackbooks.fragments.booklist.BookListByBookLocationFragment;
import com.blackbooks.fragments.booklist.BookListByCategoryFragment;
import com.blackbooks.fragments.booklist.BookListByFirstLetterFragment;
import com.blackbooks.fragments.booklist.BookListByLanguageFragment;
import com.blackbooks.fragments.booklist.BookListByLoanedFragment;
import com.blackbooks.fragments.booklist.BookListBySeriesFragment;
import com.blackbooks.fragments.booklist.BookListFavouriteFragment;
import com.blackbooks.fragments.booklist.BookListToReadFragment;
import com.blackbooks.fragments.bookloan.BookLoanFragment;
import com.blackbooks.fragments.booksearch.BookSearchFragment;
import com.blackbooks.fragments.bulkadd.BulkAddFragmentLookedUp;
import com.blackbooks.fragments.bulkadd.BulkAddFragmentPending;
import com.blackbooks.fragments.databasebackup.DatabaseBackupFragment;
import com.blackbooks.fragments.databasedelete.DatabaseDeleteFragment;
import com.blackbooks.fragments.databaserestore.DatabaseRestoreFragment;
import com.blackbooks.fragments.dialogs.AuthorDeleteFragment;
import com.blackbooks.fragments.dialogs.AuthorEditFragment;
import com.blackbooks.fragments.summary.SummaryFragment;
import com.blackbooks.repositories.AuthorRepository;
import com.blackbooks.repositories.AuthorRepositoryImpl;
import com.blackbooks.repositories.BookAuthorRepository;
import com.blackbooks.repositories.BookAuthorRepositoryImpl;
import com.blackbooks.repositories.BookCategoryRepository;
import com.blackbooks.repositories.BookCategoryRepositoryImpl;
import com.blackbooks.repositories.BookFTSRepository;
import com.blackbooks.repositories.BookFTSRepositoryImpl;
import com.blackbooks.repositories.BookLocationRepository;
import com.blackbooks.repositories.BookLocationRepositoryImpl;
import com.blackbooks.repositories.BookRepository;
import com.blackbooks.repositories.BookRepositoryImpl;
import com.blackbooks.repositories.CategoryRepository;
import com.blackbooks.repositories.CategoryRepositoryImpl;
import com.blackbooks.repositories.IsbnRepository;
import com.blackbooks.repositories.IsbnRepositoryImpl;
import com.blackbooks.repositories.PublisherRepository;
import com.blackbooks.repositories.PublisherRepositoryImpl;
import com.blackbooks.repositories.SeriesRepository;
import com.blackbooks.repositories.SeriesRepositoryImpl;
import com.blackbooks.service.BulkSearchService;
import com.blackbooks.services.AuthorService;
import com.blackbooks.services.AuthorServiceImpl;
import com.blackbooks.services.BookGroupService;
import com.blackbooks.services.BookGroupServiceImpl;
import com.blackbooks.services.BookLocationService;
import com.blackbooks.services.BookLocationServiceImpl;
import com.blackbooks.services.BookService;
import com.blackbooks.services.BookServiceImpl;
import com.blackbooks.services.CategoryService;
import com.blackbooks.services.CategoryServiceImpl;
import com.blackbooks.services.ExportService;
import com.blackbooks.services.ExportServiceImpl;
import com.blackbooks.services.FullTextSearchService;
import com.blackbooks.services.FullTextSearchServiceImpl;
import com.blackbooks.services.IsbnService;
import com.blackbooks.services.IsbnServiceImpl;
import com.blackbooks.services.PublisherService;
import com.blackbooks.services.PublisherServiceImpl;
import com.blackbooks.services.SeriesService;
import com.blackbooks.services.SeriesServiceImpl;
import com.blackbooks.services.SummaryService;
import com.blackbooks.services.SummaryServiceImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class BlackBooksApplicationModule {
    @ContributesAndroidInjector
    abstract BookAuthorsEditActivity bookAuthorsEditActivity();

    @ContributesAndroidInjector
    abstract BookCategoriesEditActivity bookCategoriesEditActivity();

    @ContributesAndroidInjector
    abstract BookDisplayDetailFragment bookDisplayDetailFragment();

    @ContributesAndroidInjector
    abstract BookEditActivity bookEditActivity();

    @ContributesAndroidInjector
    abstract BookEditGeneralFragment bookEditGeneralFragment();

    @ContributesAndroidInjector
    abstract BookEditPersonalFragment bookEditPersonalFragment();

    @ContributesAndroidInjector
    abstract BookExportFragment bookExportFragment();

    @ContributesAndroidInjector
    abstract BookGroupListAuthorFragment bookGroupListAuthorFragment();

    @ContributesAndroidInjector
    abstract BookGroupListBookLocationFragment bookGroupListBookLocationFragment();

    @ContributesAndroidInjector
    abstract BookGroupListCategoryFragment bookGroupListCategoryFragment();

    @ContributesAndroidInjector
    abstract BookGroupListFirstLetterFragment bookGroupListFirstLetterFragment();

    @ContributesAndroidInjector
    abstract BookGroupListLanguageFragment bookGroupListLanguageFragment();

    @ContributesAndroidInjector
    abstract BookGroupListLoanFragment bookGroupListLoanFragment();

    @ContributesAndroidInjector
    abstract BookGroupListSeriesFragment bookGroupListSeriesFragment();

    @ContributesAndroidInjector
    abstract BookImportColumnMappingFragment bookImportColumnMappingFragment();

    @ContributesAndroidInjector
    abstract BookImportFragment bookImportFragment();

    @ContributesAndroidInjector
    abstract BookListActivity bookListActivity();

    @ContributesAndroidInjector
    abstract BookListByAuthorFragment bookListByAuthorFragment();

    @ContributesAndroidInjector
    abstract BookListByBookLocationFragment bookListByBookLocationFragment();

    @ContributesAndroidInjector
    abstract BookListByCategoryFragment bookListByCategoryFragment();

    @ContributesAndroidInjector
    abstract BookListByFirstLetterFragment bookListByFirstLetterFragment();

    @ContributesAndroidInjector
    abstract BookListByLanguageFragment bookListByLanguageFragment();

    @ContributesAndroidInjector
    abstract BookListByLoanedFragment bookListByLoanedFragment();

    @ContributesAndroidInjector
    abstract BookListBySeriesFragment bookListBySeriesFragment();

    @ContributesAndroidInjector
    abstract BookListFavouriteFragment bookListFavouriteFragment();

    @ContributesAndroidInjector
    abstract BookListToReadFragment bookListToReadFragment();

    @ContributesAndroidInjector
    abstract BookLoanFragment bookLoanFragment();

    @ContributesAndroidInjector
    abstract BookSearchFragment bookSearchFragment();

    @ContributesAndroidInjector
    abstract BulkAddFragmentLookedUp bulkAddFragmentLookedUp();

    @ContributesAndroidInjector
    abstract BulkAddFragmentPending bulkAddFragmentPending();

    @ContributesAndroidInjector
    abstract BulkSearchService bulkSearchService();

    @ContributesAndroidInjector
    abstract DatabaseBackupFragment databaseBackupFragment();

    @ContributesAndroidInjector
    abstract DatabaseDeleteFragment databaseDeleteFragment();

    @ContributesAndroidInjector
    abstract DatabaseRestoreFragment databaseRestoreFragment();

    @ContributesAndroidInjector
    abstract AuthorDeleteFragment authorDeleteFragment();

    @ContributesAndroidInjector
    abstract AuthorEditFragment authorEditFragment();

    @ContributesAndroidInjector
    abstract IsbnLookupActivity isbnLookupActivity();

    @ContributesAndroidInjector
    abstract SummaryFragment summaryFragment();

    @Provides
    static AuthorService provideAuthorService(AuthorRepository authorRepository) {
        return new AuthorServiceImpl(authorRepository);
    }

    @Provides
    static AuthorRepository provideAuthorRepository(SQLiteHelper sqLiteHelper) {
        return new AuthorRepositoryImpl(sqLiteHelper);
    }

    @Provides
    static BookAuthorRepository provideBookAuthorRepository(SQLiteHelper sqLiteHelper) {
        return new BookAuthorRepositoryImpl(sqLiteHelper);
    }

    @Provides
    static BookCategoryRepository provideBookCategoryRepository(SQLiteHelper sqLiteHelper) {
        return new BookCategoryRepositoryImpl(sqLiteHelper);
    }

    @Provides
    static BookFTSRepository provideBookFTSRepository(SQLiteHelper sqLiteHelper) {
        return new BookFTSRepositoryImpl(sqLiteHelper);
    }

    @Provides
    static BookGroupService provideBookGroupService(SQLiteHelper sqLiteHelper) {
        return new BookGroupServiceImpl(sqLiteHelper);
    }

    @Provides
    static BookLocationRepository provideBookLocationRepository(SQLiteHelper sqLiteHelper) {
        return new BookLocationRepositoryImpl(sqLiteHelper);
    }

    @Provides
    static BookLocationService provideBookLocationService(BookLocationRepository bookLocationRepository) {
        return new BookLocationServiceImpl(bookLocationRepository);
    }

    @Provides
    static BookRepository provideBookRepository(SQLiteHelper sqLiteHelper) {
        return new BookRepositoryImpl(sqLiteHelper);
    }

    @Provides
    static BookService provideBookService(
            AuthorRepository authorRepository,
            BookAuthorRepository bookAuthorRepository,
            BookCategoryRepository bookCategoryRepository,
            BookLocationRepository bookLocationRepository,
            BookFTSRepository bookFTSRepository,
            BookRepository bookRepository,
            CategoryRepository categoryRepository,
            PublisherRepository publisherRepository,
            SeriesRepository seriesRepository,
            ThumbnailManager thumbnailManager,
            TransactionManager transactionManager) {
        return new BookServiceImpl(
                authorRepository,
                bookAuthorRepository,
                bookCategoryRepository,
                bookFTSRepository,
                bookLocationRepository,
                bookRepository,
                categoryRepository,
                publisherRepository,
                seriesRepository,
                thumbnailManager,
                transactionManager);
    }

    @Provides
    static CategoryRepository provideCategoryRepository(SQLiteHelper sqLiteHelper) {
        return new CategoryRepositoryImpl(sqLiteHelper);
    }

    @Provides
    static CategoryService provideCategoryService(CategoryRepository categoryRepository) {
        return new CategoryServiceImpl(categoryRepository);
    }

    @Provides
    static ExportService provideExportService(SQLiteHelper sqLiteHelper) {
        return new ExportServiceImpl(sqLiteHelper);
    }

    @Provides
    static FullTextSearchService provideFullTextSearchService(BookFTSRepository bookFTSRepository, BookService bookService) {
        return new FullTextSearchServiceImpl(bookFTSRepository, bookService);
    }

    @Provides
    static IsbnRepository provideIsbnRepository(SQLiteHelper sqLiteHelper) {
        return new IsbnRepositoryImpl(sqLiteHelper);
    }

    @Provides
    static IsbnService provideIsbnService(BookService bookService, IsbnRepository isbnRepository, SQLiteHelper sqLiteHelper) {
        return new IsbnServiceImpl(bookService, isbnRepository, sqLiteHelper);
    }

    @Provides
    static PublisherRepository providePublisherRepository(SQLiteHelper sqLiteHelper) {
        return new PublisherRepositoryImpl(sqLiteHelper);
    }

    @Provides
    static PublisherService providePublisherService(PublisherRepository publisherRepository) {
        return new PublisherServiceImpl(publisherRepository);
    }

    @Provides
    static SeriesRepository provideSeriesRepository(SQLiteHelper sqLiteHelper) {
        return new SeriesRepositoryImpl(sqLiteHelper);
    }

    @Provides
    static SeriesService provideSeriesService(SeriesRepository seriesRepository) {
        return new SeriesServiceImpl(seriesRepository);
    }

    @Provides
    static SQLiteHelper provideSQLiteHelper() {
        return SQLiteHelper.getInstance();
    }

    @Provides
    static SummaryService provideSummaryService(AuthorRepository authorRepository, BookLocationRepository bookLocationRepository, BookRepository bookRepository, CategoryRepository categoryRepository, SeriesRepository seriesRepository) {
        return new SummaryServiceImpl(authorRepository, bookLocationRepository, bookRepository, categoryRepository, seriesRepository);
    }

    @Provides
    @Singleton
    static ThumbnailManager provideThumbnailManager(BookRepository bookRepository) {
        return new ThumbnailManagerImpl(bookRepository);
    }

    @Provides
    static TransactionManager provideTransactionManager(SQLiteHelper sqLiteHelper) {
        return new TransactionManagerImpl(sqLiteHelper);
    }
}
