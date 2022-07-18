package com.objectcomputing.checkins.services.onboard.signrequest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SignRequest {
    private final String text;

    @JsonCreator
    public SignRequest(@JsonProperty("text") String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
