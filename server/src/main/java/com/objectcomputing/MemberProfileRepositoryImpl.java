package com.objectcomputing;

import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

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
    public MemberProfile createProfile(MemberProfile memberProfile) {
        MemberProfile newMemberProfile = new MemberProfile(memberProfile.getName(), 
                                                            memberProfile.getRole(), 
                                                            memberProfile.getPdlId(), 
                                                            memberProfile.getLocation(), 
                                                            memberProfile.getWorkEmail(), 
                                                            memberProfile.getInsperityId(), 
                                                            memberProfile.getStartDate(), 
                                                            memberProfile.getBioText());

        entityManager.persist(newMemberProfile);
        return newMemberProfile;
    }

    @Override
    @Transactional
    public int update(MemberProfile memberProfile) {
        return entityManager.createQuery("UPDATE MemberProfile m SET name = :name, role = :role, pdlId = :pdlId, location = :location, workEmail = : workEmail, insperityId = :insperityId, startDate = :startDate, bioText = :bioText WHERE uuid = :uuid")
                .setParameter("uuid", memberProfile.getUuid())
                .setParameter("name", memberProfile.getName())
                .setParameter("role", memberProfile.getRole())
                .setParameter("pdlId", memberProfile.getPdlId())
                .setParameter("location", memberProfile.getLocation())
                .setParameter("workEmail", memberProfile.getWorkEmail())
                .setParameter("insperityId", memberProfile.getInsperityId())
                .setParameter("startDate", memberProfile.getStartDate())
                .setParameter("bioText", memberProfile.getBioText())
                .executeUpdate();
    }
}
