# BlackBooks

This repository contains the source code for the Black Books Android app.

<a href="https://play.google.com/store/apps/details?id=com.blackbooks">
  <img alt="Get it on Google Play"
       src="https://play.google.com/intl/en_us/badges/images/apps/en-play-badge.png" />
</a>

## App features
Black Books helps you make an inventory of your books and manage them!

* Make a list of your books: add them manually or simply scan their ISBN (Pic2Shop required).
* Automatically get book details (including thumbnails) from Amazon, Google Books and Open Library.
* Bulk add books: scan or enter a list of ISBNs while offline and start a background search when you are connected to the Internet.
* Edit your books: thumbnail, authors, categories, series, language, page count, etc.
* Have a quick overview of your library directly from the home screen.
* Browse your books by author, category, series, language.
* Search your library using the simple full-text search feature.
* Keep track of the books to read.
* Mark your favourite books.
* Don't lose your books anymore by indicating their location.
* Manage your loans: to whom and when?
* Export your library as a Spreadsheet compatible text file.
* Import or update books from a Spreadsheet compatible text file.

## Privacy policy

This application DOES NOT collect any data from your device.

### Permissions

Black Books requires the following permissions:

* Camera: this feature allows you to take pictures of your book covers and import them directly into the app.
* Internet: this is necessary in order to retrieve the book details.
* Read contacts: when loaning a book, you can pick one of your contacts as the loanee. Only the name of the contact is stored in the app as long as the book is marked as loaned. The app does not send the contacts to any other party.
* Read logs: if an unexpected error occurs, the app offers the possibility to send us the log file, which will help us figure out what went wrong.
* Write external storage: the app needs to write the external storage when:
	* Exporting the library as a Spreadsheet compatible text file.
	* Saving a backup of the app database.

## Implementation
### Data storage
Black Books creates a SQLite database on the user's device to store all the information about the books (including thumbnails).

* The app manipulates books, authors, categories and other entities using the Java classes defined in package [com.blackbooks.model.persistent](https://github.com/h5rcode/BlackBooks/tree/master/app/src/main/java/com/blackbooks/model/persistent).
* These classes are decorated with the annotations located in package [com.blackbooks.model.metadata](https://github.com/h5rcode/BlackBooks/tree/master/app/src/main/java/com/blackbooks/model/metadata) to define a mapping between them and the SQLite tables where they will be persisted.
* The basic database operations (insert, update, select, delete) are performed using the Broker class, located in package [com.blackbooks.sql](https://github.com/h5rcode/BlackBooks/tree/master/app/src/main/java/com/blackbooks/sql).

### Caching
The book thumbnails are Bitmap images stored in the SQLite database as BLOBs (Binary Large Objects).

When displaying the list of all the books in the user's library, the app executes a select from table <code>BOOK</code>. The column where the thumbnails are stored is excluded from the select because the thumbnails are loaded separately using the class [ThumbnailManager](https://github.com/h5rcode/BlackBooks/blob/master/app/src/main/java/com/blackbooks/cache/ThumbnailManager.java)

The ThumbnailManager class loads the thumbnails asyncronously and stores them in a LruCache, providing a safer memory management. When the thumbnail of a book must be displayed, ThumbnailManager first checks if it is not already in the LruCache. If it is, the Bitmap is immediately returned (avoiding a select in the database), otherwise the thumbnail is loaded in an AsyncTask and stored in the LruCache.

### ISBN lookup
When the device is connected to the Internet, Black Books can perform ISBN lookups by making calls to the following services:

* [Google Books API](https://developers.google.com/books/)
* [Product Advertising API](https://affiliate-program.amazon.com/gp/advertising/api/detail/main.html)
* [Open Library Books API](https://openlibrary.org/dev/docs/api/books)

All the classes implementing the ISBN lookup are defined in package [com.blackbooks.services.search](https://github.com/h5rcode/BlackBooks/tree/master/app/src/main/java/com/blackbooks/search). The entry point is the method <code>search(String isbn)</code> in class [BookSearcher](https://github.com/h5rcode/BlackBooks/blob/master/app/src/main/java/com/blackbooks/search/BookSearcher.java) which calls each service and merges their results into a single instance of <code>BookInfo</code>.

The call to each service is implemented by a corresponding Java class that sends a HTTP request and parses the response before returning the result.
