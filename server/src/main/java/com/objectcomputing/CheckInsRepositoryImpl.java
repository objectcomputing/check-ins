package com.objectcomputing;

import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.objectcomputing.checkins.CheckIns;

import io.micronaut.spring.tx.annotation.Transactional;

public class CheckInsRepositoryImpl implements CheckInsRepository {

    @PersistenceContext
    private EntityManager entityManager;    

    @Override
    public List<CheckIns> findByName(String teamMember) {
        return null;
    }
    @Override
    public List<CheckIns> findByPdlId(UUID pdlId) {
        return null;
    }

    @Override
    public List<CheckIns> findByTargetQuarter(String targetYear, String targetQtr) {
        return null;
    }

    @Override
    public List<CheckIns> findAll() {
        return null;
    }

    @Override
    @Transactional
    public CheckIns createMemberCheckIn(CheckIns checkIns) {
        entityManager.persist(checkIns);
        return checkIns;
        }

	@Override
	public CheckIns update(CheckIns checkIns) {
		return null;
	}

}