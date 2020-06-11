package com.objectcomputing.checkins;

import java.util.List;
import java.util.UUID;

import com.objectcomputing.checkins.member.MemberProfile;

import io.micronaut.data.repository.CrudRepository;

public interface MemberProfileRepository extends CrudRepository<MemberProfile, UUID> {
    List<MemberProfile> findByName(String name);
    List<MemberProfile> findByRole(String name);
    List<MemberProfile> findByPdlId(UUID pdlId);
    List<MemberProfile> findAll();
}
