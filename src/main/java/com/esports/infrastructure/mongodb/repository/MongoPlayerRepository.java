package com.esports.infrastructure.mongodb.repository;

import com.esports.infrastructure.mongodb.document.PlayerDocument;
import com.mongodb.client.MongoDatabase;

import java.util.List;
import java.util.Optional;

import static com.mongodb.client.model.Filters.eq;

public class MongoPlayerRepository extends AbstractMongoRepository<PlayerDocument> {
    public static final String COLLECTION_NAME = "players";

    public MongoPlayerRepository(MongoDatabase database, MongoIdGenerator idGenerator) {
        super(database.getCollection(COLLECTION_NAME, PlayerDocument.class), idGenerator, COLLECTION_NAME);
    }

    public Optional<PlayerDocument> findByNickname(String nickname) {
        return Optional.ofNullable(collection().find(eq("nickname", nickname)).first());
    }

    public List<PlayerDocument> findByPlayerType(String type) {
        return collection().find(eq("type", type)).into(new java.util.ArrayList<>());
    }
}
