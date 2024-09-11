package com.example.quiziverse.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Answer {
    //answer id
    private String uri;
    //actual answer
    private String answerText;
    //answer type
    private AnswerType answerType;
}
