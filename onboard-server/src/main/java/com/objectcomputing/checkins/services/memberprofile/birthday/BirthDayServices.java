package com.objectcomputing.checkins.services.memberprofile.birthday;

import java.util.List;

public interface BirthDayServices {

    List<BirthDayResponseDTO> findByValue(String[] month);
}
