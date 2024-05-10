package com.objectcomputing.checkins.services.memberprofile.csvreport;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Introspected
public class MemberProfileReportQueryDTO {

    @NotNull
    @Schema(description = "list of member UUIDs to include in the query")
    private List<UUID> memberIds;

    public MemberProfileReportQueryDTO() {}

}
