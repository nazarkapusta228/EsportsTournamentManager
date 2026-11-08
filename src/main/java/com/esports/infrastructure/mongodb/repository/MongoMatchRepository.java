package com.esports.infrastructure.mongodb.repository;

import com.esports.infrastructure.mongodb.document.MatchDocument;
import com.mongodb.client.MongoDatabase;

import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.or;

public class MongoMatchRepository extends AbstractMongoRepository<MatchDocument> {
    public static final String COLLECTION_NAME = "matches";

    public MongoMatchRepository(MongoDatabase database, MongoIdGenerator idGenerator) {
        super(database.getCollection(COLLECTION_NAME, MatchDocument.class), idGenerator, COLLECTION_NAME);
    }

    public List<MatchDocument> findByStatus(String status) {
        return collection().find(eq("status", status)).into(new java.util.ArrayList<>());
    }

    public List<MatchDocument> findByGroupId(Long groupId) {
        return collection().find(eq("groupId", groupId)).into(new java.util.ArrayList<>());
    }

    public boolean existsByGroupId(Long groupId) {
        return collection().countDocuments(eq("groupId", groupId)) > 0;
    }

    public boolean existsByTeamId(Long teamId) {
        return collection().countDocuments(or(eq("team1Id", teamId), eq("team2Id", teamId))) > 0;
    }
}
