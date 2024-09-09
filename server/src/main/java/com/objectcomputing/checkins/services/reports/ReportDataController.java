package com.objectcomputing.checkins.services.reports;

import com.objectcomputing.checkins.services.permissions.Permission;
import com.objectcomputing.checkins.services.permissions.RequiredPermission;
import com.objectcomputing.checkins.services.kudos.KudosRepository;
import com.objectcomputing.checkins.services.kudos.kudos_recipient.KudosRecipientRepository;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.services.reviews.ReviewPeriodServices;
import com.objectcomputing.checkins.services.feedback_template.FeedbackTemplateServices;
import com.objectcomputing.checkins.services.feedback_request.FeedbackRequestServices;
import com.objectcomputing.checkins.services.feedback_answer.FeedbackAnswerServices;
import com.objectcomputing.checkins.services.feedback_template.template_question.TemplateQuestionServices;
import com.objectcomputing.checkins.services.employee_hours.EmployeeHoursServices;
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
    private final FeedbackTemplateServices feedbackTemplateServices;
    private final FeedbackRequestServices feedbackRequestServices;
    private final FeedbackAnswerServices feedbackAnswerServices;
    private final TemplateQuestionServices templateQuestionServices;
    private final EmployeeHoursServices employeeHoursServices;

    public ReportDataController(ReportDataServices reportDataServices,
                          KudosRepository kudosRepository,
                          KudosRecipientRepository kudosRecipientRepository,
                          MemberProfileRepository memberProfileRepository,
                          ReviewPeriodServices reviewPeriodServices,
                          FeedbackTemplateServices feedbackTemplateServices,
                          FeedbackRequestServices feedbackRequestServices,
                          FeedbackAnswerServices feedbackAnswerServices,
                          TemplateQuestionServices templateQuestionServices,
                          EmployeeHoursServices employeeHoursServices) {
        this.reportDataServices = reportDataServices;
        this.kudosRepository = kudosRepository;
        this.kudosRecipientRepository = kudosRecipientRepository;
        this.memberProfileRepository = memberProfileRepository;
        this.reviewPeriodServices = reviewPeriodServices;
        this.feedbackTemplateServices = feedbackTemplateServices;
        this.feedbackRequestServices = feedbackRequestServices;
        this.feedbackAnswerServices = feedbackAnswerServices;
        this.templateQuestionServices = templateQuestionServices;
        this.employeeHoursServices = employeeHoursServices;
    }

    @Post(uri="/upload", consumes = MediaType.MULTIPART_FORM_DATA)
    @RequiredPermission(Permission.CAN_CREATE_MERIT_REPORT)
    public Mono<List<List<String>>> upload(
                          @Part("comp") Publisher<CompletedFileUpload> comp,
                          @Part("curr") Publisher<CompletedFileUpload> curr,
                          @Part("pos") Publisher<CompletedFileUpload> pos) {
        // There is probably an easier and better way to do this!
        Mono<List<String>> compHist = Flux.from(comp)
          .subscribeOn(Schedulers.boundedElastic())
          .map(part -> uploadHelper(
                         ReportDataServices.DataType.compensationHistory, part)
          ).collectList();
        Mono<List<String>> currInfo = Flux.from(curr)
          .subscribeOn(Schedulers.boundedElastic())
          .map(part -> uploadHelper(
                         ReportDataServices.DataType.currentInformation, part)
          ).collectList();
        Mono<List<String>> posHist = Flux.from(pos)
          .subscribeOn(Schedulers.boundedElastic())
          .map(part -> uploadHelper(
                ReportDataServices.DataType.positionHistory, part)
          ).collectList();

        Flux<List<String>> merged = Flux.empty();
        merged = merged.mergeWith(compHist);
        merged = merged.mergeWith(currInfo);
        merged = merged.mergeWith(posHist);
        return merged.collectList();
    }

    private String uploadHelper(ReportDataServices.DataType dataType,
                                CompletedFileUpload file) {
      try {
        reportDataServices.store(dataType, file);
        return file.getFilename();
      } catch(IOException ex) {
        LOG.error(ex.toString());
        return "";
      }
    }

    @Get
    @RequiredPermission(Permission.CAN_CREATE_MERIT_REPORT)
    public List<ReportDataDTO> get(@NotNull List<UUID> memberIds,
                                   @NotNull UUID reviewPeriodId) {
        List<ReportDataDTO> list = new ArrayList<ReportDataDTO>();
        for (UUID memberId : memberIds) {
            ReportDataCollation data = new ReportDataCollation(
                                           memberId, reviewPeriodId,
                                           kudosRepository,
                                           kudosRecipientRepository,
                                           memberProfileRepository,
                                           reviewPeriodServices,
                                           reportDataServices,
                                           feedbackTemplateServices,
                                           feedbackRequestServices,
                                           feedbackAnswerServices,
                                           templateQuestionServices,
                                           employeeHoursServices);
            list.add(new ReportDataDTO(memberId, reviewPeriodId,
                                    data.getStartDate(), data.getEndDate(),
                                    data.getMemberProfile(), data.getKudos(),
                                    data.getCompensationHistory(),
                                    data.getCurrentInformation(),
                                    data.getPositionHistory(),
                                    data.getSelfReviews(),
                                    data.getReviews(),
                                    data.getFeedback(),
                                    data.getReportHours()));
        }
        return list;
    }
}
