package com.objectcomputing.checkins.services.feedback.suggestions;

import com.objectcomputing.checkins.configuration.CheckInsConfiguration;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.services.team.member.TeamMember;
import com.objectcomputing.checkins.services.team.member.TeamMemberServices;
import jakarta.inject.Singleton;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.objectcomputing.checkins.services.validate.PermissionsValidation.NOT_AUTHORIZED_MSG;

@Singleton
class FeedbackSuggestionServiceImpl implements FeedbackSuggestionsService {

    private final MemberProfileServices memberProfileServices;
    private final CurrentUserServices currentUserServices;
    private final TeamMemberServices teamMemberServices;
    private final Integer maxSuggestions;

    FeedbackSuggestionServiceImpl(MemberProfileServices memberProfileServices,
                                  CurrentUserServices currentUserServices,
                                  TeamMemberServices teamMemberServices,
                                  CheckInsConfiguration checkInsConfiguration) {
        this.memberProfileServices = memberProfileServices;
        this.currentUserServices = currentUserServices;
        this.teamMemberServices = teamMemberServices;
        this.maxSuggestions = checkInsConfiguration.getApplication().getFeedback().getMaxSuggestions();
    }

    @Override
    public List<FeedbackSuggestionDTO> getSuggestionsByProfileId(UUID id) {
        MemberProfile currentUser = currentUserServices.getCurrentUser();
        UUID currentUserId = currentUser.getId();
        MemberProfile suggestFor = memberProfileServices.getById(id);

        if (currentUserId == null) {
            throw new PermissionException(NOT_AUTHORIZED_MSG);
        }

        List<FeedbackSuggestionDTO> suggestions = new LinkedList<>();
        if (suggestFor.getSupervisorid() != null && !suggestFor.getSupervisorid().equals(currentUser.getId()) && isMemberActive(suggestFor.getSupervisorid())) {
            suggestions.add(new FeedbackSuggestionDTO("Supervisor of requestee", suggestFor.getSupervisorid()));
        }

        if (suggestFor.getPdlId() != null && !suggestFor.getPdlId().equals(currentUser.getId()) && isMemberActive(suggestFor.getPdlId())) {
            suggestions.add(new FeedbackSuggestionDTO("PDL of requestee", suggestFor.getPdlId()));
        }

        Set<TeamMember> teamMemberships = teamMemberServices.findByFields(null, id, null);

        for (TeamMember currentMembership : teamMemberships) {
            Set<TeamMember> teamMembers = teamMemberServices.findByFields(currentMembership.getTeamId(), null, null);
            Set<TeamMember> leads = teamMembers.stream().filter(TeamMember::isLead).collect(Collectors.toSet());
            leads = filterTerminated(leads);
            for (TeamMember lead : leads) {
                if (suggestions.size() < maxSuggestions && !lead.getMemberId().equals(id) && !lead.getMemberId().equals(currentUserId)) {
                    suggestions.add(new FeedbackSuggestionDTO("Team lead for requestee", lead.getMemberId()));
                }
            }

            if (suggestions.size() >= maxSuggestions) break;
        }

        for (TeamMember currentMembership : teamMemberships) {
            Set<TeamMember> teamMembers = teamMemberServices.findByFields(currentMembership.getTeamId(), null, null);
            teamMembers = teamMembers.stream().filter(member -> !member.isLead()).collect(Collectors.toSet());
            teamMembers = filterTerminated(teamMembers);
            for (TeamMember teamMember : teamMembers) {
                if (suggestions.size() < maxSuggestions && !teamMember.getMemberId().equals(id) && !teamMember.getMemberId().equals(currentUserId)) {
                    suggestions.add(new FeedbackSuggestionDTO("Team member for requestee", teamMember.getMemberId()));
                }
            }

            if (suggestions.size() >= maxSuggestions) break;
        }

        return suggestions;
    }

    private Set<TeamMember> filterTerminated(Set<TeamMember> suggestions) {
        suggestions = suggestions.stream().filter(suggestion -> isMemberActive(suggestion.getMemberId())).collect(Collectors.toSet());
        return suggestions;
    }

    private boolean isMemberActive(UUID memberId) {
        MemberProfile suggested = memberProfileServices.getById(memberId);
        LocalDate terminationDate = suggested.getTerminationDate();
        return !(terminationDate != null && terminationDate.isBefore(LocalDate.now().atStartOfDay().toLocalDate()));
    }
}
