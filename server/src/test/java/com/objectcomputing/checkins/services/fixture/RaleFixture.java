package com.objectcomputing.checkins.services.fixture;


import com.objectcomputing.checkins.services.rale.*;

import java.util.UUID;

public interface RaleFixture extends MemberProfileFixture, RepositoryFixture{

    default Rale createDefaultRale() {
        return getRaleRepository().save(new Rale(UUID.randomUUID(), RaleType.ADMIN, "Warriors"));
    }

    default Rale createAnotherDefaultRale() {
        return getRaleRepository().save(new Rale(UUID.randomUUID(), RaleType.ADMIN, "Warriors"));
    }

    default RaleCreateDTO createFromEntity(Rale entity) {
        return new RaleCreateDTO(entity.getRale(), entity.getDescription());
    }

    default RaleUpdateDTO updateFromEntity(Rale entity) {
        return new RaleUpdateDTO(entity.getId(), entity.getRale(), entity.getDescription());
    }

    default RaleResponseDTO responseFromEntity(Rale entity) {
        return new RaleResponseDTO(entity.getId(), entity.getRale(), entity.getDescription());
    }

    default Rale entityFromDTO(RaleUpdateDTO dto) {
        return new Rale(dto.getId(), dto.getRale(), dto.getDescription());
    }
}
