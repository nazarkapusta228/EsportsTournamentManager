package com.esports.infrastructure.mongodb.adapter;

import com.esports.domain.model.Player;
import com.esports.domain.model.Team;
import com.esports.domain.repository.TeamRepository;
import com.esports.infrastructure.mongodb.MongoDatabaseProvider;
import com.esports.infrastructure.mongodb.document.TeamDocument;
import com.esports.infrastructure.mongodb.mapper.PlayerMapper;
import com.esports.infrastructure.mongodb.mapper.TeamMapper;
import com.esports.infrastructure.mongodb.repository.MongoIdGenerator;
import com.esports.infrastructure.mongodb.repository.MongoPlayerRepository;
import com.esports.infrastructure.mongodb.repository.MongoTeamRepository;
import com.esports.infrastructure.mongodb.repository.MongoTournamentRepository;
import com.esports.infrastructure.mongodb.repository.MongoGroupRepository;
import com.esports.infrastructure.mongodb.repository.MongoMatchRepository;
import com.mongodb.client.MongoDatabase;

import java.util.List;
import java.util.Optional;

public class MongoTeamRepositoryAdapter implements TeamRepository {
    private final MongoTeamRepository repository;
    private final MongoPlayerRepository playerRepository;
    private final MongoTournamentRepository tournamentRepository;
    private final MongoGroupRepository groupRepository;
    private final MongoMatchRepository matchRepository;

    public MongoTeamRepositoryAdapter() {
        this(MongoDatabaseProvider.getDatabase());
    }

    public MongoTeamRepositoryAdapter(MongoDatabase database) {
        MongoIdGenerator idGenerator = new MongoIdGenerator(database);
        this.repository = new MongoTeamRepository(database, idGenerator);
        this.playerRepository = new MongoPlayerRepository(database, idGenerator);
        this.tournamentRepository = new MongoTournamentRepository(database, idGenerator);
        this.groupRepository = new MongoGroupRepository(database, idGenerator);
        this.matchRepository = new MongoMatchRepository(database, idGenerator);
    }

    @Override
    public void save(Team team) {
        requirePersistedPlayers(team);
        TeamDocument document = TeamMapper.toDocument(team);
        repository.save(document);
        team.setId(document.getId());
    }

    @Override
    public void update(Team team) {
        requirePersistedPlayers(team);
        repository.update(TeamMapper.toDocument(team));
    }

    @Override
    public void delete(Long id) {
        if (tournamentRepository.existsByRegisteredTeamId(id)
                || groupRepository.existsByTeamId(id)
                || matchRepository.existsByTeamId(id)) {
            throw new IllegalStateException("Cannot delete a team referenced by tournaments, groups, or matches");
        }
        repository.delete(id);
    }

    @Override
    public Optional<Team> findById(Long id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Team> findAll() {
        return repository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<Team> findByName(String name) {
        return repository.findByName(name).map(this::toDomain);
    }

    @Override
    public List<Team> findByRegion(String region) {
        return repository.findByRegion(region).stream().map(this::toDomain).toList();
    }

    /** Verifies a referenced team already exists; it never writes another collection. */
    public Long requirePersisted(Team team) {
        if (team.getId() == null || repository.findById(team.getId()).isEmpty()) {
            throw new IllegalArgumentException("Referenced team must be saved before it is linked");
        }
        return team.getId();
    }

    private void requirePersistedPlayers(Team team) {
        if (team.getPlayers() == null) return;
        for (Player player : team.getPlayers()) {
            if (player.getId() == null || playerRepository.findById(player.getId()).isEmpty()) {
                throw new IllegalArgumentException("Referenced players must be saved before the team");
            }
        }
    }

    private Team toDomain(TeamDocument document) {
        Team team = TeamMapper.toDomain(document);
        for (Long playerId : document.getPlayerIds()) {
            Player player = playerRepository.findById(playerId)
                    .map(PlayerMapper::toDomain)
                    .orElseThrow(() -> new IllegalStateException(
                            "Team " + document.getId() + " references missing player " + playerId));
            team.getPlayers().add(player);
        }
        return team;
    }
}
