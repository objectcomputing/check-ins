package com.objectcomputing.checkins.services.reports;

import com.objectcomputing.checkins.exceptions.NotFoundException;

import io.micronaut.http.multipart.CompletedFileUpload;

import java.nio.ByteBuffer;
import java.io.IOException;

public interface ReportDataServices {
    public enum DataType {
        compensationHistory, positionHistory, currentInformation
    }

    void store(DataType dataType, CompletedFileUpload file) throws IOException;

    ByteBuffer get(DataType dataType) throws NotFoundException;
}
