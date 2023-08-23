package com.objectcomputing.checkins.services.memberprofile.csvreport;

import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileUtils;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpResponse;
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
    private final Scheduler ioScheduler;

    public MemberProfileReportController(MemberProfileServices memberProfileServices,
                                         @Named(TaskExecutors.IO) ExecutorService ioExecutorService) {
        this.memberProfileServices = memberProfileServices;
        this.ioScheduler = Schedulers.fromExecutorService(ioExecutorService);
    }

    @Get(uri = "/csv", produces = MediaType.TEXT_CSV)
    public Mono<MutableHttpResponse<File>> generateCsv() {
        return Flux.defer(() -> Flux.fromIterable(memberProfileServices.findByValues(null, null, null, null, null, null, false)))
                .subscribeOn(ioScheduler)
                .map(this::mapToCsvRecord)
                .collectList()
                .flatMap(this::generateCsvFile)
                .map(file -> HttpResponse.ok(file).header("Content-Disposition", "attachment; filename=member_profiles.csv"))
                .onErrorResume(error -> Mono.just(HttpResponse.serverError()));
    }

    private CsvRecord mapToCsvRecord(MemberProfile profile) {
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
        LocalDate currentDate = LocalDate.now();

        Period period = Period.between(startDate, currentDate);
        int years = period.getYears();
        int months = period.getMonths();

        String tenure = years + " years " + months + " months";

        return new CsvRecord(profile.getFirstName(),
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

    private Mono<File> generateCsvFile(List<CsvRecord> csvRecords) {
        File csvFile = new File("member_profiles.csv");

        try (CSVPrinter csvPrinter = new CSVPrinter(new FileWriter(csvFile, StandardCharsets.UTF_8), CSVFormat.DEFAULT.withHeader("First Name", "Last Name", "Title", "Location",
                "Work Email", "Start Date", "Tenure",
                "PDL Name", "PDL Email", "Supervisor Name", "Supervisor Email"))) {
            for (CsvRecord csvRecord : csvRecords) {
                csvPrinter.printRecord(csvRecord.getFirstName(),
                        csvRecord.getLastName(),
                        csvRecord.getTitle(),
                        csvRecord.getLocation(),
                        csvRecord.getWorkEmail(),
                        csvRecord.getStartDate(),
                        csvRecord.getTenure(),
                        csvRecord.getPdlName(),
                        csvRecord.getPdlEmail(),
                        csvRecord.getSupervisorName(),
                        csvRecord.getSupervisorEmail());
            }
        } catch (IOException e) {
            throw new RuntimeException("Error generating CSV file", e);
        }

        return Mono.just(csvFile);
    }
}

