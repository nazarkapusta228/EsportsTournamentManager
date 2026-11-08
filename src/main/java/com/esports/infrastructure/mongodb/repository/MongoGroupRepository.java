package com.esports.infrastructure.mongodb.repository;

import com.esports.infrastructure.mongodb.document.GroupDocument;
import com.mongodb.client.MongoDatabase;

import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class MongoGroupRepository extends AbstractMongoRepository<GroupDocument> {
    public static final String COLLECTION_NAME = "groups";

    public MongoGroupRepository(MongoDatabase database, MongoIdGenerator idGenerator) {
        super(database.getCollection(COLLECTION_NAME, GroupDocument.class), idGenerator, COLLECTION_NAME);
    }

    public List<GroupDocument> findByTournamentId(Long tournamentId) {
        return collection().find(eq("tournamentId", tournamentId)).into(new java.util.ArrayList<>());
    }

    public boolean existsByTournamentId(Long tournamentId) {
        return collection().countDocuments(eq("tournamentId", tournamentId)) > 0;
    }

    public boolean existsByTeamId(Long teamId) {
        return collection().countDocuments(eq("teamIds", teamId)) > 0;
    }
}
