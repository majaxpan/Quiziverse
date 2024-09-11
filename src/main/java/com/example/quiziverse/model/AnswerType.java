package com.example.quiziverse.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AnswerType {
    //answer type id
    private String uri;
    //answer type name
    private String label;
}
