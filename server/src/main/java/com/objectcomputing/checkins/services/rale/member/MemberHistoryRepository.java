//package com.objectcomputing.checkins.services.rale.member;
//
//import io.micronaut.data.jdbc.annotation.JdbcRepository;
//import io.micronaut.data.model.query.builder.sql.Dialect;
//import io.micronaut.data.repository.CrudRepository;
//
//import java.util.List;
//import java.util.UUID;
//
//@JdbcRepository(dialect = Dialect.POSTGRES)
//public interface MemberHistoryRepository extends CrudRepository<MemberHistory, UUID> {
//
//    public List<MemberHistory> findByRaleIdAndMemberId (UUID raleId, UUID memberId);
//
//}
