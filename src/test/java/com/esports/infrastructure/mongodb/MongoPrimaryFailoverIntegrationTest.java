package com.esports.infrastructure.mongodb;

import com.esports.infrastructure.mongodb.document.TeamDocument;
import com.esports.infrastructure.mongodb.repository.MongoIdGenerator;
import com.esports.infrastructure.mongodb.repository.MongoTeamRepository;
import com.mongodb.MongoException;
import com.mongodb.client.MongoDatabase;
import com.mongodb.connection.ServerDescription;
import com.mongodb.connection.ServerType;
import org.bson.Document;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class MongoPrimaryFailoverIntegrationTest {
    @Test
    void repositoryRemainsUsableAfterCurrentPrimaryIsStopped() throws Exception {
        String databaseName = "failover_test_" + UUID.randomUUID().toString().replace("-", "");
        MongoDatabase database = MongoDatabaseProvider.getDatabase(databaseName);
        String stoppedService = null;
        boolean serviceStopped = false;
        Long writtenTeamId = null;

        try {
            database.runCommand(new Document("ping", 1));
            ServerDescription originalPrimary = awaitPrimary(Duration.ofSeconds(15));
            int originalPrimaryPort = originalPrimary.getAddress().getPort();
            stoppedService = serviceForPort(originalPrimaryPort);

            runCompose("stop", stoppedService);
            serviceStopped = true;

            ServerDescription electedPrimary = awaitDifferentPrimary(
                    originalPrimaryPort, Duration.ofSeconds(45));
            assertNotEquals(originalPrimaryPort, electedPrimary.getAddress().getPort());

            MongoTeamRepository repository = new MongoTeamRepository(database,
                    new MongoIdGenerator(database));
            TeamDocument team = new TeamDocument();
            team.setName("failover-team");
            team.setRegion("EU");
            repository.save(team);
            writtenTeamId = team.getId();

            TeamDocument readBack = repository.findById(team.getId()).orElseThrow();
            assertEquals("failover-team", readBack.getName());
            assertEquals("EU", readBack.getRegion());
        } finally {
            try {
                if (serviceStopped) {
                    runCompose("start", stoppedService);
                    awaitHealthyReplicaSet(Duration.ofSeconds(45));
                }
                if (writtenTeamId != null) {
                    TeamDocument afterRestore = new MongoTeamRepository(database,
                            new MongoIdGenerator(database)).findById(writtenTeamId).orElseThrow();
                    assertEquals("failover-team", afterRestore.getName());
                    assertEquals("EU", afterRestore.getRegion());
                }
            } finally {
                database.drop();
            }
        }
    }

    private static ServerDescription awaitPrimary(Duration timeout) throws InterruptedException {
        return awaitPrimaryMatching(null, timeout);
    }

    private static ServerDescription awaitDifferentPrimary(int excludedPort, Duration timeout)
            throws InterruptedException {
        return awaitPrimaryMatching(excludedPort, timeout);
    }

    private static void awaitHealthyReplicaSet(Duration timeout) throws InterruptedException {
        long deadline = System.nanoTime() + timeout.toNanos();
        while (System.nanoTime() < deadline) {
            var servers = MongoDatabaseProvider.getClient().getClusterDescription().getServerDescriptions();
            boolean allAvailable = servers.size() == 3 && servers.stream().allMatch(server ->
                    server.getType() == ServerType.REPLICA_SET_PRIMARY
                            || server.getType() == ServerType.REPLICA_SET_SECONDARY);
            if (allAvailable) {
                try {
                    MongoDatabaseProvider.getDatabase().runCommand(new Document("ping", 1));
                    return;
                } catch (MongoException ignored) {
                    // Wait for the rejoined member and current primary to accept requests.
                }
            }
            Thread.sleep(500);
        }
        throw new AssertionError("All three replica-set members did not return within "
                + timeout.toSeconds() + " seconds");
    }

    private static ServerDescription awaitPrimaryMatching(Integer excludedPort, Duration timeout)
            throws InterruptedException {
        long deadline = System.nanoTime() + timeout.toNanos();
        while (System.nanoTime() < deadline) {
            for (ServerDescription server : MongoDatabaseProvider.getClient()
                    .getClusterDescription().getServerDescriptions()) {
                if (server.getType() == ServerType.REPLICA_SET_PRIMARY
                        && (excludedPort == null || server.getAddress().getPort() != excludedPort)) {
                    try {
                        MongoDatabaseProvider.getDatabase().runCommand(new Document("ping", 1));
                        return server;
                    } catch (MongoException ignored) {
                        // The topology description can update just before the new primary accepts requests.
                    }
                }
            }
            Thread.sleep(500);
        }
        throw new AssertionError("No eligible primary elected within " + timeout.toSeconds() + " seconds");
    }

    private static String serviceForPort(int port) {
        return switch (port) {
            case 27017 -> "mongo1";
            case 27018 -> "mongo2";
            case 27019 -> "mongo3";
            default -> throw new IllegalStateException("Unexpected replica-set member port: " + port);
        };
    }

    private static void runCompose(String action, String service) throws Exception {
        File composeDirectory = new File(System.getProperty("user.dir"), "Docker");
        Process process = new ProcessBuilder("docker", "compose", "-f", "docker-compose.yml", action, service)
                .directory(composeDirectory)
                .redirectErrorStream(true)
                .start();
        if (!process.waitFor(30, java.util.concurrent.TimeUnit.SECONDS)) {
            process.destroyForcibly();
            throw new IllegalStateException("docker compose " + action + " timed out for " + service);
        }
        String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        if (process.exitValue() != 0) {
            throw new IllegalStateException("docker compose " + action + " failed: " + output);
        }
    }
}
