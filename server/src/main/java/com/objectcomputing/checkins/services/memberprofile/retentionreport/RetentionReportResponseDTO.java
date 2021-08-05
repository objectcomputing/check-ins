package com.objectcomputing.checkins.services.memberprofile.retentionreport;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public class RetentionReportResponseDTO {

    @Schema(description = "Total retention rate for specified period")
    List<Interval> totalTwelveMonthRetentionRate;

    @Schema(description = "Voluntary retention rate for specified period")
    List<Interval> voluntaryTwelveMonthRetentionRate;

    @Schema(description = "New hire retention rate for specified period")
    List<Interval> newHireRetentionRate;

    @Schema(description = "Total turnover rate for specified period")
    List<Interval> totalTwelveMonthTurnoverRate;

    @Schema(description = "Voluntary turnover rate for specified period")
    List<Interval> voluntaryTwelveMonthTurnoverRate;

    public List<Interval> getTotalTwelveMonthRetentionRate() {
        return totalTwelveMonthRetentionRate;
    }

    public void setTotalTwelveMonthRetentionRate(List<Interval> totalTwelveMonthRetentionRate) {
        this.totalTwelveMonthRetentionRate = totalTwelveMonthRetentionRate;
    }

    public List<Interval> getVoluntaryTwelveMonthRetentionRate() {
        return voluntaryTwelveMonthRetentionRate;
    }

    public void setVoluntaryTwelveMonthRetentionRate(List<Interval> voluntaryTwelveMonthRetentionRate) {
        this.voluntaryTwelveMonthRetentionRate = voluntaryTwelveMonthRetentionRate;
    }

    public List<Interval> getNewHireRetentionRate() {
        return newHireRetentionRate;
    }

    public void setNewHireRetentionRate(List<Interval> newHireRetentionRate) {
        this.newHireRetentionRate = newHireRetentionRate;
    }

    public List<Interval> getTotalTwelveMonthTurnoverRate() {
        return totalTwelveMonthTurnoverRate;
    }

    public void setTotalTwelveMonthTurnoverRate(List<Interval> totalTwelveMonthTurnoverRate) {
        this.totalTwelveMonthTurnoverRate = totalTwelveMonthTurnoverRate;
    }

    public List<Interval> getVoluntaryTwelveMonthTurnoverRate() {
        return voluntaryTwelveMonthTurnoverRate;
    }

    public void setVoluntaryTwelveMonthTurnoverRate(List<Interval> voluntaryTwelveMonthTurnoverRate) {
        this.voluntaryTwelveMonthTurnoverRate = voluntaryTwelveMonthTurnoverRate;
    }
}
