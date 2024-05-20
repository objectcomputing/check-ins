package com.objectcomputing.checkins.services.memberprofile.retentionreport;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class RetentionReportResponseDTO {

    @Schema(description = "Total retention rate for specified period")
    private List<Interval> totalTwelveMonthRetentionRate;

    @Schema(description = "Voluntary retention rate for specified period")
    private List<Interval> voluntaryTwelveMonthRetentionRate;

    @Schema(description = "New hire retention rate for specified period")
    private List<Interval> newHireRetentionRate;

    @Schema(description = "Total turnover rate for specified period")
    private List<Interval> totalTwelveMonthTurnoverRate;

    @Schema(description = "Voluntary turnover rate for specified period")
    private List<Interval> voluntaryTwelveMonthTurnoverRate;

}
