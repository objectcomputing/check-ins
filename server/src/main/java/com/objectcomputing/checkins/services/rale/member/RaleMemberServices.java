package com.objectcomputing.checkins.services.rale.member;

import java.util.Set;
import java.util.UUID;

public interface RaleMemberServices {

    RaleMember save(RaleMember raleMember);

    RaleMember read(UUID id);

    RaleMember update(RaleMember raleMember);

    void delete(UUID id);

    Set<RaleMember> findByFields(UUID raleId, UUID memberId, Boolean lead);

    void deleteByRale(UUID id);
}