package com.objectcomputing.checkins.services.reports;

import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.exceptions.BadArgException;

import io.micronaut.core.annotation.Introspected;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import lombok.AllArgsConstructor;
import lombok.Getter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Optional;
import java.util.stream.Collectors;

public class CompensationHistory extends CSVProcessor {

    @Introspected
    public record Compensation(
            UUID memberId,
            LocalDate startDate,
            float amount
    ) {
    }

    private static final Logger LOG = LoggerFactory.getLogger(CompensationHistory.class);
    private final List<Compensation> history = new ArrayList<>();

    @Override
    protected void loadImpl(MemberProfileRepository memberProfileRepository,
                            CSVParser csvParser) throws BadArgException {
      history.clear();
      for (CSVRecord csvRecord : csvParser) {
        try {
          String emailAddress = csvRecord.get("emailAddress");
          Optional<MemberProfile> memberProfile =
              memberProfileRepository.findByWorkEmail(emailAddress);
          if (memberProfile.isPresent()) {
            LocalDate date = parseDate(csvRecord.get("startDate"));
            if (date == null) {
              LOG.error("Unable to parse date: " + csvRecord.get("startDate"));
            } else {
              Compensation comp = new Compensation(
                      memberProfile.get().getId(),
                      date,
                      Float.parseFloat(csvRecord.get("compensation")
                                                .replaceAll("[^\\d\\.,]", "")));
              history.add(comp);
            }
          } else {
            LOG.error("Unable to find a profile for " + emailAddress);
          }
        } catch(IllegalArgumentException ex) {
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
