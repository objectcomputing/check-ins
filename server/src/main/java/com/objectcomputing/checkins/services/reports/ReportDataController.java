package com.objectcomputing.checkins.services.reports;

import com.objectcomputing.checkins.services.kudos.KudosRepository;
import com.objectcomputing.checkins.services.kudos.kudos_recipient.KudosRecipientRepository;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.services.reviews.ReviewPeriodServices;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Get;
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
import jakarta.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.ArrayList;
import java.util.UUID;
import java.io.IOException;

@Controller("/services/report/data")
@ExecuteOn(TaskExecutors.BLOCKING)
@Secured(SecurityRule.IS_AUTHENTICATED)
public class ReportDataController {
    private static final Logger LOG = LoggerFactory.getLogger(ReportDataController.class);
    private final ReportDataServices reportDataServices;
    private final KudosRepository kudosRepository;
    private final KudosRecipientRepository kudosRecipientRepository;
    private final MemberProfileRepository memberProfileRepository;
    private final ReviewPeriodServices reviewPeriodServices;

    public ReportDataController(ReportDataServices reportDataServices,
                          KudosRepository kudosRepository,
                          KudosRecipientRepository kudosRecipientRepository,
                          MemberProfileRepository memberProfileRepository,
                          ReviewPeriodServices reviewPeriodServices) {
        this.reportDataServices = reportDataServices;
        this.kudosRepository = kudosRepository;
        this.kudosRecipientRepository = kudosRecipientRepository;
        this.memberProfileRepository = memberProfileRepository;
        this.reviewPeriodServices = reviewPeriodServices;
    }

    @Post(uri="/upload", consumes = MediaType.MULTIPART_FORM_DATA)
    public Mono<List<String>> upload(@Part("file") Publisher<CompletedFileUpload> file) {
        return Flux.from(file)
                .subscribeOn(Schedulers.boundedElastic())
                .map(part -> {
                    try {
                        reportDataServices.store(part);
                        return part.getFilename();
                    } catch(IOException ex) {
                        LOG.error(ex.toString());
                        return "";
                    }
                })
                .collectList();
    }

    @Get
    public List<ReportDataDTO> get(@NotNull List<UUID> memberIds, @NotNull UUID reviewPeriodId) {
        List<ReportDataDTO> list = new ArrayList<ReportDataDTO>();
        for (UUID memberId : memberIds) {
            ReportDataCollation data = new ReportDataCollation(
                                           memberId, reviewPeriodId,
                                           kudosRepository,
                                           kudosRecipientRepository,
                                           memberProfileRepository,
                                           reviewPeriodServices,
                                           reportDataServices);
            list.add(new ReportDataDTO(memberId, reviewPeriodId,
                                    data.getStartDate(), data.getEndDate(),
                                    data.getMemberProfile(), data.getKudos(),
                                    data.getCompensationHistory(),
                                    data.getCurrentInformation(),
                                    data.getPositionHistory()));
        }
        return list;
    }
}
