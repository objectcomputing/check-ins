package com.objectcomputing;

import java.util.List;
import java.util.UUID;

import com.objectcomputing.member.MemberProfile;

public interface MemberProfileRepository {
    List<MemberProfile> findByName(String name);
    List<MemberProfile> findByRole(String name);
    List<MemberProfile> findByPdlId(UUID pdlId);
    List<MemberProfile> findAll();
    MemberProfile createProfile(MemberProfile memberProfile);
    MemberProfile update(MemberProfile memberProfile);
}
