package com.objectcomputing.checkins.services.team.member;

import java.util.Set;
import java.util.UUID;

public interface TeamMemberServices {

    TeamMember save(TeamMember teamMember);

    TeamMember read(UUID id);

    TeamMember update(TeamMember teamMember);

    void delete(UUID id);

    Set<TeamMember> findByFields(UUID teamId, UUID memberId, Boolean lead);

    void deleteByTeam(UUID id);
}