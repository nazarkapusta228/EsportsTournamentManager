package com.esports.infrastructure.database.adapter;

import com.esports.domain.model.Team;
import com.esports.domain.repository.TeamRepository;
import com.esports.infrastructure.database.JpaUtil;
import com.esports.infrastructure.database.entity.TeamEnt;
import com.esports.infrastructure.database.mapper.TeamMapper;
import com.esports.infrastructure.database.repository.JpaTeamRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adapter that implements domain TeamRepository interface using JPA.
 * This separates the domain layer from the infrastructure layer.
 */
public class JpaTeamRepositoryAdapter implements TeamRepository {

    @Override
    public void save(Team team) {
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            JpaTeamRepository jpaRepo = new JpaTeamRepository(em);
            TeamEnt entity = TeamMapper.toEntity(team);
            jpaRepo.save(entity);
            tx.commit();
            team.setId(entity.getId());
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Failed to save team", e);
        } finally {
            em.close();
        }
    }

    @Override
    public void update(Team team) {
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            JpaTeamRepository jpaRepo = new JpaTeamRepository(em);
            Optional<TeamEnt> existingOpt = jpaRepo.findById(team.getId());
            if (existingOpt.isPresent()) {
                TeamEnt existing = existingOpt.get();
                TeamMapper.updateEntity(team, existing);
                jpaRepo.update(existing);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Failed to update team", e);
        } finally {
            em.close();
        }
    }

    @Override
    public void delete(Long id) {
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            JpaTeamRepository jpaRepo = new JpaTeamRepository(em);
            jpaRepo.delete(id);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Failed to delete team", e);
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<Team> findById(Long id) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            JpaTeamRepository jpaRepo = new JpaTeamRepository(em);
            Optional<TeamEnt> entityOpt = jpaRepo.findById(id);
            return entityOpt.map(TeamMapper::toDomain);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Team> findAll() {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            JpaTeamRepository jpaRepo = new JpaTeamRepository(em);
            List<TeamEnt> entities = jpaRepo.findAll();
            return entities.stream()
                    .map(TeamMapper::toDomain)
                    .collect(Collectors.toList());
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<Team> findByName(String name) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            JpaTeamRepository jpaRepo = new JpaTeamRepository(em);
            Optional<TeamEnt> entityOpt = jpaRepo.findByName(name);
            return entityOpt.map(TeamMapper::toDomain);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Team> findByRegion(String region) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            JpaTeamRepository jpaRepo = new JpaTeamRepository(em);
            List<TeamEnt> entities = jpaRepo.findByRegion(region);
            return entities.stream()
                    .map(TeamMapper::toDomain)
                    .collect(Collectors.toList());
        } finally {
            em.close();
        }
    }
}
