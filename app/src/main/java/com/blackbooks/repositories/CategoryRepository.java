package com.blackbooks.repositories;

import com.blackbooks.model.persistent.Category;

import java.util.List;

public interface CategoryRepository {
    void deleteCategory(Long categoryId);

    void deleteCategoriesWithoutBooks();

    Category getCategory(long categoryId);

    long saveCategory(Category category);

    Category getCategoryByCriteria(Category criteria);

    List<Category> getCategoryListByText(String text);

    void updateCategory(long categoryId, String newName);

    int getCategoryCount();
}
