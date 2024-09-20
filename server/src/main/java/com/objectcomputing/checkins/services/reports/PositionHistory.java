package com.objectcomputing.checkins.services.reports;

import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.exceptions.BadArgException;

import io.micronaut.core.annotation.Introspected;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Optional;

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
  protected void loadImpl(MemberProfileRepository memberProfileRepository,
                          CSVParser csvParser) throws BadArgException {
    history.clear();
    for (CSVRecord csvRecord : csvParser) {
      try {
        String emailAddress = csvRecord.get("emailAddress");
        Optional<MemberProfile> memberProfile =
              memberProfileRepository.findByWorkEmail(emailAddress);
        if (memberProfile.isPresent()) {
          String csvDate = csvRecord.get("date");
          LocalDate date = parseDate(csvDate);
          if (date == null) {
            LOG.error("Unable to parse date: {}", csvDate);
          } else {
            Position position = new Position(
                       memberProfile.get().getId(),
                       date,
                       csvRecord.get("title"));
            history.add(position);
          }
        } else {
          LOG.error("Unable to find a profile for {}", emailAddress);
        }
      } catch(IllegalArgumentException ex) {
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