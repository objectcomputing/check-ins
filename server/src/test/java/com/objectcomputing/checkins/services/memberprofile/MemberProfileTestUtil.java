package com.objectcomputing.checkins.services.memberprofile;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MemberProfileTestUtil {

    public static MemberProfileCreateDTO mkCreateMemberProfileDTO() {
        MemberProfileCreateDTO dto = new MemberProfileCreateDTO();
        dto.setName("TestName");
        dto.setTitle("TestRole");
        dto.setLocation("TestLocation");
        dto.setWorkEmail("TestEmail");
        dto.setInsperityId("TestInsperityId");
        dto.setStartDate(LocalDate.of(2019, 1, 01));
        dto.setBioText("TestBio");
        return dto;
    }

    public static MemberProfileUpdateDTO mkUpdateMemberProfileDTO() {
        MemberProfileUpdateDTO dto = new MemberProfileUpdateDTO();
        dto.setId(UUID.fromString("e134d349-cf02-4a58-b9d3-42cc48375628"));
        dto.setName("TestName");
        dto.setTitle("TestRole");
        dto.setLocation("TestLocation");
        dto.setWorkEmail("TestEmail");
        dto.setInsperityId("TestInsperityId");
        dto.setStartDate(LocalDate.of(2019, 1, 01));
        dto.setBioText("TestBio");
        return dto;
    }

    public static MemberProfile mkMemberProfile(String seed) {
        return new MemberProfile("TestName" + seed,
                "TestRole" + seed,
                null,
                "TestLocation" + seed,
                "TestEmail" + seed,
                "TestInsperityId" + seed,
                LocalDate.of(2019, 1, 1),
                "TestBio" + seed,
                null,
                null);
    }

    public static MemberProfile mkMemberProfile() {
        return mkMemberProfile("");
    }

    public static void assertProfilesEqual(MemberProfile entity, MemberProfileResponseDTO dto) {
        assertEquals(entity.getName(), dto.getName());
        assertEquals(entity.getTitle(), dto.getTitle());
        assertEquals(entity.getPdlId(), dto.getPdlId());
        assertEquals(entity.getLocation(), dto.getLocation());
        assertEquals(entity.getWorkEmail(), dto.getWorkEmail());
        assertEquals(entity.getInsperityId(), dto.getInsperityId());
        assertEquals(entity.getStartDate(), dto.getStartDate());
        assertEquals(entity.getBioText(), dto.getBioText());
        assertEquals(entity.getSupervisorid(), dto.getSupervisorid());
        assertEquals(entity.getTerminationDate(), dto.getTerminationDate());
    }

    public static void assertProfilesEqual(MemberProfileUpdateDTO entity, MemberProfileResponseDTO dto) {
        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getName(), dto.getName());
        assertEquals(entity.getTitle(), dto.getTitle());
        assertEquals(entity.getPdlId(), dto.getPdlId());
        assertEquals(entity.getLocation(), dto.getLocation());
        assertEquals(entity.getWorkEmail(), dto.getWorkEmail());
        assertEquals(entity.getInsperityId(), dto.getInsperityId());
        assertEquals(entity.getStartDate(), dto.getStartDate());
        assertEquals(entity.getBioText(), dto.getBioText());
        assertEquals(entity.getSupervisorid(), dto.getSupervisorid());
        assertEquals(entity.getTerminationDate(), dto.getTerminationDate());
    }

    public static void assertProfilesEqual(MemberProfileCreateDTO entity, MemberProfileResponseDTO dto) {
        assertEquals(entity.getName(), dto.getName());
        assertEquals(entity.getTitle(), dto.getTitle());
        assertEquals(entity.getPdlId(), dto.getPdlId());
        assertEquals(entity.getLocation(), dto.getLocation());
        assertEquals(entity.getWorkEmail(), dto.getWorkEmail());
        assertEquals(entity.getInsperityId(), dto.getInsperityId());
        assertEquals(entity.getStartDate(), dto.getStartDate());
        assertEquals(entity.getBioText(), dto.getBioText());
        assertEquals(entity.getSupervisorid(), dto.getSupervisorid());
        assertEquals(entity.getTerminationDate(), dto.getTerminationDate());
    }
}
