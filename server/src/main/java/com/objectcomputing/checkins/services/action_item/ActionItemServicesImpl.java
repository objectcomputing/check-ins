package com.objectcomputing.checkins.services.action_item;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.checkins.CheckIn;
import com.objectcomputing.checkins.services.checkins.CheckInServices;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;

import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import jakarta.inject.Singleton;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.lang.reflect.Member;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

import static com.objectcomputing.checkins.util.Util.nullSafeUUIDToString;
import static com.objectcomputing.checkins.services.validate.Validation.validate;

@Singleton
public class ActionItemServicesImpl implements ActionItemServices {

    private final ActionItemRepository actionItemRepo;
    private final CheckInServices checkInServices;
    private final MemberProfileServices memberProfileServices;
    private final CurrentUserServices currentUserServices;

    public ActionItemServicesImpl(ActionItemRepository actionItemRepo,
                                  CheckInServices checkInServices,
                                  MemberProfileServices memberProfileServices,
                                  CurrentUserServices currentUserServices) {
        this.actionItemRepo = actionItemRepo;
        this.checkInServices = checkInServices;
        this.memberProfileServices = memberProfileServices;
        this.currentUserServices = currentUserServices;
    }

    public ActionItem save(@Valid @NotNull ActionItem actionItem) {
        ActionItem actionItemRet;

        validate(actionItem.getId() == null).orElseThrow(() -> {
            throw new BadArgException("Found unexpected id %s for action item", actionItem.getId());
        });

        CheckIn checkInRecord = checkInServices.read(actionItem.getCheckinid());

        validate(checkInRecord != null).orElseThrow(() -> {
            throw new BadArgException("CheckIn %s doesn't exist", actionItem.getCheckinid());
        });
        validate(memberProfileServices.getById(actionItem.getCreatedbyid()) != null).orElseThrow(() -> {
            throw new BadArgException("Member %s doesn't exist", actionItem.getCreatedbyid());
        });

        boolean isAdmin = currentUserServices.isAdmin();

        if (checkInRecord.isCompleted()) {
            validate(isAdmin).orElseThrow(() -> {
                throw new PermissionException("User is unauthorized to do this operation");
            });
        } else {
            boolean isPdl = currentUserServices.getCurrentUser().getId().equals(checkInRecord.getPdlId());
            boolean isTeamMember = currentUserServices.getCurrentUser().getId().equals(checkInRecord.getTeamMemberId());
            validate(isAdmin || isPdl || isTeamMember).orElseThrow(() -> {
                throw new PermissionException("User is unauthorized to do this operation");
            });
        }

        double lastDisplayOrder = 0;
        try {
            lastDisplayOrder = actionItemRepo.findMaxPriorityByCheckinid(actionItem.getCheckinid()).orElse((double) 0);
        } catch (NullPointerException npe) {
            //This case occurs when there is no existing record for this checkin id. We already have the display order set to 0 so
            //nothing needs to happen here.
        }
        actionItem.setPriority(lastDisplayOrder + 1);

        actionItemRet = actionItemRepo.save(actionItem);

        return actionItemRet;

    }

    public ActionItem read(@NotNull UUID id) {

        ActionItem actionItem = actionItemRepo.findById(id).orElse(null);

        if (actionItem != null) {

            CheckIn checkInRecord = checkInServices.read(actionItem.getCheckinid());

            validate(checkInRecord != null).orElseThrow(() -> {
                throw new BadArgException("CheckIn %s doesn't exist", actionItem.getCheckinid());
            });
            validate(memberProfileServices.getById(actionItem.getCreatedbyid()) != null).orElseThrow(() -> {
                throw new BadArgException("Member %s doesn't exist", actionItem.getCreatedbyid());
            });

            boolean isAdmin = currentUserServices.isAdmin();
            boolean isPdl = currentUserServices.getCurrentUser().getId().equals(checkInRecord.getPdlId());
            boolean isCreator = currentUserServices.getCurrentUser().getId().equals(checkInRecord.getTeamMemberId());
            validate(isAdmin || isPdl || isCreator).orElseThrow(() -> {
                throw new PermissionException("User is unauthorized to do this operation");
            });
        }

        return actionItem;

    }

    public ActionItem update(@Valid @NotNull ActionItem actionItem) {
        UUID checkInId = actionItem.getCheckinid();
        UUID createdById = actionItem.getCreatedbyid();

        validate(checkInId != null && createdById != null).orElseThrow(() -> {
            throw new BadArgException("Invalid action item %s", actionItem);
        });
        validate(actionItem.getId() != null && actionItemRepo.findById(actionItem.getId()).isPresent()).orElseThrow(() -> {
            throw new BadArgException("Unable to locate action item to update with id %s", actionItem.getId());
        });

        CheckIn checkInRecord = checkInServices.read(checkInId);
        validate(checkInRecord != null).orElseThrow(() -> {
            throw new BadArgException("CheckIn %s doesn't exist", checkInId);
        });
        validate(memberProfileServices.getById(createdById) != null).orElseThrow(() -> {
            throw new BadArgException("Member %s doesn't exist", createdById);
        });

        boolean isAdmin = currentUserServices.isAdmin();
        if (checkInRecord.isCompleted()) {
            validate(isAdmin).orElseThrow(() -> {
                throw new PermissionException("User is unauthorized to do this operation");
            });
        } else {
            boolean isPdl = currentUserServices.getCurrentUser().getId().equals(checkInRecord.getPdlId());
            boolean isTeamMember = currentUserServices.getCurrentUser().getId().equals(checkInRecord.getTeamMemberId());
            validate(isAdmin || isPdl || isTeamMember).orElseThrow(() -> {
                throw new PermissionException("User is unauthorized to do this operation");
            });
        }

        return actionItemRepo.update(actionItem);
    }

    public Set<ActionItem> findByFields(UUID checkinid, UUID createdbyid) {
        boolean isAdmin = currentUserServices.isAdmin();
        UUID currentUserId = currentUserServices.getCurrentUser().getId();

        if (checkinid != null) {
            validate(checkInServices.accessGranted(checkinid, currentUserId)).orElseThrow(() -> {
                throw new PermissionException("User is unauthorized to do this operation");
            });
        } else if (createdbyid != null) {
            boolean isCreator = memberProfileServices.getById(createdbyid).getId().equals(currentUserId);
            validate(isAdmin || isCreator).orElseThrow(() -> {
                throw new PermissionException("User is unauthorized to do this operation");
            });
        } else {
            validate(isAdmin).orElseThrow(() -> {
                throw new PermissionException("User is unauthorized to do this operation");
            });
        }

        String chkInId = nullSafeUUIDToString(checkinid);
        String createdBy = nullSafeUUIDToString(createdbyid);
        return new LinkedHashSet<>(actionItemRepo.search(chkInId, createdBy));
    }

    public void delete(@NotNull UUID id) {
        ActionItem actionItem = actionItemRepo.findById(id).orElseThrow(() -> {
            throw new BadArgException("Unable to locate action item to delete with id %s", id);
        });

        UUID checkInId = actionItem.getCheckinid();
        UUID creatorId = actionItem.getCreatedbyid();

        validate(checkInId != null && creatorId != null).orElseThrow(() -> {
            throw new BadArgException("Invalid action item %s", actionItem);
        });

        CheckIn checkInRecord = checkInServices.read(checkInId);
        validate(checkInRecord != null).orElseThrow(() -> {
            throw new BadArgException("CheckIn %s doesn't exist", checkInId);
        });
        validate(memberProfileServices.getById(creatorId) != null).orElseThrow(() -> {
            throw new BadArgException("Member %s doesn't exist", creatorId);
        });

        boolean isAdmin = currentUserServices.isAdmin();

        if (checkInRecord.isCompleted()) {
            validate(isAdmin).orElseThrow(() -> {
                throw new PermissionException("User is unauthorized to do this operation");
            });
        } else {
            boolean isPdl = currentUserServices.getCurrentUser().getId().equals(checkInRecord.getPdlId());
            boolean isCreator = currentUserServices.getCurrentUser().getId().equals(creatorId);
            validate(isAdmin || isPdl || isCreator).orElseThrow(() -> {
                throw new PermissionException("User is unauthorized to do this operation");
            });
        }

        actionItemRepo.deleteById(id);
    }

}


