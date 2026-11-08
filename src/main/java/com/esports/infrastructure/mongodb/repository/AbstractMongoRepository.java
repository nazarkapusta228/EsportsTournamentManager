package com.esports.infrastructure.mongodb.repository;

import com.esports.infrastructure.mongodb.document.IdentifiedDocument;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.ReplaceOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.mongodb.client.model.Filters.eq;

abstract class AbstractMongoRepository<T extends IdentifiedDocument> {
    private final MongoCollection<T> collection;
    private final MongoIdGenerator idGenerator;
    private final String sequenceName;

    AbstractMongoRepository(MongoCollection<T> collection, MongoIdGenerator idGenerator,
                            String sequenceName) {
        this.collection = collection;
        this.idGenerator = idGenerator;
        this.sequenceName = sequenceName;
    }

    protected MongoCollection<T> collection() {
        return collection;
    }

    public void save(T document) {
        if (document.getId() == null) {
            document.setId(idGenerator.nextId(sequenceName));
        }
        collection.insertOne(document);
    }

    public void update(T document) {
        if (document.getId() == null) {
            throw new IllegalArgumentException("Cannot update a document without an ID");
        }
        if (collection.replaceOne(eq("_id", document.getId()), document,
                new ReplaceOptions().upsert(false)).getMatchedCount() == 0) {
            throw new IllegalArgumentException("No document found with ID " + document.getId());
        }
    }

    public void delete(Long id) {
        collection.deleteOne(eq("_id", id));
    }

    public Optional<T> findById(Long id) {
        return Optional.ofNullable(collection.find(eq("_id", id)).first());
    }

    public List<T> findAll() {
        return collection.find().into(new ArrayList<>());
    }
}
