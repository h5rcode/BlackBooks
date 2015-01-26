package com.blackbooks.model.nonpersistent;

import com.blackbooks.model.persistent.Category;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CategoryInfo extends Category implements Serializable {

    private static final long serialVersionUID = -8014226958819494369L;

    public final List<BookInfo> books;

    public CategoryInfo() {
        super();
        this.books = new ArrayList<BookInfo>();
    }

    public CategoryInfo(Category category) {
        this();
        this.id = category.id;
        this.name = category.name;
    }
}
