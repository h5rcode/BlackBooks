package com.blackbooks.services;

import com.blackbooks.model.persistent.Category;

import java.util.List;

public interface CategoryService {
    Category getCategoryByCriteria(Category criteria);

    List<Category> getCategoryListByText(String text);

    Category getCategory(long categoryId);
}
