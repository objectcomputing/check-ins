package com.objectcomputing.checkins.services.reports;

import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.exceptions.BadArgException;

import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import io.micronaut.core.annotation.Introspected;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.*;

public class CompensationHistory extends CSVProcessor {

    @Introspected
    public record Compensation(
            UUID memberId,
            LocalDate startDate,
            String amount,
            String totalComp
    ) {
    }

    private static final Logger LOG = LoggerFactory.getLogger(CompensationHistory.class);
    private final List<Compensation> history = new ArrayList<>();

    @Override
    protected void loadImpl(MemberProfileServices memberProfileServices,
                            CSVParser csvParser) throws BadArgException {
        history.clear();
        for (CSVRecord csvRecord : csvParser) {
            String emailAddress = null;
            try {
                emailAddress = csvRecord.get("emailAddress");
                MemberProfile memberProfile = memberProfileServices.findByWorkEmail(emailAddress);
                String startDate = csvRecord.get("startDate");
                LocalDate date = parseDate(startDate);
                if (date == null) {
                    LOG.error("Unable to parse date: {}", startDate);
                } else {
                    String value = csvRecord.get("compensation");
                    Compensation comp = new Compensation(
                            memberProfile.getId(),
                            date,
                            value == null ? null : value.replaceAll("[^\\d\\.,]", ""),
                            csvRecord.get("totalComp")
                    );
                    history.add(comp);
                }
            } catch (NotFoundException nfe) {
                LOG.error("Unable to find a profile for {}", emailAddress);
            } catch (IllegalArgumentException ex) {
                throw new BadArgException("Unable to parse the compensation history");
            }
        }
    }

    public List<Compensation> getHistory(UUID memberId) {
        return history.stream()
                .filter(entry -> entry.memberId().equals(memberId))
                .toList();
    }
}
