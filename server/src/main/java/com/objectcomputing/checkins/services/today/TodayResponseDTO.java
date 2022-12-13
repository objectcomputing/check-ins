package com.objectcomputing.checkins.services.today;

import com.objectcomputing.checkins.services.memberprofile.anniversaryreport.AnniversaryReportResponseDTO;
import com.objectcomputing.checkins.services.memberprofile.birthday.BirthDayResponseDTO;
import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Introspected
public class TodayResponseDTO {
    @NotBlank
    @Schema(description = "Today's birthdays", required = true)
    private List<BirthDayResponseDTO> birthdays;

    @NotBlank
    @Schema(description = "Today's anniversaries", required = true)
    private List<AnniversaryReportResponseDTO> anniversaries;

    public List<BirthDayResponseDTO> getBirthdays() {
        return birthdays;
    }

    public void setBirthdays(List<BirthDayResponseDTO> birthdays) {
        this.birthdays = birthdays;
    }

    public List<AnniversaryReportResponseDTO> getAnniversaries() {
        return anniversaries;
    }

    public void setAnniversaries(List<AnniversaryReportResponseDTO> anniversaries) {
        this.anniversaries = anniversaries;
    }
}
