package com.example.quiziverse.rest;

import com.example.quiziverse.model.Question;
import com.example.quiziverse.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
public class QuestionRestController {

    private final QuestionService questionService;

    @Autowired
    public QuestionRestController(QuestionService questionService) {
        this.questionService = questionService;
    }

    // Endpoint to get ten questions based on category
    @GetMapping("/category/{categoryUri}")
    public List<Question> getQuestionsByCategory(@PathVariable String categoryUri) {
        return questionService.getTenQuestions(categoryUri);
    }
}

