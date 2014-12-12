package com.blackbooks.model.nonpersistent;

import java.io.Serializable;

public class BookExport implements Serializable {

	private static final long serialVersionUID = 3800916482466647250L;

	public Long id;
	public String title;
	public String subtitle;
	public String authors;
	public String categories;
	public String series;
	public Long number;
	public Long pageCount;
	public String languageCode;
	public String description;
	public String publisher;
	public String publishedDate;
	public String isbn10;
	public String isbn13;
	
	public static String getCsvHeader(char qualifier, char separator) {
		StringBuilder sb = new StringBuilder();
		String stringQualifier = String.valueOf(qualifier);
		sb.append(qualify("id", stringQualifier, separator));
		sb.append(qualify("title", stringQualifier, separator));
		sb.append(qualify("subtitle", stringQualifier, separator));
		sb.append(qualify("authors", stringQualifier, separator));
		sb.append(qualify("categories", stringQualifier, separator));
		sb.append(qualify("series", stringQualifier, separator));
		sb.append(qualify("number", stringQualifier, separator));
		sb.append(qualify("pageCount", stringQualifier, separator));
		sb.append(qualify("languageCode", stringQualifier, separator));
		sb.append(qualify("description", stringQualifier, separator));
		sb.append(qualify("publisher", stringQualifier, separator));
		sb.append(qualify("publishedDate", stringQualifier, separator));
		sb.append(qualify("isbn10", stringQualifier, separator));
		sb.append(qualify("isbn13", stringQualifier, separator));
		return sb.toString();
	}
	
	public String toCsv(char qualifier, char separator) {
		StringBuilder sb = new StringBuilder();
		String stringQualifier = String.valueOf(qualifier);
		sb.append(qualify(this.id, stringQualifier, separator));
		sb.append(qualify(this.title, stringQualifier, separator));
		sb.append(qualify(this.subtitle, stringQualifier, separator));
		sb.append(qualify(this.authors, stringQualifier, separator));
		sb.append(qualify(this.categories, stringQualifier, separator));
		sb.append(qualify(this.series, stringQualifier, separator));
		sb.append(qualify(this.number, stringQualifier, separator));
		sb.append(qualify(this.pageCount, stringQualifier, separator));
		sb.append(qualify(this.languageCode, stringQualifier, separator));
		sb.append(qualify(this.description, stringQualifier, separator));
		sb.append(qualify(this.publisher, stringQualifier, separator));
		sb.append(qualify(this.publishedDate,stringQualifier, separator));
		sb.append(qualify(this.isbn10, stringQualifier, separator));
		sb.append(qualify(this.isbn13, stringQualifier, separator));

		return sb.toString();
	}

	private static String qualify(Object object, String qualifier, char separator) {
		String result = null;
		if (object == null) {
			result = "";
		} else {
			String text = object.toString();
			text = text.replace("\n", "");
			text = text.replace(qualifier, qualifier + qualifier);
			result = qualifier + text + qualifier;
		}
		return result + separator;
	}
}
