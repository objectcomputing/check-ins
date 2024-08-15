package com.objectcomputing.checkins.services.reports;

import com.objectcomputing.checkins.exceptions.NotFoundException;

import io.micronaut.http.multipart.CompletedFileUpload;

import java.nio.ByteBuffer;
import java.io.IOException;

public interface ReportDataUploadServices {

    void store(CompletedFileUpload file) throws IOException;

    ByteBuffer get(String name) throws NotFoundException;
}
