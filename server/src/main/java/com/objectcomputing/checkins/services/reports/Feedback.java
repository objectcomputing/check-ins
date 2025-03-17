package com.objectcomputing.checkins.services.reports;

import io.micronaut.core.annotation.Introspected;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.time.LocalDate;

@AllArgsConstructor
@Getter
@Introspected
class Feedback {

  @AllArgsConstructor
  @Introspected
  @Getter
  public static class Answer {
    private final String memberName;
    private final LocalDate submitted;
    private final String question;
    private final String answer;
    private final String type;
    private final int number;
  }

  private String name;
  private List<Answer> answers;
}

