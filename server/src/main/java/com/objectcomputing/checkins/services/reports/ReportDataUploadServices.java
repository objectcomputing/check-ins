package com.objectcomputing.checkins.services.reports;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.NotFoundException;

import io.micronaut.http.multipart.CompletedFileUpload;

import java.nio.ByteBuffer;
import java.io.IOException;

public interface ReportDataUploadServices {
    public enum DataType {
        compensationHistory, positionHistory, currentInformation
    }

    void store(CompletedFileUpload file) throws IOException, BadArgException;

    ByteBuffer get(DataType dataType) throws NotFoundException;
}
