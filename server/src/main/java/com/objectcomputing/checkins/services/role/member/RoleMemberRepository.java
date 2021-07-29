package com.objectcomputing.checkins.services.role.member;


import com.objectcomputing.checkins.services.role.member.RoleMember;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface RoleMemberRepository extends CrudRepository<RoleMember, UUID> {

    List<RoleMember> findByRoleid(UUID roleid);

    List<RoleMember> findByMemberid(UUID uuid);

    Optional<RoleMember> findByRoleidAndMemberid(@NotNull UUID roleMemberid, @NotNull UUID memberId);

    RoleMember save(@NotNull RoleMember entity);

    @Query("DELETE " +
            "FROM role_member tm_ " +
            "WHERE roleid = :id ")
    void deleteByRoleId(@NotNull String id);

    void deleteByMemberid(@NotNull @NonNull UUID id);

    @Query("SELECT * " +
            "FROM role_member tm_ " +
            "WHERE (:roleId IS NULL OR tm_.roleid = :roleId) " +
            "AND (:memberid IS NULL OR tm_.memberid = :memberid) ")
    List<RoleMember> search(@Nullable String roleId, @Nullable String memberid);
}

