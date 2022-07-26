package com.objectcomputing.checkins.services.private_notes;

import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.checkins.CheckIn;
import com.objectcomputing.checkins.services.checkins.CheckInServices;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.services.role.RoleType;
import com.objectcomputing.checkins.exceptions.BadArgException;

import io.micronaut.core.annotation.Nullable;
import jakarta.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static com.objectcomputing.checkins.util.Util.nullSafeUUIDToString;
import static com.objectcomputing.checkins.services.validate.Validation.validate;

@Singleton
public class PrivateNoteServicesImpl implements PrivateNoteServices {

    private final CheckInServices checkinServices;
    private final PrivateNoteRepository privateNoteRepository;
    private final MemberProfileRepository memberRepo;
    private final MemberProfileServices memberProfileServices;
    private final CurrentUserServices currentUserServices;
    final String unauthorizedErrorMessage ="User is unauthorized to do this operation";

    public PrivateNoteServicesImpl(CheckInServices checkinServices, PrivateNoteRepository privateNoteRepository,
                                   MemberProfileRepository memberRepo, MemberProfileServices memberProfileServices,
                                   CurrentUserServices currentUserServices) {
        this.checkinServices = checkinServices;
        this.privateNoteRepository = privateNoteRepository;
        this.memberRepo = memberRepo;
        this.memberProfileServices = memberProfileServices;
        this.currentUserServices = currentUserServices;
    }

    @Override
    public PrivateNote save(@NotNull PrivateNote privateNote) {
        MemberProfile currentUser = currentUserServices.getCurrentUser();
        boolean isAdmin = currentUserServices.isAdmin();

        final UUID checkinId = privateNote.getCheckinid();
        final UUID createdById = privateNote.getCreatedbyid();
        CheckIn checkinRecord = checkinServices.read(checkinId);
        boolean isCompleted = checkinRecord != null && checkinRecord.isCompleted();

        validate(privateNote.getId() == null).orElseThrow(() -> {
            throw new BadArgException("Found unexpected id %s for private note", privateNote.getId());
        });
        validate(checkinId != null && createdById != null).orElseThrow(() -> {
            throw new BadArgException("Invalid private note %s", privateNote);
        });
        validate(checkinRecord != null).orElseThrow(() -> {
            throw new BadArgException("Checkin doesn't exits for given checkin Id");
        });
        validate(memberProfileServices.getById(createdById) != null).orElseThrow(() -> {
            throw new BadArgException("Member %s doesn't exist", createdById);
        });

        if (!isAdmin) {
            validate(checkinServices.accessGranted(checkinRecord.getId(), currentUser.getId()) && !isCompleted).orElseThrow(() -> {
                throw new PermissionException("User is unauthorized to do this operation");
            });
            validate(!currentUser.getId().equals(checkinRecord.getTeamMemberId())).orElseThrow(() -> {
                throw new PermissionException("User is unauthorized to do this operation");
            });
        }

        return privateNoteRepository.save(privateNote);
    }

    @Override
    public PrivateNote read(@NotNull UUID id) {
        MemberProfile currentUser = currentUserServices.getCurrentUser();
        boolean isAdmin = currentUserServices.isAdmin();
        PrivateNote privateNoteResult = privateNoteRepository.findById(id).orElse(null);

        validate(privateNoteResult == null).orElseThrow(() -> {
            throw new NotFoundException("Invalid private not id %s", id);
        });

        if (!isAdmin) {
            CheckIn checkinRecord = checkinServices.read(privateNoteResult.getCheckinid());

            validate(checkinRecord == null).orElseThrow(() -> {
                throw new NotFoundException("CheckIn %s doesn't exist", privateNoteResult.getCheckinid());
            });
            validate(!checkinServices.accessGranted(checkinRecord.getId(), currentUser.getId())).orElseThrow(() -> {
                throw new PermissionException("User is unauthorized to do this operation");
            });
            validate(currentUser.getId().equals(checkinRecord.getTeamMemberId())).orElseThrow(() -> {
                throw new PermissionException("User is unauthorized to do this operation");
            });
        }

        return privateNoteResult;
    }

    @Override
    public PrivateNote update(@NotNull PrivateNote privateNote) {
        MemberProfile currentUser = currentUserServices.getCurrentUser();
        boolean isAdmin = currentUserServices.isAdmin();
        boolean isPdl = currentUserServices.hasRole(RoleType.PDL);

        final UUID checkinId = privateNote.getCheckinid();
        final UUID createdById = privateNote.getCreatedbyid();
        CheckIn checkinRecord = checkinServices.read(checkinId);
        boolean isCompleted = checkinRecord != null && checkinRecord.isCompleted();

        validate(checkinId != null && createdById != null).orElseThrow(() -> {
            throw new BadArgException("Invalid private note %s", privateNote);
        });
        validate((!isAdmin || isPdl) && !isCompleted).orElseThrow(() -> {
            throw new BadArgException(unauthorizedErrorMessage);
        });
        validate(privateNote.getId() != null).orElseThrow(() -> {
            throw new BadArgException("No private note id %s found for updating", privateNote.getId());
        });
        validate(checkinRecord != null).orElseThrow(() -> {
            throw new BadArgException("Checkin doesn't exits for given checkin Id");
        });
        validate(memberProfileServices.getById(createdById) != null).orElseThrow(() -> {
            throw new BadArgException("Member %s doesn't exist", createdById);
        });

        Optional<?> t = checkinRecord != null ? Optional.of(true) : Optional.empty();

        if (!isAdmin) {

            validate(checkinServices.accessGranted(checkinRecord.getId(), currentUser.getId()) && !isCompleted).orElseThrow(() -> {
                throw new PermissionException("User is unauthorized to do this operation");
            });

            if(currentUser.getId().equals(checkinRecord.getTeamMemberId())) {
                throw new PermissionException("User is unauthorized to do this operation");
            }
        }

        return privateNoteRepository.update(privateNote);

    }

    @Override
    public Set<PrivateNote> findByFields(@Nullable UUID checkinid, @Nullable UUID createbyid) {
        MemberProfile currentUser = currentUserServices.getCurrentUser();
        boolean isAdmin = currentUserServices.isAdmin();

        if (checkinid != null) {
            if (!checkinServices.accessGranted(checkinid, currentUser.getId()))
                throw new PermissionException("User is unauthorized to do this operation");
        } else if (createbyid != null) {
            MemberProfile memberRecord = memberRepo.findById(createbyid).orElseThrow();
            if (!currentUser.getId().equals(memberRecord.getId()) && !isAdmin)
                throw new PermissionException("User is unauthorized to do this operation");
        } else if (!isAdmin) {
            throw new PermissionException("User is unauthorized to do this operation");
        }

        return privateNoteRepository.search(nullSafeUUIDToString(checkinid), nullSafeUUIDToString(createbyid));
    }

}
