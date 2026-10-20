package com.esports.domain.repository;

import com.esports.domain.model.Group;

import java.util.List;
import java.util.Optional;

public interface GroupRepository {
    void save(Group group);
    void update(Group group);
    void delete(Long id);
    Optional<Group> findById(Long id);
    List<Group> findAll();
    List<Group> findByTournamentId(Long tournamentId);
}
