package com.objectcomputing.checkins.services.teammembers;

import java.util.List;
import java.util.UUID;

public interface TeamMemberServices {
    List<TeamMember> findByTeamAndMember(UUID teamId, UUID memberId);

    TeamMember saveTeamMember(TeamMember teamMember);

    TeamMember updateTeamMember(TeamMember teamMember);
}
