package com.objectcomputing.checkins.services.memberprofile.csvreport;

import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import jakarta.inject.Named;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
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
    public Mono<MutableHttpResponse<byte[]>> generateCsv() {
        return Flux.defer(() -> Flux.fromIterable(memberProfileServices.findByValues(null, null, null, null, null, null, false)))
                .subscribeOn(ioScheduler)
                .map(this::mapToCsvRecord)
                .collectList()
                .map(this::generateCsvContent)
                .map(csvBytes -> HttpResponse.ok(csvBytes).header("Content-Disposition", "attachment; filename=member_profiles.csv"))
                .onErrorResume(error -> Mono.just(HttpResponse.serverError()));
    }

    private CsvRecord mapToCsvRecord(MemberProfile profile) {
        String pdlName = ""; // Get PDL name based on pdlId
        String supervisorName = ""; // Get Supervisor name based on supervisorid

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
                supervisorName);
    }

    private byte[] generateCsvContent(List<CsvRecord> csvRecords) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        CSVFormat csvFormat = CSVFormat.DEFAULT.withHeader("First Name", "Last Name", "Title", "Location",
                "Work Email", "Start Date", "Tenure",
                "PDL Name", "Supervisor Name");

        try (CSVPrinter csvPrinter = new CSVPrinter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8), csvFormat)) {
            for (CsvRecord csvRecord : csvRecords) {
                csvPrinter.printRecord(csvRecord.getFirstName(),
                        csvRecord.getLastName(),
                        csvRecord.getTitle(),
                        csvRecord.getLocation(),
                        csvRecord.getWorkEmail(),
                        csvRecord.getStartDate(),
                        csvRecord.getTenure(),
                        csvRecord.getPdlName(),
                        csvRecord.getSupervisorName());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return outputStream.toByteArray();
    }
}

