package com.objectcomputing.checkins.services.reports;

record ReportHours(
  float contributionHours,
  float ptoHours,
  float overtimeHours,
  float billableUtilization
) {
}
