package com.esports.domain.repository;

import com.esports.domain.model.Team;

import java.util.List;
import java.util.Optional;

public interface TeamRepository {
    void save(Team team);
    void update(Team team);
    void delete(Long id);
    Optional<Team> findById(Long id);
    List<Team> findAll();
    Optional<Team> findByName(String name);
    List<Team> findByRegion(String region);
}
