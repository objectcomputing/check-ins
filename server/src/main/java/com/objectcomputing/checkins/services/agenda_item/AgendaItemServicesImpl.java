package com.objectcomputing.checkins.services.agenda_item;

import com.objectcomputing.checkins.services.checkins.CheckIn;
import com.objectcomputing.checkins.services.checkins.CheckInRepository;
import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.services.checkins.CheckInServices;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;

import io.micronaut.core.annotation.Nullable;
import jakarta.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.util.Set;
import java.util.UUID;

import static com.objectcomputing.checkins.util.Util.nullSafeUUIDToString;

@Singleton
public class AgendaItemServicesImpl implements AgendaItemServices {

    private final CheckInRepository checkinRepo;
    private final AgendaItemRepository agendaItemRepository;
    private final MemberProfileRepository memberRepo;
    private final CurrentUserServices currentUserServices;
    private final CheckInServices checkInServices;

    public AgendaItemServicesImpl(CheckInRepository checkinRepo,
                                  AgendaItemRepository agendaItemRepository,
                                  MemberProfileRepository memberRepo,
                                  CurrentUserServices currentUserServices,
                                  CheckInServices checkInServices) {
        this.checkinRepo = checkinRepo;
        this.agendaItemRepository = agendaItemRepository;
        this.memberRepo = memberRepo;
        this.currentUserServices = currentUserServices;
        this.checkInServices = checkInServices;
    }

    @Override
    public AgendaItem save(AgendaItem agendaItem) {
        AgendaItem agendaItemRet = null;
        MemberProfile currentUser = currentUserServices.getCurrentUser();
        boolean isAdmin = currentUserServices.isAdmin();

        if (agendaItem != null) {
            final UUID checkinId = agendaItem.getCheckinid();
            final UUID createById = agendaItem.getCreatedbyid();
            double lastDisplayOrder = 0;
            try {
                lastDisplayOrder = agendaItemRepository.findMaxPriorityByCheckinid(agendaItem.getCheckinid()).orElse(Double.valueOf(0));
            } catch (NullPointerException npe) {
                //This case occurs when there is no existing record for this checkin id. We already have the display order set to 0 so
                //nothing needs to happen here.
            }
            agendaItem.setPriority(lastDisplayOrder+1);
            CheckIn checkinRecord = checkinRepo.findById(checkinId).orElse(null);
            Boolean isCompleted = checkinRecord != null ? checkinRecord.isCompleted() : null;
            final UUID pdlId = checkinRecord != null ? checkinRecord.getPdlId() : null;
            validate(checkinId == null || createById == null, "Invalid agenda item %s", agendaItem);
            validate(agendaItem.getId() != null, "Found unexpected id %s for agenda item", agendaItem.getId());
            validate(checkinRepo.findById(checkinId).isEmpty(), "CheckIn %s doesn't exist", checkinId);
            validate(memberRepo.findById(createById).isEmpty(), "Member %s doesn't exist", createById);
            if (!isAdmin && isCompleted) {
                validate(true, "User is unauthorized to do this operation");
            } else if (!isAdmin && !isCompleted) {
                validate(!currentUser.getId().equals(pdlId) && !currentUser.getId().equals(createById), "User is unauthorized to do this operation");
            }

            agendaItemRet = agendaItemRepository.save(agendaItem);
        }
        return agendaItemRet;
    }

    @Override
    public AgendaItem read(@NotNull UUID id) {
        MemberProfile currentUser = currentUserServices.getCurrentUser();
        boolean isAdmin = currentUserServices.isAdmin();
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
        MemberProfile currentUser = currentUserServices.getCurrentUser();
        boolean isAdmin = currentUserServices.isAdmin();

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
    public Set<AgendaItem> findByFields(@Nullable UUID checkinid, @Nullable UUID createbyid) {
        MemberProfile currentUser = currentUserServices.getCurrentUser();
        boolean isAdmin = currentUserServices.isAdmin();

        if (checkinid != null) {
            validate(!checkInServices.accessGranted(checkinid, currentUser.getId()), "User is unauthorized to do this operation");
        } else if (createbyid != null) {
            MemberProfile memberRecord = memberRepo.findById(createbyid).orElseThrow();
            validate(!currentUser.getId().equals(memberRecord.getId()) && !isAdmin, "User is unauthorized to do this operation");
        } else {
            validate(!isAdmin, "User is unauthorized to do this operation");
        }

        return agendaItemRepository.search(nullSafeUUIDToString(checkinid), nullSafeUUIDToString(createbyid));
    }

    @Override
    public void delete(@NotNull UUID id) {
        MemberProfile currentUser = currentUserServices.getCurrentUser();
        boolean isAdmin = currentUserServices.isAdmin();
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

    private void validate(boolean isError, String message, Object... args) {
        if (isError) {
            throw new BadArgException(message, args);
        }
    }
}