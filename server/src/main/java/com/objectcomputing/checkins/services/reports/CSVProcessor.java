package com.objectcomputing.checkins.services.reports;

import com.objectcomputing.checkins.exceptions.BadArgException;

import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;

import java.nio.ByteBuffer;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.List;

abstract class CSVProcessor {

  public void load(MemberProfileServices memberProfileServices,
                   ByteBuffer dataSource) throws IOException,
                                                 BadArgException {
    ByteArrayInputStream stream = new ByteArrayInputStream(dataSource.array());
    InputStreamReader input = new InputStreamReader(stream);
    CSVParser csvParser = CSVFormat.RFC4180
                    .builder()
                    .setHeader().setSkipHeaderRecord(true)
                    .setIgnoreSurroundingSpaces(true)
                    .setNullString("")
                    .build()
                    .parse(input);
    loadImpl(memberProfileServices, csvParser);
  }

  protected abstract void loadImpl(MemberProfileServices memberProfileServices, CSVParser csvParser) throws BadArgException;

  protected LocalDate parseDate(String date) {
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
