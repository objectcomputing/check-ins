package com.objectcomputing.checkins.services.pulse;

import java.time.LocalDate;

public interface PulseServices {
  public void sendPendingEmail(LocalDate now);
}
