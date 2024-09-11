package com.example.quiziverse.service;

import com.example.quiziverse.model.Category;

import java.util.List;

public interface CategoryService {

    List<Category> getAllCategories();

    Category getCategoryUriByName(String categoryName);
}
