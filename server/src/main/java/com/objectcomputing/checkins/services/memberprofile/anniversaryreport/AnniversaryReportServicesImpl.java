package com.objectcomputing.checkins.services.memberprofile.anniversaryreport;

import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.micronaut.core.annotation.Nullable;
import jakarta.inject.Singleton;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Singleton
public class AnniversaryReportServicesImpl implements AnniversaryServices {
    private static final Logger LOG = LoggerFactory.getLogger(AnniversaryReportServicesImpl.class);
    private final MemberProfileServices memberProfileServices;
    private final CurrentUserServices currentUserServices;

    public AnniversaryReportServicesImpl(MemberProfileServices memberProfileServices,
            CurrentUserServices currentUserServices) {
        this.memberProfileServices = memberProfileServices;
        this.currentUserServices = currentUserServices;
    }

    @Override
    public List<AnniversaryReportResponseDTO> findByValue(@Nullable String[] months) {
        if (!currentUserServices.isAdmin()) {
            throw new PermissionException("You do not have permission to access this resource.");
        }

        List<MemberProfile> memberProfileAll = new ArrayList<>();
        Set<MemberProfile> memberProfiles = memberProfileServices.findByValues(null, null, null, null, null, null,
                false);
        LOG.info(memberProfiles.toString());
        if (months != null) {
            for (String month : months) {
                List<MemberProfile> memberProfile = new ArrayList<>();
                if (month != null) {
                    memberProfile = memberProfiles
                            .stream()
                            .filter(member -> member.getStartDate() != null
                                    && month.equalsIgnoreCase(member.getStartDate().getMonth().name())
                                    && member.getTerminationDate() == null)
                            .collect(Collectors.toList());
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
                .collect(Collectors.toList());
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
