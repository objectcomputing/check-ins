package com.objectcomputing.checkins.services.memberprofile;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MemberProfileTestUtil {

    public static MemberProfileCreateDTO mkCreateMemberProfileDTO() {
        MemberProfileCreateDTO dto = new MemberProfileCreateDTO();
        dto.setFirstName("TestFirstName");
        dto.setLastName("TestLastName");
        dto.setTitle("TestRole");
        dto.setLocation("TestLocation");
        dto.setWorkEmail("TestEmail");
        dto.setEmployeeId("TestEmployeeId");
        dto.setStartDate(LocalDate.of(2019, 1, 01));
        dto.setBioText("TestBio");
        dto.setLastSeen(LocalDate.now());
        return dto;
    }

    public static MemberProfileUpdateDTO mkUpdateMemberProfileDTO() {
        MemberProfileUpdateDTO dto = new MemberProfileUpdateDTO();
        dto.setId(UUID.fromString("e134d349-cf02-4a58-b9d3-42cc48375628"));
        dto.setFirstName("TestFirstName");
        dto.setLastName("TestLastName");
        dto.setTitle("TestRole");
        dto.setLocation("TestLocation");
        dto.setWorkEmail("TestEmail");
        dto.setEmployeeId("TestEmployeeId");
        dto.setStartDate(LocalDate.of(2019, 1, 01));
        dto.setBioText("TestBio");
        dto.setLastSeen(LocalDate.now());
        return dto;
    }

    public static MemberProfile mkMemberProfile(String seed) {
        return new MemberProfile("TestFirstName" + seed,
                null,
                "TestLastName" + seed,
                null,
                "TestRole" + seed,
                null,
                "TestLocation" + seed,
                "TestEmail" + seed,
                "TestEmployeeId" + seed,
                LocalDate.of(2019, 1, 1),
                "TestBio" + seed,
                null,
                null,null, null, null, LocalDate.now());
    }

    public static MemberProfile mkMemberProfile() {
        return mkMemberProfile("");
    }

    public static void assertProfilesEqual(MemberProfile entity, MemberProfileResponseDTO dto) {
        assertEquals(entity.getFirstName(), dto.getFirstName());
        assertEquals(entity.getMiddleName(), dto.getMiddleName());
        assertEquals(entity.getLastName(), dto.getLastName());
        assertEquals(entity.getSuffix(), dto.getSuffix());
        assertEquals(entity.getTitle(), dto.getTitle());
        assertEquals(entity.getPdlId(), dto.getPdlId());
        assertEquals(entity.getLocation(), dto.getLocation());
        assertEquals(entity.getWorkEmail(), dto.getWorkEmail());
        assertEquals(entity.getEmployeeId(), dto.getEmployeeId());
        assertEquals(entity.getStartDate(), dto.getStartDate());
        assertEquals(entity.getBioText(), dto.getBioText());
        assertEquals(entity.getSupervisorid(), dto.getSupervisorid());
        assertEquals(entity.getTerminationDate(), dto.getTerminationDate());
        assertEquals(entity.getLastSeen(), dto.getLastSeen());
    }

    public static void assertProfilesEqual(MemberProfileUpdateDTO entity, MemberProfileResponseDTO dto) {
        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getFirstName(), dto.getFirstName());
        assertEquals(entity.getMiddleName(), dto.getMiddleName());
        assertEquals(entity.getLastName(), dto.getLastName());
        assertEquals(entity.getSuffix(), dto.getSuffix());
        assertEquals(entity.getTitle(), dto.getTitle());
        assertEquals(entity.getPdlId(), dto.getPdlId());
        assertEquals(entity.getLocation(), dto.getLocation());
        assertEquals(entity.getWorkEmail(), dto.getWorkEmail());
        assertEquals(entity.getEmployeeId(), dto.getEmployeeId());
        assertEquals(entity.getStartDate(), dto.getStartDate());
        assertEquals(entity.getBioText(), dto.getBioText());
        assertEquals(entity.getSupervisorid(), dto.getSupervisorid());
        assertEquals(entity.getTerminationDate(), dto.getTerminationDate());
    }

    public static void assertProfilesEqual(MemberProfileCreateDTO entity, MemberProfileResponseDTO dto) {
        assertEquals(entity.getFirstName(), dto.getFirstName());
        assertEquals(entity.getMiddleName(), dto.getMiddleName());
        assertEquals(entity.getLastName(), dto.getLastName());
        assertEquals(entity.getSuffix(), dto.getSuffix());
        assertEquals(entity.getTitle(), dto.getTitle());
        assertEquals(entity.getPdlId(), dto.getPdlId());
        assertEquals(entity.getLocation(), dto.getLocation());
        assertEquals(entity.getWorkEmail(), dto.getWorkEmail());
        assertEquals(entity.getEmployeeId(), dto.getEmployeeId());
        assertEquals(entity.getStartDate(), dto.getStartDate());
        assertEquals(entity.getBioText(), dto.getBioText());
        assertEquals(entity.getSupervisorid(), dto.getSupervisorid());
        assertEquals(entity.getTerminationDate(), dto.getTerminationDate());
    }
}
