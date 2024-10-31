package com.objectcomputing.checkins.services.memberprofile.csvreport;

import java.io.File;
import java.io.IOException;

public interface MemberProfileReportServices {

    File generateFile(MemberProfileReportQueryDTO queryDTO) throws IOException;
}
