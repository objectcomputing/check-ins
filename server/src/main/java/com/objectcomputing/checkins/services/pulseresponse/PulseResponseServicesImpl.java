package com.objectcomputing.checkins.services.pulseresponse;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.services.permissions.Permission;
import com.objectcomputing.checkins.services.role.role_permissions.RolePermissionServices;
import jakarta.inject.Singleton;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Singleton
public class PulseResponseServicesImpl implements PulseResponseService {

    private final PulseResponseRepository pulseResponseRepo;
    private final MemberProfileServices memberProfileServices;
    private final MemberProfileRepository memberRepo;
    private final CurrentUserServices currentUserServices;
    private final RolePermissionServices rolePermissionServices;

    public PulseResponseServicesImpl(
            PulseResponseRepository pulseResponseRepo,
            MemberProfileServices memberProfileServices,
            MemberProfileRepository memberRepo,
            CurrentUserServices currentUserServices,
            RolePermissionServices rolePermissionServices
    ) {
        this.pulseResponseRepo = pulseResponseRepo;
        this.memberProfileServices = memberProfileServices;
        this.memberRepo = memberRepo;
        this.currentUserServices = currentUserServices;
        this.rolePermissionServices = rolePermissionServices;
    }

    @Override
    public PulseResponse save(PulseResponse pulseResponse) {
        PulseResponse pulseResponseRet = null;
        if (pulseResponse != null) {
            final UUID memberId = pulseResponse.getTeamMemberId();
            LocalDate pulseSubDate = pulseResponse.getSubmissionDate();
            if (pulseResponse.getId() != null) {
                throw new BadArgException(String.format("Found unexpected id for pulseresponse %s", pulseResponse.getId()));
            } else if (memberRepo.findById(memberId).isEmpty()) {
                throw new BadArgException(String.format("Member %s doesn't exists", memberId));
            } else if (pulseSubDate.isBefore(LocalDate.EPOCH) || pulseSubDate.isAfter(LocalDate.MAX)) {
                throw new BadArgException(String.format("Invalid date for pulseresponse submission date %s", memberId));
            }
            pulseResponseRet = pulseResponseRepo.save(pulseResponse);
        }
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
                throw new BadArgException(String.format("Invalid pulseresponse %s", pulseResponse));
            } else if (pulseSubDate.isBefore(LocalDate.EPOCH) || pulseSubDate.isAfter(LocalDate.MAX)) {
                throw new BadArgException(String.format("Invalid date for pulseresponse submission date %s", memberId));
            }
            pulseResponseRet = pulseResponseRepo.update(pulseResponse);
        }
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
        return pulse.getTeamMemberId().equals(currentUserId) ||
                memberProfileServices.getSubordinatesForId(currentUserId)
                        .stream().anyMatch(member -> member.getId().equals(pulse.getTeamMemberId()));
    }
}