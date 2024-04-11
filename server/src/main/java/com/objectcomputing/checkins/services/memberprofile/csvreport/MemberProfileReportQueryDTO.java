package com.objectcomputing.checkins.services.memberprofile.csvreport;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Introspected
public class MemberProfileReportQueryDTO {

    @NotNull
    @Schema(description = "list of member UUIDs to include in the query")
    private List<UUID> memberIds;

    public MemberProfileReportQueryDTO() {}

    public List<UUID> getMemberIds() {
        return memberIds;
    }

    public void setMemberIds(List<UUID> memberIds) {
        this.memberIds = memberIds;
    }
}
