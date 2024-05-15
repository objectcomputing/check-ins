package com.objectcomputing.checkins.services.member_skill;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface MemberSkillRepository extends CrudRepository<MemberSkill, UUID> {

    List<MemberSkill> findAll();

    Optional<MemberSkill> findById(UUID id);

    List<MemberSkill> findByMemberid(UUID uuid);

    List<MemberSkill> findBySkillid(UUID skillid);

    Optional<MemberSkill> findByMemberidAndSkillid(UUID memberId,UUID skillid );

}
