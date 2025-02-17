package com.objectcomputing.checkins.services.memberprofile.anniversaryreport;

import com.objectcomputing.checkins.services.permissions.Permission;
import com.objectcomputing.checkins.services.permissions.RequiredPermission;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import io.micronaut.core.annotation.Nullable;
import jakarta.inject.Singleton;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Singleton
public class AnniversaryReportServicesImpl implements AnniversaryServices {

    private final MemberProfileServices memberProfileServices;

    public AnniversaryReportServicesImpl(MemberProfileServices memberProfileServices) {
        this.memberProfileServices = memberProfileServices;
    }

    @Override
    @RequiredPermission(Permission.CAN_VIEW_ANNIVERSARY_REPORT)
    public List<AnniversaryReportResponseDTO> findByValue(@Nullable String[] months) {
        List<MemberProfile> memberProfileAll = new ArrayList<>();
        Set<MemberProfile> memberProfiles = memberProfileServices.findByValues(null, null, null, null, null, null,
                false);

        if (months != null) {
            for (String month : months) {
                List<MemberProfile> memberProfile = new ArrayList<>();
                if (month != null) {
                    memberProfile = memberProfiles
                            .stream()
                            .filter(member -> member.getStartDate() != null
                                    && month.equalsIgnoreCase(member.getStartDate().getMonth().name())
                                    && member.getTerminationDate() == null)
                            .toList();
                }
                memberProfileAll.addAll(memberProfile);
            }
        }
        return profileToAnniversaryResponseDto(memberProfileAll);

    }

    @Override
    public List<AnniversaryReportResponseDTO> getTodaysAnniversaries() {
        Set<MemberProfile> memberProfiles = memberProfileServices.findByValues(null, null, null, null, null, null,
                false);
        LocalDate today = LocalDate.now();
        List<MemberProfile> results = memberProfiles
                .stream()
                .filter(member -> member.getStartDate() != null
                        && today.getMonthValue() == member.getStartDate().getMonthValue())
                .toList();
        return profileToAnniversaryResponseDto(results);
    }

    private List<AnniversaryReportResponseDTO> profileToAnniversaryResponseDto(List<MemberProfile> memberProfiles) {

        List<AnniversaryReportResponseDTO> anniversaries = new ArrayList<>();
        LocalDate currentDate = LocalDate.now();
        for (MemberProfile member : memberProfiles) {
            if (member.getTerminationDate() == null || member.getTerminationDate().isAfter(currentDate)) {
                DecimalFormat df = new DecimalFormat("#.##");
                df.setRoundingMode(RoundingMode.CEILING);
                double yearsOfService = (currentDate.toEpochDay() - member.getStartDate().toEpochDay()) / 365.25;

                AnniversaryReportResponseDTO anniversary = new AnniversaryReportResponseDTO();
                anniversary.setUserId(member.getId());
                anniversary.setName(member.getFirstName() + " " + member.getLastName());
                anniversary.setYearsOfService(Double.parseDouble(df.format(yearsOfService)));
                if (member.getStartDate() != null) {
                    anniversary.setAnniversary(
                            member.getStartDate().getMonthValue() + "/" + member.getStartDate().getDayOfMonth() + "/"
                                    + member.getStartDate().getYear());
                }
                anniversaries.add(anniversary);
            }
        }
        return anniversaries;
    }
}
