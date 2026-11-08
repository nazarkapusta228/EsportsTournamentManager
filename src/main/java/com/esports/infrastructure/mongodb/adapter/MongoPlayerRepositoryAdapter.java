package com.esports.infrastructure.mongodb.adapter;

import com.esports.domain.model.Player;
import com.esports.domain.repository.PlayerRepository;
import com.esports.infrastructure.mongodb.MongoDatabaseProvider;
import com.esports.infrastructure.mongodb.document.PlayerDocument;
import com.esports.infrastructure.mongodb.mapper.PlayerMapper;
import com.esports.infrastructure.mongodb.repository.MongoIdGenerator;
import com.esports.infrastructure.mongodb.repository.MongoPlayerRepository;
import com.esports.infrastructure.mongodb.repository.MongoTeamRepository;
import com.mongodb.client.MongoDatabase;

import java.util.List;
import java.util.Optional;

public class MongoPlayerRepositoryAdapter implements PlayerRepository {
    private final MongoPlayerRepository repository;
    private final MongoTeamRepository teamRepository;

    public MongoPlayerRepositoryAdapter() {
        this(MongoDatabaseProvider.getDatabase());
    }

    public MongoPlayerRepositoryAdapter(MongoDatabase database) {
        MongoIdGenerator idGenerator = new MongoIdGenerator(database);
        this.repository = new MongoPlayerRepository(database, idGenerator);
        this.teamRepository = new MongoTeamRepository(database, idGenerator);
    }

    @Override
    public void save(Player player) {
        PlayerDocument document = PlayerMapper.toDocument(player);
        repository.save(document);
        player.setId(document.getId());
    }

    @Override
    public void update(Player player) {
        repository.update(PlayerMapper.toDocument(player));
    }

    @Override
    public void delete(Long id) {
        if (teamRepository.existsByPlayerId(id)) {
            throw new IllegalStateException("Cannot delete a player referenced by a team");
        }
        repository.delete(id);
    }

    @Override
    public Optional<Player> findById(Long id) {
        return repository.findById(id).map(PlayerMapper::toDomain);
    }

    @Override
    public List<Player> findAll() {
        return repository.findAll().stream().map(PlayerMapper::toDomain).toList();
    }

    @Override
    public Optional<Player> findByNickname(String nickname) {
        return repository.findByNickname(nickname).map(PlayerMapper::toDomain);
    }

    @Override
    public List<Player> findByPlayerType(String playerType) {
        return repository.findByPlayerType(playerType).stream().map(PlayerMapper::toDomain).toList();
    }
}
