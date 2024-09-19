package com.objectcomputing.checkins.services.reports;

import io.micronaut.core.annotation.Introspected;

@Introspected
record ReportHours(
  float contributionHours,
  float ptoHours,
  float overtimeHours,
  float billableUtilization
) {
}
