package com.objectcomputing.checkins.services.memberprofile.csvreport;

import com.objectcomputing.checkins.security.permissions.Permissions;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileUtils;
import com.objectcomputing.checkins.services.permissions.RequiredPermission;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Post;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import jakarta.inject.Named;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.concurrent.ExecutorService;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@Controller("/services/reports/member")
@Secured(SecurityRule.IS_AUTHENTICATED)
public class MemberProfileReportController {

    private static final Logger LOG = LoggerFactory.getLogger(com.objectcomputing.checkins.services.memberprofile.MemberProfileController.class);
    private final MemberProfileServices memberProfileServices;
    private final MemberProfileReportServices memberProfileReportServices;
    private final Scheduler ioScheduler;

    public MemberProfileReportController(MemberProfileServices memberProfileServices,
                                         MemberProfileReportServices memberProfileReportServices,
                                         @Named(TaskExecutors.IO) ExecutorService ioExecutorService) {
        this.memberProfileServices = memberProfileServices;
        this.memberProfileReportServices = memberProfileReportServices;
        this.ioScheduler = Schedulers.fromExecutorService(ioExecutorService);
    }

    @Get(uri = "/csv", produces = MediaType.TEXT_CSV)
    @RequiredPermission(Permissions.CAN_VIEW_PROFILE_REPORT)
    public Mono<MutableHttpResponse<File>> generateCsv() {
        return Flux.defer(() -> Flux.fromIterable(memberProfileServices.findByValues(null, null, null, null, null, null, false)))
                .subscribeOn(ioScheduler)
                .map(this::mapToCsvRecord)
                .collectList()
                .flatMap(this::generateCsvFile)
                .map(file -> HttpResponse.ok(file).header("Content-Disposition", "attachment; filename=member_profiles.csv"))
                .onErrorResume(error -> {
                    LOG.error("Something went terribly wrong during export... ", error);
                    return Mono.just(HttpResponse.serverError());
                });
    }

    /**
     * Read-only POST to mimic a GET with many parameters
     * @param dto The {@link MemberProfileReportQueryDTO} containing the UUIDs of the members to include in the generated CSV
     * @return HTTP response with the CSV file
     */
    @Post(produces = MediaType.TEXT_CSV)
    @RequiredPermission(Permissions.CAN_VIEW_PROFILE_REPORT)
    public Mono<MutableHttpResponse<File>> getCsvFile(@Body MemberProfileReportQueryDTO dto) {
        System.out.println(dto);
        return Mono.defer(() -> Mono.just(memberProfileReportServices.generateFile(dto)))
                .subscribeOn(ioScheduler)
                .map(file -> HttpResponse
                        .ok(file)
                        .header("Content-Disposition", String.format("attachment; filename=%s", file.getName())))
                .onErrorResume(error -> {
                    LOG.error("Something went terribly wrong during export... ", error);
                    return Mono.just(HttpResponse.serverError());
                });
    }

    private MemberProfileRecord mapToCsvRecord(MemberProfile profile) {
        MemberProfile pdlProfile = profile.getPdlId() != null ? memberProfileServices.getById(profile.getPdlId()) : null;
        MemberProfile supervisorProfile = profile.getSupervisorid() != null ? memberProfileServices.getById(profile.getSupervisorid()) : null;

        String pdlName = "", pdlEmail = "";
        if(pdlProfile != null) {
            pdlName = MemberProfileUtils.getFullName(pdlProfile);
            pdlEmail = pdlProfile.getWorkEmail();
        }
        String supervisorName = "", supervisorEmail = "";
        if(supervisorProfile != null) {
            supervisorName = MemberProfileUtils.getFullName(supervisorProfile);
            supervisorEmail = supervisorProfile.getWorkEmail();
        }

        LocalDate startDate = profile.getStartDate();

        String tenure = "";
        if(startDate != null) {
            LocalDate currentDate = LocalDate.now();
            Period period = Period.between(startDate, currentDate);
            int years = period.getYears();
            int months = period.getMonths();

            tenure = years + " years " + months + " months";
        }

        return new MemberProfileRecord(profile.getFirstName(),
                profile.getLastName(),
                profile.getTitle(),
                profile.getLocation(),
                profile.getWorkEmail(),
                startDate,
                tenure,
                pdlName,
                pdlEmail,
                supervisorName,
                supervisorEmail);
    }

    private Mono<File> generateCsvFile(List<MemberProfileRecord> memberProfileRecords) {
        File csvFile = new File("member_profiles.csv");

        try (CSVPrinter csvPrinter = new CSVPrinter(new FileWriter(csvFile, StandardCharsets.UTF_8), CSVFormat.DEFAULT.withHeader("First Name", "Last Name", "Title", "Location",
                "Work Email", "Start Date", "Tenure",
                "PDL Name", "PDL Email", "Supervisor Name", "Supervisor Email"))) {
            for (MemberProfileRecord memberProfileRecord : memberProfileRecords) {
                csvPrinter.printRecord(memberProfileRecord.getFirstName(),
                        memberProfileRecord.getLastName(),
                        memberProfileRecord.getTitle(),
                        memberProfileRecord.getLocation(),
                        memberProfileRecord.getWorkEmail(),
                        memberProfileRecord.getStartDate(),
                        memberProfileRecord.getTenure(),
                        memberProfileRecord.getPdlName(),
                        memberProfileRecord.getPdlEmail(),
                        memberProfileRecord.getSupervisorName(),
                        memberProfileRecord.getSupervisorEmail());
            }
        } catch (IOException e) {
            throw new RuntimeException("Error generating CSV file", e);
        }

        return Mono.just(csvFile);
    }
}

