package com.objectcomputing.checkins.services.questions;

public class QuestionDuplicateException extends RuntimeException {
    public QuestionDuplicateException(String message) {
        super(message);
    }
}
