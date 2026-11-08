package com.esports.infrastructure.mongodb.adapter;

import com.esports.domain.model.Group;
import com.esports.domain.model.Match;
import com.esports.domain.model.MatchStatus;
import com.esports.domain.model.Team;
import com.esports.domain.repository.MatchRepository;
import com.esports.infrastructure.mongodb.MongoDatabaseProvider;
import com.esports.infrastructure.mongodb.document.GroupDocument;
import com.esports.infrastructure.mongodb.document.MatchDocument;
import com.esports.infrastructure.mongodb.mapper.GroupMapper;
import com.esports.infrastructure.mongodb.mapper.MatchMapper;
import com.esports.infrastructure.mongodb.repository.MongoGroupRepository;
import com.esports.infrastructure.mongodb.repository.MongoIdGenerator;
import com.esports.infrastructure.mongodb.repository.MongoMatchRepository;
import com.mongodb.client.MongoDatabase;

import java.util.List;
import java.util.Optional;

public class MongoMatchRepositoryAdapter implements MatchRepository {
    private final MongoMatchRepository repository;
    private final MongoGroupRepository groupRepository;
    private final MongoTeamRepositoryAdapter teamAdapter;

    public MongoMatchRepositoryAdapter() {
        this(MongoDatabaseProvider.getDatabase());
    }

    public MongoMatchRepositoryAdapter(MongoDatabase database) {
        MongoIdGenerator idGenerator = new MongoIdGenerator(database);
        this.repository = new MongoMatchRepository(database, idGenerator);
        this.groupRepository = new MongoGroupRepository(database, idGenerator);
        this.teamAdapter = new MongoTeamRepositoryAdapter(database);
    }

    @Override
    public void save(Match match) {
        ensureTeam(match.getTeam1());
        ensureTeam(match.getTeam2());
        ensureGroup(match.getGroup());
        MatchDocument document = MatchMapper.toDocument(match);
        repository.save(document);
        match.setId(document.getId());
    }

    @Override
    public void update(Match match) {
        ensureTeam(match.getTeam1());
        ensureTeam(match.getTeam2());
        ensureGroup(match.getGroup());
        repository.update(MatchMapper.toDocument(match));
    }

    @Override
    public void delete(Long id) {
        repository.delete(id);
    }

    @Override
    public Optional<Match> findById(Long id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Match> findAll() {
        return repository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public List<Match> findByStatus(MatchStatus status) {
        return repository.findByStatus(status.name()).stream().map(this::toDomain).toList();
    }

    @Override
    public List<Match> findByGroupId(Long groupId) {
        return repository.findByGroupId(groupId).stream().map(this::toDomain).toList();
    }

    /** Maps a stored match with the supplied group to avoid recursive group loading. */
    public Match toDomain(MatchDocument document, Group group) {
        return MatchMapper.toDomain(document, findTeam(document.getTeam1Id()),
                findTeam(document.getTeam2Id()), group);
    }

    private Match toDomain(MatchDocument document) {
        Group group = null;
        if (document.getGroupId() != null) {
            GroupDocument groupDocument = groupRepository.findById(document.getGroupId())
                    .orElseThrow(() -> new IllegalStateException(
                            "Match " + document.getId() + " references missing group " + document.getGroupId()));
            group = GroupMapper.toDomain(groupDocument);
        }
        return toDomain(document, group);
    }

    private Team findTeam(Long id) {
        if (id == null) return null;
        return teamAdapter.findById(id)
                .orElseThrow(() -> new IllegalStateException("Match references missing team " + id));
    }

    private void ensureTeam(Team team) {
        if (team != null) teamAdapter.requirePersisted(team);
    }

    private void ensureGroup(Group group) {
        if (group != null && (group.getId() == null || groupRepository.findById(group.getId()).isEmpty())) {
            throw new IllegalArgumentException("Referenced group must be saved before the match");
        }
    }
}
