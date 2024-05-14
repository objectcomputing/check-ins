package com.objectcomputing.checkins.services.today;

import com.objectcomputing.checkins.services.memberprofile.anniversaryreport.AnniversaryReportResponseDTO;
import com.objectcomputing.checkins.services.memberprofile.birthday.BirthDayResponseDTO;
import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Introspected
public class TodayResponseDTO {
    @NotNull
    @Schema(description = "Today's birthdays")
    private List<BirthDayResponseDTO> birthdays;

    @NotNull
    @Schema(description = "Today's anniversaries")
    private List<AnniversaryReportResponseDTO> anniversaries;

}
