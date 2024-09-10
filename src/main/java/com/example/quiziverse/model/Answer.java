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
    private String uri;
    private String answerText;
    private String answerType;
    private String answerTypeUri;
}
