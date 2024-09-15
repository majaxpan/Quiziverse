package com.example.quiziverse.rest;

import com.example.quiziverse.model.Category;
import com.example.quiziverse.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryRestController {

    private final CategoryService categoryService;

    public CategoryRestController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/by-name")
    public ResponseEntity<Category> getCategoryByName(@RequestParam("name") String name) {
        Category category = categoryService.getCategoryUriByName(name);
        if (category != null) {
            return ResponseEntity.ok(category);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

