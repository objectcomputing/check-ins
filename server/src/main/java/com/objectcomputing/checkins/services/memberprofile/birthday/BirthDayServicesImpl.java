package com.objectcomputing.checkins.services.memberprofile.birthday;

import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;

import jakarta.inject.Singleton;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.objectcomputing.checkins.util.Validation.validate;

@Singleton
public class BirthDayServicesImpl implements BirthDayServices{

    private final MemberProfileServices memberProfileServices;
    private final CurrentUserServices currentUserServices;

    public BirthDayServicesImpl(MemberProfileServices memberProfileServices, CurrentUserServices currentUserServices) {
        this.memberProfileServices = memberProfileServices;
        this.currentUserServices = currentUserServices;
    }

    @Override
    public List<BirthDayResponseDTO> findByValue(String[] months) {

        validate(currentUserServices.isAdmin()).orElseThrow(() -> {
            throw new PermissionException("You do not have permission to access this resource.");
        });

        List<MemberProfile> memberProfileAll = new ArrayList<>();
        Set<MemberProfile> memberProfiles = memberProfileServices.findByValues(null, null, null, null, null, null, false);
        if (months != null) {
            for (String month : months) {
                List<MemberProfile> memberProfile = new ArrayList<>();
                if (month != null) {
                    memberProfile = memberProfiles
                            .stream()
                            .filter(member -> member.getBirthDate() != null && month.equalsIgnoreCase(member.getBirthDate().getMonth().name()) && member.getTerminationDate() == null)
                            .collect(Collectors.toList());
                }
                memberProfileAll.addAll(memberProfile);
            }
        }

        return profileToBirthDateResponseDto(memberProfileAll);
    }

    private List<BirthDayResponseDTO> profileToBirthDateResponseDto(List<MemberProfile> memberProfiles) {
        List<BirthDayResponseDTO> birthDays= new ArrayList<>();
        LocalDate currentDate = LocalDate.now();
        for (MemberProfile member : memberProfiles) {
            if (member.getTerminationDate() == null || member.getTerminationDate().isAfter(currentDate)) {
                BirthDayResponseDTO birthDayResponseDTO = new BirthDayResponseDTO();
                birthDayResponseDTO.setUserId(member.getId());
                birthDayResponseDTO.setName(member.getFirstName() + "" +member.getLastName());
                birthDayResponseDTO.setBirthDay(member.getBirthDate().getMonthValue() + "/" +member.getBirthDate().getDayOfMonth());
                birthDays.add(birthDayResponseDTO);
            }
        }
        return birthDays;
    }
}
