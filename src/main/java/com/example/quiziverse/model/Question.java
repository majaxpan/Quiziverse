package com.example.quiziverse.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Question {
    private String uri;
    private String questionText;
    private String categoryUri; // Maybe not?
    private String correctAnswerUri;
    private String requiresAnswerTypeUri;
}
