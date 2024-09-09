package com.objectcomputing.checkins.services.reports;

import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.exceptions.BadArgException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import lombok.AllArgsConstructor;
import lombok.Getter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Optional;
import java.util.stream.Collectors;

public class CompensationHistory {

    @AllArgsConstructor
    @Getter
    public class Compensation {
      private UUID memberId;
      private LocalDate startDate;
      private float amount;
    }

    private static final Logger LOG = LoggerFactory.getLogger(CompensationHistory.class);
    private List<Compensation> history = new ArrayList<Compensation>();

    public CompensationHistory() {
    }

    public void load(MemberProfileRepository memberProfileRepository,
                     ByteBuffer dataSource) throws IOException, BadArgException {
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
               .filter(entry -> entry.getMemberId().equals(memberId))
               .collect(Collectors.toList());
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
}
