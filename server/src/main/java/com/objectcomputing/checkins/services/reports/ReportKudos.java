package com.objectcomputing.checkins.services.reports;

import io.micronaut.core.annotation.Introspected;

import java.time.LocalDate;

@Introspected
record ReportKudos(
  LocalDate dateCreated,
  String message,
  String sender
) {
}
