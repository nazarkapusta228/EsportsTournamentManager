package com.esports.domain.repository;

import com.esports.domain.model.Tournament;
import com.esports.domain.model.TournamentStatus;

import java.util.List;
import java.util.Optional;

public interface TournamentRepository {
    void save(Tournament tournament);
    void update(Tournament tournament);
    void delete(Long id);
    Optional<Tournament> findById(Long id);
    List<Tournament> findAll();
    List<Tournament> findByStatus(TournamentStatus status);
    Optional<Tournament> findByName(String name);
}
