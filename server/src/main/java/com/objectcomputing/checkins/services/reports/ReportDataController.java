package com.objectcomputing.checkins.services.reports;

import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.kudos.KudosRepository;
import com.objectcomputing.checkins.services.kudos.kudos_recipient.KudosRecipientRepository;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.services.reviews.ReviewPeriodServices;
import com.objectcomputing.checkins.services.feedback_template.FeedbackTemplateServices;
import com.objectcomputing.checkins.services.feedback_request.FeedbackRequestServices;
import com.objectcomputing.checkins.services.feedback_answer.FeedbackAnswerServices;
import com.objectcomputing.checkins.services.feedback_template.template_question.TemplateQuestionServices;
import com.objectcomputing.checkins.services.employee_hours.EmployeeHoursServices;
import com.objectcomputing.checkins.services.file.FileServices;

import io.micronaut.http.MediaType;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Body;
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
import jakarta.validation.Valid;

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
    private final MemberProfileServices memberProfileServices;
    private final ReviewPeriodServices reviewPeriodServices;
    private final FeedbackTemplateServices feedbackTemplateServices;
    private final FeedbackRequestServices feedbackRequestServices;
    private final FeedbackAnswerServices feedbackAnswerServices;
    private final TemplateQuestionServices templateQuestionServices;
    private final EmployeeHoursServices employeeHoursServices;
    private final FileServices fileServices;

    public ReportDataController(ReportDataServices reportDataServices,
                          KudosRepository kudosRepository,
                          KudosRecipientRepository kudosRecipientRepository,
                          MemberProfileServices memberProfileServices,
                          ReviewPeriodServices reviewPeriodServices,
                          FeedbackTemplateServices feedbackTemplateServices,
                          FeedbackRequestServices feedbackRequestServices,
                          FeedbackAnswerServices feedbackAnswerServices,
                          TemplateQuestionServices templateQuestionServices,
                          EmployeeHoursServices employeeHoursServices,
                          FileServices fileServices) {
        this.reportDataServices = reportDataServices;
        this.kudosRepository = kudosRepository;
        this.kudosRecipientRepository = kudosRecipientRepository;
        this.memberProfileServices = memberProfileServices;
        this.reviewPeriodServices = reviewPeriodServices;
        this.feedbackTemplateServices = feedbackTemplateServices;
        this.feedbackRequestServices = feedbackRequestServices;
        this.feedbackAnswerServices = feedbackAnswerServices;
        this.templateQuestionServices = templateQuestionServices;
        this.employeeHoursServices = employeeHoursServices;
        this.fileServices = fileServices;
    }

    @Post(uri="/upload", consumes = MediaType.MULTIPART_FORM_DATA)
    public Mono<List<List<String>>> uploadDataFiles(
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

    @Post(uri="/generate")
    public HttpStatus generate(@Body @Valid ReportDataDTO dto) {
        MarkdownGeneration markdown =
                new MarkdownGeneration(reportDataServices,
                                       kudosRepository,
                                       kudosRecipientRepository,
                                       memberProfileServices,
                                       reviewPeriodServices,
                                       feedbackTemplateServices,
                                       feedbackRequestServices,
                                       feedbackAnswerServices,
                                       templateQuestionServices,
                                       employeeHoursServices,
                                       fileServices);
        markdown.upload(dto.getMemberIds(), dto.getReviewPeriodId());
        return HttpStatus.OK;
    }
}
