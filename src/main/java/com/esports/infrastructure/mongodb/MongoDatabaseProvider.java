package com.esports.infrastructure.mongodb;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.codecs.BigDecimalCodec;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import static org.bson.codecs.configuration.CodecRegistries.fromCodecs;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public final class MongoDatabaseProvider {
    private static final String CONNECTION_STRING = System.getProperty("mongodb.uri",
            System.getenv().getOrDefault("MONGODB_URI",
                    "mongodb://mongo-rs.local:27017,mongo-rs.local:27018,mongo-rs.local:27019/?replicaSet=rs0"));
    private static final String DATABASE_NAME = System.getProperty("mongodb.database",
            System.getenv().getOrDefault("MONGODB_DATABASE", "esportsdb"));

    private static final MongoClient CLIENT = MongoClients.create(CONNECTION_STRING);
    private static final CodecRegistry CODEC_REGISTRY = fromRegistries(
            fromCodecs(new BigDecimalCodec()),
            CLIENT.getDatabase(DATABASE_NAME).getCodecRegistry(),
            fromProviders(PojoCodecProvider.builder().automatic(true).build()));
    private static final MongoDatabase DATABASE = getDatabase(DATABASE_NAME);

    private MongoDatabaseProvider() {
    }

    public static MongoDatabase getDatabase() {
        return DATABASE;
    }

    public static MongoClient getClient() {
        return CLIENT;
    }

    public static String getConnectionString() {
        return CONNECTION_STRING;
    }

    public static MongoDatabase getDatabase(String databaseName) {
        MongoDatabase database = CLIENT.getDatabase(databaseName).withCodecRegistry(CODEC_REGISTRY);
        MongoSchemaInitializer.initialize(database);
        return database;
    }

    public static void close() {
        CLIENT.close();
    }
}
