package com.objectcomputing.checkins.services.reports;
import io.micronaut.core.annotation.Introspected;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Introspected
class ReportHours {
  private final float contributionHours;
  private final float ptoHours;
  private final float overtimeHours;
  private final float billableUtilization;
}

