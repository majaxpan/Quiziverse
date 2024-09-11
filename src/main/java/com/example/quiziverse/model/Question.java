package com.example.quiziverse.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Question {
    //question id
    private String uri;
    //actual question
    private String questionText;
    //the correct answer with uri and answer type
    private Answer correctAnswer;
    //list of wrong answers with uri and answer type
    private List<Answer> wrongAnswers;
    //the required answer type for this question
    private AnswerType requiredAnswerType;
}
