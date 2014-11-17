package com.blackbooks.search.amazon;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

/**
 * Utility class to parse XML results returned by the Amazon Product Advertising
 * API.
 */
public final class AmazonXmlParser {

	private static final String ITEM_LOOKUP_RESPONSE = "ItemLookupResponse";
	private static final String ITEMS = "Items";
	private static final String ITEM = "Item";
	private static final String SMALL_IMAGE = "SmallImage";
	private static final String MEDIUM_IMAGE = "MediumImage";
	private static final String LARGE_IMAGE = "LargeImage";
	private static final String URL = "URL";
	private static final String ITEM_ATTRIBUTES = "ItemAttributes";
	private static final String AUTHOR = "Author";
	private static final String ISBN = "ISBN";
	private static final String TITLE = "Title";
	private static final String PUBLISHER = "Publisher";

	// We don't use namespaces
	private static final String ns = null;

	/**
	 * Private constructor.
	 */
	private AmazonXmlParser() {
	}

	/**
	 * Parse the XML data returned by the Amazon Product Advertising API and
	 * return an instance of AmazonBook.
	 * 
	 * @param xml
	 *            The XML data to parse.
	 * @return AmazonBook.
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	public static AmazonBook parse(String xml) throws XmlPullParserException, IOException {
		XmlPullParser parser = Xml.newPullParser();
		parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
		parser.setInput(new StringReader(xml));
		parser.nextTag();
		return readItemLookupResponse(parser);
	}

	/**
	 * Read the XML response and build an AmazonBook from it.
	 * 
	 * @param parser
	 *            XmlPullParser.
	 * @return AmazonBook.
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	private static AmazonBook readItemLookupResponse(XmlPullParser parser) throws XmlPullParserException, IOException {
		AmazonBook amazonBook = null;
		List<Item> items = null;

		parser.require(XmlPullParser.START_TAG, ns, ITEM_LOOKUP_RESPONSE);
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name.equals(ITEMS)) {
				items = readItems(parser);
			} else {
				skip(parser);
			}
		}

		if (items != null && !items.isEmpty()) {
			Item item = items.get(0);
			amazonBook = new AmazonBook();

			ItemAttributes itemAttributes = item.itemAttributes;
			if (itemAttributes != null) {
				amazonBook.title = itemAttributes.title;
				amazonBook.author = itemAttributes.author;
				amazonBook.isbn = itemAttributes.isbn;
				amazonBook.publisher = itemAttributes.publisher;
			}

			Image smallImage = item.smallImage;
			if (smallImage != null) {
				amazonBook.smallImageLink = smallImage.url;
			}

			Image mediumImage = item.mediumImage;
			if (mediumImage != null) {
				amazonBook.mediumImageLink = mediumImage.url;
			}

			Image largeImage = item.largeImage;
			if (largeImage != null) {
				amazonBook.largeImageLink = largeImage.url;
			}
		}

		return amazonBook;
	}

	/**
	 * Read the Items element and return a list of Item.
	 * 
	 * @param parser
	 *            XmlPullParser.
	 * @return List of Item.
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	private static List<Item> readItems(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, ITEMS);

		List<Item> items = new ArrayList<Item>();

		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name.equals(ITEM)) {
				Item item = readItem(parser);
				items.add(item);
			} else {
				skip(parser);
			}
		}
		return items;
	}

	/**
	 * Read an Item element.
	 * 
	 * @param parser
	 *            XmlPullParser.
	 * @return Item.
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	private static Item readItem(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, ITEM);

		ItemAttributes itemAttributes = null;
		Image smallImage = null;
		Image mediumImage = null;
		Image largeImage = null;

		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name.equals(ITEM_ATTRIBUTES)) {
				itemAttributes = readItemAttributes(parser);
			} else if (SMALL_IMAGE.equals(name)) {
				smallImage = readImage(parser);
			} else if (MEDIUM_IMAGE.equals(name)) {
				mediumImage = readImage(parser);
			} else if (LARGE_IMAGE.equals(name)) {
				largeImage = readImage(parser);
			} else {
				skip(parser);
			}
		}
		return new Item(itemAttributes, smallImage, mediumImage, largeImage);
	}

	/**
	 * Read an ItemAttributes element.
	 * 
	 * @param parser
	 *            XmlPullParser.
	 * @return ItemAttributes.
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	private static ItemAttributes readItemAttributes(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, ITEM_ATTRIBUTES);

		String author = null;
		String title = null;
		String isbn = null;
		String publisher = null;

		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name.equals(AUTHOR)) {
				author = readText(parser, AUTHOR);
			} else if (name.equals(ISBN)) {
				isbn = readText(parser, ISBN);
			} else if (name.equals(TITLE)) {
				title = readText(parser, TITLE);
			} else if (name.equals(PUBLISHER)) {
				publisher = readText(parser, PUBLISHER);
			} else {
				skip(parser);
			}
		}
		return new ItemAttributes(author, title, isbn, publisher);
	}

	/**
	 * Read an Image element.
	 * 
	 * @param parser
	 *            XmlPullParser.
	 * @return Image.
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	private static Image readImage(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, SMALL_IMAGE);

		String url = null;

		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name.equals(URL)) {
				url = readText(parser, URL);
			} else {
				skip(parser);
			}
		}
		return new Image(url);
	}

	/**
	 * Read text.
	 * 
	 * @param parser
	 *            XmlPullParser.
	 * @param name
	 *            Name of the XML element containing the text.
	 * @return The text.
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	private static String readText(XmlPullParser parser, String name) throws IOException, XmlPullParserException {
		String result = "";
		parser.require(XmlPullParser.START_TAG, ns, name);
		if (parser.next() == XmlPullParser.TEXT) {
			result = parser.getText();
			parser.nextTag();
		}
		parser.require(XmlPullParser.END_TAG, ns, name);
		return result;
	}

	/**
	 * Skip the current XML element.
	 * 
	 * @param parser
	 *            XmlPullParser.
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	private static void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
		if (parser.getEventType() != XmlPullParser.START_TAG) {
			throw new IllegalStateException();
		}
		int depth = 1;
		while (depth != 0) {
			switch (parser.next()) {
			case XmlPullParser.END_TAG:
				depth--;
				break;
			case XmlPullParser.START_TAG:
				depth++;
				break;
			}
		}
	}

	/**
	 * Item.
	 */
	private static class Item {

		public final ItemAttributes itemAttributes;
		public final Image smallImage;
		public final Image mediumImage;
		public final Image largeImage;

		/**
		 * Constructor.
		 * 
		 * @param itemAttributes
		 *            ItemAttributes.
		 * @param smallImage
		 *            Image.
		 * @param mediumImage
		 *            Image.
		 * @param largeImage
		 *            Image.
		 */
		public Item(ItemAttributes itemAttributes, Image smallImage, Image mediumImage, Image largeImage) {
			this.itemAttributes = itemAttributes;
			this.smallImage = smallImage;
			this.mediumImage = mediumImage;
			this.largeImage = largeImage;
		}

	}

	/**
	 * ItemAttributes.
	 */
	private static class ItemAttributes {

		public final String author;
		public final String title;
		public final String isbn;
		public final String publisher;

		/**
		 * Constructor.
		 * 
		 * @param author
		 *            Author.
		 * @param title
		 *            Title.
		 * @param isbn
		 *            ISBN.
		 * @param publisher
		 *            Publisher.
		 */
		public ItemAttributes(String author, String title, String isbn, String publisher) {
			this.author = author;
			this.title = title;
			this.isbn = isbn;
			this.publisher = publisher;
		}
	}

	/**
	 * Image.
	 */
	private static class Image {
		public final String url;

		/**
		 * Constructor.
		 * 
		 * @param url
		 *            URL.
		 */
		public Image(String url) {
			this.url = url;
		}
	}
}
