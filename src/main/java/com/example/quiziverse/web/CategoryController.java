package com.example.quiziverse.web;

import com.example.quiziverse.model.Category;
import com.example.quiziverse.service.CategoryService;
import com.example.quiziverse.service.QuestionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;

@Controller
public class CategoryController {

    private final CategoryService categoryService;
    private final QuestionService questionService;

    public CategoryController(CategoryService categoryService, QuestionService questionService) {
        this.categoryService = categoryService;
        this.questionService = questionService;
    }

    @GetMapping("/")
    public String getCategories(Model model) {
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        return "home";
    }

    @PostMapping("/chooseCategory")
    public RedirectView chooseCategory(@RequestParam("categoryName") String categoryName) {
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl("/quiz?category=" + categoryName);
        return redirectView;
    }
}
