package com.objectcomputing.checkins.services.memberprofile.retentionreport;

import com.objectcomputing.checkins.exceptions.BadArgException;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.netty.channel.EventLoopGroup;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.concurrent.ExecutorService;

@Controller("/reports/retention")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "retention-report")
public class RetentionReportController {

    private final RetentionReportServices retentionReportServices;
    private final EventLoopGroup eventLoopGroup;
    private final ExecutorService executorService;

    public RetentionReportController(RetentionReportServices retentionReportServices,
                                     EventLoopGroup eventLoopGroup,
                                     ExecutorService executorService) {
        this.retentionReportServices = retentionReportServices;
        this.eventLoopGroup = eventLoopGroup;
        this.executorService = executorService;
    }

    /**
     * Create a retention report from requested parameters
     *
     * @param requestBody {@link RetentionReportRequestDTO} Body of the request
     * @return {@link RetentionReportResponseDTO} Returned retention report
     */
    @Post()
    public Single<HttpResponse<RetentionReportResponseDTO>> reportRetention(@Body @Valid @NotNull RetentionReportRequestDTO requestBody,
                                                                      HttpRequest<RetentionReportRequestDTO> request) {
        if (requestBody.getStartDate().isAfter(requestBody.getEndDate()) ||
                requestBody.getStartDate().isEqual(requestBody.getEndDate())) {
            throw new BadArgException("Start date must be before end date");
        }

        return Single.fromCallable(() -> retentionReportServices.report(dtoFromRequest(requestBody)))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(responseBody -> (HttpResponse<RetentionReportResponseDTO>) HttpResponse
                        .created(responseBody)
                        .headers(headers -> headers.location(URI.create(String.format("%s", request.getPath())))))
                .subscribeOn(Schedulers.from(executorService));
    }

    private RetentionReportDTO dtoFromRequest(RetentionReportRequestDTO request) {
        RetentionReportDTO dto = new RetentionReportDTO();
        dto.setStartDate(request.getStartDate());
        dto.setEndDate(request.getEndDate());
        if (request.getFrequency()!= null &&
                request.getFrequency().equalsIgnoreCase(FrequencyType.DAILY.toString())) {
            dto.setFrequency(FrequencyType.DAILY);
        } else if (request.getFrequency()!= null &&
                request.getFrequency().equalsIgnoreCase(FrequencyType.WEEKLY.toString())) {
            dto.setFrequency(FrequencyType.WEEKLY);
        } else {
            dto.setFrequency(FrequencyType.MONTHLY);
        }
        return dto;
    }
}
