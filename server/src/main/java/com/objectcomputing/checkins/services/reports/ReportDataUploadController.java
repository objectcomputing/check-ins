package com.objectcomputing.checkins.services.reports;

import com.objectcomputing.checkins.exceptions.NotFoundException;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.multipart.CompletedFileUpload;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.http.annotation.Part;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.io.IOException;

@Controller("/services/report/data")
@ExecuteOn(TaskExecutors.BLOCKING)
@Secured(SecurityRule.IS_AUTHENTICATED)
public class ReportDataUploadController {
    private static final Logger LOG = LoggerFactory.getLogger(ReportDataUploadController.class);
    private final ReportDataUploadServices reportDataUploadServices;

    public ReportDataUploadController(ReportDataUploadServices reportDataUploadServices) {
        this.reportDataUploadServices = reportDataUploadServices;
    }

    /**
     * Parse the CSV file and store it to employee hours table
     * @param file
     * @{@link HttpResponse<ReportDataUploadResponseDTO>}
     */
    @Post(uri="/upload" , consumes = MediaType.MULTIPART_FORM_DATA)
    public Mono<List<String>> upload(@Part("file") Publisher<CompletedFileUpload> file){
        return Flux.from(file)
                .subscribeOn(Schedulers.boundedElastic())
                .map(part -> {
                    try {
                        reportDataUploadServices.store(part);
                        return part.getFilename();
                    } catch(IOException ex) {
                        LOG.error(ex.toString());
                        return "";
                    }
                })
                .collectList();
    }
}
