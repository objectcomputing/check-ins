package com.objectcomputing.checkins.services.memberprofile.retentionreport;

import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;

import jakarta.inject.Singleton;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class RetentionReportServicesImpl implements RetentionReportServices {

    private final MemberProfileServices memberProfileServices;
    private final CurrentUserServices currentUserServices;

    public RetentionReportServicesImpl(MemberProfileServices memberProfileServices, CurrentUserServices currentUserServices) {
        this.memberProfileServices = memberProfileServices;
        this.currentUserServices = currentUserServices;
    }

    @Override
    public RetentionReportResponseDTO report(RetentionReportDTO request) {
        if (!currentUserServices.isAdmin()) {
            throw new PermissionException("Requires admin privileges");
        }

        RetentionReportResponseDTO response;
        List<MemberProfile> memberProfiles = memberProfileServices.findAll();
        LocalDate periodStartDate = getIntervalStartDate(request.getStartDate(), request.getFrequency());
        LocalDate periodEndDate = getIntervalEndDate(request.getEndDate(), request.getFrequency());
        if (request.getFrequency() == FrequencyType.DAILY) {
            response = getReportValues(periodStartDate, periodEndDate, memberProfiles, FrequencyType.DAILY);
        } else if (request.getFrequency() == FrequencyType.WEEKLY) {
            response = getReportValues(periodStartDate, periodEndDate, memberProfiles, FrequencyType.WEEKLY);
        } else {
            response = getReportValues(periodStartDate, periodEndDate, memberProfiles, FrequencyType.MONTHLY);
        }

        return response;
    }

    private LocalDate getIntervalStartDate(LocalDate periodStartDate, FrequencyType frequency) {
        LocalDate startDate;
        switch (frequency) {
            case FrequencyType.DAILY -> startDate = periodStartDate;
            case FrequencyType.WEEKLY -> {
                DayOfWeek day = periodStartDate.getDayOfWeek();
                startDate = switch (day) {
                    case SUNDAY -> periodStartDate.plusDays(6);
                    case MONDAY -> periodStartDate.plusDays(5);
                    case TUESDAY -> periodStartDate.plusDays(4);
                    case WEDNESDAY -> periodStartDate.plusDays(3);
                    case THURSDAY -> periodStartDate.plusDays(2);
                    case FRIDAY -> periodStartDate.plusDays(1);
                    default -> periodStartDate;
                };
            }
            default -> startDate = periodStartDate.withDayOfMonth(periodStartDate.lengthOfMonth());
        }

        return startDate;
    }

    private LocalDate getIntervalEndDate(LocalDate periodEndDate, FrequencyType frequency) {
        LocalDate endDate;
        if (frequency == FrequencyType.DAILY) {
            endDate = periodEndDate;
        } else if (frequency == FrequencyType.WEEKLY) {
            DayOfWeek day = periodEndDate.getDayOfWeek();
            endDate = switch (day) {
                case SUNDAY -> periodEndDate.minusDays(1);
                case MONDAY -> periodEndDate.minusDays(2);
                case TUESDAY -> periodEndDate.minusDays(3);
                case WEDNESDAY -> periodEndDate.minusDays(4);
                case THURSDAY -> periodEndDate.minusDays(5);
                case FRIDAY -> periodEndDate.minusDays(6);
                default -> periodEndDate.minusDays(7);
            };
        } else {
            LocalDate newDate = periodEndDate.minusMonths(1);
            endDate = newDate.withDayOfMonth(newDate.lengthOfMonth());
        }
        return endDate;
    }

    private RetentionReportResponseDTO getReportValues(LocalDate periodStartDate, LocalDate periodEndDate, List<MemberProfile> memberProfiles, FrequencyType frequency) {
        RetentionReportResponseDTO response = new RetentionReportResponseDTO();
        LocalDate dateToStudy = periodStartDate;
        List<Interval> retRate = new ArrayList<>();
        List<Interval> retRateVol = new ArrayList<>();
        List<Interval> retRateNewHire = new ArrayList<>();
        List<Interval> turnoverRate = new ArrayList<>();
        List<Interval> turnoverRateVol = new ArrayList<>();

        while (dateToStudy.isBefore(periodEndDate) || dateToStudy.isEqual(periodEndDate)) {
            // retention rate for interval
            Interval retInterval = new Interval(dateToStudy, getRetentionRate(dateToStudy, memberProfiles));
            retRate.add(retInterval);

            // voluntary retention rate for interval
            Interval volRetInterval = new Interval(dateToStudy, getVoluntaryRetentionRate(dateToStudy, memberProfiles));
            retRateVol.add(volRetInterval);

            // new hire retention for interval
            Interval newHireRetInterval = new Interval(dateToStudy, getNewHireRetentionRate(dateToStudy, memberProfiles));
            retRateNewHire.add(newHireRetInterval);

            // total turnover rate for interval
            Interval turnoverRateForInterval = new Interval(dateToStudy, getTurnoverRate(dateToStudy, memberProfiles, frequency));
            turnoverRate.add(turnoverRateForInterval);

            // voluntary turnover rate for interval
            Interval voluntaryTurnoverRateForInterval = new Interval(dateToStudy, getVoluntaryTurnoverRate(dateToStudy, memberProfiles, frequency));
            turnoverRateVol.add(voluntaryTurnoverRateForInterval);

            if (frequency == FrequencyType.DAILY) {
                dateToStudy = dateToStudy.plusDays(1);
            } else if (frequency == FrequencyType.WEEKLY) {
                dateToStudy = dateToStudy.plusWeeks(1);
            } else {
                LocalDate newDate = dateToStudy.plusMonths(1);
                dateToStudy = newDate.withDayOfMonth(newDate.lengthOfMonth());
            }
        }

        response.setNewHireRetentionRate(retRateNewHire);
        response.setTotalTwelveMonthRetentionRate(retRate);
        response.setVoluntaryTwelveMonthRetentionRate(retRateVol);
        response.setTotalTwelveMonthTurnoverRate(turnoverRate);
        response.setVoluntaryTwelveMonthTurnoverRate(turnoverRateVol);
        return response;
    }

    public float getTurnoverRate(LocalDate dateToStudy, List<MemberProfile> memberProfiles, FrequencyType frequency) {
        LocalDate beginningDate;
        switch (frequency) {
            case FrequencyType.DAILY -> beginningDate = dateToStudy.minusDays(1);
            case FrequencyType.WEEKLY -> beginningDate = dateToStudy.minusMonths(1);
            default -> beginningDate = dateToStudy.minusMonths(1);
        }
        float beginningEmployees = getNumberOfEmployees(beginningDate, memberProfiles);
        float endingEmployees = getNumberOfEmployees(dateToStudy, memberProfiles);
        float terminationsWithinMonth = getTerminationsForMonth(dateToStudy, memberProfiles);
        return terminationsWithinMonth/((beginningEmployees + endingEmployees)/2);
    }

    public float getVoluntaryTurnoverRate(LocalDate dateToStudy, List<MemberProfile> memberProfiles, FrequencyType frequency) {
        LocalDate beginningDate;
        switch (frequency) {
            case FrequencyType.DAILY -> beginningDate = dateToStudy.minusDays(1);
            case FrequencyType.WEEKLY -> beginningDate = dateToStudy.minusMonths(1);
            default -> beginningDate = dateToStudy.minusMonths(1);
        }
        float beginningEmployees = getNumberOfEmployees(beginningDate, memberProfiles);
        float endingEmployees = getNumberOfEmployees(dateToStudy, memberProfiles);
        float terminationsWithinMonth = getVoluntaryTerminationsForMonth(dateToStudy, memberProfiles);
        return terminationsWithinMonth/((beginningEmployees + endingEmployees)/2);
    }

    public float getRetentionRate(LocalDate dateToStudy, List<MemberProfile> memberProfiles) {
        float beginningEmployees = getNumberOfEmployees(dateToStudy.minusYears(1), memberProfiles);
        float yearsTerminations = getTerminationsForYear(dateToStudy, memberProfiles);
        float termsWithinYear = getTerminationsWithinYear(dateToStudy, memberProfiles);
        float rate = 0;
        if (beginningEmployees > 0) {
            rate = (beginningEmployees - (yearsTerminations - termsWithinYear)) / beginningEmployees;
        }
        return rate;
    }

    public float getVoluntaryRetentionRate(LocalDate dateToStudy, List<MemberProfile> memberProfiles) {
        float beginningEmployees = getNumberOfEmployees(dateToStudy.minusYears(1), memberProfiles);
        float yearsVolTerminations = getVoluntaryTerminationsForYear(dateToStudy, memberProfiles);
        float withinYearVolTerms = getVoluntaryTerminationsWithinYear(dateToStudy, memberProfiles);
        float rate = 0;
        if (beginningEmployees > 0) {
            rate = (beginningEmployees - (yearsVolTerminations - withinYearVolTerms))/beginningEmployees;
        }
        return rate;
    }

    public float getNewHireRetentionRate(LocalDate dateToStudy, List<MemberProfile> memberProfiles) {
        float beginningNewHires = getNumberOfNewHires(dateToStudy.minusYears(1), memberProfiles);
        float yearsTerminations = getNewHireTerminations(dateToStudy, memberProfiles);
        float rate = 0;
        if (beginningNewHires > 0) {
            rate = (beginningNewHires - yearsTerminations)/beginningNewHires;
        }
        return rate;

    }

    public int getNumberOfEmployees(LocalDate dateToStudy, List<MemberProfile> memberProfiles) {
        int numberOfEmployees = 0;
        for (MemberProfile mp : memberProfiles) {
            if (!Boolean.TRUE.equals(mp.getExcluded())
                    && mp.getStartDate() != null
                    && (mp.getStartDate().isBefore(dateToStudy) || mp.getStartDate().isEqual(dateToStudy))
                    && (mp.getTerminationDate() == null || mp.getTerminationDate().isAfter(dateToStudy))) {
                numberOfEmployees += 1;
            }
        }
        return numberOfEmployees;
    }

    public int getNumberOfNewHires(LocalDate dateToStudy, List<MemberProfile> memberProfiles) {
        int numberOfEmployees = 0;
        for (MemberProfile mp : memberProfiles) {
            if (!Boolean.TRUE.equals(mp.getExcluded())
                    && mp.getStartDate() != null
                    && (mp.getStartDate().isAfter(dateToStudy) || mp.getStartDate().isEqual(dateToStudy))) {
                numberOfEmployees += 1;
            }
        }

        return numberOfEmployees;
    }

    public int getTerminationsForMonth(LocalDate dateToStudy, List<MemberProfile> memberProfiles) {
        int numberOfTerms = 0;
        LocalDate beginningDate = dateToStudy.minusMonths(1);
        for (MemberProfile mp : memberProfiles) {
            if (!Boolean.TRUE.equals(mp.getExcluded())
                    && mp.getTerminationDate() != null
                    && mp.getTerminationDate().isAfter(beginningDate)
                    && (mp.getTerminationDate().isBefore(dateToStudy) || mp.getTerminationDate().isEqual(dateToStudy))) {
                numberOfTerms += 1;
            }
        }
        return numberOfTerms;
    }

    public int getVoluntaryTerminationsForMonth(LocalDate dateToStudy, List<MemberProfile> memberProfiles) {
        int numberOfTerms = 0;
        LocalDate beginningDate = dateToStudy.minusMonths(1);
        for (MemberProfile mp : memberProfiles) {
            if (!Boolean.TRUE.equals(mp.getExcluded())
                    && mp.getTerminationDate() != null
                    && mp.getTerminationDate().isAfter(beginningDate)
                    && (mp.getTerminationDate().isBefore(dateToStudy) || mp.getTerminationDate().isEqual(dateToStudy))
                    && Boolean.TRUE.equals(mp.getVoluntary())) {
                numberOfTerms += 1;
            }
        }
        return numberOfTerms;
    }

    public int getTerminationsForYear(LocalDate dateToStudy, List<MemberProfile> memberProfiles) {
        int numberOfTerms = 0;
        LocalDate beginningDate = dateToStudy.minusYears(1);
        for (MemberProfile mp : memberProfiles) {
            if (!Boolean.TRUE.equals(mp.getExcluded())
                    && mp.getTerminationDate() != null
                    && mp.getTerminationDate().isAfter(beginningDate)
                    && (mp.getTerminationDate().isBefore(dateToStudy) || mp.getTerminationDate().isEqual(dateToStudy))) {
                numberOfTerms += 1;
            }
        }
        return numberOfTerms;
    }

    public int getVoluntaryTerminationsForYear(LocalDate dateToStudy, List<MemberProfile> memberProfiles) {
        int numberOfTerms = 0;
        LocalDate beginningDate = dateToStudy.minusYears(1);
        for (MemberProfile mp : memberProfiles) {
            if (!Boolean.TRUE.equals(mp.getExcluded())
                    && mp.getTerminationDate() != null
                    && mp.getTerminationDate().isAfter(beginningDate)
                    && (mp.getTerminationDate().isBefore(dateToStudy) || mp.getTerminationDate().isEqual(dateToStudy))
                    && Boolean.TRUE.equals(mp.getVoluntary())) {
                numberOfTerms += 1;
            }
        }
        return numberOfTerms;
    }

    public int getNewHireTerminations(LocalDate dateToStudy, List<MemberProfile> memberProfiles) {
        int numberOfTerms = 0;
        LocalDate beginningDate = dateToStudy.minusYears(1);
        for (MemberProfile mp : memberProfiles) {
            if (!Boolean.TRUE.equals(mp.getExcluded())
                    && mp.getStartDate() != null
                    && (mp.getStartDate().isEqual(beginningDate) || mp.getStartDate().isAfter(beginningDate))
                    && mp.getTerminationDate() != null
                    && (mp.getTerminationDate().isBefore(dateToStudy) || mp.getTerminationDate().isEqual(dateToStudy))) {
                numberOfTerms += 1;
            }
        }
        return numberOfTerms;
    }

    public int getTerminationsWithinYear(LocalDate dateToStudy, List<MemberProfile> memberProfiles) {
        int numberOfTerms = 0;
        LocalDate beginningDate = dateToStudy.minusYears(1);
        for (MemberProfile mp : memberProfiles) {
            if (!Boolean.TRUE.equals(mp.getExcluded())
                    && mp.getStartDate() != null
                    && (mp.getStartDate().isEqual(beginningDate) || mp.getStartDate().isAfter(beginningDate))
                    && mp.getTerminationDate() != null
                    && (mp.getTerminationDate().isBefore(dateToStudy) || mp.getTerminationDate().isEqual(dateToStudy))) {
                numberOfTerms += 1;
            }
        }
        return numberOfTerms;
    }

    public int getVoluntaryTerminationsWithinYear(LocalDate dateToStudy, List<MemberProfile> memberProfiles) {
        int numberOfTerms = 0;
        LocalDate beginningDate = dateToStudy.minusYears(1);
        for (MemberProfile mp : memberProfiles) {
            if (!Boolean.TRUE.equals(mp.getExcluded())
                    && mp.getStartDate() != null
                    && (mp.getStartDate().isEqual(beginningDate) || mp.getStartDate().isAfter(beginningDate))
                    && mp.getTerminationDate() != null
                    && (mp.getTerminationDate().isBefore(dateToStudy) || mp.getTerminationDate().isEqual(dateToStudy))
                    && Boolean.TRUE.equals(mp.getVoluntary())) {
                numberOfTerms += 1;
            }
        }
        return numberOfTerms;
    }
}
