package com.objectcomputing.checkins.services.memberprofile.anniversaryreport;

import java.util.List;

public interface AnniversaryServices {

    List<AnniversaryReportResponseDTO> findByValue(String[] month);

    List<AnniversaryReportResponseDTO> getTodaysAnniversaries();
}
