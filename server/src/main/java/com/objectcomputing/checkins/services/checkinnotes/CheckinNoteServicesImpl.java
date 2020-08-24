package com.objectcomputing.checkins.services.checkinnotes;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;

import com.objectcomputing.checkins.services.checkins.CheckInRepository;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;

public class CheckinNoteServicesImpl implements CheckinNoteServices {

    @Inject
    private CheckInRepository checkinRepo;

    @Inject
    private CheckinNoteRepository checkinNoteRepository;

    @Inject
    private MemberProfileRepository memberRepo;

    @Override
    public CheckinNote save(CheckinNote checkinNote) {
        CheckinNote checkinNoteRet = null;
        if (checkinNote != null) {
            final UUID checkinId = checkinNote.getCheckinid();
            final UUID createById = checkinNote.getCreatedbyid();
            if (checkinId == null || createById == null) {
                throw new CheckinNotesBadArgException(String.format("Invalid checkin note %s", checkinNote));
            } else if (checkinNote.getId() != null) {
                throw new CheckinNotesBadArgException(String.format("Found unexpected id %s for check in note", checkinNote.getId()));
            } else if (!checkinRepo.findById(checkinId).isPresent()) {
                throw new CheckinNotesBadArgException(String.format("CheckIn %s doesn't exist", checkinId));
            } else if (!memberRepo.findById(createById).isPresent()) {
                throw new CheckinNotesBadArgException(String.format("Member %s doesn't exist", createById));
            }

            checkinNoteRet = checkinNoteRepository.save(checkinNote);
        }
        return checkinNoteRet;
    }

    @Override
    public CheckinNote read(@NotNull UUID id) {
        return checkinNoteRepository.findById(id).orElse(null);
    }


    @Override
    public CheckinNote update(CheckinNote checkinNote) {
        CheckinNote checkinNoteRet = null;
        if (checkinNote != null) {
            final UUID id = checkinNote.getId();
            final UUID checkinId = checkinNote.getCheckinid();
            final UUID createById = checkinNote.getCreatedbyid();
            if (checkinId == null || createById == null) {
                throw new CheckinNotesBadArgException(String.format("Invalid checkin note %s", checkinNote));
            } else if (id==null ||!checkinNoteRepository.findById(id).isPresent()) {
                throw new CheckinNotesBadArgException(String.format("Unable to locate checkin note to update with id %s", checkinNote.getId()));
            } else if (!checkinRepo.findById(checkinId).isPresent()) {
                throw new CheckinNotesBadArgException(String.format("CheckIn %s doesn't exist", checkinId));
            } else if (!memberRepo.findById(createById).isPresent()) {
                throw new CheckinNotesBadArgException(String.format("Member %s doesn't exist", createById));
            }

            checkinNoteRet = checkinNoteRepository.update(checkinNote);
        }
        return checkinNoteRet;
    }
    

    @Override
    public Set<CheckinNote> findByFields(UUID checkinid, UUID createbyid) {
        Set<CheckinNote> checkinNote = new HashSet<>();
        checkinNoteRepository.findAll().forEach(checkinNote::add);
        if(checkinid!=null){
            checkinNote.retainAll(checkinNoteRepository.findByCheckinid(checkinid));
        } else if(createbyid!=null){
            checkinNote.retainAll(checkinNoteRepository.findByCreatedbyid(createbyid));
        }
        return checkinNote;
    }

    @Override
    public void delete(@NotNull UUID uuid) {
        checkinNoteRepository.deleteById(uuid);
    }
    
}