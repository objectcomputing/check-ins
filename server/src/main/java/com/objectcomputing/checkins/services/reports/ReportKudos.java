package com.objectcomputing.checkins.services.reports;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
class ReportKudos {
  private final LocalDate dateCreated;
  private final String message;
  private final String sender;
}

