package com.objectcomputing;

import java.util.Optional;

import com.objectcomputing.member.MemberProfile;

public class MemberProfileRepositoryImpl implements MemberProfileRepository {

    @Override
    public MemberProfile createProfile(String name) {
        return null;
    }

    @Override
    public Optional<MemberProfile> findBy(String name, String role, String pdlId) {
        return null;
    }

    @Override
    public String update(String name, String pdlId) {
        return null;
    }

	@Override
	public MemberProfile getProfile(String id) {
		return null;
	}

}