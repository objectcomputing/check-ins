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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.UUID;

import static com.objectcomputing.checkins.util.Util.nullSafeUUIDToString;

@Singleton
public class AgendaItemServicesImpl implements AgendaItemServices {
    public static final Logger LOG = LoggerFactory.getLogger(AgendaItemServicesImpl.class);

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

        if (agendaItem != null) {
            validate(agendaItem.getId() != null, "Found unexpected id %s for agenda item", agendaItem.getId());

            final UUID checkinId = agendaItem.getCheckinid();
            validate(checkinId == null, "Invalid agenda item %s, checkinId null", agendaItem);

            CheckIn checkinRecord = checkinRepo.findById(checkinId).orElse(null);
            validate(checkinRepo.findById(checkinId).isEmpty(), "CheckIn %s doesn't exist", checkinId);

            final UUID createById = agendaItem.getCreatedbyid();
            validate(createById == null, "Invalid agenda item %s, createById null", agendaItem);
            validate(memberRepo.findById(createById).isEmpty(), "Member %s doesn't exist", createById);

            final UUID currentUserId = currentUserServices.getCurrentUser().getId();
            boolean canUpdateAllCheckins = checkInServices.canUpdateAllCheckins(currentUserId);

            if (!canUpdateAllCheckins) {
                boolean isCompleted = checkinRecord != null && checkinRecord.isCompleted();
                validate(isCompleted, "User is unauthorized to do this operation");

                final UUID pdlId = checkinRecord != null ? checkinRecord.getPdlId() : null;
                boolean currentUserIsCheckinParticipant = currentUserId.equals(pdlId) || currentUserId.equals(createById);
                validate(!currentUserIsCheckinParticipant, "User is unauthorized to do this operation");
            }

            double lastDisplayOrder = agendaItemRepository.findMaxPriorityByCheckinid(agendaItem.getCheckinid()).orElse(0d);
            agendaItem.setPriority(lastDisplayOrder+1);
            
            agendaItemRet = agendaItemRepository.save(agendaItem);
        }
        return agendaItemRet;
    }

    @Override
    public AgendaItem read(@NotNull UUID id) {
        final UUID currentUserId = currentUserServices.getCurrentUser().getId();
        boolean canViewAllCheckins = checkInServices.canViewAllCheckins(currentUserId);

        AgendaItem agendaItemResult = agendaItemRepository.findById(id).orElse(null);
        validate(agendaItemResult == null, "Invalid agenda item id %s", id);
        if (!canViewAllCheckins) {
            CheckIn checkinRecord = checkinRepo.findById(agendaItemResult.getCheckinid()).orElse(null);
            final UUID pdlId = checkinRecord != null ? checkinRecord.getPdlId() : null;
            final UUID createById = checkinRecord != null ? checkinRecord.getTeamMemberId() : null;
            
            boolean currentUserIsCheckinParticipant = currentUserId.equals(pdlId) || currentUserId.equals(createById);
            validate(!currentUserIsCheckinParticipant, "User is unauthorized to do this operation");
        }

        return agendaItemResult;
    }


    @Override
    public AgendaItem update(AgendaItem agendaItem) {
        AgendaItem agendaItemRet = null;

        if (agendaItem != null) {
            final UUID id = agendaItem.getId();
            validate(id == null || agendaItemRepository.findById(id).isEmpty(), "Unable to locate agenda item to update with id %s", agendaItem.getId());

            final UUID currentUserId = currentUserServices.getCurrentUser().getId();
            boolean canUpdateAllCheckins = checkInServices.canUpdateAllCheckins(currentUserId);

            final UUID checkinId = agendaItem.getCheckinid();
            validate(checkinId == null, "Invalid agenda item %s, checkinId null", agendaItem);
            validate(checkinRepo.findById(checkinId).isEmpty(), "CheckIn %s doesn't exist", checkinId);

            CheckIn checkinRecord = checkinRepo.findById(checkinId).orElse(null);
            boolean isCompleted = checkinRecord != null && checkinRecord.isCompleted();
            validate(isCompleted, "User is unauthorized to do this operation");
            final UUID pdlId = checkinRecord != null ? checkinRecord.getPdlId() : null;

            final UUID createById = agendaItem.getCreatedbyid();
            validate(createById == null, "Invalid agenda item %s, createById null", agendaItem);
            validate(memberRepo.findById(createById).isEmpty(), "Member %s doesn't exist", createById);
                
            if (!canUpdateAllCheckins) {
                boolean currentUserIsCheckinParticipant = currentUserId.equals(pdlId) || currentUserId.equals(createById);
                validate(!currentUserIsCheckinParticipant, "User is unauthorized to do this operation");
            }

            agendaItemRet = agendaItemRepository.update(agendaItem);
        }
        return agendaItemRet;
    }

    @Override
    public Set<AgendaItem> findByFields(@Nullable UUID checkinid, @Nullable UUID createbyid) {
        MemberProfile currentUser = currentUserServices.getCurrentUser();
        final UUID currentUserId = currentUser.getId();
        boolean canViewAllCheckins = checkInServices.canViewAllCheckins(currentUserId);

        if (checkinid != null) {
            validate(!checkInServices.accessGranted(checkinid, currentUserId), "User is unauthorized to do this operation");
        } else if (createbyid != null) {
            MemberProfile memberRecord = memberRepo.findById(createbyid).orElseThrow();
            validate(!currentUserId.equals(memberRecord.getId()) && !canViewAllCheckins, "User is unauthorized to do this operation");
        } else {
            validate(!canViewAllCheckins, "User is unauthorized to do this operation");
        }

        return agendaItemRepository.search(nullSafeUUIDToString(checkinid), nullSafeUUIDToString(createbyid));
    }

    @Override
    public void delete(@NotNull UUID id) {
        AgendaItem agendaItemResult = agendaItemRepository.findById(id).orElse(null);
        validate(agendaItemResult == null, "Invalid agenda item id %s", id);

        final UUID currentUserId = currentUserServices.getCurrentUser().getId();
        boolean canUpdateAllCheckins = checkInServices.canUpdateAllCheckins(currentUserId);
        if (!canUpdateAllCheckins) {
            CheckIn checkinRecord = checkinRepo.findById(agendaItemResult.getCheckinid()).orElse(null);

            boolean isCompleted = checkinRecord != null && checkinRecord.isCompleted();
            validate(isCompleted, "User is unauthorized to do this operation");
            final UUID pdlId = checkinRecord != null ? checkinRecord.getPdlId() : null;
            final UUID createById = checkinRecord != null ? checkinRecord.getTeamMemberId() : null;

            boolean currentUserIsCheckinParticipant = currentUserId.equals(pdlId) || currentUserId.equals(createById);
            validate(!currentUserIsCheckinParticipant, "User is unauthorized to do this operation");
        }
        agendaItemRepository.deleteById(id);
    }

    private void validate(boolean isError, String message, Object... args) {
        if (isError) {
            throw new BadArgException(String.format(message, args));
        }
    }
}