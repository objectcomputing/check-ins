package com.objectcomputing.checkins.services.memberprofile;

import com.objectcomputing.checkins.exceptions.NotFoundException;

import javax.inject.Singleton;
import java.util.Optional;
import java.util.UUID;

@Singleton
public class MemberProfileRetrievalServicesImpl implements MemberProfileRetrievalServices {

    private final MemberProfileRepository memberProfileRepository;

    public MemberProfileRetrievalServicesImpl(MemberProfileRepository memberProfileRepository) {
        this.memberProfileRepository = memberProfileRepository;
    }

    @Override
    public Optional<MemberProfile> getById(UUID id) {
        return memberProfileRepository.findById(id);
    }

    @Override
    public Optional<MemberProfile> findByWorkEmail(String workEmail) {
        return memberProfileRepository.findByWorkEmail(workEmail);
    }
}
