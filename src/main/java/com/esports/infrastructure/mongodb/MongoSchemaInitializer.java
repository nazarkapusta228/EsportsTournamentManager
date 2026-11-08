package com.esports.infrastructure.mongodb;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.ValidationAction;
import com.mongodb.client.model.ValidationLevel;
import com.mongodb.client.model.ValidationOptions;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

/** Installs database-enforced document shape and business constraints. */
public final class MongoSchemaInitializer {
    private MongoSchemaInitializer() {
    }

    public static synchronized void initialize(MongoDatabase database) {
        ensureCollection(database, "players", Document.parse("""
                { "$jsonSchema": {
                    "bsonType": "object",
                    "required": ["_id", "type", "nickname", "role"],
                    "properties": {
                      "_id": {"bsonType": "long"},
                      "type": {"bsonType": "string", "enum": ["PRO", "AMATEUR"]},
                      "nickname": {"bsonType": "string", "minLength": 1},
                      "role": {"bsonType": "string", "minLength": 1},
                      "salary": {"bsonType": ["decimal", "null"]},
                      "sponsor": {"bsonType": ["string", "null"]},
                      "rank": {"bsonType": ["string", "null"]},
                      "hoursPlayed": {"bsonType": ["int", "long", "null"], "minimum": 0}
                    }
                }}
                """));

        ensureCollection(database, "teams", Document.parse("""
                { "$jsonSchema": {
                    "bsonType": "object",
                    "required": ["_id", "name", "region", "playerIds"],
                    "properties": {
                      "_id": {"bsonType": "long"},
                      "name": {"bsonType": "string", "minLength": 1},
                      "region": {"bsonType": "string", "minLength": 1},
                      "playerIds": {
                        "bsonType": "array", "maxItems": 7, "uniqueItems": true,
                        "items": {"bsonType": "long"}
                      }
                    }
                }}
                """));

        Document tournamentShape = Document.parse("""
                { "$jsonSchema": {
                    "bsonType": "object",
                    "required": ["_id", "type", "name", "status", "maxTeams", "registeredTeamIds"],
                    "properties": {
                      "_id": {"bsonType": "long"},
                      "type": {"bsonType": "string", "enum": ["LAN", "ONLINE"]},
                      "name": {"bsonType": "string", "minLength": 1},
                      "status": {"bsonType": "string", "enum": ["UPCOMING", "REGISTRATION_OPEN", "REGISTRATION_CLOSED", "IN_PROGRESS", "COMPLETED", "CANCELLED"]},
                      "maxTeams": {"bsonType": ["int", "long"], "minimum": 2},
                      "registeredTeamIds": {
                        "bsonType": "array", "uniqueItems": true,
                        "items": {"bsonType": "long"}
                      }
                    }
                }}
                """
        );
        Document tournamentCapacity = new Document("$expr", new Document("$lte", List.of(
                new Document("$size", new Document("$ifNull", List.of("$registeredTeamIds", List.of()))),
                "$maxTeams")));
        ensureCollection(database, "tournaments",
                new Document("$and", List.of(tournamentShape, tournamentCapacity)));

        ensureCollection(database, "groups", Document.parse("""
                { "$jsonSchema": {
                    "bsonType": "object",
                    "required": ["_id", "name", "teamIds"],
                    "properties": {
                      "_id": {"bsonType": "long"},
                      "name": {"bsonType": "string", "minLength": 1},
                      "tournamentId": {"bsonType": ["long", "null"]},
                      "teamIds": {
                        "bsonType": "array", "uniqueItems": true,
                        "items": {"bsonType": "long"}
                      }
                    }
                }}
                """));

        ensureCollection(database, "matches", Document.parse("""
                { "$jsonSchema": {
                    "bsonType": "object",
                    "required": ["_id", "team1Score", "team2Score", "status"],
                    "properties": {
                      "_id": {"bsonType": "long"},
                      "team1Id": {"bsonType": ["long", "null"]},
                      "team2Id": {"bsonType": ["long", "null"]},
                      "team1Score": {"bsonType": "int", "minimum": 0},
                      "team2Score": {"bsonType": "int", "minimum": 0},
                      "matchDate": {"bsonType": ["date", "null"]},
                      "status": {"bsonType": "string", "enum": ["SCHEDULED", "IN_PROGRESS", "COMPLETED", "CANCELLED"]},
                      "groupId": {"bsonType": ["long", "null"]},
                      "round": {"bsonType": ["string", "null"]}
                    }
                }}
                """));

        ensureCollection(database, "counters", Document.parse("""
                { "$jsonSchema": {
                    "bsonType": "object",
                    "required": ["_id", "value"],
                    "properties": {
                      "_id": {"bsonType": "string"},
                      "value": {"bsonType": "long", "minimum": 0}
                    }
                }}
                """));

        database.getCollection("players").createIndex(Indexes.ascending("nickname"),
                new IndexOptions().unique(true));
        database.getCollection("teams").createIndex(Indexes.ascending("name"),
                new IndexOptions().unique(true));
        database.getCollection("tournaments").createIndex(Indexes.ascending("name"),
                new IndexOptions().unique(true));
        database.getCollection("groups").createIndex(Indexes.ascending("tournamentId"));
        database.getCollection("matches").createIndex(Indexes.ascending("groupId"));
    }

    private static void ensureCollection(MongoDatabase database, String name, Document validator) {
        boolean exists = database.listCollectionNames().into(new ArrayList<>()).contains(name);
        if (exists) {
            database.runCommand(new Document("collMod", name)
                    .append("validator", validator)
                    .append("validationLevel", "strict")
                    .append("validationAction", "error"));
            return;
        }
        ValidationOptions validation = new ValidationOptions()
                .validator(validator)
                .validationLevel(ValidationLevel.STRICT)
                .validationAction(ValidationAction.ERROR);
        database.createCollection(name, new CreateCollectionOptions().validationOptions(validation));
    }
}
