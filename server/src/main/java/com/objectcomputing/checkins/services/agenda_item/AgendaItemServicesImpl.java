package com.objectcomputing.checkins.services.agenda_item;

import com.objectcomputing.checkins.exceptions.PermissionException;
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
import static com.objectcomputing.checkins.services.validate.Validation.validate;

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
            agendaItem.setPriority(lastDisplayOrder + 1);

            CheckIn checkinRecord = checkinRepo.findById(checkinId).orElseThrow(() -> {
               throw new BadArgException("CheckIn %s doesn't exist", checkinId);
            });
            boolean isCompleted = checkinRecord.isCompleted();
            final UUID pdlId = checkinRecord.getPdlId();

            validate(createById != null).orElseThrow(() -> {
                throw new BadArgException("Invalid agenda item %s", agendaItem);
            });
            validate(agendaItem.getId() == null).orElseThrow(() -> {
                throw new BadArgException("Found unexpected id %s for agenda item", agendaItem.getId());
            });
            validate(memberRepo.findById(createById).isPresent()).orElseThrow(() -> {
                throw new BadArgException("Member %s doesn't exist", createById);
            });

            if (isCompleted) {
                validate(isAdmin).orElseThrow(() -> {
                    throw new PermissionException("User is unauthorized to do this operation");
                });
            } else {
                boolean isPdl = currentUser.getId().equals(pdlId);
                boolean isCreator = currentUser.getId().equals(createById);
                validate(isAdmin || isPdl || isCreator).orElseThrow(() -> {
                    throw new PermissionException("User is unauthorized to do this operation");
                });
            }

            agendaItemRet = agendaItemRepository.save(agendaItem);
        }
        return agendaItemRet;
    }

    @Override
    public AgendaItem read(@NotNull UUID id) {
        MemberProfile currentUser = currentUserServices.getCurrentUser();
        boolean isAdmin = currentUserServices.isAdmin();
        AgendaItem agendaItemResult = agendaItemRepository.findById(id).orElseThrow(() -> {
            throw new BadArgException("Invalid agenda item id %s", id);
        });

        CheckIn checkinRecord = checkinRepo.findById(agendaItemResult.getCheckinid()).orElse(null);
        final UUID pdlId = checkinRecord != null ? checkinRecord.getPdlId() : null;
        final UUID createById = checkinRecord != null ? checkinRecord.getTeamMemberId() : null;

        boolean isPdl = currentUser.getId().equals(pdlId);
        boolean isCreator = currentUser.getId().equals(createById);
        validate(isAdmin || isPdl || isCreator).orElseThrow(() -> {
            throw new PermissionException("User is unauthorized to do this operation");
        });

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
            CheckIn checkinRecord = checkinRepo.findById(checkinId).orElseThrow(() -> {
                throw new BadArgException("CheckIn %s doesn't exist", checkinId);
            });
            boolean isCompleted = checkinRecord.isCompleted();
            final UUID pdlId = checkinRecord.getPdlId();

            validate(createById != null).orElseThrow(() -> {
                throw new BadArgException("Invalid agenda item %s", agendaItem);
            });
            validate(id != null && agendaItemRepository.findById(id).isPresent()).orElseThrow(() -> {
                throw new BadArgException("Unable to locate agenda item to update with id %s", agendaItem.getId());
            });
            memberRepo.findById(createById).orElseThrow(() -> {
                throw new BadArgException("Member %s doesn't exist", createById);
            });

            if (isCompleted) {
                validate(isAdmin).orElseThrow(() -> {
                    throw new PermissionException("User is unauthorized to do this operation");
                });
            } else {
                boolean isPdl = currentUser.getId().equals(pdlId);
                boolean isCreator = currentUser.getId().equals(createById);
                validate(isAdmin || isPdl || isCreator).orElseThrow(() -> {
                    throw new PermissionException("User is unauthorized to do this operation");
                });
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
            validate(checkInServices.accessGranted(checkinid, currentUser.getId())).orElseThrow(() -> {
                throw new PermissionException("User is unauthorized to do this operation");
            });
        } else if (createbyid != null) {
            MemberProfile memberRecord = memberRepo.findById(createbyid).orElseThrow();
            validate(isAdmin || currentUser.getId().equals(memberRecord.getId())).orElseThrow(() -> {
                throw new PermissionException("User is unauthorized to do this operation");
            });
        } else {
            validate(isAdmin).orElseThrow(() -> {
                throw new PermissionException("User is unauthorized to do this operation");
            });
        }

        return agendaItemRepository.search(nullSafeUUIDToString(checkinid), nullSafeUUIDToString(createbyid));
    }

    @Override
    public void delete(@NotNull UUID id) {
        MemberProfile currentUser = currentUserServices.getCurrentUser();
        boolean isAdmin = currentUserServices.isAdmin();
        AgendaItem agendaItemResult = agendaItemRepository.findById(id).orElseThrow(() -> {
            throw new BadArgException("Invalid agenda item id %s", id);
        });

        CheckIn checkinRecord = checkinRepo.findById(agendaItemResult.getCheckinid()).orElse(null);
        final UUID pdlId = checkinRecord != null ? checkinRecord.getPdlId() : null;
        final UUID createById = checkinRecord != null ? checkinRecord.getTeamMemberId() : null;
        Boolean isCompleted = checkinRecord != null ? checkinRecord.isCompleted() : null;

        if (isCompleted) {
            validate(isAdmin).orElseThrow(() -> {
                throw new PermissionException("User is unauthorized to do this operation");
            });
        } else {
            boolean isPdl = currentUser.getId().equals(pdlId);
            boolean isCreator = currentUser.getId().equals(createById);
            validate(isAdmin || isPdl || isCreator).orElseThrow(() -> {
                throw new PermissionException("User is unauthorized to do this operation");
            });
        }

        agendaItemRepository.deleteById(id);
    }


}