package com.example.quiziverse.web;

import com.example.quiziverse.model.Question;
import com.example.quiziverse.service.QuestionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class QuizController {

    private final QuestionService questionService;

    public QuizController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping("/quiz")
    public String showQuiz(@RequestParam("categoryName") String categoryName, Model model) {
        if (categoryName == null || categoryName.isEmpty()) {
            model.addAttribute("error", "Category name is missing");
            return "error"; // Return the view name for error handling
        }

        try {
            List<Question> questions = questionService.getTenQuestions(categoryName);
            model.addAttribute("questions", questions);
            return "quiz"; // Return the view name for displaying quiz questions
        } catch (Exception e) {
            model.addAttribute("error", "An error occurred while fetching questions.");
            return "error"; // Return the view name for error handling
        }
    }
}
