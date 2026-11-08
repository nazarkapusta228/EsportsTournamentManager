package com.esports.infrastructure.mongodb.repository;

import com.esports.infrastructure.mongodb.document.TeamDocument;
import com.mongodb.client.MongoDatabase;

import java.util.List;
import java.util.Optional;

import static com.mongodb.client.model.Filters.eq;

public class MongoTeamRepository extends AbstractMongoRepository<TeamDocument> {
    public static final String COLLECTION_NAME = "teams";

    public MongoTeamRepository(MongoDatabase database, MongoIdGenerator idGenerator) {
        super(database.getCollection(COLLECTION_NAME, TeamDocument.class), idGenerator, COLLECTION_NAME);
    }

    public Optional<TeamDocument> findByName(String name) {
        return Optional.ofNullable(collection().find(eq("name", name)).first());
    }

    public List<TeamDocument> findByRegion(String region) {
        return collection().find(eq("region", region)).into(new java.util.ArrayList<>());
    }

    public boolean existsByPlayerId(Long playerId) {
        return collection().countDocuments(eq("playerIds", playerId)) > 0;
    }
}
