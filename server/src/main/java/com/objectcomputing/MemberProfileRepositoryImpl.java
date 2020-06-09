package com.objectcomputing;

import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;

import com.objectcomputing.member.MemberProfile;

import io.micronaut.configuration.hibernate.jpa.scope.CurrentSession;
import io.micronaut.spring.tx.annotation.Transactional;

public class MemberProfileRepositoryImpl implements MemberProfileRepository {

    @PersistenceContext
    private EntityManager entityManager;
    
    public MemberProfileRepositoryImpl(@CurrentSession EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MemberProfile> findByName(String name) {
        String qlString = "SELECT m FROM MemberProfile m where name = :name";
        TypedQuery<MemberProfile> query = entityManager.createQuery(qlString, MemberProfile.class).setParameter("name", name);
        return query.getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MemberProfile> findByRole(String role) {
        String qlString = "SELECT m FROM MemberProfile m where role = :role";
        TypedQuery<MemberProfile> query = entityManager.createQuery(qlString, MemberProfile.class).setParameter("role", role);
        return query.getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MemberProfile> findByPdlId(UUID pdlId) {
        String qlString = "SELECT m FROM MemberProfile m where pdlId = :pdlId";
        TypedQuery<MemberProfile> query = entityManager.createQuery(qlString, MemberProfile.class).setParameter("pdlId", pdlId);
        return query.getResultList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MemberProfile> findAll() {
        String qlString = "SELECT m FROM MemberProfile m";
        TypedQuery<MemberProfile> query = entityManager.createQuery(qlString, MemberProfile.class);
        return query.getResultList();
    }

    @Override
    @Transactional
    public MemberProfile createProfile(@NotNull MemberProfile memberProfile) {
        entityManager.persist(memberProfile);
        return memberProfile;
    }

    @Override
    @Transactional
    public MemberProfile update(@NotNull MemberProfile memberProfile) {
        return entityManager.merge(memberProfile);
    }
}
