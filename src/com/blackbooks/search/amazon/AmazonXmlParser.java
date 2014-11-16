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
public class AmazonXmlParser {

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
	 * Parse the JSON data returned by Google Books API and return an instance
	 * of GoogleBook.
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

			SmallImage smallImage = item.smallImage;
			if (smallImage != null) {
				amazonBook.smallImageLink = smallImage.url;
			}

			MediumImage mediumImage = item.mediumImage;
			if (mediumImage != null) {
				amazonBook.mediumImageLink = mediumImage.url;
			}

			LargeImage largeImage = item.largeImage;
			if (largeImage != null) {
				amazonBook.largeImageLink = largeImage.url;
			}
		}

		return amazonBook;
	}

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

	private static Item readItem(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, ITEM);

		ItemAttributes itemAttributes = null;
		SmallImage smallImage = null;
		MediumImage mediumImage = null;
		LargeImage largeImage = null;

		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name.equals(ITEM_ATTRIBUTES)) {
				itemAttributes = readItemAttributes(parser);
			} else if (SMALL_IMAGE.equals(name)) {
				smallImage = readSmallImage(parser);
			} else if (MEDIUM_IMAGE.equals(name)) {
				mediumImage = readMediumImage(parser);
			} else if (LARGE_IMAGE.equals(name)) {
				largeImage = readLargeImage(parser);
			} else {
				skip(parser);
			}
		}
		return new Item(itemAttributes, smallImage, mediumImage, largeImage);
	}

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

	private static SmallImage readSmallImage(XmlPullParser parser) throws XmlPullParserException, IOException {
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
		return new SmallImage(url);
	}

	private static MediumImage readMediumImage(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, MEDIUM_IMAGE);

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
		return new MediumImage(url);
	}

	private static LargeImage readLargeImage(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, LARGE_IMAGE);

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
		return new LargeImage(url);
	}

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

	private static class Item {

		public final ItemAttributes itemAttributes;
		public final SmallImage smallImage;
		public final MediumImage mediumImage;
		public final LargeImage largeImage;

		public Item(ItemAttributes itemAttributes, SmallImage smallImage, MediumImage mediumImage, LargeImage largeImage) {
			this.itemAttributes = itemAttributes;
			this.smallImage = smallImage;
			this.mediumImage = mediumImage;
			this.largeImage = largeImage;
		}

	}

	private static class ItemAttributes {

		public final String author;
		public final String title;
		public final String isbn;
		public final String publisher;

		public ItemAttributes(String author, String title, String isbn, String publisher) {
			this.author = author;
			this.title = title;
			this.isbn = isbn;
			this.publisher = publisher;
		}
	}

	private static class SmallImage {

		public final String url;

		public SmallImage(String url) {
			this.url = url;
		}
	}

	private static class MediumImage {

		public final String url;

		public MediumImage(String url) {
			this.url = url;
		}
	}

	private static class LargeImage {

		public final String url;

		public LargeImage(String url) {
			this.url = url;
		}
	}
}
