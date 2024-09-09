package com.objectcomputing.checkins.services.reports;

import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;

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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Optional;
import java.util.stream.Collectors;

public class CurrentInformation {

    @AllArgsConstructor
    @Getter
    public class Information {
      private UUID memberId;
      private float salary;
      private String range;
      private String nationalRange;
      private String biography;
      private String commitments;
    }

    private static final Logger LOG = LoggerFactory.getLogger(CurrentInformation.class);
    private List<Information> information = new ArrayList<Information>();

    public CurrentInformation() {
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

        information.clear();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
        for (CSVRecord csvRecord : csvParser) {
            String emailAddress = csvRecord.get("emailAddress");
            Optional<MemberProfile> memberProfile =
                memberProfileRepository.findByWorkEmail(emailAddress);
            if (memberProfile.isPresent()) {
                Information comp = new Information(
                    memberProfile.get().getId(),
                    Float.parseFloat(csvRecord.get("salary")
                                              .replaceAll("[^\\d\\.,]", "")),
                    csvRecord.get("range"),
                    csvRecord.get("nationalRange"),
                    csvRecord.get("biography"),
                    csvRecord.get("commitments")
                );
                information.add(comp);
            } else {
                LOG.error("Unable to find a profile for " + emailAddress);
            }
        }
    }

    public Information getInformation(UUID memberId) {
        // There should only be one entry per member.
        List<Information> list = information.stream()
               .filter(entry -> entry.getMemberId().equals(memberId))
               .collect(Collectors.toList());
        if (list.isEmpty()) {
          throw new NotFoundException("Current Information not found for member: " + memberId);
        } else {
          return list.get(0);
        }
    }
}