package com.blackbooks.model.nonpersistent;

import com.blackbooks.model.persistent.Category;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CategoryInfo extends Category implements Serializable {

    private static final long serialVersionUID = -8014226958819494369L;

    public final List<BookInfo> books = new ArrayList<BookInfo>();

    /**
     * Default constructor.
     */
    public CategoryInfo() {
        super();
    }

    /**
     * Copy constructor.
     *
     * @param category Category.
     */
    public CategoryInfo(Category category) {
        super(category);
    }
}
