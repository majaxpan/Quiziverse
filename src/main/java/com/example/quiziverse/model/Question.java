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
    private String uri;
    private String questionText;
    //private Category category;
    private Answer correctAnswer;
    private List<Answer> wrongAnswers;
    private AnswerType requiredAnswerType;
}
