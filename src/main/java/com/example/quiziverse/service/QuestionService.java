package com.example.quiziverse.service;

import com.example.quiziverse.model.Answer;
import com.example.quiziverse.model.Question;

import java.util.List;

public interface QuestionService {

    List<Question> getTenQuestions(String category);
}
