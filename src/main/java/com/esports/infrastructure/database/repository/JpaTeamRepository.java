package com.esports.infrastructure.database.repository;

import com.esports.infrastructure.database.entity.TeamEnt;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

public class JpaTeamRepository {
    
    private final EntityManager entityManager;

    public JpaTeamRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void save(TeamEnt team) {
        entityManager.persist(team);
    }

    public void update(TeamEnt team) {
        entityManager.merge(team);
    }

    public void delete(Long id) {
        TeamEnt team = entityManager.find(TeamEnt.class, id);
        if (team != null) {
            entityManager.remove(team);
        }
    }

    public Optional<TeamEnt> findById(Long id) {
        TeamEnt team = entityManager.find(TeamEnt.class, id);
        return Optional.ofNullable(team);
    }

    public Optional<TeamEnt> findByIdWithLock(Long id) {
        TeamEnt team = entityManager.find(TeamEnt.class, id, LockModeType.OPTIMISTIC);
        return Optional.ofNullable(team);
    }

    public List<TeamEnt> findAll() {
        TypedQuery<TeamEnt> query = entityManager.createQuery(
                "SELECT t FROM TeamEnt t", TeamEnt.class);
        return query.getResultList();
    }

    public Optional<TeamEnt> findByName(String name) {
        TypedQuery<TeamEnt> query = entityManager.createQuery(
                "SELECT t FROM TeamEnt t WHERE t.name = :name", TeamEnt.class);
        query.setParameter("name", name);
        List<TeamEnt> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public List<TeamEnt> findByRegion(String region) {
        TypedQuery<TeamEnt> query = entityManager.createQuery(
                "SELECT t FROM TeamEnt t WHERE t.region = :region", TeamEnt.class);
        query.setParameter("region", region);
        return query.getResultList();
    }
}
