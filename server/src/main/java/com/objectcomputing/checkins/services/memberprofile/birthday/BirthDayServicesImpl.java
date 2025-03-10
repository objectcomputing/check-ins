package com.objectcomputing.checkins.services.memberprofile.birthday;

import com.objectcomputing.checkins.services.permissions.Permission;
import com.objectcomputing.checkins.services.permissions.RequiredPermission;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;

import jakarta.inject.Singleton;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Singleton
public class BirthDayServicesImpl implements BirthDayServices{

    private final MemberProfileServices memberProfileServices;

    public BirthDayServicesImpl(MemberProfileServices memberProfileServices) {
        this.memberProfileServices = memberProfileServices;
    }

    @Override
    @RequiredPermission(Permission.CAN_VIEW_BIRTHDAY_REPORT)
    public List<BirthDayResponseDTO> findByValue(String[] months, Integer[] daysOfMonth) {
        Set<MemberProfile> memberProfiles = memberProfileServices.findByValues(null, null, null, null, null, null, false);
        List<MemberProfile> memberProfileAll = new ArrayList<>(memberProfiles);
        if (months == null && daysOfMonth == null) {
            // If nothing was passed in, get all members without birthdays.
            memberProfileAll = memberProfileAll
                .stream()
                .filter(member -> member.getBirthDate() == null)
                .toList();
        }
        if (months != null) {
            for (String month : months) {
                if (month != null) {
                    memberProfileAll = memberProfileAll
                            .stream()
                            .filter(member -> member.getBirthDate() != null && month.equalsIgnoreCase(member.getBirthDate().getMonth().name()) && member.getTerminationDate() == null && (member.getIgnoreBirthday() == null || member.getIgnoreBirthday() == Boolean.FALSE))
                            .toList();
                }
            }
        }
        if(daysOfMonth != null) {
            for(Integer day: daysOfMonth) {
                if (day != null) {
                    memberProfileAll = memberProfiles
                            .stream()
                            .filter(member -> member.getBirthDate() != null && day.equals(member.getBirthDate().getDayOfMonth()) && member.getTerminationDate() == null && (member.getIgnoreBirthday() == null || member.getIgnoreBirthday() == Boolean.FALSE))
                            .toList();
                }
            }
        }

        return profileToBirthDateResponseDto(memberProfileAll);
    }

    @Override
    public List<BirthDayResponseDTO> getTodaysBirthdays() {
        Set<MemberProfile> memberProfiles = memberProfileServices.findByValues(null, null, null, null, null, null, false);
        LocalDate today = LocalDate.now();
        List<MemberProfile> results = memberProfiles
                .stream()
                .filter(member -> member.getBirthDate() != null && today.getMonthValue() == member.getBirthDate().getMonthValue() && (member.getIgnoreBirthday() == null || member.getIgnoreBirthday() == Boolean.FALSE))
                .toList();
        return profileToBirthDateResponseDto(results);
    }

    private List<BirthDayResponseDTO> profileToBirthDateResponseDto(List<MemberProfile> memberProfiles) {
        List<BirthDayResponseDTO> birthDays= new ArrayList<>();
        LocalDate currentDate = LocalDate.now();
        for (MemberProfile member : memberProfiles) {
            if (member.getTerminationDate() == null || member.getTerminationDate().isAfter(currentDate)) {
                BirthDayResponseDTO birthDayResponseDTO = new BirthDayResponseDTO();
                birthDayResponseDTO.setUserId(member.getId());
                birthDayResponseDTO.setName(member.getFirstName() + "" +member.getLastName());
                if (member.getBirthDate() != null) {
                    birthDayResponseDTO.setBirthDay(member.getBirthDate().getMonthValue() + "/" +member.getBirthDate().getDayOfMonth());
                }
                birthDays.add(birthDayResponseDTO);
            }
        }
        return birthDays;
    }
}
