package com.example.quiziverse.service;

import com.example.quiziverse.model.AnswerType;

public interface AnswerTypeService {
    AnswerType getAnswerTypeLabelByUri(String answerTypeUri);
}
