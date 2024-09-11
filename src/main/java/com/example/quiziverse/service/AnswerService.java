package com.example.quiziverse.service;

import com.example.quiziverse.model.Answer;
import com.example.quiziverse.model.AnswerType;

import java.util.List;

public interface AnswerService {

    Answer getAnswerByUri(String answerUri);

    List<Answer> getThreeWrongAnswers(Answer correctAnswer);

}
