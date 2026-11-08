package com.esports.infrastructure.mongodb.adapter;

import com.esports.domain.model.Group;
import com.esports.domain.model.Match;
import com.esports.domain.model.Team;
import com.esports.domain.repository.GroupRepository;
import com.esports.infrastructure.mongodb.MongoDatabaseProvider;
import com.esports.infrastructure.mongodb.document.GroupDocument;
import com.esports.infrastructure.mongodb.document.MatchDocument;
import com.esports.infrastructure.mongodb.mapper.GroupMapper;
import com.esports.infrastructure.mongodb.repository.MongoGroupRepository;
import com.esports.infrastructure.mongodb.repository.MongoIdGenerator;
import com.esports.infrastructure.mongodb.repository.MongoMatchRepository;
import com.mongodb.client.MongoDatabase;

import java.util.List;
import java.util.Optional;

public class MongoGroupRepositoryAdapter implements GroupRepository {
    private final MongoGroupRepository repository;
    private final MongoMatchRepository matchRepository;
    private final MongoTournamentRepositoryAdapter tournamentAdapter;
    private final MongoTeamRepositoryAdapter teamAdapter;
    private final MongoMatchRepositoryAdapter matchAdapter;

    public MongoGroupRepositoryAdapter() {
        this(MongoDatabaseProvider.getDatabase());
    }

    public MongoGroupRepositoryAdapter(MongoDatabase database) {
        MongoIdGenerator idGenerator = new MongoIdGenerator(database);
        this.repository = new MongoGroupRepository(database, idGenerator);
        this.matchRepository = new MongoMatchRepository(database, idGenerator);
        this.tournamentAdapter = new MongoTournamentRepositoryAdapter(database);
        this.teamAdapter = new MongoTeamRepositoryAdapter(database);
        this.matchAdapter = new MongoMatchRepositoryAdapter(database);
    }

    @Override
    public void save(Group group) {
        validateReferences(group);
        GroupDocument document = GroupMapper.toDocument(group);
        repository.save(document);
        group.setId(document.getId());
    }

    @Override
    public void update(Group group) {
        validateReferences(group);
        repository.update(GroupMapper.toDocument(group));
    }

    @Override
    public void delete(Long id) {
        if (matchRepository.existsByGroupId(id)) {
            throw new IllegalStateException("Cannot delete a group that still has matches");
        }
        repository.delete(id);
    }

    @Override
    public Optional<Group> findById(Long id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Group> findAll() {
        return repository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public List<Group> findByTournamentId(Long tournamentId) {
        return repository.findByTournamentId(tournamentId).stream().map(this::toDomain).toList();
    }

    private void validateReferences(Group group) {
        for (Team team : group.getTeams()) {
            teamAdapter.requirePersisted(team);
        }
        if (group.getTournament() != null) {
            if (group.getTournament().getId() == null
                    || tournamentAdapter.findById(group.getTournament().getId()).isEmpty()) {
                throw new IllegalArgumentException("Referenced tournament must be saved before the group");
            }
        }
    }

    private Group toDomain(GroupDocument document) {
        Group group = GroupMapper.toDomain(document);
        if (document.getTournamentId() != null) {
            group.setTournament(tournamentAdapter.findById(document.getTournamentId())
                    .orElseThrow(() -> new IllegalStateException(
                            "Group " + document.getId() + " references missing tournament "
                                    + document.getTournamentId())));
        }
        for (Long teamId : document.getTeamIds()) {
            Team team = teamAdapter.findById(teamId)
                    .orElseThrow(() -> new IllegalStateException(
                            "Group " + document.getId() + " references missing team " + teamId));
            group.getTeams().add(team);
        }
        for (MatchDocument matchDocument : matchRepository.findByGroupId(document.getId())) {
            Match match = matchAdapter.toDomain(matchDocument, group);
            group.getMatches().add(match);
        }
        return group;
    }
}
