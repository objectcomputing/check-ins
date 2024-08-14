package com.objectcomputing.checkins.services.reports;

import io.micronaut.http.multipart.CompletedFileUpload;
import java.io.IOException;

public interface ReportDataUploadServices {

    void store(CompletedFileUpload file) throws IOException;

}
