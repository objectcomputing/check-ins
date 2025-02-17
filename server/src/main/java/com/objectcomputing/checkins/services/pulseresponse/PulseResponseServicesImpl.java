package com.objectcomputing.checkins.services.pulseresponse;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.notifications.email.EmailSender;
import com.objectcomputing.checkins.notifications.email.MailJetFactory;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.services.permissions.Permission;
import com.objectcomputing.checkins.services.role.role_permissions.RolePermissionServices;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Singleton
public class PulseResponseServicesImpl implements PulseResponseService {

    private static final Logger LOG = LoggerFactory.getLogger(PulseResponseServicesImpl.class);
    private final PulseResponseRepository pulseResponseRepo;
    private final MemberProfileServices memberProfileServices;
    private final MemberProfileRepository memberRepo;
    private final CurrentUserServices currentUserServices;
    private final RolePermissionServices rolePermissionServices;
    private final EmailSender emailSender;

    public PulseResponseServicesImpl(
            PulseResponseRepository pulseResponseRepo,
            MemberProfileServices memberProfileServices,
            MemberProfileRepository memberRepo,
            CurrentUserServices currentUserServices,
            RolePermissionServices rolePermissionServices,
            @Named(MailJetFactory.HTML_FORMAT) EmailSender emailSender
    ) {
        this.pulseResponseRepo = pulseResponseRepo;
        this.memberProfileServices = memberProfileServices;
        this.memberRepo = memberRepo;
        this.currentUserServices = currentUserServices;
        this.rolePermissionServices = rolePermissionServices;
        this.emailSender = emailSender;
    }

    @Override
    public PulseResponse save(PulseResponse pulseResponse) {
        if (pulseResponse != null) {
            verifyPulseData(pulseResponse);
            final UUID memberId = pulseResponse.getTeamMemberId();
            UUID currentUserId = currentUserServices.getCurrentUser().getId();
            if (memberId != null &&
                !currentUserId.equals(memberId) &&
                !isSubordinateTo(memberId, currentUserId)) {
                throw new BadArgException(
                    String.format("User %s does not have permission to create pulse response for user %s",
                                  currentUserId, memberId));
            }
            return saveCommon(pulseResponse);
        } else {
            return null;
        }
    }

    @Override
    public PulseResponse unsecureSave(PulseResponse pulseResponse) {
        PulseResponse pulseResponseRet = null;
        if (pulseResponse != null) {
            // External users could submit a pulse resonse multiple times.  We
            // need to check to see if this user has already submitted one
            // today.
            boolean submitted = false;
            final UUID memberId = pulseResponse.getTeamMemberId();
            if (memberId != null) {
                Optional<PulseResponse> existing =
                    pulseResponseRepo.getByTeamMemberIdAndSubmissionDate(
                        memberId, pulseResponse.getSubmissionDate());
                submitted = existing.isPresent();
            }
            if (!submitted) {
                verifyPulseData(pulseResponse);
                return saveCommon(pulseResponse);
            }
        }
        return null;
    }

    private void verifyPulseData(PulseResponse pulseResponse) {
        final UUID memberId = pulseResponse.getTeamMemberId();
        LocalDate pulseSubDate = pulseResponse.getSubmissionDate();
        if (pulseResponse.getId() != null) {
            throw new BadArgException(String.format("Found unexpected id for pulseresponse %s", pulseResponse.getId()));
        } else if (memberId != null &&
                   memberRepo.findById(memberId).isEmpty()) {
            throw new BadArgException(String.format("Member %s doesn't exists", memberId));
        } else if (pulseSubDate.isBefore(LocalDate.EPOCH) || pulseSubDate.isAfter(LocalDate.MAX)) {
            throw new BadArgException(String.format("Invalid date for pulseresponse submission date %s", memberId));
        }
    }

    private PulseResponse saveCommon(PulseResponse pulseResponse) {
        PulseResponse pulseResponseRet = pulseResponseRepo.save(pulseResponse);

        // Send low pulse survey score if appropriate
        sendPulseLowScoreEmail(pulseResponseRet);

        return pulseResponseRet;
    }

    @Override
    public PulseResponse read(@NotNull UUID id) {
        UUID currentUserId = currentUserServices.getCurrentUser().getId();
        boolean hasPermission = rolePermissionServices.findUserPermissions(currentUserId).contains(Permission.CAN_VIEW_ALL_PULSE_RESPONSES);

        return pulseResponseRepo.findById(id)
                .filter(pulse -> hasPermission || canViewDueToReportingHierarchy(pulse, currentUserId))
                .orElse(null);
    }

    @Override
    public PulseResponse update(PulseResponse pulseResponse) {
        UUID currentUserId = currentUserServices.getCurrentUser().getId();
        PulseResponse pulseResponseRet = null;
        if (pulseResponse != null) {
            final UUID id = pulseResponse.getId();
            final UUID memberId = pulseResponse.getTeamMemberId();
            LocalDate pulseSubDate = pulseResponse.getSubmissionDate();
            if (id == null || pulseResponseRepo.findById(id).isEmpty()) {
                throw new BadArgException(String.format("Unable to find pulseresponse record with id %s", pulseResponse.getId()));
            } else if (memberRepo.findById(memberId).isEmpty()) {
                throw new BadArgException(String.format("Member %s doesn't exist", memberId));
            } else if (memberId == null) {
                throw new BadArgException("Cannot update anonymous pulse response");
            } else if (pulseSubDate.isBefore(LocalDate.EPOCH) || pulseSubDate.isAfter(LocalDate.MAX)) {
                throw new BadArgException(String.format("Invalid date for pulseresponse submission date %s", memberId));
            } else if (!currentUserId.equals(memberId) && !isSubordinateTo(memberId, currentUserId)) {
                throw new BadArgException(String.format("User %s does not have permission to update pulse response for user %s", currentUserId, memberId));
            }
            pulseResponseRet = pulseResponseRepo.update(pulseResponse);
        }

        // Send low pulse survey score if appropriate
        sendPulseLowScoreEmail(pulseResponseRet);

        return pulseResponseRet;
    }

    @Override
    public Set<PulseResponse> findByFields(UUID teamMemberId, LocalDate dateFrom, LocalDate dateTo) {
        UUID currentUserId = currentUserServices.getCurrentUser().getId();
        boolean hasPermission = rolePermissionServices.findUserPermissions(currentUserId).contains(Permission.CAN_VIEW_ALL_PULSE_RESPONSES);

        Set<PulseResponse> pulseResponse = pulseResponseRepo.findAll()
                .stream()
                .filter(pulse -> hasPermission || canViewDueToReportingHierarchy(pulse, currentUserId))
                .collect(Collectors.toSet());

        if (teamMemberId != null) {
            pulseResponse.retainAll(pulseResponseRepo.findByTeamMemberId(teamMemberId));
        } else if (dateFrom != null && dateTo != null) {
            pulseResponse.retainAll(pulseResponseRepo.findBySubmissionDateBetween(dateFrom, dateTo));
        }
        return pulseResponse;
    }

    // The current user can view the pulse response if they are the team member who submitted the pulse response
    // or if they are the supervisor of the team member who submitted the pulse response
    private boolean canViewDueToReportingHierarchy(PulseResponse pulse, UUID currentUserId) {
        UUID id = pulse.getTeamMemberId();
        return id != null &&
               (id.equals(currentUserId) || isSubordinateTo(id, currentUserId));
    }

    private boolean isSubordinateTo(UUID reportMember, UUID currentUserId) {
        return memberProfileServices.getSubordinatesForId(currentUserId)
                .stream().anyMatch(member -> member.getId().equals(reportMember));
    }

    public void sendPulseLowScoreEmail(PulseResponse pulseResponse) {
        if (pulseResponse == null) return;

        boolean hasLowInternalScore = pulseResponse.getInternalScore() != null && pulseResponse.getInternalScore() <= 2;
        boolean hasLowExternalScore = pulseResponse.getExternalScore() != null && pulseResponse.getExternalScore() <= 2;

        if (!(hasLowInternalScore || hasLowExternalScore)) return;

        UUID teamMemberId = pulseResponse.getTeamMemberId();
        MemberProfile surveyTakerMemberProfile;
        if (teamMemberId != null && memberRepo.existsById(teamMemberId)) {
            surveyTakerMemberProfile = memberProfileServices.getById(teamMemberId);
        } else {
            LOG.warn("survey taker profile does not exist");
            return;
        }

        String firstName = surveyTakerMemberProfile.getFirstName();
        String lastName = surveyTakerMemberProfile.getLastName();

        String subject = String.format("%s pulse scores are low for team member %s %s",
                hasLowInternalScore && hasLowExternalScore ? "Internal and external" :
                        hasLowInternalScore ? "Internal" : "External", firstName, lastName);

        StringBuilder bodyBuilder = new StringBuilder()
                .append(String.format("Team member %s %s has left low %s pulse scores. Please consider reaching out to this employee at %s<br>", firstName, lastName,
                        hasLowInternalScore && hasLowExternalScore ? "internal and external" :
                                hasLowInternalScore ? "internal" : "external",
                        surveyTakerMemberProfile.getWorkEmail()));

        if (hasLowInternalScore) {
            bodyBuilder.append(String.format("Internal Feelings: %s<br>", pulseResponse.getInternalFeelings()));
        }
        if (hasLowExternalScore) {
            bodyBuilder.append(String.format("External Feelings: %s<br>", pulseResponse.getExternalFeelings()));
        }

        Set<String> recipients = new HashSet<>();
        UUID pdlId = surveyTakerMemberProfile.getPdlId();
        if (pdlId != null && memberRepo.existsById(pdlId)) {
            recipients.add(memberProfileServices.getById(pdlId).getWorkEmail());
        }

        UUID supervisorId = surveyTakerMemberProfile.getSupervisorid();
        if (supervisorId != null && memberRepo.existsById(supervisorId)) {
            recipients.add(memberProfileServices.getById(supervisorId).getWorkEmail());
        }

        if (!recipients.isEmpty()) {
            emailSender.sendEmail(null, null, subject, bodyBuilder.toString(), recipients.toArray(new String[0]));
        }
    }
}
