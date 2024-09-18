package com.objectcomputing.checkins.services.reports;

import java.time.LocalDate;

record ReportKudos(
  LocalDate dateCreated,
  String message,
  String sender
) {
}
