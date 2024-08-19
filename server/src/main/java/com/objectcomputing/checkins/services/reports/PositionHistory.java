package com.objectcomputing.checkins.services.reports;

import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Optional;
import java.util.stream.Collectors;
import java.time.format.DateTimeParseException;

public class PositionHistory {

    public class Position {
      private UUID memberId;
      private LocalDate date;
      private String title;

      public Position(UUID memberId, LocalDate date, String title) {
      	  this.memberId = memberId;
          this.date = date;
          this.title = title;
      }

      public UUID getMemberId() {
          return memberId;
      }

      public LocalDate getDate() {
          return date;
      }

      public String getTitle() {
          return title;
      }
    }

    private static final Logger LOG = LoggerFactory.getLogger(PositionHistory.class);
    private List<Position> history = new ArrayList<Position>();

    public PositionHistory() {
    }

    public void load(MemberProfileRepository memberProfileRepository,
                     ByteBuffer dataSource) throws IOException {
        ByteArrayInputStream stream = new ByteArrayInputStream(dataSource.array());
        InputStreamReader input = new InputStreamReader(stream);
        CSVParser csvParser = CSVFormat.RFC4180
                    .builder()
                    .setHeader().setSkipHeaderRecord(true)
                    .setIgnoreSurroundingSpaces(true)
                    .setNullString("")
                    .build()
                    .parse(input);

        history.clear();
        for (CSVRecord csvRecord : csvParser) {
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
        }
    }

    private LocalDate parseDate(String date) {
      List<String> formatStrings = List.of("yyyy", "M/d/yyyy");
      for(String format: formatStrings) {
        try {
          return LocalDate.parse(date,
                                 new DateTimeFormatterBuilder()
                                 .appendPattern(format)
                                 .parseDefaulting(ChronoField.MONTH_OF_YEAR, 1)
                                 .parseDefaulting(ChronoField.DAY_OF_MONTH, 1)
                                 .toFormatter());
        } catch(DateTimeParseException ex) {
        }
      }
      return null;
    }

    public List<Position> getHistory(UUID memberId) {
        return history.stream()
               .filter(entry -> entry.getMemberId().equals(memberId))
               .collect(Collectors.toList());
    }
}
