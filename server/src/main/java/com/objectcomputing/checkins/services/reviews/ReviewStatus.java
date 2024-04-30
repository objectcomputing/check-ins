package com.objectcomputing.checkins.services.reviews;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true)
public enum ReviewStatus {
  PLANNING,
  AWAITING_APPROVAL,
  OPEN,
  CLOSED,
  UNKNOWN
}
