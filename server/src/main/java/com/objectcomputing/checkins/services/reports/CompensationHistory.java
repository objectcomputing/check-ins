package com.objectcomputing.checkins.services.reports;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.nio.ByteBuffer;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CompensationHistory {

    public class Compensation {
      private LocalDate startDate;
      private float amount;

      public Compensation(LocalDate startDate, float amount) {
          this.startDate = startDate;
          this.amount = amount;
      }

      public LocalDate getStartDate() {
          return startDate;
      }

      public float getAmount() {
          return amount;
      }
    }

    private List<Compensation> history = new ArrayList<Compensation>();

    public CompensationHistory() {
    }

    public void load(ByteBuffer dataSource) throws IOException {
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
            Compensation comp = new Compensation(
                LocalDate.parse(csvRecord.get("startDate"), formatter),
                Float.parseFloat(csvRecord.get("compensation")));
            history.add(comp);
        }
    }

    public List<Compensation> getHistory() {
        return history;
    }
}
