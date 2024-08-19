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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Optional;
import java.util.stream.Collectors;

public class CompensationHistory {

    public class Compensation {
      private UUID memberId;
      private LocalDate startDate;
      private float amount;

      public Compensation(UUID memberId, LocalDate startDate, float amount) {
      	  this.memberId = memberId;
          this.startDate = startDate;
          this.amount = amount;
      }

      public UUID getMemberId() {
          return memberId;
      }

      public LocalDate getStartDate() {
          return startDate;
      }

      public float getAmount() {
          return amount;
      }
    }

    private static final Logger LOG = LoggerFactory.getLogger(CompensationHistory.class);
    private List<Compensation> history = new ArrayList<Compensation>();

    public CompensationHistory() {
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
        for (CSVRecord csvRecord : csvParser) {
            String emailAddress = csvRecord.get("emailAddress");
            Optional<MemberProfile> memberProfile =
                memberProfileRepository.findByWorkEmail(emailAddress);
            if (memberProfile.isPresent()) {
                Compensation comp = new Compensation(
                    memberProfile.get().getId(),
                    LocalDate.parse(csvRecord.get("startDate"), formatter),
                    Float.parseFloat(csvRecord.get("compensation")));
                history.add(comp);
            } else {
                LOG.error("Unable to find a profile for " + emailAddress);
            }
        }
    }

    public List<Compensation> getHistory(UUID memberId) {
        return history.stream()
               .filter(entry -> entry.getMemberId().equals(memberId))
               .collect(Collectors.toList());
    }
}
