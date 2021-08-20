package com.objectcomputing.checkins.services.role.member_roles;

import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface MemberRoleRepository extends CrudRepository<MemberRole, MemberRoleId> {

    List<MemberRole> findAll();

    @Query("INSERT INTO member_roles " +
            "    (roleid, memberid) " +
            "VALUES " +
            "    (:roleid, :memberid)")
    MemberRole save(UUID memberid, UUID roleid);

    @Query("DELETE FROM " +
            "member_roles " +
            "WHERE (memberid, roleid) = (:memberid, :roleid)")
    void deleteById(String memberid, String roleid);


    @Query("DELETE FROM " +
            "member_roles " +
            "WHERE (memberid) = (:memberid)")
    void removeMemberFromRoles(String memberid);
}
