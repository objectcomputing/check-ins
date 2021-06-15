package com.objectcomputing.checkins.services.feedback.suggestions;


import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.services.team.member.TeamMember;
import com.objectcomputing.checkins.services.team.member.TeamMemberServices;
import io.micronaut.context.annotation.Property;

import javax.inject.Singleton;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Singleton
public class FeedbackSuggestionServiceImpl implements FeedbackSuggestionsService {

    public static final String MAX_SUGGESTIONS = "check-ins.application.feedback.max-suggestions";

    private final MemberProfileServices memberProfileServices;
    private final CurrentUserServices currentUserServices;
    private final TeamMemberServices teamMemberServices;
    private final Integer maxSuggestions;

    public FeedbackSuggestionServiceImpl(MemberProfileServices memberProfileServices,
                                         CurrentUserServices currentUserServices,
                                         TeamMemberServices teamMemberServices,
                                         @Property(name = MAX_SUGGESTIONS) Integer maxSuggestions) {
        this.memberProfileServices = memberProfileServices;
        this.currentUserServices = currentUserServices;
        this.teamMemberServices = teamMemberServices;
        this.maxSuggestions = maxSuggestions;
    }

    @Override
    public List<FeedbackSuggestionDTO> getSuggestionsByProfileId(UUID id) {
        MemberProfile currentUser = currentUserServices.getCurrentUser();
        MemberProfile suggestFor = memberProfileServices.getById(id);

        List<FeedbackSuggestionDTO> suggestions = new LinkedList<FeedbackSuggestionDTO>();
        if(suggestFor.getSupervisorid() != currentUser.getId()) {
            suggestions.add(new FeedbackSuggestionDTO("Supervisor of requestee", suggestFor.getSupervisorid()));
        }

        if(suggestFor.getPdlId() != currentUser.getId()) {
            suggestions.add(new FeedbackSuggestionDTO("PDL of requestee", suggestFor.getPdlId()));
        }

        Set<TeamMember> teamMemberships = teamMemberServices.findByFields(null, id, null);

        for(TeamMember currentMembership: teamMemberships){
            Set<TeamMember> teamMembers = teamMemberServices.findByFields(currentMembership.getTeamid(), null, null);
            Set<TeamMember> leads = teamMembers.stream().filter((member)-> member.isLead()).collect(Collectors.toSet());
            for(TeamMember lead: leads) {
                if(suggestions.size() < maxSuggestions) {
                    suggestions.add(new FeedbackSuggestionDTO("Team lead for requestee", lead.getMemberid()));
                }
            }

            if(suggestions.size() >= maxSuggestions) break;
        }

        for(TeamMember currentMembership: teamMemberships){
            Set<TeamMember> teamMembers = teamMemberServices.findByFields(currentMembership.getTeamid(), null, null);
            teamMembers = teamMembers.stream().filter((member)-> !member.isLead()).collect(Collectors.toSet());
            for(TeamMember teamMember: teamMembers) {
                if(suggestions.size() < maxSuggestions) {
                    suggestions.add(new FeedbackSuggestionDTO("Team member for requestee", teamMember.getMemberid()));
                }
            }

            if(suggestions.size() >= maxSuggestions) break;
        }
        return suggestions;
    }
}
