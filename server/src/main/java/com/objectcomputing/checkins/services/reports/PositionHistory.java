package com.objectcomputing.checkins.services.reports;

import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.exceptions.BadArgException;

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

public class PositionHistory extends CSVProcessor {

  @AllArgsConstructor
  @Getter
  public class Position {
    private UUID memberId;
    private LocalDate date;
    private String title;
  }

  private static final Logger LOG = LoggerFactory.getLogger(PositionHistory.class);
  private List<Position> history = new ArrayList<Position>();

  public PositionHistory() {
  }

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
          LocalDate date = parseDate(csvRecord.get("date"));
          if (date == null) {
            LOG.error("Unable to parse date: " + csvRecord.get("date"));
          } else {
            Position position = new Position(
                       memberProfile.get().getId(),
                       date,
                       csvRecord.get("title"));
            history.add(position);
          }
        } else {
          LOG.error("Unable to find a profile for " + emailAddress);
        }
      } catch(IllegalArgumentException ex) {
        throw new BadArgException("Unable to parse the position history");
      }
    }
  }

  public List<Position> getHistory(UUID memberId) {
    return history.stream()
             .filter(entry -> entry.getMemberId().equals(memberId))
             .collect(Collectors.toList());
  }
}
