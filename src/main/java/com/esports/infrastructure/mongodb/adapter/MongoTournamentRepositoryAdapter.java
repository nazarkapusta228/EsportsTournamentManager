package com.esports.infrastructure.mongodb.adapter;

import com.esports.domain.model.Team;
import com.esports.domain.model.Tournament;
import com.esports.domain.model.TournamentStatus;
import com.esports.domain.repository.TournamentRepository;
import com.esports.infrastructure.mongodb.MongoDatabaseProvider;
import com.esports.infrastructure.mongodb.document.TournamentDocument;
import com.esports.infrastructure.mongodb.mapper.TournamentMapper;
import com.esports.infrastructure.mongodb.repository.MongoIdGenerator;
import com.esports.infrastructure.mongodb.repository.MongoTournamentRepository;
import com.esports.infrastructure.mongodb.repository.MongoGroupRepository;
import com.mongodb.client.MongoDatabase;

import java.util.List;
import java.util.Optional;

public class MongoTournamentRepositoryAdapter implements TournamentRepository {
    private final MongoTournamentRepository repository;
    private final MongoGroupRepository groupRepository;
    private final MongoTeamRepositoryAdapter teamAdapter;

    public MongoTournamentRepositoryAdapter() {
        this(MongoDatabaseProvider.getDatabase());
    }

    public MongoTournamentRepositoryAdapter(MongoDatabase database) {
        this.repository = new MongoTournamentRepository(database, new MongoIdGenerator(database));
        this.groupRepository = new MongoGroupRepository(database, new MongoIdGenerator(database));
        this.teamAdapter = new MongoTeamRepositoryAdapter(database);
    }

    @Override
    public void save(Tournament tournament) {
        validateTeamReferences(tournament);
        TournamentDocument document = TournamentMapper.toDocument(tournament);
        repository.save(document);
        tournament.setId(document.getId());
    }

    @Override
    public void update(Tournament tournament) {
        validateTeamReferences(tournament);
        repository.update(TournamentMapper.toDocument(tournament));
    }

    @Override
    public void delete(Long id) {
        if (groupRepository.existsByTournamentId(id)) {
            throw new IllegalStateException("Cannot delete a tournament that still has groups");
        }
        repository.delete(id);
    }

    @Override
    public Optional<Tournament> findById(Long id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Tournament> findAll() {
        return repository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public List<Tournament> findByStatus(TournamentStatus status) {
        return repository.findByStatus(status.name()).stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<Tournament> findByName(String name) {
        return repository.findByName(name).map(this::toDomain);
    }

    private void validateTeamReferences(Tournament tournament) {
        for (Team team : tournament.getRegisteredTeams()) {
            teamAdapter.requirePersisted(team);
        }
    }

    private Tournament toDomain(TournamentDocument document) {
        Tournament tournament = TournamentMapper.toDomain(document);
        for (Long teamId : document.getRegisteredTeamIds()) {
            Team team = teamAdapter.findById(teamId)
                    .orElseThrow(() -> new IllegalStateException(
                            "Tournament " + document.getId() + " references missing team " + teamId));
            tournament.getRegisteredTeams().add(team);
        }
        return tournament;
    }
}
