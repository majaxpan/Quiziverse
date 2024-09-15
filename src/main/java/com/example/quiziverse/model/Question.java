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
    //list of 4 answers, in which one is the correct
    private List<Answer> answersList;
}
