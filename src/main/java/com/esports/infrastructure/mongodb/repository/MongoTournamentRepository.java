package com.esports.infrastructure.mongodb.repository;

import com.esports.infrastructure.mongodb.document.TournamentDocument;
import com.mongodb.client.MongoDatabase;

import java.util.List;
import java.util.Optional;

import static com.mongodb.client.model.Filters.eq;

public class MongoTournamentRepository extends AbstractMongoRepository<TournamentDocument> {
    public static final String COLLECTION_NAME = "tournaments";

    public MongoTournamentRepository(MongoDatabase database, MongoIdGenerator idGenerator) {
        super(database.getCollection(COLLECTION_NAME, TournamentDocument.class), idGenerator, COLLECTION_NAME);
    }

    public List<TournamentDocument> findByStatus(String status) {
        return collection().find(eq("status", status)).into(new java.util.ArrayList<>());
    }

    public Optional<TournamentDocument> findByName(String name) {
        return Optional.ofNullable(collection().find(eq("name", name)).first());
    }

    public boolean existsByRegisteredTeamId(Long teamId) {
        return collection().countDocuments(eq("registeredTeamIds", teamId)) > 0;
    }
}
