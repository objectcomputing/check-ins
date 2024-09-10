package com.objectcomputing.checkins.services.reports;

import com.objectcomputing.checkins.exceptions.NotFoundException;
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

public class CurrentInformation extends CSVProcessor {

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

  @Override
  protected void loadImpl(MemberProfileRepository memberProfileRepository,
                          CSVParser csvParser) throws BadArgException {
    information.clear();
    for (CSVRecord csvRecord : csvParser) {
      try {
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
      } catch(IllegalArgumentException ex) {
        throw new BadArgException("Unable to parse the current information");
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
