package com.objectcomputing.checkins.services.teammembers;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.UUID;

@Singleton
public class TeamMemberServicesImpl implements TeamMemberServices {
    @Inject
    private TeamMemberRepository teamMemberRepository;


    @Override
    public List<TeamMember> findByTeamAndMember(UUID teamId, UUID memberId) {
        if (teamId != null && memberId != null) {
            return teamMemberRepository.findByTeamIdAndMemberId(teamId, memberId);
        }
        else if (teamId != null) {
            return teamMemberRepository.findByTeamId(teamId);
        }
        else if (memberId != null) {
            return teamMemberRepository.findByMemberId(memberId);
        }
        else {
            return teamMemberRepository.findAll();
        }
    }

    @Override
    public TeamMember saveTeamMember(TeamMember teamMember) {
        return teamMemberRepository.save(teamMember);
    }

    @Override
    public TeamMember updateTeamMember(TeamMember teamMember) {
        return teamMemberRepository.update(teamMember);
    }
}
