package com.example.quiziverse.service;

import com.example.quiziverse.model.Answer;
import com.example.quiziverse.model.Question;

import java.util.List;

public interface QuestionService {

    // Method to get questions from a category
    List<Question> getQuestionsFromCategory(String category);

    // Method to get the URI of a category by name
    String getCategoryUriByName(String categoryName);

    //Method to get 3 wrong answers for given question
    List<Answer> getWrongAnswers(String answerTypeUri, String correctAnswerUri);
}
