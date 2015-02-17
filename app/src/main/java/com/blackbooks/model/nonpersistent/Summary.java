package com.blackbooks.model.nonpersistent;

import java.io.Serializable;

public class Summary implements Serializable {

    private static final long serialVersionUID = 7266966828363599604L;

    public int books;
    public int authors;
    public int categories;
    public int series;
    public int bookLocations;
    public int toRead;
    public int favourites;
    public int languages;
    public int loans;
}
