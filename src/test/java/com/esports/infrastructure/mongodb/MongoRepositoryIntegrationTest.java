package com.esports.infrastructure.mongodb;

import com.esports.domain.model.MatchStatus;
import com.esports.domain.model.TournamentStatus;
import com.esports.infrastructure.mongodb.document.GroupDocument;
import com.esports.infrastructure.mongodb.document.MatchDocument;
import com.esports.infrastructure.mongodb.document.PlayerDocument;
import com.esports.infrastructure.mongodb.document.TeamDocument;
import com.esports.infrastructure.mongodb.document.TournamentDocument;
import com.esports.infrastructure.mongodb.repository.MongoGroupRepository;
import com.esports.infrastructure.mongodb.repository.MongoIdGenerator;
import com.esports.infrastructure.mongodb.repository.MongoMatchRepository;
import com.esports.infrastructure.mongodb.repository.MongoPlayerRepository;
import com.esports.infrastructure.mongodb.repository.MongoTeamRepository;
import com.esports.infrastructure.mongodb.repository.MongoTournamentRepository;
import com.mongodb.client.MongoDatabase;
import com.mongodb.MongoWriteException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MongoRepositoryIntegrationTest {
    private static MongoDatabase database;
    private static MongoIdGenerator idGenerator;

    @BeforeAll
    static void setUp() {
        database = MongoDatabaseProvider.getDatabase("repo_test_" + UUID.randomUUID().toString().replace("-", ""));
        database.runCommand(new org.bson.Document("ping", 1));
        idGenerator = new MongoIdGenerator(database);
    }

    @AfterAll
    static void tearDown() {
        if (database != null) database.drop();
    }

    @Test
    void repositoriesSupportCreateReadUpdateDeleteAndQueries() {
        MongoPlayerRepository players = new MongoPlayerRepository(database, idGenerator);
        PlayerDocument player = new PlayerDocument();
        player.setType("PRO");
        player.setNickname("repo-player");
        player.setRole("AWPer");
        players.save(player);
        assertNotNull(player.getId());
        assertEquals(player.getId(), players.findByNickname("repo-player").orElseThrow().getId());
        assertEquals(1, players.findByPlayerType("PRO").size());
        player.setRole("AWPer");
        players.update(player);
        assertEquals("AWPer", players.findById(player.getId()).orElseThrow().getRole());
        players.delete(player.getId());
        assertTrue(players.findById(player.getId()).isEmpty());

        MongoTeamRepository teams = new MongoTeamRepository(database, idGenerator);
        TeamDocument team = new TeamDocument();
        team.setName("repo-team");
        team.setRegion("EU");
        team.getPlayerIds().add(11L);
        teams.save(team);
        assertEquals(team.getId(), teams.findByName("repo-team").orElseThrow().getId());
        assertEquals(1, teams.findByRegion("EU").size());

        TeamDocument overfullTeam = new TeamDocument();
        overfullTeam.setName("overfull-team");
        overfullTeam.setRegion("EU");
        overfullTeam.setPlayerIds(java.util.stream.LongStream.rangeClosed(1, 8).boxed().toList());
        assertThrows(MongoWriteException.class, () -> teams.save(overfullTeam));

        MongoTournamentRepository tournaments = new MongoTournamentRepository(database, idGenerator);
        TournamentDocument tournament = new TournamentDocument();
        tournament.setType("ONLINE");
        tournament.setName("repo-tournament");
        tournament.setStatus(TournamentStatus.UPCOMING.name());
        tournament.setMaxTeams(8);
        tournaments.save(tournament);
        assertEquals(tournament.getId(), tournaments.findByName("repo-tournament").orElseThrow().getId());
        assertEquals(1, tournaments.findByStatus(TournamentStatus.UPCOMING.name()).size());

        TournamentDocument overCapacity = new TournamentDocument();
        overCapacity.setType("ONLINE");
        overCapacity.setName("over-capacity");
        overCapacity.setStatus(TournamentStatus.UPCOMING.name());
        overCapacity.setMaxTeams(2);
        overCapacity.setRegisteredTeamIds(java.util.List.of(1L, 2L, 3L));
        assertThrows(MongoWriteException.class, () -> tournaments.save(overCapacity));

        MongoGroupRepository groups = new MongoGroupRepository(database, idGenerator);
        GroupDocument group = new GroupDocument();
        group.setName("repo-group");
        group.setTournamentId(tournament.getId());
        groups.save(group);
        assertEquals(group.getId(), groups.findByTournamentId(tournament.getId()).get(0).getId());

        MongoMatchRepository matches = new MongoMatchRepository(database, idGenerator);
        MatchDocument match = new MatchDocument();
        match.setTeam1Id(team.getId());
        match.setGroupId(group.getId());
        match.setTeam1Score(0);
        match.setTeam2Score(0);
        match.setStatus(MatchStatus.SCHEDULED.name());
        matches.save(match);
        assertEquals(match.getId(), matches.findByStatus(MatchStatus.SCHEDULED.name()).get(0).getId());
        assertEquals(match.getId(), matches.findByGroupId(group.getId()).get(0).getId());
        matches.delete(match.getId());
        assertTrue(matches.findById(match.getId()).isEmpty());
    }
}
