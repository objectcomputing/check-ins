package com.objectcomputing.checkins.services.memberprofile.csvreport;

import java.io.File;

public interface MemberProfileReportServices {

    File generateFile(MemberProfileReportQueryDTO queryDTO);

}
