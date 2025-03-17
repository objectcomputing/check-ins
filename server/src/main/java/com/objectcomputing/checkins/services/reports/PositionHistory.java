package com.objectcomputing.checkins.services.reports;

import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.exceptions.BadArgException;

import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import io.micronaut.core.annotation.Introspected;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.*;

public class PositionHistory extends CSVProcessor {

    @Introspected
    public record Position(
            UUID memberId,
            LocalDate date,
            String title
    ) {
    }

    private static final Logger LOG = LoggerFactory.getLogger(PositionHistory.class);
    private final List<Position> history = new ArrayList<>();

    @Override
    protected void loadImpl(MemberProfileServices memberProfileServices,
                            CSVParser csvParser) throws BadArgException {
        history.clear();
        for (CSVRecord csvRecord : csvParser) {
            String emailAddress = null;
            try {
                emailAddress = csvRecord.get("emailAddress");
                MemberProfile memberProfile = memberProfileServices.findByWorkEmail(emailAddress);
                String csvDate = csvRecord.get("date");
                LocalDate date = parseDate(csvDate);
                if (date == null) {
                    LOG.error("Unable to parse date: {}", csvDate);
                } else {
                    Position position = new Position(
                            memberProfile.getId(),
                            date,
                            csvRecord.get("title"));
                    history.add(position);
                }
            } catch (NotFoundException nfe) {
                LOG.error("Unable to find a profile for {}", emailAddress);
            } catch (IllegalArgumentException ex) {
                throw new BadArgException("Unable to parse the position history");
            }
        }
    }

    public List<Position> getHistory(UUID memberId) {
        return history.stream()
                .filter(entry -> entry.memberId().equals(memberId))
                .toList();
    }
}
