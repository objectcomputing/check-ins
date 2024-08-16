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
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    @NotNull
    private MemberProfile memberProfile;

    @Nullable
    private List<Kudos> kudos;

    @Nullable
    private List<CompensationHistory.Compensation> compensationHistory;
}
