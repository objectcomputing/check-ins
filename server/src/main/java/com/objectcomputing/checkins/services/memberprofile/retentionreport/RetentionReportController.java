package com.objectcomputing.checkins.services.memberprofile.retentionreport;

import com.objectcomputing.checkins.exceptions.BadArgException;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.net.URI;

@Controller("/reports/retention")
@ExecuteOn(TaskExecutors.BLOCKING)
@Secured(SecurityRule.IS_AUTHENTICATED)
@Tag(name = "retention-report")
public class RetentionReportController {

    private final RetentionReportServices retentionReportServices;

    public RetentionReportController(RetentionReportServices retentionReportServices) {
        this.retentionReportServices = retentionReportServices;
    }

    /**
     * Create a retention report from requested parameters
     *
     * @param requestBody {@link RetentionReportRequestDTO} Body of the request
     * @return {@link RetentionReportResponseDTO} Returned retention report
     */
    @Post
    public HttpResponse<RetentionReportResponseDTO> reportRetention(@Body @Valid @NotNull RetentionReportRequestDTO requestBody,
                                                                    HttpRequest<?> request) {
        if (requestBody.getStartDate().isAfter(requestBody.getEndDate()) ||
                requestBody.getStartDate().isEqual(requestBody.getEndDate())) {
            throw new BadArgException("Start date must be before end date");
        }

        RetentionReportResponseDTO responseBody = retentionReportServices.report(dtoFromRequest(requestBody));
        return HttpResponse.created(responseBody)
                .headers(headers -> headers.location(URI.create(String.format("%s", request.getPath()))));
    }

    private RetentionReportDTO dtoFromRequest(RetentionReportRequestDTO request) {
        RetentionReportDTO dto = new RetentionReportDTO();
        dto.setStartDate(request.getStartDate());
        dto.setEndDate(request.getEndDate());
        if (request.getFrequency() != null &&
                request.getFrequency().equalsIgnoreCase(FrequencyType.DAILY.toString())) {
            dto.setFrequency(FrequencyType.DAILY);
        } else if (request.getFrequency() != null &&
                request.getFrequency().equalsIgnoreCase(FrequencyType.WEEKLY.toString())) {
            dto.setFrequency(FrequencyType.WEEKLY);
        } else {
            dto.setFrequency(FrequencyType.MONTHLY);
        }
        return dto;
    }
}
