package com.objectcomputing.checkins.services.reports;

import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.kudos.Kudos;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@Introspected
public class ReportDataDTO {

    @NotNull
    private UUID memberId;

    @NotNull
    private UUID reviewPeriodId;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    @NotNull
    private MemberProfile memberProfile;

    @NotNull
    private List<Kudos> kudos;

    @NotNull
    private List<CompensationHistory.Compensation> compensationHistory;

    @NotNull
    private CurrentInformation.Information currentInformation;

    @NotNull
    private List<PositionHistory.Position> positionHistory;

    @NotNull
    private List<Feedback> feedback;
}
