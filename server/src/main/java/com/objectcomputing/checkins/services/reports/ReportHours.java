package com.objectcomputing.checkins.services.reports;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
class ReportHours {
  private final float contributionHours;
  private final float ptoHours;
  private final float overtimeHours;
  private final float billableUtilization;
}

