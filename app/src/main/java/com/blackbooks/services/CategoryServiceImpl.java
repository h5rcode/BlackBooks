package com.blackbooks.services;

import com.blackbooks.model.persistent.Category;
import com.blackbooks.repositories.CategoryRepository;

import java.util.List;

/**
 * Services related to the Category class.
 */
public final class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category getCategory(long catId) {
        return categoryRepository.getCategory(catId);
    }

    public Category getCategoryByCriteria(Category criteria) {
        return categoryRepository.getCategoryByCriteria(criteria);
    }

    public List<Category> getCategoryListByText(String text) {
        return categoryRepository.getCategoryListByText(text);
    }
}
