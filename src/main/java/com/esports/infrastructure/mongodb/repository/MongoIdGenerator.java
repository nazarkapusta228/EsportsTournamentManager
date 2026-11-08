package com.esports.infrastructure.mongodb.repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.model.Updates;
import com.mongodb.WriteConcern;
import org.bson.Document;

import static com.mongodb.client.model.Filters.eq;

/** Generates monotonically increasing Long identifiers using an atomic counter document. */
public class MongoIdGenerator {
    private static final String COUNTERS_COLLECTION = "counters";
    private final MongoCollection<Document> counters;

    public MongoIdGenerator(MongoDatabase database) {
        this.counters = database.getCollection(COUNTERS_COLLECTION)
                .withWriteConcern(WriteConcern.MAJORITY);
    }

    public long nextId(String sequenceName) {
        Document counter = counters.findOneAndUpdate(
                eq("_id", sequenceName),
                Updates.inc("value", 1L),
                new FindOneAndUpdateOptions().upsert(true).returnDocument(ReturnDocument.AFTER));
        if (counter == null || !(counter.get("value") instanceof Number value)) {
            throw new IllegalStateException("Could not allocate MongoDB ID for " + sequenceName);
        }
        return value.longValue();
    }
}
