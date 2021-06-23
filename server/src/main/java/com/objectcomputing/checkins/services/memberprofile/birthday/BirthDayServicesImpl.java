package com.objectcomputing.checkins.services.memberprofile.birthday;

import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;

import javax.inject.Singleton;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class BirthDayServicesImpl implements BirthDayServices{

    private final MemberProfileServices memberProfileServices;
    private final CurrentUserServices currentUserServices;

    public BirthDayServicesImpl(MemberProfileServices memberProfileServices, CurrentUserServices currentUserServices) {
        this.memberProfileServices = memberProfileServices;
        this.currentUserServices = currentUserServices;
    }

    @Override
    public List<BirthDayResponseDTO> findByValue(String month) {
        if (!currentUserServices.isAdmin()) {
            throw new PermissionException("You do not have permission to access this resource.");
        }
        List<MemberProfile> memberProfiles = memberProfileServices.findAll();
        if (month != null) {
            memberProfiles = memberProfiles
                    .stream()
                    .filter(member -> month.equalsIgnoreCase(member.getStartDate().getMonth().name()) && member.getTerminationDate() == null)
                    .collect(Collectors.toList());
        }

        return profileToBirthDateResponseDto(memberProfiles);
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
