package com.blackbooks.model.nonpersistent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class LanguageInfo implements Serializable {

    private static final long serialVersionUID = 8807563807190599027L;

    public String languageCode;
    public String displayName;
    public List<BookInfo> books = new ArrayList<BookInfo>();
}
