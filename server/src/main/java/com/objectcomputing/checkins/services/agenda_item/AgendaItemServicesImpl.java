package com.objectcomputing.checkins.services.agenda_item;

import com.objectcomputing.checkins.services.checkins.CheckIn;
import com.objectcomputing.checkins.services.checkins.CheckInRepository;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.services.role.RoleType;
import io.micronaut.security.utils.SecurityService;

import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.objectcomputing.checkins.util.Util.nullSafeUUIDToString;

@Singleton
public class AgendaItemServicesImpl implements AgendaItemServices {

    private CheckInRepository checkinRepo;
    private AgendaItemRepository agendaItemRepository;
    private MemberProfileRepository memberRepo;
    private SecurityService securityService;
    private CurrentUserServices currentUserServices;

    public AgendaItemServicesImpl(CheckInRepository checkinRepo, AgendaItemRepository agendaItemRepository,
                                   MemberProfileRepository memberRepo, SecurityService securityService,
                                   CurrentUserServices currentUserServices) {
        this.checkinRepo = checkinRepo;
        this.agendaItemRepository = agendaItemRepository;
        this.memberRepo = memberRepo;
        this.securityService = securityService;
        this.currentUserServices = currentUserServices;
    }


    @Override
    public AgendaItem save(AgendaItem agendaItem) {
        AgendaItem agendaItemRet = null;
        String workEmail = securityService != null ? securityService.getAuthentication().get().getAttributes().get("email").toString() : null;
        MemberProfile currentUser = workEmail != null ? currentUserServices.findOrSaveUser(null, workEmail) : null;
        Boolean isAdmin = securityService != null ? securityService.hasRole(RoleType.Constants.ADMIN_ROLE) : false;

        if (agendaItem != null) {
            final UUID checkinId = agendaItem.getCheckinid();
            final UUID createById = agendaItem.getCreatedbyid();
            double lastDisplayOrder = 0;
            try {
                lastDisplayOrder = agendaItemRepo.findMaxPriorityByCheckinid(agendaItem.getCheckinid()).orElse(Double.valueOf(0));
            } catch (NullPointerException npe) {
                //This case occurs when there is no existing record for this checkin id. We already have the display order set to 0 so
                //nothing needs to happen here.
            }
            agendaItem.setPriority(lastDisplayOrder+1);
            if (checkinId == null || createById == null) {
                throw new AgendaItemBadArgException(String.format("Invalid agendaItem %s", agendaItem));
            } else if (agendaItem.getId() != null) {
                throw new AgendaItemBadArgException(String.format("Found unexpected id %s for agenda item", agendaItem.getId()));
            } else if (!checkinRepo.findById(checkinId).isPresent()) {
                throw new AgendaItemBadArgException(String.format("CheckIn %s doesn't exist", checkinId));
            } else if (!memberRepo.findById(createById).isPresent()) {
                throw new AgendaItemBadArgException(String.format("Member %s doesn't exist", createById));
            }

            agendaItemRet = agendaItemRepository.save(agendaItem);
        }
        return agendaItemRet;
    }

    @Override
    public AgendaItem read(@NotNull UUID id) {
        String workEmail = securityService != null ? securityService.getAuthentication().get().getAttributes().get("email").toString() : null;
        MemberProfile currentUser = workEmail != null ? currentUserServices.findOrSaveUser(null, workEmail) : null;
        Boolean isAdmin = securityService != null ? securityService.hasRole(RoleType.Constants.ADMIN_ROLE) : false;
        AgendaItem agendaItemResult = agendaItemRepository.findById(id).orElse(null);
        validate(agendaItemResult == null, "Invalid agenda item id %s", id);
        if (!isAdmin) {
            CheckIn checkinRecord = checkinRepo.findById(agendaItemResult.getCheckinid()).orElse(null);
            final UUID pdlId = checkinRecord != null ? checkinRecord.getPdlId() : null;
            final UUID createById = checkinRecord != null ? checkinRecord.getTeamMemberId() : null;
            validate(!currentUser.getId().equals(pdlId) && !currentUser.getId().equals(createById), "User is unauthorized to do this operation");
        }

        return agendaItemResult;
    }


    @Override
    public AgendaItem update(AgendaItem agendaItem) {
        AgendaItem agendaItemRet = null;
        String workEmail = securityService != null ? securityService.getAuthentication().get().getAttributes().get("email").toString() : null;
        MemberProfile currentUser = workEmail != null ? currentUserServices.findOrSaveUser(null, workEmail) : null;
        Boolean isAdmin = securityService != null ? securityService.hasRole(RoleType.Constants.ADMIN_ROLE) : false;

        if (agendaItem != null) {
            final UUID id = agendaItem.getId();
            final UUID checkinId = agendaItem.getCheckinid();
            final UUID createById = agendaItem.getCreatedbyid();
            CheckIn checkinRecord = checkinRepo.findById(checkinId).orElse(null);
            Boolean isCompleted = checkinRecord != null ? checkinRecord.isCompleted() : null;
            final UUID pdlId = checkinRecord != null ? checkinRecord.getPdlId() : null;
            validate(checkinId == null || createById == null, "Invalid agenda item %s", agendaItem);
            validate(id == null || agendaItemRepository.findById(id).isEmpty(), "Unable to locate agenda item to update with id %s", agendaItem.getId());
            validate(checkinRepo.findById(checkinId).isEmpty(), "CheckIn %s doesn't exist", checkinId);
            validate(memberRepo.findById(createById).isEmpty(), "Member %s doesn't exist", createById);
            if (!isAdmin && isCompleted) {
                validate(true, "User is unauthorized to do this operation");
            } else if (!isAdmin && !isCompleted) {
                validate(!currentUser.getId().equals(pdlId) && !currentUser.getId().equals(createById), "User is unauthorized to do this operation");
            }

            agendaItemRet = agendaItemRepository.update(agendaItem);
        }
        return agendaItemRet;
    }


    @Override
    public Set<AgendaItem> findByFields(UUID checkinid, UUID createbyid) {
        String workEmail = securityService != null ? securityService.getAuthentication().get().getAttributes().get("email").toString() : null;
        MemberProfile currentUser = workEmail != null ? currentUserServices.findOrSaveUser(null, workEmail) : null;
        Boolean isAdmin = securityService != null ? securityService.hasRole(RoleType.Constants.ADMIN_ROLE) : false;

        if (checkinid != null) {
            CheckIn checkinRecord = checkinRepo.findById(checkinid).orElse(null);
            final UUID pdlId = checkinRecord != null ? checkinRecord.getPdlId() : null;
            final UUID teamMemberId = checkinRecord != null ? checkinRecord.getTeamMemberId() : null;
            validate(!currentUser.getId().equals(pdlId) && !currentUser.getId().equals(teamMemberId) && !isAdmin, "User is unauthorized to do this operation");
        } else if (createbyid != null) {
            MemberProfile memberRecord = memberRepo.findById(createbyid).orElse(null);
            validate(!currentUser.getId().equals(memberRecord.getId()) && !isAdmin, "User is unauthorized to do this operation");
        } else {
            validate(!isAdmin, "User is unauthorized to do this operation");
        }

        Set<AgendaItem> agendaItem = new HashSet<>(agendaItemRepository.search(nullSafeUUIDToString(checkinid), nullSafeUUIDToString(createbyid)));

        return agendaItem;
    }

    @Override
    public void delete(@NotNull UUID id) {
        String workEmail = securityService != null ? securityService.getAuthentication().get().getAttributes().get("email").toString() : null;
        MemberProfile currentUser = workEmail != null ? currentUserServices.findOrSaveUser(null, workEmail) : null;
        Boolean isAdmin = securityService != null ? securityService.hasRole(RoleType.Constants.ADMIN_ROLE) : false;
        AgendaItem agendaItemResult = agendaItemRepository.findById(id).orElse(null);
        validate(agendaItemResult == null, "Invalid agenda item id %s", id);

        CheckIn checkinRecord = checkinRepo.findById(agendaItemResult.getCheckinid()).orElse(null);
        final UUID pdlId = checkinRecord != null ? checkinRecord.getPdlId() : null;
        final UUID createById = checkinRecord != null ? checkinRecord.getTeamMemberId() : null;
        Boolean isCompleted = checkinRecord != null ? checkinRecord.isCompleted() : null;
        if (!isAdmin && isCompleted) {
            validate(true, "User is unauthorized to do this operation");
        } else if (!isAdmin && !isCompleted) {
            validate(!currentUser.getId().equals(pdlId) && !currentUser.getId().equals(createById), "User is unauthorized to do this operation");
        }

        agendaItemRepository.deleteById(id);
    }

    private void validate(@NotNull boolean isError, @NotNull String message, Object... args) {
        if (isError) {
            throw new AgendaItemBadArgException(String.format(message, args));
        }
    }
}