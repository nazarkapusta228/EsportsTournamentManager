package com.esports.domain.repository;

import com.esports.domain.model.Match;
import com.esports.domain.model.MatchStatus;

import java.util.List;
import java.util.Optional;

public interface MatchRepository {
    void save(Match match);
    void update(Match match);
    void delete(Long id);
    Optional<Match> findById(Long id);
    List<Match> findAll();
    List<Match> findByStatus(MatchStatus status);
    List<Match> findByGroupId(Long groupId);
}
